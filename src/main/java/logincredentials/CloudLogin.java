/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logincredentials;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
/**
 *
 * @author sunil
 */
public class CloudLogin {
        
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
}
