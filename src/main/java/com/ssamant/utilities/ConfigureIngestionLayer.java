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
import static com.ssamant.pocresourcemanagement.MainForm.lblClusterStatus;
import static com.ssamant.pocresourcemanagement.MainForm.lblInstanceStopMsg;
import static com.ssamant.pocresourcemanagement.MainForm.lblStartedInstance;
import static com.ssamant.pocresourcemanagement.MainForm.lblStopInstance;
import static com.ssamant.pocresourcemanagement.MainForm.txtAreaClusterInfo;
import static com.ssamant.pocresourcemanagement.MainForm.txtAreaIngestionDetails;
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
public class ConfigureIngestionLayer {

    public ConfigureIngestionLayer() {

    }
    public static String amiId = null;
    public static String zkDnsName = "ec2-13-211-235-35.ap-southeast-2.compute.amazonaws.com";

    public static void buildIngestionLayerCluster(int noOfBrokers, String instType) {
        try {
            AmazonEC2 ec2Client = CloudLogin.getEC2Client();
            createEC2Instances(ec2Client, instType, noOfBrokers);
        } catch (InterruptedException ex) {
            System.out.printf("Error in instance creation " + ex.getMessage());
        }

    }

    public static void createZkServer(String instanceType) {
        String zkAmi = DatabaseConnection.getServiceAmi("zookeeper");
        if (zkAmi != null || !"".equals(zkAmi)) {
            AmazonEC2 ec2Client = CloudLogin.getEC2Client();
            RunInstancesRequest runRequest = new RunInstancesRequest()
                    .withImageId(zkAmi) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                    .withInstanceType(instanceType) //free -tier instance type t2.micro
                    .withKeyName("mySSHkey") //keypair name
                    .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                    .withMaxCount(1)
                    .withMinCount(1);
            RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
            Instance inst = runResponse.getReservation().getInstances().get(0);
            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue("zookeeper-server");
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                    .withResources(inst.getInstanceId())
                    .withTags(tag);
            CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
            StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
            Instance curInstance = null;
            try {
                curInstance = waitForRunningState(ec2Client, inst.getInstanceId());
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigureIngestionLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (curInstance != null) {
                try {
                    dbUpdateZkServerInfo(curInstance.getPublicDnsName());
                    sleep(10000);
                    startZookeeperServer(curInstance.getPublicDnsName());
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConfigureIngestionLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            System.out.println("No AMI exist for zookeeper service.");
        }

    }

    public static void createEC2Instances(AmazonEC2 ec2Client, String instType, int noOfBrokers) throws InterruptedException {
        amiId = DatabaseConnection.getServiceAmi("kafka-broker");
        if (amiId == null || "".equals(amiId)) {
            amiId = ""; //set fixed value
        }
        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                .withInstanceType(instType) //free -tier instance type t2.micro
                .withKeyName("mySSHkey") //keypair name
                .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                .withMaxCount(noOfBrokers)
                .withMinCount(1);

        @SuppressWarnings("ThrowableResultIgnored")
        RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
        // List<String> instanceIds = new ArrayList<>();
        //WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true); //to save cluster info in a text file.
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
                    .withValue("kafka-0" + String.valueOf(i));
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                    .withResources(inst.getInstanceId())
                    .withTags(tag);
            CreateTagsResult tag_response = ec2Client.createTags(createTagsRequest);
            startEC2Instance(ec2Client, inst, inst.getPlacement(), i);
            i++;
        }
    }

    public static void startEC2Instance(AmazonEC2 ec2Client, Instance inst, Placement az, int brokerId) throws InterruptedException {
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance curInstance = waitForRunningState(ec2Client, inst.getInstanceId());
        if (curInstance != null) {
            System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
            txtAreaClusterInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP:" + curInstance.getPublicIpAddress()
                    + ", InstanceStatus: " + curInstance.getState().getName() + ", BrokerId: " + brokerId + ".\n");
            txtAreaClusterInfo.append("---------------------------------------------------------------------------------------------------------------------");
            try {
                dbInsertInstanceInfo(curInstance.getInstanceId(), curInstance.getInstanceType(), az.getAvailabilityZone(), curInstance.getPublicDnsName(), curInstance.getPublicIpAddress(), curInstance.getState().getName(), brokerId);
                updateIngestionClusterInfo(curInstance.getInstanceType());
                sleep(10000);
                String brokId = Integer.toString(brokerId - 1);
                configureNewlyCreatedBroker(curInstance.getPublicDnsName(), brokId);
            } catch (SQLException ex) {
                Logger.getLogger(ConfigureIngestionLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Instances are not running.");
        }
    }

    public static void dbUpdateZkServerInfo(String zkDnsName) {

        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE ingestion_cluster_info SET zk_dnsname = ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setString(1, zkDnsName);
                update.setInt(2, 100);
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void dbInsertInstanceInfo(String instanceId, String instanceType, String az, String pubDnsName, String publicIp, String status, int brokerId) throws SQLException {
        String query = "INSERT INTO ingestion_nodes_info (instance_id, instance_type, availability_zone, public_dnsname, public_ip, status, broker_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStmt = DatabaseConnection.con.prepareStatement(query)) {
            preparedStmt.setString(1, instanceId);
            preparedStmt.setString(2, instanceType);
            preparedStmt.setString(3, az);
            preparedStmt.setString(4, pubDnsName);
            preparedStmt.setString(5, publicIp);
            preparedStmt.setString(6, status);
            preparedStmt.setString(7, String.valueOf(brokerId - 1));
            preparedStmt.execute();
        }
    }

    public static void stopKafkaBrokerNode(String instanceId) throws InterruptedException {
        if (instanceId != null) {
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
            Instance curInstance = waitForRunningState(ec2Client, instanceId);
            System.out.printf("Successfully stop instance: %s", instanceId);
            //lblInstanceStopMsg.setText("Successfully stop the instance: " + instanceId + ".");
            if (curInstance != null) {
                lblStopInstance.setText("Successfully stop the instance: " + instanceId + ".");
                // WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
                updateInstanceInfoDbKafka(curInstance.getInstanceId(), curInstance.getState().getName());
                updateIngestionClusterRemoveNode(curInstance.getInstanceType());
            } else {
                System.out.println("Instances are not running.");
            }
        } else {
            lblStopInstance.setText("");
            lblStopInstance.setText("Enter the valid instance ID.");
        }
    }

    public static void updateIngestionClusterRemoveNode(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE ingestion_cluster_info SET no_of_nodes = no_of_nodes - ?, instance_type = REPLACE(instance_type, ?, ''), replication_factor = replication_factor - ?, partitions_count = partitions_count - ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType);
                update.setInt(3, i);
                update.setInt(4, i);
                update.setInt(5, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateInstanceInfoDbKafka(String instanceId, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE ingestion_nodes_info SET status = ?, public_dnsname = ?, public_ip = ? WHERE instance_id = ?";
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

    public static void restartKafkaBrokerNode(String instId) throws InterruptedException {

        AmazonEC2 ec2Client = CloudLogin.getEC2Client();
        // WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instId);
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance inst = waitForRunningState(ec2Client, instId);
        if (inst != null) {
            lblInstanceStopMsg.setText("Instance with Id: " + instId + " starts running.");
            lblStartedInstance.setText("Instance with Id: " + instId + " starts running successfully.");
            updateRestartedInstanceInfoIngestion(inst.getInstanceId(), inst.getPublicDnsName(), inst.getPublicIpAddress(), inst.getState().getName());
            updateIngestionClusterInfo(inst.getInstanceType());
        }
    }

    public static void updateRestartedInstanceInfoIngestion(String instanceId, String pubDns, String pubIp, String status) {
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE ingestion_nodes_info SET status = ?, public_dnsname = ?, public_ip = ? WHERE instance_id = ?";
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

    public static void updateIngestionClusterInfo(String instanceType) {
        int i = 1;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "UPDATE ingestion_cluster_info SET no_of_nodes = no_of_nodes + ?, instance_type = CONCAT(instance_type, ?), replication_factor = replication_factor + ?, partitions_count = partitions_count + ? WHERE cluster_id = ?";
            try (PreparedStatement update = DatabaseConnection.con.prepareStatement(query)) {
                update.setInt(1, i);
                update.setString(2, "1X" + instanceType);
                update.setInt(3, i);
                update.setInt(4, i);
                update.setInt(5, 100); //clusterId= 100 fixed
                update.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function returns the info about currently allocated and any stopped
     * broker nodes available
     *
     * @return
     */
    public static ResultSet loadCurrentClusterDetails() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM ingestion_nodes_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    /**
     * This function returns the current ingestion cluster info from the
     * database.
     *
     * @return
     */
    public static ResultSet loadIngestionClusterCapacityDetails() {
        ResultSet rs = null;
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM ingestion_cluster_info";
            Statement st = DatabaseConnection.con.createStatement();
            rs = st.executeQuery(query);
            //st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static void loadFromFileKafkaClusterDetails(boolean isDPP) {

        String fileName = "C:\\Code\\KafkaClusterDetails.txt";
        if (!isDPP) {
            txtAreaClusterInfo.setText("");
            try {
                if (new File("C:\\Code\\KafkaClusterDetails.txt").exists()) {
                    FileReader file = new FileReader(fileName);
                    BufferedReader rdr = new BufferedReader(file);
                    String aLine;
                    while ((aLine = rdr.readLine()) != null) {
                        txtAreaClusterInfo.append(aLine);
                        txtAreaClusterInfo.append("\n");
                    }
                    rdr.close();
                } else {
                    System.out.println("File does not exist. No existing cluster info present.");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            txtAreaIngestionDetails.setText("");
            try {
                if (new File("C:\\Code\\KafkaClusterDetails.txt").exists()) {
                    FileReader file = new FileReader(fileName);
                    BufferedReader rdr = new BufferedReader(file);
                    String aLine;
                    while ((aLine = rdr.readLine()) != null) {
                        txtAreaIngestionDetails.append(aLine);
                        txtAreaIngestionDetails.append("\n");
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

    /**
     * This function configures the server.properties for new kafka broker and
     * starts the server [assumes that zookeeper server is up and running]
     *
     * @param pubDnsName
     * @param newBrokerId
     */
    public static void configureNewlyCreatedBroker(String pubDnsName, String newBrokerId) {
        if ("".equals(newBrokerId)) {
            newBrokerId = "0";
        }
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem");
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect();
            String command = "sudo bash configNewBroker.sh " + newBrokerId;
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            String command1 = "sudo bash restartKafkaService.sh"; //command to start new kafka broker
            ChannelExec channel1 = (ChannelExec) session.openChannel("exec");
            channel1.setCommand(command1);
            channel1.setErrStream(System.err);
            channel1.connect();
            readInputStreamFromSshSession(channel1);
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function returns the first available active kafka broker from the
     * newly build cluster - required to launch the kafka topic creation command
     * before making the the cluster ready for accepting the data streams from
     * the producers.
     *
     * @return
     */
    public static String getActiveBrokerDns() {
        String brokerDns = "";
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String query = "select public_dnsname from dpp_resources.ingestion_nodes_info where status = ? limit 1";
            try (PreparedStatement pst = DatabaseConnection.con.prepareStatement(query)) {
                pst.setString(1, "running");
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    
                    brokerDns = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return brokerDns;
    }
    public static String getZookeeperDns(){
                String zkDns = "";
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "select zk_dnsname from dpp_resources.ingestion_cluster_info where cluster_id = ? limit 1";
                    try (PreparedStatement pst = DatabaseConnection.con.prepareStatement(query)) {
                        pst.setInt(1, 100);
                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            
                            zkDns = rs.getString(1);
                        }       }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zkDns;
    }

    /**
     * This function creates the kafka topic based on the size of the current
     * kafka cluster. We assume parition count and replication factor are equal
     * for small size cluster.
     *
     * @param partitionsCount
     */
    public static void configureKafkaTopic(String partitionsCount) {
        JSch jschClient = new JSch();
        String brokerDns = getActiveBrokerDns();
        if ("".equals(brokerDns)) {
            brokerDns = MainForm.txtFieldInstId.getText().trim();
        }
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", brokerDns, 22);
            session.connect();
            //run commands
            deleteTopicFromZookeeper();
            sleep(5000);
            String cmd = "sudo bash configKafkaTopic.sh 1 " + partitionsCount + " 1 1"; //command to configure kafka topic before starting the cluster - based on no of kafka nodes.
            ChannelExec channel3 = (ChannelExec) session.openChannel("exec");
            channel3.setCommand(cmd);
            channel3.setErrStream(System.err);
            channel3.connect();
            readInputStreamFromSshSession(channel3);
            sleep(5000);
            String cmd1 = "sudo bash createTopic.sh"; //command to create new kafka topic with new partitions and replication factor.
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd1);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(3000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function is used to delete the existing topic from the zookeeper,
     * whenever there is a change in the number of brokers in the cluster due to
     * scaling.
     */
    public static void deleteTopicFromZookeeper() {

        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            String zkDns = getZookeeperDns();
            Session session = jschClient.getSession("ubuntu", zkDns, 22);
            session.connect();
            //run commands
            String command = "sudo bash deleteTopicsZk.sh";         //script file must be available on the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public static void startZookeeperServer(String zkDns){
      JSch jschClient = new JSch();
        try {
            sleep(5000);
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");            
            Session session = jschClient.getSession("ubuntu", zkDns, 22);
            session.connect();
            //run commands
            String command = "sudo bash runZookeeperService.sh";         //script file must be available on the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    public static void readInputStreamFromSshSession(ChannelExec channel) throws InterruptedException {
        InputStream input = null;
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
                    System.out.println(new String(tmp, 0, i));
                }
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (channel.isClosed()) {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            } else if (channel.isEOF()) {
                break;
            } else if (channel.getExitStatus() == 0) {
                break;
            }
            sleep(1000);
        }
        channel.disconnect();
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
}
