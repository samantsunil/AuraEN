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
import java.util.ArrayList;
import java.util.List;
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
                sleep(5000);
                StopInstancesRequest request = new StopInstancesRequest()
                        .withInstanceIds(instanceId);

                ec2Client.stopInstances(request);
                Instance curInstance = waitForRunningState(ec2Client, instanceId);
                System.out.printf("Successfully stopped the instance: %s", instanceId);
                if (curInstance != null) {
                    MainForm.lblInstanceStatus.setText("");
                    MainForm.lblInstanceStatus.setText("Successfully stop the instance: " + instanceId + ".");
                    // WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
                    updateInstanceInfoDb(curInstance.getInstanceId(), curInstance.getState().getName());
                    updateCassandraClusterNodeRemoveInfo(curInstance.getInstanceType());
                } else {
                    System.out.println("Instances are not running.");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void updateCassandraClusterNodeRemoveInfo(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_cluster_info SET no_of_nodes = no_of_nodes - ?, instance_types = REPLACE(instance_types, ?, '') WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType);
                update.setInt(3, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
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
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, status);
                update.setString(2, "");
                update.setString(3, "");
                update.setString(4, instanceId);
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restartInstanceStorageLayer(String instanceId) {
        // WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
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
            updateRestartedInstanceInfo(inst.getInstanceId(), inst.getPublicDnsName(), inst.getPublicIpAddress(), inst.getPrivateIpAddress(), inst.getState().getName());
            updateStorageClusterAddNodeInfo(inst.getInstanceType());
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
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, status);
                update.setString(2, pubDns);
                update.setString(3, pubIp);
                update.setString(4, PrivIp);
                update.setString(5, instanceId);
                update.executeUpdate();
            }
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
            session.connect(60000);
            String command = "sudo bash shutDownCassandraNode.sh";
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect(60000);
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
            session.connect(60000);
            String command = "";
            if (isNewNode && !"".equals(seedIp)) {
                updateNodeTypeStatus(InstanceId);
                sleep(10000);
                command = "sudo service cassandra stop;sudo bash clearCassandraLogs.sh;sudo bash configureCassandraNewNode.sh " + seedIp;
            } else {
                command = "sudo service cassandra stop;sudo bash configureCassandraNode.sh;sudo bash clearCassandraLogs.sh";
            }
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect(60000);
            readInputStreamFromSshSession(channel);
            sleep(5000);
            String command1 = "sudo bash restartCassandra.sh"; //command to start new cassandra node
            ChannelExec channel1 = (ChannelExec) session.openChannel("exec");
            channel1.setCommand(command1);
            channel1.setErrStream(System.err);
            channel1.connect(60000);
            readInputStreamFromSshSession(channel1);
            sleep(5000);
            if (!isNewNode && "".equals(seedIp)) {
                String command2 = "sudo bash createKeyspaceTables.sh"; //command to create keyspace and tables for the seed node.           
                ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
                channel2.setCommand(command2);
                channel2.setErrStream(System.err);
                channel2.connect(60000);
                readInputStreamFromSshSession(channel2);
                sleep(5000);
            }
            String command3 = "sudo bash returnNodeHostId.sh"; 
            ChannelExec channel3 = (ChannelExec) session.openChannel("exec");
            channel3.setCommand(command3);
            channel3.setErrStream(System.err);
            channel3.connect(60000);
            hostId = readInputStreamFromSshSession(channel3);
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!"".equals(hostId)) {
           // WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true);
            //data.writeToFile("InstanceID: " + InstanceId + ", HostID: " + hostId + ".");
            updateCassandraNodeHostId(hostId, InstanceId);
        }
        return hostId;
    }
public static void updateNodeTypeStatus(String instanceId) {
           try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_nodes_info SET node_type = ? WHERE instance_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, "non-seed");
                update.setString(2, instanceId);
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        } 
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
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, hostId);
                update.setString(2, instanceId);
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void buildNoSqlStorageCluster(int noOfNodes, String instanceType, Boolean dppScaling) throws SQLException {
        MainForm.btnBuildStorageCluster.setEnabled(false);
        String amiId=null;
        amiId = DatabaseConnection.getServiceAmi("cassandra");
        if("".equals(amiId) || amiId ==null){
            return;
        }
         
        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        createEC2Instances(ec2Client, amiId, instanceType, noOfNodes, dppScaling);
        MainForm.btnBuildStorageCluster.setEnabled(true);
        MainForm.progressBarStorage.setIndeterminate(false);
        MainForm.progressBarStorage.setValue(100);
    }

    public static void createEC2Instances(AmazonEC2 ec2Client, String amiId, String instanceType, int noOfNodes, Boolean dppScaling) {
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
        //WriteFile data = new WriteFile("C:\\Code\\CassandraClusterDetails.txt", true); //to save cluster info in a text file - append mode.
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
                startEC2Instance(ec2Client, inst, inst.getPlacement(), dppScaling);
                i++;
            } catch (InterruptedException | SQLException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                MainForm.txtAreaCassandraResourcesInfo.setText(ex.getMessage());
            }
        }

    }

    public static void startEC2Instance(AmazonEC2 ec2Client, Instance inst, Placement az, Boolean dppScaling) throws InterruptedException, SQLException {
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance curInstance = waitForRunningState(ec2Client, inst.getInstanceId());
        if (curInstance != null) {
            System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
            //MainForm.txtAreaCassandraResourcesInfo.append("Successfully created the following ec2 instances for the Cassandra Cluster:\n");
            MainForm.txtAreaCassandraResourcesInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".\n");
            MainForm.txtAreaCassandraResourcesInfo.append("---------------------------------------------------------------\n");
            //data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: " + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
            //        + ", PublicIP: " + curInstance.getPublicIpAddress() + ", PrivateIP: " + curInstance.getPrivateIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
            dbInsertInstanceInfo(curInstance.getInstanceId(), curInstance.getInstanceType(), az.getAvailabilityZone(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getPrivateIpAddress(), curInstance.getState().getName(), "", dppScaling);
            updateStorageClusterAddNodeInfo(curInstance.getInstanceType());
            if(dppScaling){
                configureNoSqlNonSeedNode(curInstance.getPublicDnsName(), curInstance.getInstanceId());
            }
        } else {
            System.out.println("Instances are not running.");
        }
    }
    public static void configureNoSqlNonSeedNode(String pubDns, String instanceId){
        JSch jschClient = new JSch();
        String seedIp = getSeedIpForNewNode();
        String hostId = "";
        //String seedIp = "172.31.34.236";
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem");
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDns, 22);
            session.connect(60000);
            
            String command = "sudo service cassandra stop;sudo bash clearCassandraLogs.sh;sudo bash configureCassandraNewNode.sh " + seedIp;
             
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect(60000);
            try {
                readInputStreamFromSshSession(channel);
            } catch (IOException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            sleep(5000);
            String command1 = "sudo bash restartCassandra.sh"; //command to start new cassandra node
            ChannelExec channel1 = (ChannelExec) session.openChannel("exec");
            channel1.setCommand(command1);
            channel1.setErrStream(System.err);
            channel1.connect(60000);
            try {
                readInputStreamFromSshSession(channel1);
            } catch (IOException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            sleep(5000);
            String command3 = "sudo bash returnNodeHostId.sh"; 
            ChannelExec channel3 = (ChannelExec) session.openChannel("exec");
            channel3.setCommand(command3);
            channel3.setErrStream(System.err);
            channel3.connect(60000);
            try {
                hostId = readInputStreamFromSshSession(channel3);
            } catch (IOException ex) {
                Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!"".equals(hostId)) {
            updateCassandraNodeHostId(hostId, instanceId);
        }
        
    }
    public static List<String> getPubDnsName(String limit) {
      List<String> instanceIds = new ArrayList<>();
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT instance_id FROM dpp_resources.storage_nodes_info WHERE status = 'running' AND node_type = 'non-seed' LIMIT " + limit;
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
    public static List<String> getNonSeedNodesInstanceIds(String limit){
         List<String> pubDnsNames = new ArrayList<>();
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT public_dnsname FROM dpp_resources.storage_nodes_info WHERE status = 'running' AND node_type = 'non-seed' LIMIT " + limit;
            try (Statement st = DatabaseConnection.con.createStatement()) {
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {

                    pubDnsNames.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pubDnsNames;
    
    }

    public static String getSeedIpForNewNode(){
        String cassandraSeeds = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT private_ip FROM dpp_resources.storage_nodes_info WHERE status = ? AND node_type = ?";
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
    public static void updateStorageClusterAddNodeInfo(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE storage_cluster_info SET no_of_nodes = no_of_nodes + ?, instance_types = CONCAT(instance_types, ?) WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType + "");
                update.setInt(3, 100); //clusterId= 100 fixed
                update.executeUpdate();
                update.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void dbInsertInstanceInfo(String instanceId, String instanceType, String az, String pubDnsName, String publicIp, String privateIp, String status, String nodeHostId, Boolean dppScaling) throws SQLException {
        String node_type = null;
        if(dppScaling){
            node_type = "non-seed";
        }
        else {
           node_type = "seed"; 
        }
        String query = "INSERT INTO storage_nodes_info (instance_id, instance_type, availability_zone, public_dnsname, public_ip, private_ip, status, node_hostId, node_type)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStmt = DatabaseConnection.con.prepareStatement(query)) {
            preparedStmt.setString(1, instanceId);
            preparedStmt.setString(2, instanceType);
            preparedStmt.setString(3, az);
            preparedStmt.setString(4, pubDnsName);
            preparedStmt.setString(5, publicIp);
            preparedStmt.setString(6, privateIp);
            preparedStmt.setString(7, status);
            preparedStmt.setString(8, nodeHostId);
            preparedStmt.setString(9, node_type);
            preparedStmt.execute();
            preparedStmt.close();
        }
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
                    try (BufferedReader rdr = new BufferedReader(file)) {
                        String aLine;
                        while ((aLine = rdr.readLine()) != null) {
                            MainForm.txtAreaCassandraResourcesInfo.append(aLine);
                            MainForm.txtAreaCassandraResourcesInfo.append("\n");
                        }
                    }
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
                    try (BufferedReader rdr = new BufferedReader(file)) {
                        String aLine;
                        while ((aLine = rdr.readLine()) != null) {
                            MainForm.txtAreaStorageResources.append(aLine);
                            MainForm.txtAreaStorageResources.append("\n");
                        }
                    }
                } else {
                    System.out.println("File does not exist. No existing cluster info present.");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
