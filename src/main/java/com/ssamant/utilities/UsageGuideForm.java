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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Sunil
 */
public class UsageGuideForm extends javax.swing.JFrame {

    /**
     * Creates new form UsageGuideForm
     */
    public UsageGuideForm() {
        initComponents();
        displayUsageGuidelines();
        setWindowSize();
    }

    private void setWindowSize() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    public final void displayUsageGuidelines() {
        //txtPaneUsageGuide.setText("The application usage guidelines step-by-step !!!");
        StyledDocument doc = txtPaneUsageGuide.getStyledDocument();
        SimpleAttributeSet defaultSetting = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultSetting, Color.BLACK);
        StyleConstants.setBackground(defaultSetting, Color.WHITE);
        StyleConstants.setBold(defaultSetting, true);

        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.RED);
        StyleConstants.setBackground(keyWord, Color.YELLOW);
        StyleConstants.setBold(keyWord, true);
        StyleConstants.setFontSize(keyWord, 16);

        SimpleAttributeSet prereqAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(prereqAttr, Color.BLUE);
        StyleConstants.setBackground(prereqAttr, Color.GRAY);
        StyleConstants.setBold(prereqAttr, true);
        StyleConstants.setFontSize(prereqAttr, 12);
        StyleConstants.setItalic(prereqAttr, true);

        SimpleAttributeSet keyWordIngestion = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWordIngestion, Color.GREEN);
        StyleConstants.setBackground(keyWordIngestion, Color.DARK_GRAY);
        StyleConstants.setBold(keyWordIngestion, true);

        SimpleAttributeSet keyWordProcessing = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWordProcessing, Color.RED);
        StyleConstants.setBackground(keyWordProcessing, Color.YELLOW);
        StyleConstants.setBold(keyWordProcessing, true);

        SimpleAttributeSet keyWordStorage = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWordStorage, Color.BLUE);
        StyleConstants.setBackground(keyWordStorage, Color.CYAN);
        StyleConstants.setBold(keyWordStorage, true);
        try {
            doc.insertString(0, "-----------------The application usage guidelines step-by-step !!!-----------\n", keyWord);
            doc.insertString(doc.getLength(), "\n", keyWord);
            doc.insertString(doc.getLength(), "*************Application prerequisties***************\n", prereqAttr);
            doc.insertString(doc.getLength(), "The following items are expected to have installed/configured/created before using the application:\n", defaultSetting);
            doc.insertString(doc.getLength(), "1. Must have created an AWS account having access to AWS EC2 related service requests.\n", defaultSetting);
            doc.insertString(doc.getLength(), "2. Created SSH key for accessing the EC2 instances remotely & the private key should be available to the application.\n", defaultSetting);
            doc.insertString(doc.getLength(), "3. Must have created appropriate security groups allowing access to inbound/outbound traffic in the appropriate ports required in corresponding service components for communication among the services.\n", defaultSetting);
            doc.insertString(doc.getLength(), "4. Must have created the AMIs (https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-instances-and-amis.html) for each service component of the pipeline, and the AMI ID for each service component should be available in the AMI_INFO database table. The AMI for each service component"
                    + " should be installed with relevant software components and any configuration scripts required (supplied).\n", defaultSetting);
            doc.insertString(doc.getLength(), "5. A MySQL database server running instance with connection information and configured with the given database schema (supplied by the application) and tables.\n", defaultSetting);
            doc.insertString(doc.getLength(), "6. A sustainable QoS profile for candidate cloud instances (EC2 instances) should be available.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n\n", defaultSetting);
            doc.insertString(doc.getLength(), "For the initial deployment of pipeline services on the (EC2) cloud instances go to the "
                    + "'DPP Resource Allocation Details & Scaling' tab/module to find the intial set of cloud instances required for the pipeline"
                    + " services based on the given future workload value (in X1000) and the end-to-end latency (in milliseconds) requirement using the full-scale capacity optimization( by default)"
                    + " strategy.\n After getting the info about resources required for ingestion, processing and storage layers, start building the resource cluster for each"
                    + " layer starting from the ingestion layer, then storage layer and finally for the processing layer navigating to the corresponding tabs or modules explained below.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n", keyWord);
            doc.insertString(doc.getLength(), "--------------------------------------INGESTION LAYER--------------------------------\n", keyWordIngestion);
            doc.insertString(doc.getLength(), "\n", defaultSetting);
            doc.insertString(doc.getLength(), "1. Click button 'Load Current Cluster Details' to display the existing cluster resources info for the layer. There is no details for initial deployment step.\n", defaultSetting);
            doc.insertString(doc.getLength(), "2. If no cluster resources are found then start building ingestion service cluster by creating the zookeeper service node selecting the appropriate instance type.\n", defaultSetting);
            doc.insertString(doc.getLength(), "3. After successfully creating the zookeeper node, start building the service cluster (Kafka brokers) selecting the required number of broker nodes with selected instance type.\n", defaultSetting);
            doc.insertString(doc.getLength(), "4. After successfully creating kafka broker nodes, configure topic properties and create topic with appropriate topic paritions and replication count."
                    + " The recommendation is set the number of topic partitions equals to the number of active kafka broker nodes based on the QoS profile data.\n", defaultSetting);
            doc.insertString(doc.getLength(), "5. After completion of step 4, the kafka service is running and ready to accept the data ingestion requests from the data generators and ready to serve data to the data consumers.\n", defaultSetting);
            doc.insertString(doc.getLength(), "6. Additionally, the module provides provision for stopping or restarting the individual broker node in the cluster if required.\n", defaultSetting);
            doc.insertString(doc.getLength(), "7. Clicking the 'Delete Cluster' button terminates all the allocated resources from the ingestion cluster.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "-------------------------------------STORAGE LAYER------------------------------------\n", keyWordStorage);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "1. Click button 'Load Cluster Details' to display the existing cluster resources info. If there is no details available then build storage cluster set-up for initial deployment.\n", defaultSetting);
            doc.insertString(doc.getLength(), "2. Select the number of nodes and instance type before clicking the 'Build Cluster' button, this will create cluster resources for Cassandra service.\n", defaultSetting);
            doc.insertString(doc.getLength(), "3. As per Cassandra cluster set-up recommendation, it is required to configure some of the nodes as seed node and remaining as non-seed node, therefore after launching the "
                    + " the required number of instances, configure instances as seed or non-seed nodes depening upon the resource configurations. For example, for a single "
                    + "node set-up, it is by-default a seed node in the cluster, and for a two node cluster set-up configure one node as a seed node and other one can be configured as non-seed node. To configure a node as a seed node "
                    + "only DNS name and instance Id are required before clicking the 'Configure Cluster Node' button, whereas for a non-seed node, it requires additional parameter as value of the seed node(s) ip (as the private ip address "
                    + "of the seed node) and checked the non-seed radio button. However, more than one node can be configured as seed nodes for better fault-tolerance and resiliency. Seed node's public ip or public DNS can be passed to clients requesting to store or extract data"
                    + " from the Cassandra cluster.\n", defaultSetting);
            doc.insertString(doc.getLength(), "4. After configuring all the nodes in step 3, the Cassandra cluster is ready for accpeting the workload both for write or read requests. It is assumed that the required Keyspace and tables are already created for the cluster based on pre-installed scripts in the AMI"
                    + " for the given IoT application to be run on the pipeline.\n", defaultSetting);
            doc.insertString(doc.getLength(), "5. The Storage Layer module has option for deleting the entire resource cluster for Cassandra service in addition to stopping or restarting the individual "
                    + "cluster nodes.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n", defaultSetting);
            doc.insertString(doc.getLength(), "---------------------------------------PROCESSING LAYER----------------------------------------\n", keyWordProcessing);
            doc.insertString(doc.getLength(), "\n", defaultSetting);
            doc.insertString(doc.getLength(), "1. Click the button 'Load Cluster Info'  to display the existing cluster resource info if previously deployed.\n", defaultSetting);
            doc.insertString(doc.getLength(), "2. For the initial set-up of processing layer cluster, it is assumed that the ingestion layer and storage layer services are up and running.\n", defaultSetting);
            doc.insertString(doc.getLength(), "3. After making sure ingestion and storage layer services are running, there are two options for Spark as a processing layer to build the cluster."
                    + " One of option is to build the local set-up for single node cluster set-up. In the single node set-up all the components of the Spark service run within the same node "
                    + " so there is no need to configure separate master node.\n", defaultSetting);
            doc.insertString(doc.getLength(), "4. For local set-up select the single-node cluster type in the Build Cluster inputs which fixed the number of node as one. Also, it "
                    + "requires to select the right instance type before building the cluster.\n", defaultSetting);
            doc.insertString(doc.getLength(), "5. After building the cluster in single-node, it requires to Configure the node by selecting the node type as 'local'. "
                    + "This process runs the Spark node in a single node as a local cluster and submits the pre-installed job to cluster in the client mode. The pipeline can be seen running by checking "
                    + "at url: www.<public-dns-name-of-node>:4040\n", defaultSetting);
                        doc.insertString(doc.getLength(), "6. After verifying the pipeline is running, it is ready to accept the workload from the data sources connected to the ingestion layer "
                                + "with the expected workload as data ingestion rate to achieve the given end-to-end latency requirements.\n", defaultSetting);
            doc.insertString(doc.getLength(), "6. To set-up multi-node Spark cluster, first it requires to launch the worker nodes by selecting the instance type, selecting multi-node cluster type and number of nodes before clicking the 'Build Cluster' button. \n", defaultSetting);
            doc.insertString(doc.getLength(), "7. After launching the resources for worker nodes, it requires to build and configure the master node by selecting the instance type to run the Master node on.\n", defaultSetting);
            doc.insertString(doc.getLength(), "8. After successfully launching master node, check the master node is running at url: www.<public-dns-master-node>:8080\n", defaultSetting);
            doc.insertString(doc.getLength(), "9. After checking the master node running at step 8, start configuring the worker nodes, selecting the node type as 'worker' before clicking 'Configure Spark Node' button.\n", defaultSetting);
            doc.insertString(doc.getLength(), "10. Once all the worker nodes are active and attached to the master node verified through the master url, the data pipeline job is submitted to cluster through master node by clicking 'Submit Job to Cluster' button.\n", defaultSetting);
            doc.insertString(doc.getLength(), "11. To Check if the launched application is running or not, visit the master node URL and if it running then it is seen with the given application name in the master node home page.\n", defaultSetting);
            doc.insertString(doc.getLength(), "12. By clicking the link of the submitted application, the status of the spark streaming application can be viewed which is available at url:"
                    + " www.<master-node-dns>:4040\n", defaultSetting);
            doc.insertString(doc.getLength(), "13. There is an option for stopping or restarting the worker nodes from the cluster, in addition to the option for deleting the entire resource cluster.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "-------------------------------------------DPP Resource Scaling-----------------------------------------------------\n", keyWordIngestion);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "In the 'DPP Resource Allocation Details and Scaling' module allows to view the current resource allocation for each service component"
                    + " including the cluster specific parameters information. Also, this module allows to compute the one-step ahead resource allocation for DPP services "
                    + "based on the given predicted workload and end-to-end QoS requirements. To find the resource allocation there are two options for scaling strategy: delta-scale and full-scale. In "
                    + "delta-scale strategy the resource optimizer computes the resource allocation for the difference in the workload values between the current workload and given predicted workload value."
                    + " After computing the resource allocation based on the given parameters, the 'Scale DPP Resources' button initiates the appropriate resource scaling actions to scale resources at each layer"
                    + " based on the current allocation and computed allocation at the right time. The right time for initiating the resource scaling operation depends on the one-step ahead horizon set for the pipeline."
                    + " For example, if one-step ahead time is two hour range then around fifteen minutes before the two hour time, the resource scaling requests can be initiated to make sure the right amount of "
                    + "resources are available for the pipeline to handle the predicted workload with the given end-to-end QoS requirement.\n", defaultSetting);
            doc.insertString(doc.getLength(), "\n", defaultSetting);
            doc.insertString(doc.getLength(), "In the 'View QoS Profile' module, the QoS profile of the candidate cloud instances can be viewd available through performance benchmarking process using the "
                    + "given use case streaming application as benchmark application.\n", defaultSetting);
            doc.insertString(doc.getLength(), "At the moment, the application loads already available QoS profile data, however, the module can be extended to allow updating the"
                    + " existing QoS profile if new use case application is used or additional candidate cloud instances are needed to consider for the resource allocation.\n", defaultSetting);
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtPaneUsageGuide = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("The Application Usage Guidelines");
        setLocation(new java.awt.Point(0, 0));

        txtPaneUsageGuide.setEditable(false);
        jScrollPane1.setViewportView(txtPaneUsageGuide);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UsageGuideForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UsageGuideForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UsageGuideForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UsageGuideForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UsageGuideForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane txtPaneUsageGuide;
    // End of variables declaration//GEN-END:variables
}
