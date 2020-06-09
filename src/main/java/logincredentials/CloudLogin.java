/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logincredentials;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
/**
 *
 * @author sunil
 */
public class CloudLogin {

    public CloudLogin() {
    }         
   public static final  AWSCredentials getAWSCredentials(){
       AWSCredentials credentials_profile = null;
   try {
        
         credentials_profile = new ProfileCredentialsProvider("default").getCredentials();
    }
    catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load credentials from .aws/credentials file. " +
                    "Make sure that the credentials file exists and that the profile name is defined within it.",
                    e);
        }
   return credentials_profile;
   }
    public static AmazonEC2 getEC2Client() {
        AmazonEC2 ec2Client = null;
        try {
            ec2Client = AmazonEC2ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(CloudLogin.getAWSCredentials()))
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();
        } catch (Exception ex) {
            System.out.printf("Error in instance creation: " + ex.getMessage());
        }
        return ec2Client;
    }
}
