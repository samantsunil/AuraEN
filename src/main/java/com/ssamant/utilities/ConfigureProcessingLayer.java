/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public static String ami_id_spark = "ami-028bc88846f2d33b3";

    public static void buildProcessingLayerCluster(int noOfNodes, String instanceType) {
        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        createEC2NodeForProcessingLayer(noOfNodes, instanceType, ec2Client);
    }

    public static void createEC2NodeForProcessingLayer(int noOfNodes, String instanceType, AmazonEC2 ec2Client) {
        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(ami_id_spark) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                .withInstanceType(InstanceType.T2Micro) //free -tier instance type used
                .withKeyName("mySSHkey") //keypair name
                .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                .withMaxCount(noOfNodes)
                .withMinCount(1);

        @SuppressWarnings("ThrowableResultIgnored")
        RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
        // List<String> instanceIds = new ArrayList<>();
        WriteFile data = new WriteFile("C:\\Code\\SparkClusterDetails.txt", true); //to save cluster info in a text file - append mode.
        if (DatabaseConnection.con == null) {
            try {
                DatabaseConnection.con = DatabaseConnection.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int i = 1;
        for (Instance inst : runResponse.getReservation().getInstances()) {
            System.out.println("EC2 Instance Id: " + inst.getInstanceId());
            // instanceIds.add(inst.getInstanceId());
            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue("spark-0" + String.valueOf(i));
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                    .withResources(inst.getInstanceId())
                    .withTags(tag);
            CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
            startInstanceForProcessingCluster(ec2Client, inst, inst.getPlacement(), data);
            i++;
        }
    }

    public static void startInstanceForProcessingCluster(AmazonEC2 ec2Client, Instance inst, Placement az, WriteFile data) {
        try {
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance curInstance = ConfigureStorageLayer.waitForRunningState(ec2Client, inst.getInstanceId());
            if (curInstance != null) {
                System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
                MainForm.txtAreaSparkResourcesInfo.append("Successfully created the following ec2 instances for the Cassandra Cluster:\n");
                MainForm.txtAreaSparkResourcesInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".\n");
                try {

                    data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                            + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                    dbInsertSpakInstanceDetail(curInstance.getInstanceId(), curInstance.getInstanceType(), az.getAvailabilityZone(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress(), curInstance.getState().getName());
                    updateSparkClusterInfo(curInstance.getInstanceType());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    MainForm.txtAreaSparkResourcesInfo.append("Error while writing to a file: " + ex.getMessage());
                }
            } else {
                System.out.println("Instances are not running.");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
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
            String query = "UPDATE processing_cluster_info SET no_of_nodes = no_of_nodes + ?, instance_type = CONCAT(instance_type, ?) WHERE cluster_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setInt(1, i);
            update.setString(2, "1X" + instanceType +",");
                 update.setInt(3, 100); //clusterId= 100 fixed
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    public static void dbInsertSpakInstanceDetail(String instanceId, String instanceType, String az, String pubDnsName, String publicIp, String privateIp, String status) {
        try {
            String query = "INSERT INTO processing_nodes_info (instance_id, instance_type, availability_zone, public_dnsname, public_ip, private_ip, status)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = DatabaseConnection.con.prepareStatement(query);
            preparedStmt.setString(1, instanceId);
            preparedStmt.setString(2, instanceType);
            preparedStmt.setString(3, az);
            preparedStmt.setString(4, pubDnsName);
            preparedStmt.setString(5, publicIp);
            preparedStmt.setString(6, privateIp);
            preparedStmt.setString(7, status);
            preparedStmt.execute();
            preparedStmt.close();
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
            st.close();
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
            st.close();
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
                BufferedReader rdr = new BufferedReader(file);
                String aLine;
                while ((aLine = rdr.readLine()) != null) {
                    MainForm.txtAreaSparkResourcesInfo.append(aLine);
                    MainForm.txtAreaSparkResourcesInfo.append("\n");
                }
                rdr.close();
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
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setString(1, status);
            update.setString(2, pubDns);
            update.setString(3, pubIp);
            update.setString(4, instanceId);
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void stopSparkNode(String instanceId) {
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
                    MainForm.lblStopRestartStatus.setText("Successfully stop the instance: " + instanceId + ".");
                    WriteFile data = new WriteFile("C:\\Code\\SparkClusterDetails.txt", true);
                    try {

                        data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + curInstance.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                                + ", PublicIP:" + curInstance.getPublicIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                        updateSparkRestartNodeDetails(curInstance.getInstanceId(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getState().getName());
                        updateSparkClusterNodeRemoveInfo(curInstance.getInstanceType());
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        MainForm.lblStopRestartStatus.setText("Error while writing to a file: " + ex.getMessage());
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
            String query = "UPDATE processing_cluster_info SET no_of_nodes = no_of_nodes - ?, instance_type = REPLACE(instance_type, ?, '') WHERE cluster_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setInt(1, i);
            update.setString(2, "1X" + instanceType);
            update.setInt(3, 100); //clusterId= 100 fixed
            update.executeUpdate();
            update.close();
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
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setString(1, status);
            update.setString(2, pubDns);
            update.setString(3, pubIp);
            update.setString(4, instanceId);
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restartedSparkProcessingNode(String instId) {
        if("".equals(instId)){
            return;
        }        
        try {
            AmazonEC2 ec2Client = CloudLogin.getEC2Client();
            WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instId);
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance inst = ConfigureIngestionLayer.waitForRunningState(ec2Client, instId);
            if (inst != null) {
                MainForm.lblStopRestartStatus.setText("Instance with Id: " + instId + " starts running successfully.");
                try {

                    data.writeToFile("InstanceID: " + inst.getInstanceId() + " , InstanceType: " + inst.getInstanceType() + ", AZ: ." + inst.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + inst.getPublicDnsName()
                            + ", PublicIP:" + inst.getPublicIpAddress() + ", Status: " + inst.getState().getName() + ".");
                    updateRestartedInstanceInfoSpark(inst.getInstanceId(), inst.getPublicDnsName(), inst.getPublicIpAddress(), inst.getState().getName());
                    updateSparkClusterInfo(inst.getInstanceType());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    MainForm.lblStopRestartStatus.setText("Error while writing to a file: " + ex.getMessage());
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
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
            PreparedStatement st = DatabaseConnection.con.prepareStatement(query);
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
            String query = "SELECT public_ip FROM storage_nodes_info WHERE status =?";
            PreparedStatement st = DatabaseConnection.con.prepareStatement(query);
            st.setString(1, "running");
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
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cassandraSeeds;

    }

    public static void configureNewlyCreatedSparkNode(String instanceId, String pubDnsName, String instanceType) {
        String brokerId = getCurrentBrokerIds();
        String cassandraSeedIp = getCurrentCassandraSeedIps();
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect();
            //run commands
            String command = "sudo bash updateConfigParamsSpark.sh " + brokerId + " " + cassandraSeedIp + "";         //script file must be available in the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            String cmd = "sudo bash runSparkApp.sh";         //check to make sure ingestion and storage services are running before executing this script.
            ChannelExec chnl = (ChannelExec) session.openChannel("exec");
            chnl.setCommand(cmd);
            chnl.setErrStream(System.err);
            chnl.connect();
            readInputStreamFromSshSession(chnl);
            sleep(5000);
            session.disconnect();
        } catch (JSchException | InterruptedException ex) {

        }
    }

}
