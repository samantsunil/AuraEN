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
import com.ssamant.pocresourcemanagement.MainForm;
import static com.ssamant.utilities.ConfigureStorageLayer.dbInsertInstanceInfo;
import static com.ssamant.utilities.ConfigureStorageLayer.waitForRunningState;
import static com.ssamant.utilities.DatabaseConnection.getConnection;
import java.io.IOException;
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
    public static String ami_id_spark = "";

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
            stratInstanceForProcessingCluster(ec2Client, inst, inst.getPlacement(), data);
            i++;
        }
    }

    public static void stratInstanceForProcessingCluster(AmazonEC2 ec2Client, Instance inst, Placement az, WriteFile data) {
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

    public static void loadSparkClusterDetailsFromDb() {
        MainForm.txtAreaSparkResourcesInfo.setText("");
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
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String instanceId = rs.getString("instance_id");
                String instanceType = rs.getString("instance_type");
                String az = rs.getString("availability_zone");
                String publicDnsName = rs.getString("public_dnsname");
                String publicIp = rs.getString("public_ip");
                String privateIp = rs.getString("private_ip");
                String status = rs.getString("status");
                System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, privateIp, status);
                MainForm.txtAreaSparkResourcesInfo.append("InstanceID: " + instanceId + ", InstanceType: " + instanceType + ", AvailabilityZone: " + az + ", PublicDns: " + publicDnsName + ", PublicIp: " + publicIp + ", PrivateIp: " + privateIp + ", Status: " + status + ".\n");
            }
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
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
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    MainForm.lblStopRestartStatus.setText("Error while writing to a file: " + ex.getMessage());
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigureProcessingLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
