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
package com.ssamant.pocresourcemanagement;

import com.ssamant.utilities.GetPropertyFileKeyValues;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sunil
 */
public class ResourceOptimizer {

    public ResourceOptimizer() {

    }
    public static int[] dppResourcesCount = new int[3];
    public static String[] dppInstanceType = new String[3];

    public static Boolean getResourceAllocation(int w1, int w2, int w3, int e2eQoS) {
        @SuppressWarnings("UnusedAssignment")
        Boolean success = false;

        try {
            String e2eResourceAllocation = "";           
            int[][] S1_W = new int[][]{{1000, 10000, 30000, 30001, 30001, 30001}, 
                {1000, 10000, 30000, 50000, 50001, 50001}, 
                {1000, 10000, 30000, 50000, 80000, 90000}}; //aws - t2- micro, small, medium
             
            int[][] S2_W = new int[][]{{1000, 2000, 3000, 40000, 5000, 5000}, 
                {1000, 2000, 3000, 4000, 5000, 5000}, {1000, 2000, 3000, 4000, 5000, 6000}};
            int[][] S3_W = new int[][]{{1000, 5000, 10000, 15000, 18000, 19000, 19000},
                {1000, 5000, 10000, 15000, 18000, 19000, 19000}, 
                {1000, 5000, 10000, 15000, 20000, 25000, 30000}};

            int[][] S1_Q = new int[][]{{73, 75, 79, 5000, 5000, 5000}, {28, 35, 44, 45, 5000, 5000}, {16, 25, 31, 33, 52, 60}};
            int[][] S2_Q = new int[][]{{300, 400, 700, 900, 5000, 5000}, {300, 400, 700, 900, 5000, 5000}, {200, 400, 700, 800, 850, 900}};
            int[][] S3_Q = new int[][]{{15, 17, 20, 25, 51, 5000, 5000}, {15, 16, 18, 25, 35, 5000, 5000}, {15, 15, 16, 17, 20, 25, 40}};
            Float[] price = GetPropertyFileKeyValues.getInstancePrice();//new float[]{0.0146F, 0.0292F, 0.0584F}; //on-demand t2.micro, t2.small and t2.medium price in USD/hr
            int delta_A = GetPropertyFileKeyValues.getDeltaMinAQoS(); //based on the minimum latency required in layer 2
            int delta_B = GetPropertyFileKeyValues.getDeltaMinBQoS(); //based on minimum latency required in layer 3
            float total_cost = 0.0F;
            int aggQoS = delta_A + delta_B;
            List<String> soln = new ArrayList<>();
            List<Float> totCost = new ArrayList<>();
            List<Integer> qos = new ArrayList<>();
            List<String> soln2 = new ArrayList<>();
            List<Float> totCost2 = new ArrayList<>();
            List<Integer> qos2 = new ArrayList<>();
            List<String> soln3 = new ArrayList<>();
            List<Float> totCost3 = new ArrayList<>();
            List<Integer> qos3 = new ArrayList<>();

            for (int k = 1; k <= 3; k++) {
                switch (k) {
                    case 1: {
                        String instance_type = null;
                        for (int i = 0; i < S1_W.length; i++) {
                            for (int j = 0; j < S1_W[0].length; j++) {
                                if ((aggQoS + S1_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S1_W[i][j] >= w1) {
                                        z = 1;
                                    } else {
                                        while ((z * S1_W[i][j]) < w1) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "t2.micro";
                                            break;
                                        case 1:
                                            instance_type = "t2.small";
                                            break;
                                        default:
                                            instance_type = "t2.medium";
                                            break;
                                    }
                                    soln.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost.add(z * price[i]);
                                    qos.add(S1_Q[i][j]);
                                }

                            }
                        }
                        String[] instS1 = soln.toArray(new String[0]);//((String[]) soln.toArray());
                        Float[] costS1 = totCost.toArray(new Float[0]);//((Float[]) totCost.toArray());
                        Integer[] qosS1 = qos.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS1.length; i++) {
                            for (int j = i + 1; j < instS1.length; j++) {
                                if (costS1[j] < costS1[i]) {
                                    tmp = costS1[i];
                                    tmp_val = instS1[i];
                                    tm_qos = qosS1[i];
                                    costS1[i] = costS1[j];
                                    instS1[i] = instS1[j];
                                    qosS1[i] = qosS1[j];
                                    costS1[j] = tmp;
                                    instS1[j] = tmp_val;
                                    qosS1[j] = tm_qos;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS1[0];
                        total_cost = total_cost + costS1[0];
                        System.out.println("For Service S1:");
                        //foreach (String item in instS1) {
                        MainForm.txtAreaIngestionResources.append("Instances required for ingestion layer: " + instS1[0] + "\n");
                        //int xInd = instS1[0].indexOf("X");
                        String[] split = instS1[0].split("X");
                        //String subString = null;
                       // if (xInd != -1) {
                        //    subString = instS1[0].substring(0, xInd);
                        //}
                        dppResourcesCount[0] = Integer.parseInt(split[0]);
                        dppInstanceType[0] = split[1];
                        // System.out.print(instS1[0] + '\t');
                        //}
                        System.out.println();
                        // foreach (float item in costS1) {
                        // MainForm.txtAreaIngestionResources.append("")
                        System.out.print(String.valueOf(costS1[0]) + '\t');
                        // }
                        System.out.println();
                        System.out.println(String.valueOf(qosS1[0]));
                        break;
                    }
                    case 2: {
                        aggQoS = aggQoS - delta_A;
                        String instance_type = null;
                        for (int i = 0; i < S2_W.length; i++) {
                            for (int j = 0; j < S2_W[0].length; j++) {
                                if ((aggQoS + S2_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S2_W[i][j] >= w2) {
                                        z = 1;
                                    } else {
                                        while ((z * S2_W[i][j]) < w2) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "t2.micro";
                                            break;
                                        case 1:
                                            instance_type = "t2.small";
                                            break;
                                        default:
                                            instance_type = "t2.medium";
                                            break;
                                    }
                                    soln2.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost2.add(z * price[i]);
                                    qos2.add(S2_Q[i][j]);
                                }

                            }
                        }
                        String[] instS2 = soln2.toArray(new String[0]);
                        Float[] costS2 = totCost2.toArray(new Float[0]);
                        Integer[] qosS2 = qos2.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS2.length; i++) {
                            for (int j = i + 1; j < instS2.length; j++) {
                                if (costS2[j] < costS2[i]) {
                                    tmp = costS2[i];
                                    tmp_val = instS2[i];
                                    tm_qos = qosS2[i];
                                    costS2[i] = costS2[j];
                                    instS2[i] = instS2[j];
                                    qosS2[i] = qosS2[j];
                                    costS2[j] = tmp;
                                    instS2[j] = tmp_val;
                                    qosS2[j] = tm_qos;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS2[0];
                        total_cost = total_cost + costS2[0];
                        System.out.println("For Service S2:");
                        //foreach (String item in instS2) {
                        MainForm.txtAreaProcessingResources.append("Instances required for processing layer: " + instS2[0] + "\n");
                        System.out.print(instS2[0] + '\t');
                        String[] split = instS2[0].split("X");
                       // String subString = null;
                       // if (xInd != -1) {
                        //    subString = instS1[0].substring(0, xInd);
                        //}
                        dppResourcesCount[1] = Integer.parseInt(split[0]);
                        dppInstanceType[1] = split[1];
                        //}
                        System.out.println();
                        //foreach (float item in costS2) {
                        System.out.print(String.valueOf(costS2[0]) + '\t');
                        // }
                        System.out.println();
                        System.out.println(String.valueOf(qosS2[0]));
                        break;
                    }
                    case 3: {
                        aggQoS = aggQoS - delta_B;
                        String instance_type = null;
                        for (int i = 0; i < S3_W.length; i++) {
                            for (int j = 0; j < S3_W[0].length; j++) {
                                if ((aggQoS + S3_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S3_W[i][j] >= w3) {
                                        z = 1;
                                    } else {
                                        while ((z * S3_W[i][j]) < w3) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "t2.micro";
                                            break;
                                        case 1:
                                            instance_type = "t2.small";
                                            break;
                                        default:
                                            instance_type = "t2.medium";
                                            break;
                                    }
                                    soln3.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost3.add(z * price[i]);
                                    qos3.add(S3_Q[i][j]);
                                }

                            }
                        }
                        String[] instS3 = soln3.toArray(new String[0]);
                        Float[] costS3 = totCost3.toArray(new Float[0]);
                        Integer[] qosS3 = qos3.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS3.length; i++) {
                            for (int j = i + 1; j < instS3.length; j++) {
                                if (costS3[j] < costS3[i]) {
                                    tmp = costS3[i];
                                    tmp_val = instS3[i];
                                    tm_qos = qosS3[i];
                                    costS3[i] = costS3[j];
                                    instS3[i] = instS3[j];
                                    qosS3[i] = qosS3[j];
                                    costS3[j] = tmp;
                                    instS3[j] = tmp_val;
                                    qosS3[j] = tm_qos;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS3[0];
                        total_cost = total_cost + costS3[0];
                        System.out.println("For Service S3:");
                        //foreach (String item in instS3) {
                        MainForm.txtAreaStorageResources.append("Instances required for storage layer: " + instS3[0] + "\n");
                        System.out.print(instS3[0] + '\t');
                        String[] split = instS3[0].split("X");
                        //String subString = null;
                       // if (xInd != -1) {
                        //    subString = instS1[0].substring(0, xInd);
                        //}
                        dppResourcesCount[2] = Integer.parseInt(split[0]);
                        dppInstanceType[2] = split[1];
                        // }
                        System.out.println();
                        // foreach (float item in costS3) {
                        System.out.print(String.valueOf(costS3[0]) + '\t');
                        // }
                        System.out.println();
                        System.out.println(String.valueOf(qosS3[0]));
                        break;
                    }
                    default:
                        break;
                }

            }
           // System.out.println("Total cost: " + String.valueOf(total_cost));
            //System.out.println("total end-to-end QoS: " + String.valueOf(aggQoS));
            MainForm.lblTotalCost.setText("");
            MainForm.lblTotalCost.setText("Total cost: " + String.valueOf(total_cost));
            MainForm.lblE2eQoS.setText("");
            MainForm.lblE2eQoS.setText("Total end-to-end latency:" + String.valueOf(aggQoS));
            success = true;

        } catch (NumberFormatException ex) {
            success = false;

        }
        return success;

    }

}
