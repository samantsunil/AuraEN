/*
 * The MIT License
 *
 * Copyright 2020 Sunil.
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
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import logincredentials.CloudLogin;

/**
 *
 * @author Sunil
 */
public class EC2InstanceOperation {
    
     public static void terminateEc2Instance(String instanceId){
            if (instanceId != null) {
                DryRunSupportedRequest<TerminateInstancesRequest> dryRequest
                        = () -> {
                            TerminateInstancesRequest request = new TerminateInstancesRequest()
                                    .withInstanceIds(instanceId);
                            
                            return request.getDryRunRequest();
                        };
                AmazonEC2 ec2Client = CloudLogin.getEC2Client();
                DryRunResult dryResponse = ec2Client.dryRun(dryRequest);
                if (!dryResponse.isSuccessful()) {
                    System.out.printf("Failed dry run to terminate instance %s", instanceId);
                    throw dryResponse.getDryRunResponse();
                }
                TerminateInstancesRequest request = new TerminateInstancesRequest()
                        .withInstanceIds(instanceId);
                ec2Client.terminateInstances(request);
                //waitForRunningState(ec2Client, instanceId);
            }
            else {
                System.out.println("Instance does not exist or invalid instance id.");
            }
    }
    
}
