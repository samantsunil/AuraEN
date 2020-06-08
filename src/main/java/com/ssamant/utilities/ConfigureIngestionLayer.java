/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import static com.ssamant.pocresourcemanagement.MainForm.getEC2Client;
import static com.ssamant.pocresourcemanagement.MainForm.lblClusterStatus;
import static com.ssamant.pocresourcemanagement.MainForm.lblInstanceStopMsg;
import static com.ssamant.pocresourcemanagement.MainForm.lblStartedInstance;
import static com.ssamant.pocresourcemanagement.MainForm.lblStopInstance;
import static com.ssamant.pocresourcemanagement.MainForm.txtAreaClusterInfo;
import static com.ssamant.pocresourcemanagement.MainForm.txtAreaIngestionDetails;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunil
 */
public class ConfigureIngestionLayer {

    public ConfigureIngestionLayer() {

    }
    public static String amiId = "ami-0a139537a122f6310";

    public static void buildIngestionLayerCluster(int noOfBrokers, String instType) {
        try {
            AmazonEC2 ec2Client = getEC2Client();
            createEC2Instances(ec2Client, instType, noOfBrokers);
        } catch (InterruptedException ex) {
            System.out.printf("Error in instance creation " + ex.getMessage());
        }

    }

    public static void createEC2Instances(AmazonEC2 ec2Client, String instType, int noOfBrokers) throws InterruptedException {
        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId) //img id for ubuntu machine image, can be replaced with AMI image built using snapshot
                .withInstanceType(InstanceType.T2Micro) //free -tier instance type used
                .withKeyName("mySSHkey") //keypair name
                .withSecurityGroupIds("sg-66130614", "sg-03dcfd207ba24daae")
                .withMaxCount(noOfBrokers)
                .withMinCount(1);

        @SuppressWarnings("ThrowableResultIgnored")
        RunInstancesResult runResponse = ec2Client.runInstances(runRequest);
        // List<String> instanceIds = new ArrayList<>();
        WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true); //to save cluster info in a text file.
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
            startEC2Instance(ec2Client, inst, inst.getPlacement(), data);
            i++;
        }
    }

    public static void startEC2Instance(AmazonEC2 ec2Client, Instance inst, Placement az, WriteFile data) throws InterruptedException {
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(inst.getInstanceId());
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance curInstance = waitForRunningState(ec2Client, inst.getInstanceId());
        if (curInstance != null) {
            System.out.printf("Successfully started EC2 instance %s based on type %s", curInstance.getInstanceId(), curInstance.getInstanceType());
            txtAreaClusterInfo.append("Successfully created following ec2 instances for the Ingestion Cluster:\n");
            txtAreaClusterInfo.append("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName() + ", PublicIP:" + curInstance.getPublicIpAddress() + ".\n");
            try {

                data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + az.getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                        + ", PublicIP:" + curInstance.getPublicIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                lblClusterStatus.setText("Error while writing to a file: " + ex.getMessage());
            }
        } else {
            System.out.println("Instances are not running.");
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
            AmazonEC2 ec2Client = getEC2Client();
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
                WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
                try {

                    data.writeToFile("InstanceID: " + curInstance.getInstanceId() + " , InstanceType: " + curInstance.getInstanceType() + ", AZ: ." + curInstance.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + curInstance.getPublicDnsName()
                            + ", PublicIP:" + curInstance.getPublicIpAddress() + ", Status: " + curInstance.getState().getName() + ".");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    lblStopInstance.setText("Error while writing to a file: " + ex.getMessage());
                }
            } else {
                System.out.println("Instances are not running.");
            }
        } else {
            lblStopInstance.setText("");
            lblStopInstance.setText("Enter the valid instance ID.");
        }
    }

    public static void restartKafkaBrokerNode(String instId) throws InterruptedException {

        AmazonEC2 ec2Client = getEC2Client();
        WriteFile data = new WriteFile("C:\\Code\\KafkaClusterDetails.txt", true);
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instId);
        StartInstancesResult result = ec2Client.startInstances(startInstancesRequest);
        Instance inst = waitForRunningState(ec2Client, instId);
        if (inst != null) {
            lblInstanceStopMsg.setText("Instance with Id: " + instId + " starts running.");
            lblStartedInstance.setText("Instance with Id: " + instId + " starts running successfully.");
            try {

                data.writeToFile("InstanceID: " + inst.getInstanceId() + " , InstanceType: " + inst.getInstanceType() + ", AZ: ." + inst.getPlacement().getAvailabilityZone() + ", PublicDNSName: " + inst.getPublicDnsName()
                        + ", PublicIP:" + inst.getPublicIpAddress() + ", Status: " + inst.getState().getName() + ".");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                lblStartedInstance.setText("Error while writing to a file: " + ex.getMessage());
            }
        }
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

    public static void configureNewlyCreatedBroker(String pubDnsName, String newReplicationFactor, String newPartitionsCount, String newBrokerId) {
        String oldReplicationFactor = "1"; //obtain from existing info       
        String oldPartitionsCount = "1"; //obtain from existing info stored in DB 
        if ("".equals(newBrokerId)) {
            newBrokerId = "0";
        }
        if ("".equals(newReplicationFactor)) {
            newReplicationFactor = "1";
        }
        if ("".equals(newPartitionsCount)) {
            newPartitionsCount = "1";
        }
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem");
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", pubDnsName, 22);
            session.connect();
            //run commands - with 5 arguments: 
            String command = "sudo bash configNewBroker.sh " + newBrokerId + " " + oldPartitionsCount + " " + newPartitionsCount + " " + oldReplicationFactor + " " + newReplicationFactor;
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
            readInputStreamFromSshSession(channel);
            sleep(5000);
            if (!oldPartitionsCount.equals(newPartitionsCount)) {
                String command2 = "sudo bash deleteTopic.sh"; //command to delete existing topic
                ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
                channel2.setCommand(command2);
                channel2.setErrStream(System.err);
                channel2.connect();
                readInputStreamFromSshSession(channel2);
                sleep(5000);
                deleteTopicFromZookeeper();
                sleep(5000);
                String cmd = "sudo bash createTopic.sh"; //command to create new kafka topic with new partitions and replication factor.
                ChannelExec channel3 = (ChannelExec) session.openChannel("exec");
                channel3.setCommand(cmd);
                channel3.setErrStream(System.err);
                channel3.connect();
                readInputStreamFromSshSession(channel3);
            }
            session.disconnect();
        } catch (JSchException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteTopicFromZookeeper() {
        String zkDnsName = "ec2-3-24-240-124.ap-southeast-2.compute.amazonaws.com"; //hard-coded now ... as zk is not managed by the app
        JSch jschClient = new JSch();
        try {
            jschClient.addIdentity("C:\\Code\\mySSHkey.pem"); //ssh key location .pem file
            JSch.setConfig("StrictHostKeyChecking", "no");
            Session session = jschClient.getSession("ubuntu", zkDnsName, 22);
            session.connect();
            //run commands
            String command = "sudo bash deleteTopicsZk.sh";         //script file must be available on the instance home directory
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            readInputStreamFromSshSession(channel);
            sleep(5000);
            readInputStreamFromSshSession(channel);
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
