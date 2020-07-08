/* 
 * The MIT License
 *
 * Copyright 2020 sunil.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ssamant.utilities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ssamant.pocresourcemanagement.MainForm;
import static com.ssamant.utilities.ConfigureIngestionLayer.readInputStreamFromSshSession;
import static com.ssamant.utilities.DatabaseConnection.getConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logincredentials.CloudLogin;

/**
 *
 * @author sunil
 */
public class ConfigureProcessingLayer {

    public ConfigureProcessingLayer() {

    }
    public static String ami_id_spark = "";

    public static void buildProcessingLayerCluster(int noOfNodes, String instanceType, String ami_id, String clusterType, Boolean fromDPPScaling) {
        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        createEC2NodeForProcessingLayer(noOfNodes, instanceType, ec2Client, ami_id, clusterType, fromDPPScaling);
    }

    public static void createMasterNode(String instanceType) {
        ami_id_spark = DatabaseConnection.getServiceAmi("spark-master");
        try {
            AmazonEC2 ec2Client = CloudLogin.getEC2Client();
            RunInstancesRequest runRequest = new RunInstancesRequest()
                    .withImageId(ami_id_spark)
                    .withInstanceType(instanceType) //free -tier instance type used
                    .withKeyName("mySSHkey") //keypair name
                    .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                    .withMaxCount(1)
                    .withMinCount(1);
            RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
            Instance inst = runResponse.getReservation().getInstances().get(0);
            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue("spark-master");
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                    .withResources(inst.getInstanceId())
                    .withTags(tag);
            CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance curInstance = ConfigureStorageLayer.waitForRunningState(ec2Client, inst.getInstanceId());
            if (curInstance != null) {
                System.out.println("successfully created master node for the spark cluster.");
                dbUpdateMasterNodeInfo(curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress(), curInstance.getInstanceId());
                MainForm.txtAreaSparkResourcesInfo.append("--------Spark Master Node Info-----------------\n");
                MainForm.txtAreaSparkResourcesInfo.append("Public DNS: " + curInstance.getPublicDnsName() + ", Public IP: " + curInstance.getPublicIpAddress() + ", Private IP: " + curInstance.getPrivateIpAddress() + ", Instance Id: " + curInstance.getInstanceId() + ".\n");
                sleep(5000);
                Boolean success = configureAndRunMasterNode(curInstance.getPublicDnsName());
                if (success) {
                    MainForm.txtAreaSparkResourcesInfo.append("----------------------------\n");
                    MainForm.txtAreaSparkResourcesInfo.append("Spark Cluster Running... accessible at: www." + curInstance.getPublicDnsName() + ":8080\n");
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void dbUpdateMasterNodeInfo(String pubDns, String pubIp, String privIp, String instId) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_cluster_info SET master_instance_id = ?, master_public_dnsname = ?, master_public_ip = ?, master_private_ip = ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, instId);
                update.setString(2, pubDns);
                update.setString(3, pubIp);
                update.setString(4, privIp);
                update.setInt(5, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createEC2NodeForProcessingLayer(int noOfNodes, String instanceType, AmazonEC2 ec2Client, String amiId, String clusterType, Boolean fromDPPScaling) {
        String nodeType = "";
        String tagValue = "";
        String serviceName = "";
        if ("Single-node".equals(clusterType)) {
            tagValue = "spark-node-0";
            nodeType = "local";
            serviceName = "spark-local";
        }
        if ("Multi-node".equals(clusterType)) {
            tagValue = "spark-worker-0";
            nodeType = "worker";
            serviceName = "spark-worker";
        }
        String imageId = DatabaseConnection.getServiceAmi(serviceName);

        if (!"".equals(imageId)) {
            amiId = imageId;
        }

        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                .withInstanceType(instanceType) //free -tier instance type used
                .withKeyName("mySSHkey") //keypair name
                .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                .withMaxCount(noOfNodes)
                .withMinCount(1);

        @SuppressWarnings("ThrowableResultIgnored")
        RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
        // List<String> instanceIds = new ArrayList<>();
        // WriteFile data = new WriteFile("C:\\Code\\SparkClusterDetails.txt", true); //to save cluster info in a text file - append mode.
        if (DatabaseConnection.con == null) {
            try {
                DatabaseConnection.con = DatabaseConnection.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int i = 1;
        int noOfWorkers = DatabaseConnection.getCurrentInstanceCount("processing");
        if (noOfWorkers == 0) {
            i = 1;
        }
        if(noOfWorkers>0){
            i = noOfWorkers +1;
        }
        for (Instance inst : runResponse.getReservation().getInstances()) {
            System.out.println("EC2 Instance Id: " + inst.getInstanceId());
            // instanceIds.add(inst.getInstanceId());
            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue(tagValue + String.valueOf(i));
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                    .withResources(inst.getInstanceId())
                    .withTags(tag);
            CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
            startInstanceForProcessingCluster(ec2Client, inst, inst.getPlacement(), nodeType, fromDPPScaling);
            i++;
        }
    }

    public static void startInstanceForProcessingCluster(AmazonEC2 ec2Client, Instance inst, Placement az, String nodeType, Boolean fromDPPScaling) {
        try {
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance curInstance = ConfigureStorageLayer.waitForRunningState(ec2Client, inst.getInstanceId());
            if (curInstance != null) {
                System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
                // MainForm.txtAreaSparkResourcesInfo.append("Successfully created the following ec2 instances for the Spark Cluster:\n");
                MainForm.txtAreaSparkResourcesInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".\n");
                dbInsertSpakInstanceDetail(curInstance.getInstanceId(), curInstance.getInstanceType(), az.getAvailabilityZone(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress(), curInstance.getState().getName(), nodeType);
                updateSparkClusterInfo(curInstance.getInstanceType());
                if (fromDPPScaling) {
                    updateMasterNode();
                    configureNewlyCreatedSparkNode(curInstance.getPublicDnsName(), "worker");
                }
            } else {
                System.out.println("Instances are not running.");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
            MainForm.txtAreaSparkResourcesInfo.append(ex.getMessage());
        }
    }

    public static void updateSparkClusterInfo(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_cluster_info SET no_of_nodes = no_of_nodes + ?, instance_types = CONCAT(instance_types, ?) WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType + "");
                update.setInt(3, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void dbInsertSpakInstanceDetail(String instanceId, String instanceType, String az, String pubDnsName, String publicIp, String privateIp, String status, String nodeType) {
        try {
            String query = "INSERT INTO processing_nodes_info (instance_id, instance_type, availability_zone, public_dnsname, public_ip, private_ip, status, node_type)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStmt = DatabaseConnection.con.prepareStatement(query)) {
                preparedStmt.setString(1, instanceId);
                preparedStmt.setString(2, instanceType);
                preparedStmt.setString(3, az);
                preparedStmt.setString(4, pubDnsName);
                preparedStmt.setString(5, publicIp);
                preparedStmt.setString(6, privateIp);
                preparedStmt.setString(7, status);
                preparedStmt.setString(8, nodeType);
                preparedStmt.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ResultSet loadSparkClusterDetailsFromDb() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM processing_nodes_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet loadCurrentSparkClusterInfo() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM processing_cluster_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;

    }

    public static void loadSparkClusterInfoFromFile() {
        MainForm.txtAreaSparkResourcesInfo.setText("");
        String fileName = "C:\\Code\\SparkClusterDetails.txt";
        try {
            if (new File("C:\\Code\\KafkaClusterDetails.txt").exists()) {
                FileReader file = new FileReader(fileName);
                try (BufferedReader rdr = new BufferedReader(file)) {
                    String aLine;
                    while ((aLine = rdr.readLine()) != null) {
                        MainForm.txtAreaSparkResourcesInfo.append(aLine);
                        MainForm.txtAreaSparkResourcesInfo.append("\n");
                    }
                }
            } else {
                System.out.println("File does not exist. No existing cluster info present.");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void updateSparkRestartNodeDetails(String instanceId, String pubDns, String pubIp, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_nodes_info SET status = ?, public_dnsname = ?, public_ip = ? WHERE instance_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, status);
                update.setString(2, pubDns);
                update.setString(3, pubIp);
                update.setString(4, instanceId);
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void stopSparkNode(String instanceId, Boolean isMasterNode) {
        if (instanceId != null) {
            try {
                DryRunSupportedRequest<StopInstancesRequest> dryRequest
                        = () -> {
                            StopInstancesRequest request = new StopInstancesRequest()
                                    .withInstanceIds(instanceId);

                            return request.getDryRunRequest();
                        };
                AmazonEC2 ec2Client = CloudLogin.getEC2Client();
                DryRunResult dryResponse = ec2Client.dryRun(dryRequest);

                if (!dryResponse.isSuccessful()) {
                    System.out.printf("Failed dry run to stop instance %s", instanceId);
                    throw dryResponse.getDryRunResponse();
                }

                StopInstancesRequest request = new StopInstancesRequest()
                        .withInstanceIds(instanceId);

                ec2Client.stopInstances(request);
                Instance curInstance = ConfigureStorageLayer.waitForRunningState(ec2Client, instanceId);
                System.out.printf("Successfully stopped the instance: %s", instanceId);
                //lblInstanceStopMsg.setText("Successfully stop the instance: " + instanceId + ".");
                if (curInstance != null) {
                    MainForm.lblStopRestartStatus.setText("Successfully stopped the instance: " + instanceId + ".");
                    //data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + curInstance.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                    //       + ", PublicIP:" + curInstance.getPublicIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                    if (!isMasterNode) {
                        updateSparkRestartNodeDetails(curInstance.getInstanceId(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getState().getName());
                        updateSparkClusterNodeRemoveInfo(curInstance.getInstanceType());
                    } else {
                        updateSparkMasterNodeInfo(curInstance.getInstanceId(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress());
                    }
                } else {
                    System.out.println("Instances are not running.");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            MainForm.lblStopRestartStatus.setText("");
            MainForm.lblStopRestartStatus.setText("Enter the valid instance ID.");
        }
    }

    public static void updateSparkMasterNodeInfo(String instanceId, String pubDns, String pubIp, String privIp) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_cluster_info SET master_instance_id = ?, master_public_dnsname = ?, master_public_ip = ?, master_private_ip = ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, instanceId);
                update.setString(2, pubDns);
                update.setString(3, pubIp);
                update.setString(4, privIp);
                update.setInt(5, 100); //clusterId= 100 fixed
                update.executeUpdate();
                update.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateSparkClusterNodeRemoveInfo(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_cluster_info SET no_of_nodes = no_of_nodes - ?, instance_types = REPLACE(instance_types, ?, '') WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType);
                update.setInt(3, 100); //clusterId= 100 fixed
                update.executeUpdate();
                update.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateRestartedInstanceInfoSpark(String instanceId, String pubDns, String pubIp, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_nodes_info SET status = ?, public_dnsname = ?, public_ip = ? WHERE instance_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, status);
                update.setString(2, pubDns);
                update.setString(3, pubIp);
                update.setString(4, instanceId);
                update.executeUpdate();
                update.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restartedSparkProcessingNode(String instId, Boolean isMaster) {
        if ("".equals(instId)) {
            return;
        }
        try {
            AmazonEC2 ec2Client = CloudLogin.getEC2Client();
            //WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instId);
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance inst = ConfigureIngestionLayer.waitForRunningState(ec2Client, instId);
            if (inst != null) {
                MainForm.lblStopRestartStatus.setText("Instance with Id: " + instId + " starts running successfully.");
                if (!isMaster) {
                    updateRestartedInstanceInfoSpark(inst.getInstanceId(), inst.getPublicDnsName(), inst.getPublicIpAddress(), inst.getState().getName());
                    updateSparkClusterInfo(inst.getInstanceType());
                } else {
                    updateClusterInfoDb(inst.getPublicDnsName(), inst.getPublicIpAddress());
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateClusterInfoDb(String masterDns, String masterPubIp) {

        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE processing_cluster_info SET master_public_dnsname = ?, master_public_ip = ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, masterDns);
                update.setString(2, masterPubIp);
                update.setInt(3, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getCurrentBrokerIds() {
        String broker_ids = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT public_dnsname FROM ingestion_nodes_info WHERE status =?";
            try (PreparedStatement st = DatabaseConnection.con.prepareStatement(query)) {
                st.setString(1, "running");
                ResultSet rs = st.executeQuery();
                int i = 0;
                while (rs.next()) {

                    String brokerDns = rs.getString(1);
                    if (i == 0) {
                        broker_ids = brokerDns + ":9092";
                    } else {
                        broker_ids = broker_ids + "," + brokerDns + ":9092";
                    }
                    i++;
                }
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return broker_ids;
    }

    public static String getCurrentCassandraSeedIps() {
        String cassandraSeeds = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT public_ip FROM storage_nodes_info WHERE status = ? AND node_type = ?";
            try (PreparedStatement st = DatabaseConnection.con.prepareStatement(query)) {
                st.setString(1, "running");
                st.setString(2, "seed");
                ResultSet rs = st.executeQuery();
                int i = 0;
                while (rs.next()) {

                    String cassSeedIp = rs.getString(1);
                    if (i == 0) {
                        cassandraSeeds = cassSeedIp;
                    } else {
                        cassandraSeeds = cassandraSeeds + "," + cassSeedIp;
                    }
                    i++;
                }
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cassandraSeeds;

    }

    public static String getMasterNodeDns(Boolean isSubmit) {
        String masterUrl = "";
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT master_public_dnsname FROM processing_cluster_info WHERE cluster_id = ?";
            try (PreparedStatement st = DatabaseConnection.con.prepareStatement(query)) {
                st.setInt(1, 100);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    masterUrl = rs.getString(1);
                }
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!isSubmit) {
            masterUrl = "spark://" + masterUrl + ":7077";
        }
        return masterUrl;
    }

    public static void configureNewlyCreatedSparkNode(String pubDnsName, String nodeType) {
        JSch jschClient = new JSch();
        if ("local".equals(nodeType)) {
            String brokerId = getCurrentBrokerIds();
            String cassandraSeedIp = getCurrentCassandraSeedIps();
            try {
                jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
                JSch.setConfig("StrictHostKeyChecking", "no");
                Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
                session.connect(60000);
                //run commands
                String command = "sudo bash updateConfigParamsSpark.sh " + brokerId + " " + cassandraSeedIp + "";  //script file must be available in the instance home directory
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                channel.setErrStream(System.err);
                channel.connect(60000);
                readInputStreamFromSshSession(channel);
                sleep(5000);
                String cmd = "sudo bash runSparkApp.sh";         //check to make sure ingestion and storage services are running before executing this script.
                ChannelExec chnl = (ChannelExec) session.openChannel("exec");
                chnl.setCommand(cmd);
                chnl.setErrStream(System.err);
                chnl.connect(60000);
                readInputStreamFromSshSession(chnl);
                sleep(5000);
                session.disconnect();
            } catch (JSchException | InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            String masterUrl = getMasterNodeDns(false);
            if (masterUrl == null || "".equals(masterUrl)) {
                System.out.println("Please create and then start the master node first!");
                return;
            }
            try {
                jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
                JSch.setConfig("StrictHostKeyChecking", "no");
                Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
                session.connect(60000);
                //run commands
                String command = "sudo bash startWorker.sh " + masterUrl;         //script file must be available in the instance home directory
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                channel.setErrStream(System.err);
                channel.connect(60000);
                readInputStreamFromSshSession(channel);
                sleep(5000);
                session.disconnect();
            } catch (JSchException | InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static String getSparkWorkerIps() {
        String privIps = "";
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT private_ip FROM processing_nodes_info WHERE node_type = ?";
            try (PreparedStatement st = DatabaseConnection.con.prepareStatement(query)) {
                st.setString(1, "worker");
                ResultSet rs = st.executeQuery();
                int i = 0;
                while (rs.next()) {

                    String ip = rs.getString(1);
                    privIps = privIps + ip + " ";
                }
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        privIps = privIps.trim();
        return privIps;
    }

    public static void updateMasterNode() {
        String masterDns = getMasterNodeDns(true);
        String brokerId = getCurrentBrokerIds();
        String cassandraSeedIp = getCurrentCassandraSeedIps();
        String workerIps = getSparkWorkerIps();
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", masterDns, 22);
            session.connect(60000);
            //run commands
            String command = "sudo bash upd8SparkClusterConfig.sh " + brokerId + " " + cassandraSeedIp + " " + workerIps;         //script file must be available in the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect(60000);
            readInputStreamFromSshSession(channel);
            sleep(5000);
            session.disconnect();
        } catch (JSchException | InterruptedException ex) {

        }

    }

    public static Boolean configureAndRunMasterNode(String pubDnsName) {
        Boolean success = false;
        String brokerId = getCurrentBrokerIds();
        String cassandraSeedIp = getCurrentCassandraSeedIps();
        String workerIps = getSparkWorkerIps();
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect(60000);
            //run commands
            String command = "sudo bash upd8SparkClusterConfig.sh " + brokerId + " " + cassandraSeedIp + " " + workerIps;         //script file must be available in the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect(60000);
            readInputStreamFromSshSession(channel);
            sleep(5000);
            String cmd = "sudo bash startSparkCluster.sh";         //check to make sure ingestion and storage services are running before executing this script.
            ChannelExec chnl = (ChannelExec) session.openChannel("exec");
            chnl.setCommand(cmd);
            chnl.setErrStream(System.err);
            chnl.connect(60000);
            readInputStreamFromSshSession(chnl);
            sleep(5000);
            session.disconnect();
            success = true;
        } catch (JSchException | InterruptedException ex) {

        }
        return success;
    }

    public static void submitJobToSparkCluster() {
        String masterDns = getMasterNodeDns(true);
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", masterDns, 22);
            session.connect(60000);
            String cmd = "sudo bash runSparkAppCluster.sh";         //check to make sure ingestion and storage services are running before executing this script.
            ChannelExec chnl = (ChannelExec) session.openChannel("exec");
            chnl.setCommand(cmd);
            chnl.setErrStream(System.err);
            chnl.connect(60000);
            readInputStreamFromSshSession(chnl);
            sleep(5000);
            session.disconnect();
        } catch (JSchException | InterruptedException ex) {

        }
    }

    public static List<String> getWorkerInstanceIds(String limit) {
        List<String> instanceIds = new ArrayList<>();
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT instance_id FROM dpp_resources.processing_nodes_info WHERE status = 'running' AND node_type = 'worker' LIMIT " + limit;
            try (Statement st = DatabaseConnection.con.createStatement()) {
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {

                    instanceIds.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return instanceIds;
    }

}
