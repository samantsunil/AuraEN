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
package com.ssamant.cloudformation;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResource;
import com.amazonaws.services.cloudformation.model.StackStatus;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logincredentials.CloudLogin;

/**
 *
 * @author sunil
 */
public class CloudFormationSparkCluster {
    
    public static void createCloudFormation(){
        AmazonCloudFormation stackbuilder = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(CloudLogin.getAWSCredentials()))
            .withRegion(Regions.AP_SOUTHEAST_2)
            .build();       
        System.out.println("===========================================");
        System.out.println("Getting Started with AWS CloudFormation for Spark Cluster");
        System.out.println("===========================================\n");
        String stackName  = "SparkStackDPP";
        String logicalResourceName = "sparkcluster";
        try {
            // Create a stack
            CreateStackRequest createRequest = new CreateStackRequest();
            createRequest.setStackName(stackName);
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("sparkclustercloudform.template");
            createRequest.setTemplateBody(convertStreamToString(is));            
            System.out.println("Creating a stack called " + createRequest.getStackName() + ".");
            stackbuilder.createStack(createRequest);
            String status = waitForCompletion(stackbuilder, stackName);
            // Wait for stack to be created 
            String info = "Stack creation completed, the stack " + stackName + " completed with " + status;
             System.out.println(info);
            
            // Show all the stacks for this account along with the resources for each stack
            for (Stack stack : stackbuilder.describeStacks(new DescribeStacksRequest()).getStacks()) {
                System.out.println("Stack : " + stack.getStackName() + " [" + stack.getStackStatus() + "]");
                
                DescribeStackResourcesRequest stackResourceRequest = new DescribeStackResourcesRequest();
                stackResourceRequest.setStackName(stack.getStackName());
                for (StackResource resource : stackbuilder.describeStackResources(stackResourceRequest).getStackResources()) {
                    System.out.format("    %1$-40s %2$-25s %3$s\n", resource.getResourceType(), resource.getLogicalResourceId(), resource.getPhysicalResourceId());
                  
                }
            }

            // Lookup a resource by its logical name
            DescribeStackResourcesRequest logicalNameResourceRequest = new DescribeStackResourcesRequest();
            logicalNameResourceRequest.setStackName(stackName);
            logicalNameResourceRequest.setLogicalResourceId(logicalResourceName);
            System.out.format("Looking up resource name %1$s from stack %2$s\n", logicalNameResourceRequest.getLogicalResourceId(), logicalNameResourceRequest.getStackName());
            for (StackResource resource : stackbuilder.describeStackResources(logicalNameResourceRequest).getStackResources()) {
                System.out.format("    %1$-40s %2$-25s %3$s\n", resource.getResourceType(), resource.getLogicalResourceId(), resource.getPhysicalResourceId());
            }

            // Delete the stack
//            DeleteStackRequest deleteRequest = new DeleteStackRequest();
//            deleteRequest.setStackName(stackName);
//            System.out.println("Deleting the stack called " + deleteRequest.getStackName() + ".");
//            stackbuilder.deleteStack(deleteRequest);
//            // Wait for stack to be deleted           
//            System.out.println("Stack creation completed, the stack " + stackName + " completed with " + waitForCompletion(stackbuilder, stackName));

        }
        catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS CloudFormation, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS CloudFormation, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(CloudFormationKafkaCluster.class.getName()).log(Level.SEVERE, null, ex);
            stackbuilder.shutdown();
        }
    }
    public static void deleteStack() {
      
         AmazonCloudFormation stackBuilder = AmazonCloudFormationClientBuilder.standard()
                                            .withCredentials(new AWSStaticCredentialsProvider(CloudLogin.getAWSCredentials()))
                                            .withRegion(Regions.AP_SOUTHEAST_2)
                                            .build();
         
         String stackName = "SparkStackDPP";
         String logicalResourceName = "sparkcluster";
        try {
            DeleteStackRequest deleteRequest = new DeleteStackRequest();
            deleteRequest.setStackName(stackName);
            
            System.out.println("Deleting the stack called " + deleteRequest.getStackName() + ".");
          
            stackBuilder.deleteStack(deleteRequest);
            // Wait for stack to be deleted  
            String status = waitForCompletion(stackBuilder, stackName);
            String info = "Stack deletion completed, the stack " + stackName + " completed with " + status;
            System.out.println(info);
         
       } 
         catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS CloudFormation, but was rejected with an error response for some reason.");
          
            System.out.println("Error Message:    " + ase.getMessage());
           
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
           
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
          
            System.out.println("Error Type:       " + ase.getErrorType());
          
            System.out.println("Request ID:       " + ase.getRequestId());
            
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS CloudFormation, "
                    + "such as not being able to access the network.");
           
            System.out.println("Error Message: " + ace.getMessage());
            
            
        }
         catch (Exception ex) {
            Logger.getLogger(CloudFormationKafkaCluster.class.getName()).log(Level.SEVERE, null, ex);
            stackBuilder.shutdown();
        }
       
    }
    public static String convertStreamToString(InputStream in) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringbuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          stringbuilder.append(line + "\n");
        }
        in.close();
        return stringbuilder.toString();
      }

     public static String waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        Boolean completed = false;
        String  stackStatus = "Unknown";
        String  stackReason = "";

        System.out.print("Waiting");

        while (!completed) {
            List<Stack> stacks = stackbuilder.describeStacks(wait).getStacks();
            if (stacks.isEmpty())
            {
                completed   = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                    }
                    else if(stack.getStackStatus().equals(StackStatus.ROLLBACK_COMPLETE.toString())){
                        completed =true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                    }
                }
            }

            // Show we are waiting
            System.out.print(".");

            // Not done yet so sleep for 10 seconds.
            if (!completed) Thread.sleep(10000);
        }

        // Show we are done
        System.out.print("done\n");

        return stackStatus + " (" + stackReason + ")";
    }
    
}
