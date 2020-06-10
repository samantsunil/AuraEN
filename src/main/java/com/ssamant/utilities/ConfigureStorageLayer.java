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
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
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
import static com.ssamant.utilities.DatabaseConnection.getConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
public class ConfigureStorageLayer {

    public ConfigureStorageLayer() {

    }

    public static void stopInstanceStorageLayer(String instanceId, String dnsName) throws IOException {
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

                stopCassandraNode(dnsName); //call to remove the node as decommission from the cluster and then stop the service on the node.
                StopInstancesRequest request = new StopInstancesRequest()
                        .withInstanceIds(instanceId);

                ec2Client.stopInstances(request);
                Instance curInstance = waitForRunningState(ec2Client, instanceId);
                System.out.printf("Successfully stopped the instance: %s", instanceId);
                if (curInstance != null) {
                    MainForm.lblInstanceStatus.setText("");
                    MainForm.lblInstanceStatus.setText("Successfully stop the instance: " + instanceId + ".");
                    WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
                    try {
                        data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + curInstance.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                                + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                        updateInstanceInfoDb(curInstance.getInstanceId(), curInstance.getState().getName());
                        updateCassandraClusterNodeRemoveInfo(curInstance.getInstanceType());
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        MainForm.txtAreaCassandraResourcesInfo.append("Error while writing to a file: " + ex.getMessage());
                    }
                } else {
                    System.out.println("Instances are not running.");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
public static void updateCassandraClusterNodeRemoveInfo(String instanceType){
    int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_cluster_info SET no_of_nodes = no_of_nodes - ?, instance_type = REPLACE(instance_type, ?, '') WHERE cluster_id = ?";
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
    public static void updateInstanceInfoDb(String instanceId, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_nodes_info SET status = ?, public_dnsname = ?, public_ip = ? WHERE instance_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setString(1, status);
            update.setString(2, "");
            update.setString(3, "");
            update.setString(4, instanceId);
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restartInstanceStorageLayer(String instanceId) {
        WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);
        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance inst = null;
        try {
            inst = waitForRunningState(ec2Client, instanceId);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (inst != null) {
            MainForm.lblInstanceStatus.setText("");
            MainForm.lblInstanceStatus.setText("Instance with Id: " + instanceId + " starts running.");
            try {

                data.writeToFile("InstanceID: " + inst.getInstanceId() + " , InstanceType: " + inst.getInstanceType() + ", AZ: ." + inst.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + inst.getPublicDnsName()
                        + ", PublicIP:" + inst.getPublicIpAddress() + ", PrivateIP: " + inst.getPrivateIpAddress() + ", Status: " + inst.getState().getName() + ".");
                updateRestartedInstanceInfo(inst.getInstanceId(), inst.getPublicDnsName(), inst.getPublicIpAddress(), inst.getPrivateIpAddress(), inst.getState().getName());
                updateStorageClusterAddNodeInfo(inst.getInstanceType());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                MainForm.txtAreaCassandraResourcesInfo.append("Error while writing to a file: " + ex.getMessage());
            }
        }
    }

    public static void updateRestartedInstanceInfo(String instanceId, String pubDns, String pubIp, String PrivIp, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_nodes_info SET status = ?, public_dnsname = ?, public_ip = ?, private_ip = ? WHERE instance_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setString(1, status);
            update.setString(2, pubDns);
            update.setString(3, pubIp);
            update.setString(4, PrivIp);
            update.setString(5, instanceId);
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Instance waitForRunningState(AmazonEC2 ec2Client, String instId) throws InterruptedException {

        DescribeInstancesRequest rqst = new DescribeInstancesRequest().withInstanceIds(instId);
        String status = "unknown";
        Instance instance = null;
        Boolean completed = false;
        while (!completed) {
            instance = ec2Client.describeInstances(rqst).getReservations().get(0).getInstances().get(0);
            if (instance.getState().getCode() == 48) {
                completed = true;
                status = "Instance already terminated";
            }
            if (instance.getState().getCode() == 80) {
                completed = true;
                status = "Instance already stopped";
            }

            if (instance.getState().getCode() == 16) {
                status = "Instance is running";
                completed = true;
            }

            if (!completed) {
                Thread.sleep(5000);
            }
        }
        System.out.println(status);
        return instance;
    }

    public static String readInputStreamFromSshSession(ChannelExec channel) throws InterruptedException, IOException {
        InputStream input = null;
        String line = "";
        try {
            input = channel.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] tmp = new byte[1024];
        while (true) {
            try {
                while (input.available() > 0) {
                    int i = input.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(tmp, 0, i);
                    System.out.println(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (channel.isClosed()) {
                if (input.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
        }
        channel.disconnect();
        return line;
    }

    public static void stopCassandraNode(String pubDnsName) throws IOException {
        JSch jschClient = new JSch();
        String msg = null;
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem");
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect();
            String command = "sudo bash shutDownCassandraNode.sh";
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            try {
                msg = readInputStreamFromSshSession(channel);
                MainForm.lblInstanceStatus.setText(msg);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String configureNoSqlServerNode(String pubDnsName, Boolean isNewNode, String seedIp, String InstanceId) throws IOException {
        JSch jschClient = new JSch();
        String hostId = "";
        //String seedIp = "172.31.34.236";
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem");
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect();
            String command = "";
            if (isNewNode && !"".equals(seedIp)) {
                command = "sudo service cassandra stop;sudo bash clearCassandraLogs.sh;sudo bash configureCassandraNewNode.sh " + seedIp;
            } else {
                command = "sudo service cassandra stop;sudo bash configureCassandraNode.sh;sudo bash clearCassandraLogs.sh";
            }
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            String command1 = "sudo bash restartCassandra.sh"; //command to start new cassandra node
            ChannelExec channel1 = (ChannelExec) session.openChannel("exec");
            channel1.setCommand(command1);
            channel1.setErrStream(System.err);
            channel1.connect();
            readInputStreamFromSshSession(channel1);
            sleep(5000);
            if (!isNewNode && "".equals(seedIp)) {
                String command2 = "sudo bash createKeyspaceTables.sh"; //command to create keyspace and tables for the seed node.           
                ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
                channel2.setCommand(command2);
                channel2.setErrStream(System.err);
                channel2.connect();
                readInputStreamFromSshSession(channel2);
                sleep(5000);
            }
            String command3 = "sudo bash returnNodeHostId.sh"; //command to create keyspace and tables if does not exist
            ChannelExec channel3 = (ChannelExec) session.openChannel("exec");
            channel3.setCommand(command3);
            channel3.setErrStream(System.err);
            channel3.connect();
            hostId = readInputStreamFromSshSession(channel3);
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!"".equals(hostId)) {
            WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
            //data.writeToFile("InstanceID: " + InstanceId + ", HostID: " + hostId + ".");
            updateCassandraNodeHostId(hostId, InstanceId);
        }
        return hostId;
    }

    public static void updateCassandraNodeHostId(String hostId, String instanceId) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_nodes_info SET node_hostId = ? WHERE instance_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setString(1, hostId);
            update.setString(2, instanceId);
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void buildNoSqlStorageCluster(int noOfNodes, String instanceType) throws SQLException {
        MainForm.btnBuildStorageCluster.setEnabled(false);
        String amiId = "ami-07e22925f7bf77a0c"; //fixed given AMI - prebuilt with Cassandra service installed and associated tools. 
        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        createEC2Instances(ec2Client, amiId, instanceType, noOfNodes);
        MainForm.btnBuildStorageCluster.setEnabled(true);
        MainForm.progressBarStorage.setIndeterminate(false);
        MainForm.progressBarStorage.setValue(100);
    }

    public static void createEC2Instances(AmazonEC2 ec2Client, String amiId, String instanceType, int noOfNodes) {
        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                .withInstanceType(InstanceType.T2Micro) //free -tier instance type used
                .withKeyName("mySSHkey") //keypair name
                .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                .withMaxCount(noOfNodes)
                .withMinCount(1);

        @SuppressWarnings("ThrowableResultIgnored")
        RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
        // List<String> instanceIds = new ArrayList<>();
        WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true); //to save cluster info in a text file - append mode.
        if (DatabaseConnection.con == null) {
            try {
                DatabaseConnection.con = DatabaseConnection.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int i = 1;
        for (Instance inst : runResponse.getReservation().getInstances()) {
            try {
                System.out.println("EC2 Instance Id: " + inst.getInstanceId());
                // instanceIds.add(inst.getInstanceId());
                Tag tag = new Tag()
                        .withKey("Name")
                        .withValue("cassandra-0" + String.valueOf(i));
                CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                        .withResources(inst.getInstanceId())
                        .withTags(tag);
                CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
                startEC2Instance(ec2Client, inst, inst.getPlacement(), data);
                i++;
            } catch (InterruptedException | SQLException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                MainForm.txtAreaCassandraResourcesInfo.setText(ex.getMessage());
            }
        }

    }

    public static void startEC2Instance(AmazonEC2 ec2Client, Instance inst, Placement az, WriteFile data) throws InterruptedException, SQLException {
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance curInstance = waitForRunningState(ec2Client, inst.getInstanceId());
        if (curInstance != null) {
            System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
            MainForm.txtAreaCassandraResourcesInfo.append("Successfully created the following ec2 instances for the Cassandra Cluster:\n");
            MainForm.txtAreaCassandraResourcesInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".\n");
            try {

                data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                        + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                dbInsertInstanceInfo(curInstance.getInstanceId(), curInstance.getInstanceType(), az.getAvailabilityZone(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress(), curInstance.getState().getName(), "");
                updateStorageClusterAddNodeInfo(curInstance.getInstanceType());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                MainForm.txtAreaCassandraResourcesInfo.append("Error while writing to a file: " + ex.getMessage());
            }
        } else {
            System.out.println("Instances are not running.");
        }
    }
public static void updateStorageClusterAddNodeInfo(String instanceType){
    int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_cluster_info SET no_of_nodes = no_of_nodes + ?, instance_type = CONCAT(instance_type, ?) WHERE cluster_id = ?";
            PreparedStatement update = DatabaseConnection.con.prepareStatement(query);
            update.setInt(1, i);
            update.setString(2, "1X" + instanceType+",");
            update.setInt(3, 100); //clusterId= 100 fixed
            update.executeUpdate();
            update.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    public static void dbInsertInstanceInfo(String instanceId, String instanceType, String az, String pubDnsName, String publicIp, String privateIp, String status, String nodeHostId) throws SQLException {
        String query = "INSERT INTO storage_nodes_info (instance_id, instance_type, availability_zone, public_dnsname, public_ip, private_ip, status, node_hostId)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = DatabaseConnection.con.prepareStatement(query);
        preparedStmt.setString(1, instanceId);
        preparedStmt.setString(2, instanceType);
        preparedStmt.setString(3, az);
        preparedStmt.setString(4, pubDnsName);
        preparedStmt.setString(5, publicIp);
        preparedStmt.setString(6, privateIp);
        preparedStmt.setString(7, status);
        preparedStmt.setString(8, nodeHostId);
        preparedStmt.execute();
        preparedStmt.close();
    }

    public static ResultSet loadStorageClusterInfoFromDatabase() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM storage_nodes_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet loadCurrentCassandraClusterInfo() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM storage_cluster_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static void loadFromFileStorageClusterDetails(boolean isDPP) {

        String fileName = "C:\\Code\\CassandraClusterDetails.txt";
        if (!isDPP) {
            MainForm.txtAreaCassandraResourcesInfo.setText("");
            try {
                if (new File("C:\\Code\\CassandraClusterDetails.txt").exists()) {
                    FileReader file = new FileReader(fileName);
                    BufferedReader rdr = new BufferedReader(file);
                    String aLine;
                    while ((aLine = rdr.readLine()) != null) {
                        MainForm.txtAreaCassandraResourcesInfo.append(aLine);
                        MainForm.txtAreaCassandraResourcesInfo.append("\n");
                    }
                    rdr.close();
                } else {
                    System.out.println("File does not exist. No existing cluster info present.");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            MainForm.txtAreaStorageResources.setText("");
            try {
                if (new File("C:\\Code\\CassandraClusterDetails.txt").exists()) {
                    FileReader file = new FileReader(fileName);
                    BufferedReader rdr = new BufferedReader(file);
                    String aLine;
                    while ((aLine = rdr.readLine()) != null) {
                        MainForm.txtAreaStorageResources.append(aLine);
                        MainForm.txtAreaStorageResources.append("\n");
                    }
                    rdr.close();
                } else {
                    System.out.println("File does not exist. No existing cluster info present.");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
