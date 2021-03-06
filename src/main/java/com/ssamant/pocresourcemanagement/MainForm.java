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

import java.awt.Dimension;
import java.awt.Toolkit;
import com.ssamant.utilities.ConfigureIngestionLayer;
import com.ssamant.utilities.ConfigureProcessingLayer;
import com.ssamant.utilities.ConfigureStorageLayer;
import com.ssamant.utilities.DatabaseConnection;
import com.ssamant.utilities.UsageGuideForm;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.Random;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author sunil
 */
public class MainForm extends javax.swing.JFrame implements PropertyChangeListener {

    /**
     * Creates new form MainForm
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MainForm() {
        initComponents();
        setWindowSize();
        setCurrentWorkload();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close this window?", "Close Application?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (DatabaseConnection.con != null) {
                        try {
                            DatabaseConnection.con.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.exit(0);
                }
            }
        });
    }
    public static Boolean isInitialDeployment = true;
    public static Boolean isDeltaScaleStrategy = false;

    private void setWindowSize() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
                URL url = getClass().getResource("/croodap.png");
        ImageIcon imgicon = new ImageIcon(url);
        this.setIconImage(imgicon.getImage());
    }

    private void setCurrentWorkload() {
        String currWorkload = ConfigureIngestionLayer.getCurrentDataIngestionRate();
        if (currWorkload == null || "".equals(currWorkload)) {
            txtFieldCurrentWorkload.setText("0");
        } else {
            txtFieldCurrentWorkload.setText(currWorkload);
        }
    }

    class Task extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            ConfigureProcessingLayer.deleteProcessingCluster();
            //Make random progress.
            progress = 100;
            setProgress(Math.min(progress, 100));
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            btnDeleteCluster.setEnabled(true);
            txtAreaSparkResourcesInfo.setText("");
            txtAreaSparkResourcesInfo.setText("Resources for processing cluster deleted successfully.\n");
            setCursor(null); //turn off the wait cursor

        }
    }

    class ScaleTask extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            scaleDppResourcesFunction();
            //Make random progress.
            progress = 100;
            setProgress(Math.min(progress, 100));
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            btnScaleDppResources.setEnabled(true);
            progressBarDppScaling.setIndeterminate(false);
            setCursor(null); //turn off the wait cursor            
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBarProcessing.setValue(progress);
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

        dppLayersTab = new javax.swing.JTabbedPane();
        panDPPLayers = new javax.swing.JPanel();
        btnIngestion = new javax.swing.JButton();
        btnProcessing = new javax.swing.JButton();
        btnStorage = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaIngestionDetails = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaStorageDetails = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAreaProcessingDetails = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtFieldFutureWorkload = new javax.swing.JTextField();
        txtFieldE2eLatency = new javax.swing.JTextField();
        btnComputeDPPResAllocation = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtFieldCurrentWorkload = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        comboBoxScalingStrategy = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAreaIngestionResources = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtAreaProcessingResources = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtAreaStorageResources = new javax.swing.JTextArea();
        btnScaleDppResources = new javax.swing.JButton();
        progressBarDppScaling = new javax.swing.JProgressBar();
        lblErrorMsgCompResAllocation = new javax.swing.JLabel();
        btnClearAllDppLayers = new javax.swing.JButton();
        lblTotalCost = new javax.swing.JLabel();
        lblE2eQoS = new javax.swing.JLabel();
        panIngestion = new javax.swing.JPanel();
        lblInstanceStopMsg = new javax.swing.JLabel();
        lblErrDnsName = new javax.swing.JLabel();
        lblStartedInstance = new javax.swing.JLabel();
        lblStopInstance = new javax.swing.JLabel();
        lblClusterStatus = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jPanel4 = new javax.swing.JPanel();
        btnBuildIngestionCluster = new javax.swing.JButton();
        comboBoxInstanceType = new javax.swing.JComboBox<>();
        comboBoxBrokersNo = new javax.swing.JComboBox<>();
        comboBoxIngestionServices = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanel13 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtFieldInstId = new javax.swing.JTextField();
        btnBuildZkServer = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        txtFieldPartitions = new javax.swing.JTextField();
        txtFieldReplication = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnStartKafkaCluster = new javax.swing.JButton();
        comboBoxZkInstType = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaClusterInfo = new javax.swing.JTextArea();
        progressBarIngestion = new javax.swing.JProgressBar();
        jPanel16 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtFieldInstanceId = new javax.swing.JTextField();
        btnStopInstance = new javax.swing.JButton();
        btnStartInstance = new javax.swing.JButton();
        btnLoadIngestionClusterInfo = new javax.swing.JButton();
        javax.swing.JButton btnClearIngestionFormData = new javax.swing.JButton();
        btnDeleteIngestionCluster = new javax.swing.JButton();
        chkBoxZookeeperNode = new javax.swing.JCheckBox();
        panStorage = new javax.swing.JPanel();
        progressBarStorage = new javax.swing.JProgressBar();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtAreaCassandraResourcesInfo = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        txtFieldDnsNameStorage = new javax.swing.JTextField();
        btnConfigureStorageNode = new javax.swing.JButton();
        rdBtnNewNode = new javax.swing.JRadioButton();
        txtFieldSeedIp = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        lblMissingDnsNameStorage = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtFieldInstIdConfigure = new javax.swing.JTextField();
        lblNonSeedMsg = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnClearAllStorage = new javax.swing.JButton();
        btnLoadStorageClusterDetails = new javax.swing.JButton();
        btnDeleteStorageCluster = new javax.swing.JButton();
        lblInstanceStatus = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtFieldStorageInstId = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        btnInstanceStorageStart = new javax.swing.JButton();
        btnStopInstanceStorage = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        txtFieldDnsName = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblBuildClusterstatus = new javax.swing.JLabel();
        comboBoxNoSQLDb = new javax.swing.JComboBox<>();
        comboBoxNoNodes = new javax.swing.JComboBox<>();
        comboBoxDbInstType = new javax.swing.JComboBox<>();
        btnBuildStorageCluster = new javax.swing.JButton();
        panProcessing = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtAreaSparkResourcesInfo = new javax.swing.JTextArea();
        progressBarProcessing = new javax.swing.JProgressBar();
        lblBuildProcessingCluster1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFieldStartRestartInstId = new javax.swing.JTextField();
        btnStopInstanceProc = new javax.swing.JButton();
        btnRestartInstanceProc = new javax.swing.JButton();
        lblStopRestartStatus = new javax.swing.JLabel();
        btnClearFieldsProcessing = new javax.swing.JButton();
        btnLoadProcessingDetails = new javax.swing.JButton();
        btnDeleteCluster = new javax.swing.JButton();
        chkBoxMasterNode = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        txtFieldInstanceIdConfigure = new javax.swing.JTextField();
        txtFieldDnsNameConfigure = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        btnConfigureSparkNode = new javax.swing.JButton();
        lblConfigureSparkNode = new javax.swing.JLabel();
        comboBoxNodeType = new javax.swing.JComboBox<>();
        btnSparkSubmitApp = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        btnBuildProcessingCluster = new javax.swing.JButton();
        comboBoxNoSparkNodes = new javax.swing.JComboBox<>();
        comboBoxProcFrameworks = new javax.swing.JComboBox<>();
        comboBoxSparkInstType = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        comboBoxMasterNodeInstType = new javax.swing.JComboBox<>();
        btnCreateMasterNode = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        comboBoxClusterType = new javax.swing.JComboBox<>();
        lblBuildProcessingCluster = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtFieldInstanceIdConfigure1 = new javax.swing.JTextField();
        txtFieldDnsNameConfigure1 = new javax.swing.JTextField();
        txtFieldInstTypeConfigure1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        btnConfigureSparkNode1 = new javax.swing.JButton();
        lblConfigureSparkNode1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        btnBuildProcessingCluster1 = new javax.swing.JButton();
        comboBoxNoSparkNodes1 = new javax.swing.JComboBox<>();
        comboBoxProcFrameworks1 = new javax.swing.JComboBox<>();
        comboBoxSparkInstType1 = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        panQoSProfile = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblSustainableQoSProfile = new javax.swing.JTable();
        btnViewQoSProfile = new javax.swing.JButton();
        btnUpdateQoSProfile = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuAboutApp = new javax.swing.JMenu();
        menuItemUsage = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AuraEN");
        setIconImages(null);
        setName("mainForm"); // NOI18N
        setSize(new java.awt.Dimension(0, 0));

        dppLayersTab.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        btnIngestion.setText("Kafka Cluster");
        btnIngestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngestionActionPerformed(evt);
            }
        });

        btnProcessing.setText("Spark Cluster");
        btnProcessing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessingActionPerformed(evt);
            }
        });

        btnStorage.setText("Cassandra Cluster");
        btnStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStorageActionPerformed(evt);
            }
        });

        txtAreaIngestionDetails.setEditable(false);
        txtAreaIngestionDetails.setBackground(new java.awt.Color(204, 255, 204));
        txtAreaIngestionDetails.setColumns(20);
        txtAreaIngestionDetails.setForeground(new java.awt.Color(153, 0, 0));
        txtAreaIngestionDetails.setLineWrap(true);
        txtAreaIngestionDetails.setRows(5);
        txtAreaIngestionDetails.setText("Current Allocation:");
        txtAreaIngestionDetails.setWrapStyleWord(true);
        txtAreaIngestionDetails.setEnabled(false);
        jScrollPane2.setViewportView(txtAreaIngestionDetails);

        txtAreaStorageDetails.setBackground(new java.awt.Color(204, 255, 204));
        txtAreaStorageDetails.setColumns(20);
        txtAreaStorageDetails.setForeground(new java.awt.Color(153, 0, 0));
        txtAreaStorageDetails.setLineWrap(true);
        txtAreaStorageDetails.setRows(5);
        txtAreaStorageDetails.setText("Current Allocation:");
        txtAreaStorageDetails.setWrapStyleWord(true);
        txtAreaStorageDetails.setEnabled(false);
        jScrollPane3.setViewportView(txtAreaStorageDetails);

        txtAreaProcessingDetails.setBackground(new java.awt.Color(204, 255, 204));
        txtAreaProcessingDetails.setColumns(20);
        txtAreaProcessingDetails.setForeground(new java.awt.Color(153, 0, 0));
        txtAreaProcessingDetails.setLineWrap(true);
        txtAreaProcessingDetails.setRows(5);
        txtAreaProcessingDetails.setText("Current Allocation:");
        txtAreaProcessingDetails.setWrapStyleWord(true);
        txtAreaProcessingDetails.setEnabled(false);
        jScrollPane4.setViewportView(txtAreaProcessingDetails);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 51)));

        jLabel11.setText("Future Workload (ds/sec):");

        btnComputeDPPResAllocation.setText("Compute Resource Allocation");
        btnComputeDPPResAllocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComputeDPPResAllocationActionPerformed(evt);
            }
        });

        jLabel12.setText("End-to-end Latency (milliseconds):");

        jLabel27.setText("Current Workload (ds/sec):");

        txtFieldCurrentWorkload.setEditable(false);

        jLabel41.setText("Scaling Strategy:");

        comboBoxScalingStrategy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Scaling Strategy", "delta-scale optimization", "full-scale optimization" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnComputeDPPResAllocation))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(26, 26, 26))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxScalingStrategy, 0, 140, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtFieldE2eLatency, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addComponent(txtFieldFutureWorkload, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtFieldCurrentWorkload, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtFieldCurrentWorkload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldFutureWorkload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldE2eLatency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(comboBoxScalingStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(btnComputeDPPResAllocation)
                .addContainerGap())
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel13.setText("Ingestion Layer Resources:");

        jLabel14.setText("Processing Layer Resources:");

        jLabel15.setText("Storage Layer Resources:");

        txtAreaIngestionResources.setColumns(20);
        txtAreaIngestionResources.setRows(5);
        jScrollPane5.setViewportView(txtAreaIngestionResources);

        txtAreaProcessingResources.setColumns(20);
        txtAreaProcessingResources.setRows(5);
        jScrollPane6.setViewportView(txtAreaProcessingResources);

        txtAreaStorageResources.setColumns(20);
        txtAreaStorageResources.setRows(5);
        jScrollPane7.setViewportView(txtAreaStorageResources);

        btnScaleDppResources.setText("Scale DPP Resources");
        btnScaleDppResources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScaleDppResourcesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnScaleDppResources, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(progressBarDppScaling, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel13)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jLabel14)
                        .addGap(73, 73, 73)
                        .addComponent(jLabel15))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBarDppScaling, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScaleDppResources))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblErrorMsgCompResAllocation.setForeground(new java.awt.Color(255, 51, 51));
        lblErrorMsgCompResAllocation.setAutoscrolls(true);

        btnClearAllDppLayers.setText("Clear All");
        btnClearAllDppLayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllDppLayersActionPerformed(evt);
            }
        });

        lblTotalCost.setText("Total cost($/hr):");

        lblE2eQoS.setText("End-to-end Latency(ms):");

        javax.swing.GroupLayout panDPPLayersLayout = new javax.swing.GroupLayout(panDPPLayers);
        panDPPLayers.setLayout(panDPPLayersLayout);
        panDPPLayersLayout.setHorizontalGroup(
            panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDPPLayersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIngestion, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearAllDppLayers, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblE2eQoS, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDPPLayersLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 738, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblErrorMsgCompResAllocation, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panDPPLayersLayout.createSequentialGroup()
                        .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panDPPLayersLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnProcessing, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panDPPLayersLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDPPLayersLayout.createSequentialGroup()
                                .addComponent(btnStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panDPPLayersLayout.setVerticalGroup(
            panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDPPLayersLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIngestion)
                    .addComponent(btnStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProcessing))
                .addGap(18, 18, 18)
                .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panDPPLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panDPPLayersLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearAllDppLayers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalCost)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblE2eQoS)
                        .addGap(43, 43, 43))
                    .addGroup(panDPPLayersLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblErrorMsgCompResAllocation, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dppLayersTab.addTab("DPP Resource Allocation Details & Scaling ", panDPPLayers);

        lblInstanceStopMsg.setForeground(new java.awt.Color(255, 0, 51));

        lblErrDnsName.setForeground(new java.awt.Color(255, 0, 51));

        lblStartedInstance.setForeground(new java.awt.Color(51, 153, 0));
        lblStartedInstance.setToolTipText("");

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 153)));

        btnBuildIngestionCluster.setText("Build Cluster");
        btnBuildIngestionCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildIngestionClusterActionPerformed(evt);
            }
        });

        comboBoxInstanceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose an instance type", "t2.micro", "t2.small", "t2.medium" }));

        comboBoxBrokersNo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select the number of broker nodes", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        comboBoxIngestionServices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose the messaging framework", "Kafka" }));
        comboBoxIngestionServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIngestionServicesActionPerformed(evt);
            }
        });

        jLabel1.setText("Messaging Framework:");

        jLabel2.setText("No of Brokers:");

        jLabel3.setText("Instance Type:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129)
                        .addComponent(btnBuildIngestionCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxInstanceType, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(36, 36, 36)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(comboBoxBrokersNo, 0, 254, Short.MAX_VALUE)
                                .addComponent(comboBoxIngestionServices, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxIngestionServices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxBrokersNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxInstanceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuildIngestionCluster))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 102)));

        jLabel19.setText("Service:");

        jLabel21.setText("Instance Type:");

        txtFieldInstId.setEditable(false);
        txtFieldInstId.setText("Zookeeper");
        txtFieldInstId.setName(""); // NOI18N

        btnBuildZkServer.setText("Build Zookeeper Service");
        btnBuildZkServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildZkServerActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102)));

        txtFieldPartitions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldPartitionsActionPerformed(evt);
            }
        });

        jLabel18.setText("Topic Partitions:");

        jLabel20.setText("Replication Factor:");

        btnStartKafkaCluster.setText("Configure & Create Topic");
        btnStartKafkaCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartKafkaClusterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFieldPartitions, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldReplication, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStartKafkaCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldPartitions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldReplication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addComponent(btnStartKafkaCluster)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        comboBoxZkInstType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose an instance type", "t2.micro", "t2.small", "t2.medium", "a1.large" }));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuildZkServer, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(comboBoxZkInstType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFieldInstId, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtFieldInstId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21)
                    .addComponent(comboBoxZkInstType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuildZkServer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        txtAreaClusterInfo.setColumns(20);
        txtAreaClusterInfo.setLineWrap(true);
        txtAreaClusterInfo.setRows(5);
        txtAreaClusterInfo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtAreaClusterInfo);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBarIngestion, javax.swing.GroupLayout.PREFERRED_SIZE, 1118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 667, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(progressBarIngestion, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 0, 102)));

        jLabel16.setText("Instance Id:");

        btnStopInstance.setText("Stop");
        btnStopInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopInstanceActionPerformed(evt);
            }
        });

        btnStartInstance.setText("Restart");
        btnStartInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartInstanceActionPerformed(evt);
            }
        });

        btnLoadIngestionClusterInfo.setText("Load Current Cluster Details");
        btnLoadIngestionClusterInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadIngestionClusterInfoActionPerformed(evt);
            }
        });

        btnClearIngestionFormData.setText("Clear All");
        btnClearIngestionFormData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearIngestionFormDataActionPerformed(evt);
            }
        });

        btnDeleteIngestionCluster.setText("Delete Cluster");
        btnDeleteIngestionCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteIngestionClusterActionPerformed(evt);
            }
        });

        chkBoxZookeeperNode.setText("Zookeeper Node");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFieldInstanceId, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBoxZookeeperNode, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(299, 299, 299)
                        .addComponent(btnDeleteIngestionCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(btnStopInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStartInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnLoadIngestionClusterInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClearIngestionFormData, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtFieldInstanceId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStopInstance)
                    .addComponent(btnLoadIngestionClusterInfo)
                    .addComponent(btnClearIngestionFormData)
                    .addComponent(btnStartInstance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteIngestionCluster)
                    .addComponent(chkBoxZookeeperNode))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panIngestionLayout = new javax.swing.GroupLayout(panIngestion);
        panIngestion.setLayout(panIngestionLayout);
        panIngestionLayout.setHorizontalGroup(
            panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panIngestionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblErrDnsName, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(313, 313, 313))
            .addGroup(panIngestionLayout.createSequentialGroup()
                .addGroup(panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStartedInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addGap(1303, 1303, 1303)
                        .addComponent(lblStopInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panIngestionLayout.createSequentialGroup()
                .addGroup(panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addComponent(lblInstanceStopMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89)
                        .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addGap(505, 505, 505)
                        .addComponent(lblClusterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addGap(782, 782, 782)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1293, 1293, 1293))))
            .addGroup(panIngestionLayout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panIngestionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panIngestionLayout.setVerticalGroup(
            panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panIngestionLayout.createSequentialGroup()
                .addGroup(panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panIngestionLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(panIngestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panIngestionLayout.createSequentialGroup()
                                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panIngestionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(160, 160, 160)))
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblClusterStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblErrDnsName)
                .addGap(13, 13, 13)
                .addComponent(lblStopInstance)
                .addGap(114, 114, 114)
                .addComponent(lblInstanceStopMsg)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStartedInstance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dppLayersTab.addTab("Ingestion Layer", panIngestion);

        txtAreaCassandraResourcesInfo.setColumns(20);
        txtAreaCassandraResourcesInfo.setRows(5);
        jScrollPane8.setViewportView(txtAreaCassandraResourcesInfo);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 51)));

        jLabel23.setText("DNS Name:");

        btnConfigureStorageNode.setText("Configure Cluster Node");
        btnConfigureStorageNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigureStorageNodeActionPerformed(evt);
            }
        });

        rdBtnNewNode.setSelected(true);
        rdBtnNewNode.setText("Non-Seed Node");

        jLabel24.setText("Seed IP:");

        jLabel25.setText("Instance Id:");

        lblNonSeedMsg.setForeground(new java.awt.Color(204, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMissingDnsNameStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtFieldInstIdConfigure, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                            .addComponent(rdBtnNewNode)
                            .addGap(18, 18, 18)
                            .addComponent(lblNonSeedMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(txtFieldDnsNameStorage, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnConfigureStorageNode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                        .addComponent(txtFieldSeedIp, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtFieldInstIdConfigure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldDnsNameStorage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldSeedIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdBtnNewNode)
                    .addComponent(lblNonSeedMsg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfigureStorageNode)
                .addGap(7, 7, 7)
                .addComponent(lblMissingDnsNameStorage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));

        btnClearAllStorage.setText("Clear All");
        btnClearAllStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllStorageActionPerformed(evt);
            }
        });

        btnLoadStorageClusterDetails.setText("Load Cluster Details");
        btnLoadStorageClusterDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadStorageClusterDetailsActionPerformed(evt);
            }
        });

        btnDeleteStorageCluster.setText("Delete Cluster");
        btnDeleteStorageCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteStorageClusterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblInstanceStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnClearAllStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoadStorageClusterDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteStorageCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInstanceStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearAllStorage)
                    .addComponent(btnLoadStorageClusterDetails)
                    .addComponent(btnDeleteStorageCluster))
                .addGap(30, 30, 30))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 102)));

        jLabel22.setText("Instance Id:");

        btnInstanceStorageStart.setText("Restart");
        btnInstanceStorageStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInstanceStorageStartActionPerformed(evt);
            }
        });

        btnStopInstanceStorage.setText("Stop");
        btnStopInstanceStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopInstanceStorageActionPerformed(evt);
            }
        });

        jLabel26.setText("Dns Name:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnInstanceStorageStart, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(btnStopInstanceStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFieldStorageInstId)
                            .addComponent(txtFieldDnsName))
                        .addGap(10, 10, 10))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldStorageInstId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtFieldDnsName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStopInstanceStorage)
                    .addComponent(btnInstanceStorageStart))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 51)));

        jLabel8.setText("Storage service [NoSQL DB]:");

        jLabel10.setText("Instance Type:");

        jLabel9.setText("No of Nodes:");

        lblBuildClusterstatus.setText(":");

        comboBoxNoSQLDb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "Cassandra", " " }));

        comboBoxNoNodes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        comboBoxDbInstType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "t2.micro", "t2.small", "t2.medium" }));

        btnBuildStorageCluster.setText("Build Cluster");
        btnBuildStorageCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildStorageClusterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBuildStorageCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboBoxNoSQLDb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxNoNodes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxDbInstType, 0, 270, Short.MAX_VALUE))))
                .addGap(33, 33, 33))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblBuildClusterstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 162, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(comboBoxNoSQLDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxNoNodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(comboBoxDbInstType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnBuildStorageCluster)
                .addGap(18, 18, 18)
                .addComponent(lblBuildClusterstatus)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panStorageLayout = new javax.swing.GroupLayout(panStorage);
        panStorage.setLayout(panStorageLayout);
        panStorageLayout.setHorizontalGroup(
            panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panStorageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panStorageLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 1064, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(progressBarStorage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panStorageLayout.createSequentialGroup()
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(47, 47, 47))
        );
        panStorageLayout.setVerticalGroup(
            panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panStorageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBarStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panStorageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        dppLayersTab.addTab("Storage Layer", panStorage);

        txtAreaSparkResourcesInfo.setColumns(20);
        txtAreaSparkResourcesInfo.setRows(5);
        jScrollPane9.setViewportView(txtAreaSparkResourcesInfo);

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setPreferredSize(new java.awt.Dimension(1000, 91));

        jLabel5.setText("Instance Id:");

        btnStopInstanceProc.setText("Stop");
        btnStopInstanceProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopInstanceProcActionPerformed(evt);
            }
        });

        btnRestartInstanceProc.setText("Restart");
        btnRestartInstanceProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartInstanceProcActionPerformed(evt);
            }
        });

        btnClearFieldsProcessing.setText("Clear All");
        btnClearFieldsProcessing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFieldsProcessingActionPerformed(evt);
            }
        });

        btnLoadProcessingDetails.setText("Load Current Cluster Details");
        btnLoadProcessingDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadProcessingDetailsActionPerformed(evt);
            }
        });

        btnDeleteCluster.setText("Delete Cluster");
        btnDeleteCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteClusterActionPerformed(evt);
            }
        });

        chkBoxMasterNode.setText("Master Node");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStopRestartStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkBoxMasterNode, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .addComponent(txtFieldStartRestartInstId))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStopInstanceProc, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRestartInstanceProc)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(155, 155, 155)
                                .addComponent(btnDeleteCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(btnClearFieldsProcessing, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLoadProcessingDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearFieldsProcessing)
                    .addComponent(btnLoadProcessingDetails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeleteCluster)
                .addGap(39, 39, 39)
                .addComponent(lblStopRestartStatus)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldStartRestartInstId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btnStopInstanceProc)
                    .addComponent(btnRestartInstanceProc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkBoxMasterNode)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)));

        jLabel28.setText("Instance Id:");

        jLabel30.setText("Dns Name:");

        jLabel29.setText("Node Type:");

        btnConfigureSparkNode.setText("Configure Spark Node");
        btnConfigureSparkNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigureSparkNodeActionPerformed(evt);
            }
        });

        comboBoxNodeType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "local", "worker" }));

        btnSparkSubmitApp.setText("Submit Job to Cluster");
        btnSparkSubmitApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSparkSubmitAppActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFieldDnsNameConfigure, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldInstanceIdConfigure, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSparkSubmitApp, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(comboBoxNodeType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConfigureSparkNode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblConfigureSparkNode, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtFieldInstanceIdConfigure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldDnsNameConfigure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(comboBoxNodeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConfigureSparkNode)
                    .addComponent(btnConfigureSparkNode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSparkSubmitApp)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setName(""); // NOI18N

        btnBuildProcessingCluster.setText("Build Cluster");
        btnBuildProcessingCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildProcessingClusterActionPerformed(evt);
            }
        });

        comboBoxNoSparkNodes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        comboBoxProcFrameworks.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "Spark" }));

        comboBoxSparkInstType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "t2.micro", "t2.small", "t2.medium", " " }));

        jLabel4.setText("Processing Framework:");

        jLabel7.setText("Instance Type:");

        jLabel6.setText("No of Nodes:");

        jLabel31.setText("Instance Type:");

        comboBoxMasterNodeInstType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "t2.micro", "t2.small", "t2.medium" }));

        btnCreateMasterNode.setText("Build Master Node");
        btnCreateMasterNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateMasterNodeActionPerformed(evt);
            }
        });

        jLabel39.setText("Cluster Type:");

        comboBoxClusterType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "Single-node", "Multi-node" }));
        comboBoxClusterType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxClusterTypeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btnBuildProcessingCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(144, 144, 144)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboBoxMasterNodeInstType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnCreateMasterNode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(comboBoxClusterType, 0, 204, Short.MAX_VALUE)
                                    .addComponent(comboBoxNoSparkNodes, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxSparkInstType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxProcFrameworks, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 284, Short.MAX_VALUE))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lblBuildProcessingCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxProcFrameworks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel31)
                    .addComponent(comboBoxMasterNodeInstType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxSparkInstType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(comboBoxClusterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(btnCreateMasterNode)))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxNoSparkNodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuildProcessingCluster)
                .addGap(26, 26, 26)
                .addComponent(lblBuildProcessingCluster))
        );

        jPanel11.add(txtFieldInstanceIdConfigure1);
        jPanel11.add(txtFieldDnsNameConfigure1);
        jPanel11.add(txtFieldInstTypeConfigure1);

        jLabel32.setText("Instance Id:");
        jPanel11.add(jLabel32);

        jLabel33.setText("Dns Name:");
        jPanel11.add(jLabel33);

        jLabel34.setText("Instance Type:");
        jPanel11.add(jLabel34);

        btnConfigureSparkNode1.setText("Configure Spark Node");
        btnConfigureSparkNode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigureSparkNodeActionPerformed(evt);
            }
        });
        jPanel11.add(btnConfigureSparkNode1);
        jPanel11.add(lblConfigureSparkNode1);

        btnBuildProcessingCluster1.setText("Build Cluster");
        btnBuildProcessingCluster1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildProcessingClusterActionPerformed(evt);
            }
        });
        jPanel12.add(btnBuildProcessingCluster1);

        comboBoxNoSparkNodes1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jPanel12.add(comboBoxNoSparkNodes1);

        comboBoxProcFrameworks1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "Spark" }));
        jPanel12.add(comboBoxProcFrameworks1);

        comboBoxSparkInstType1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--select--", "t2.micro", "t2.small", "t2.medium", " " }));
        jPanel12.add(comboBoxSparkInstType1);

        jLabel35.setText("Processing Framework:");
        jPanel12.add(jLabel35);

        jLabel36.setText("Instance Type:");
        jPanel12.add(jLabel36);

        jLabel37.setText("No of Nodes:");
        jPanel12.add(jLabel37);

        javax.swing.GroupLayout panProcessingLayout = new javax.swing.GroupLayout(panProcessing);
        panProcessing.setLayout(panProcessingLayout);
        panProcessingLayout.setHorizontalGroup(
            panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panProcessingLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panProcessingLayout.createSequentialGroup()
                        .addGroup(panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1092, Short.MAX_VALUE)
                                .addComponent(progressBarProcessing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panProcessingLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
        );
        panProcessingLayout.setVerticalGroup(
            panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProcessingLayout.createSequentialGroup()
                .addGroup(panProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBarProcessing, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(395, 395, 395))
        );

        dppLayersTab.addTab("Processing Layer", panProcessing);

        tblSustainableQoSProfile.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Service Name", "Instance Type", "Sustainable Workload(X1000/sec)", "Sustainable Latency(ms)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane10.setViewportView(tblSustainableQoSProfile);

        btnViewQoSProfile.setText("View QoS Profile");
        btnViewQoSProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewQoSProfileActionPerformed(evt);
            }
        });

        btnUpdateQoSProfile.setText("Update QoS Profile");

        javax.swing.GroupLayout panQoSProfileLayout = new javax.swing.GroupLayout(panQoSProfile);
        panQoSProfile.setLayout(panQoSProfileLayout);
        panQoSProfileLayout.setHorizontalGroup(
            panQoSProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panQoSProfileLayout.createSequentialGroup()
                .addGroup(panQoSProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE)
                    .addGroup(panQoSProfileLayout.createSequentialGroup()
                        .addComponent(btnViewQoSProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdateQoSProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panQoSProfileLayout.setVerticalGroup(
            panQoSProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panQoSProfileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panQoSProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panQoSProfileLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnViewQoSProfile))
                    .addGroup(panQoSProfileLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateQoSProfile)))
                .addContainerGap(326, Short.MAX_VALUE))
        );

        dppLayersTab.addTab("View QoS Profile", panQoSProfile);

        jMenuAboutApp.setText("Help");
        jMenuAboutApp.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                jMenuAboutAppMenuKeyPressed(evt);
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });
        jMenuAboutApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutAppActionPerformed(evt);
            }
        });

        menuItemUsage.setText("How to Use");
        menuItemUsage.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                menuItemUsageMenuKeyPressed(evt);
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });
        menuItemUsage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemUsageActionPerformed(evt);
            }
        });
        jMenuAboutApp.add(menuItemUsage);

        menuItemAbout.setText("About");
        jMenuAboutApp.add(menuItemAbout);

        jMenuBar1.add(jMenuAboutApp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dppLayersTab, javax.swing.GroupLayout.PREFERRED_SIZE, 1126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dppLayersTab, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        dppLayersTab.getAccessibleContext().setAccessibleName("dppLayers");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public boolean isBuild = false;
    private void btnBuildIngestionClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildIngestionClusterActionPerformed
        isBuild = true;
        progressBarIngestion.setIndeterminate(true);
        btnBuildIngestionCluster.setEnabled(false);
        //btnDeleteIngestionCluster.setEnabled(false);
        txtAreaClusterInfo.setText("");
        if (Integer.parseInt(String.valueOf(comboBoxBrokersNo.getSelectedItem())) <= 5) {
            String no = String.valueOf(comboBoxBrokersNo.getSelectedItem());
            int noOfBrokers = Integer.parseInt(no);
            String instType = String.valueOf(comboBoxInstanceType.getSelectedItem());
            try {
                ConfigureIngestionLayer.buildIngestionLayerCluster(noOfBrokers, instType);
            } catch (Exception ex) {
                System.out.printf("Error in instance creation " + ex.getMessage());
            } finally {
                btnBuildIngestionCluster.setEnabled(true);
                progressBarIngestion.setIndeterminate(false);
                progressBarIngestion.setValue(100);
            }
        } else {

        }
    }//GEN-LAST:event_btnBuildIngestionClusterActionPerformed
    /**
     * Function to update/write stack/cluster build status for Kafka cluster
     *
     * @param info
     */
    public void updateClusterBuildStatus(String info) {
        txtAreaClusterInfo.append(info);
    }
    int maxWorkloadCapacity = 10;
    int maxSusLatency = 25;
    int currentIncomingWorkload = 8;
    int currentSusLatency = 22;

    /**
     *
     * @param evt
     */
    private void btnIngestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngestionActionPerformed
        try {
            txtAreaIngestionDetails.setText("");
            txtAreaIngestionDetails.setText("Current Allocation:");
            txtAreaIngestionDetails.append("\n");
            txtAreaIngestionDetails.append("---------------------------\n");
            try (ResultSet rss = ConfigureIngestionLayer.loadIngestionClusterCapacityDetails()) {
                while (rss.next()) {
                    int clusterID = rss.getInt("cluster_id");
                    int noOfNodes = rss.getInt("no_of_nodes");
                    String clusterComposition = rss.getString("instance_type");
                    int replicationFactor = rss.getInt("replication_factor");
                    int partitionsCount = rss.getInt("partitions_count");
                    String topicName = rss.getString("topic_name");
                    String zkDnsName = rss.getString("zk_dnsname");
                    int throughput = rss.getInt("throughput");
                    int latency = rss.getInt("latency");
                    String dataIngestionRate = rss.getString("data_ingestion_rate");
                    txtFieldCurrentWorkload.setText(dataIngestionRate);
                    //System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, status, brokerId);
                    txtAreaIngestionDetails.append("No of Nodes: " + Integer.toString(noOfNodes) + "\n");
                    txtAreaIngestionDetails.append("Cluster Resources: " + clusterComposition + "\n");
                    txtAreaIngestionDetails.append("Replication factor: " + Integer.toString(replicationFactor) + "\n");
                    txtAreaIngestionDetails.append("Topic partitions: " + Integer.toString(partitionsCount) + "\n");
                    txtAreaIngestionDetails.append("Throughput: " + Integer.toString(throughput) + "\n");
                    txtAreaIngestionDetails.append("Latency: " + Integer.toString(latency) + "\n");
                    txtAreaIngestionDetails.append("Data Ingestion Rate: " + dataIngestionRate + "\n");
                    txtAreaIngestionDetails.append("-----------------------------------------------------------\n");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnIngestionActionPerformed
    /**
     * *
     * @param evt
     */
    private void btnComputeDPPResAllocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComputeDPPResAllocationActionPerformed

        if ("".equals(txtFieldFutureWorkload.getText().trim()) && "".equals(txtFieldE2eLatency.getText().trim())) {
            lblErrorMsgCompResAllocation.setText("Enter the values for predicted workload and end-to-end latency.");
        } else if (txtFieldCurrentWorkload.getText().trim().equals(txtFieldFutureWorkload.getText().trim())) {
            lblErrorMsgCompResAllocation.setText("NO computation for resource allocation is required for same value of current and future workload!");
        } else {
            //call resource optimizer algorithm -
            Boolean foundAllocation;
            clearFields();
            btnScaleDppResources.setEnabled(true);
            int currentWorkload = Integer.parseInt(txtFieldCurrentWorkload.getText().trim());
            int futureWorkload = Integer.parseInt(txtFieldFutureWorkload.getText().trim());
            int deltaWorkload;

            if (currentWorkload > 0) {
                //txtFieldCurrentWorkload.setText(currentDIR);
                //currentWorkload = Integer.parseInt(currentDIR);
                isInitialDeployment = false;
                if (comboBoxScalingStrategy.getSelectedIndex() == 1) {
                    isDeltaScaleStrategy = true;
                    deltaWorkload = Math.abs(futureWorkload - currentWorkload);
                    foundAllocation = ResourceOptimizer.getResourceAllocation(deltaWorkload, deltaWorkload, deltaWorkload, Integer.parseInt(txtFieldE2eLatency.getText().trim()));
                    if (!foundAllocation) {
                        txtAreaIngestionResources.setText("---No resource allocation found using the candidate resources---");
                        txtAreaProcessingResources.setText("---No resource allocation found using the candidate resources---");
                        txtAreaStorageResources.setText("---No resource allocation found using the candidate resources---");
                        btnScaleDppResources.setEnabled(false);
                    }
                } else {
                    foundAllocation = ResourceOptimizer.getResourceAllocation(futureWorkload, futureWorkload, futureWorkload, Integer.parseInt(txtFieldE2eLatency.getText().trim()));
                    isDeltaScaleStrategy = false;
                    if (!foundAllocation) {
                        txtAreaIngestionResources.setText("---No resource allocation found using the candidate resources---");
                        txtAreaProcessingResources.setText("---No resource allocation found using the candidate resources---");
                        txtAreaStorageResources.setText("---No resource allocation found using the candidate resources---");
                        btnScaleDppResources.setEnabled(false);
                    }
                }
            }
            if (currentWorkload == 0) {
                foundAllocation = ResourceOptimizer.getResourceAllocation(futureWorkload, futureWorkload, futureWorkload, Integer.parseInt(txtFieldE2eLatency.getText().trim()));
                isInitialDeployment = true;
                if (!foundAllocation) {
                    txtAreaIngestionResources.setText("---No resource allocation found using the candidate resources---");
                    txtAreaProcessingResources.setText("---No resource allocation found using the candidate resources---");
                    txtAreaStorageResources.setText("---No resource allocation found using the candidate resources---");
                    btnScaleDppResources.setEnabled(false);

                }
            }
        }
    }//GEN-LAST:event_btnComputeDPPResAllocationActionPerformed

    public void computeE2eDppResourceAllocation() {
        //call external api to compute e2e resource allocation based on future workload, current workload and e2e latency.
    }
    private void comboBoxIngestionServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxIngestionServicesActionPerformed
        // TODO add your handling code here:
        btnBuildIngestionCluster.setText("Build " + String.valueOf(comboBoxIngestionServices.getSelectedItem()) + " Cluster");
    }//GEN-LAST:event_comboBoxIngestionServicesActionPerformed

    private void btnStopInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopInstanceActionPerformed
        Boolean isZookeeperNode = chkBoxZookeeperNode.isSelected();
        try {
            ConfigureIngestionLayer.stopKafkaBrokerNode(txtFieldInstanceId.getText().trim(), isZookeeperNode);

        } catch (InterruptedException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnStopInstanceActionPerformed

    private void btnDeleteIngestionClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteIngestionClusterActionPerformed
        // TODO add your handling code here:
        isBuild = false;
        int option = JOptionPane.showConfirmDialog(null, "Are you sure to delete the cluster?", "Delete", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            ConfigureIngestionLayer.deleteIngestionCluster();
        }
    }//GEN-LAST:event_btnDeleteIngestionClusterActionPerformed

    private void btnClearIngestionFormDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearIngestionFormDataActionPerformed
        clearIngestionTabControls();
    }//GEN-LAST:event_btnClearIngestionFormDataActionPerformed
    public void clearIngestionTabControls() {
        comboBoxIngestionServices.setSelectedIndex(0);
        comboBoxBrokersNo.setSelectedIndex(0);
        comboBoxInstanceType.setSelectedIndex(0);
        txtAreaClusterInfo.setText(null);
        btnBuildIngestionCluster.setText("Build Cluster");
        txtFieldInstanceId.setText("");
        //txtFieldInstId.setText("");
        txtFieldPartitions.setText("");
        txtFieldReplication.setText("");
        lblErrDnsName.setText("");
        lblStartedInstance.setText("");
        lblStopInstance.setText("");
        lblClusterStatus.setText("");
    }

    /**
     * Loads existing cluster details for Ingestion layer if exists.
     *
     * @param evt
     */
    private void btnLoadIngestionClusterInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadIngestionClusterInfoActionPerformed

        try {
            txtAreaClusterInfo.setText("");
            txtAreaClusterInfo.append("");
            try (ResultSet rs = ConfigureIngestionLayer.loadCurrentClusterDetails()) {
                while (rs.next()) {
                    String instanceId = rs.getString("instance_id");
                    String instanceType = rs.getString("instance_type");
                    String az = rs.getString("availability_zone");
                    String publicDnsName = rs.getString("public_dnsname");
                    String publicIp = rs.getString("public_ip");
                    String status = rs.getString("status");
                    String brokerId = rs.getString("broker_id");
                    //System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, status, brokerId);
                    txtAreaClusterInfo.append("InstanceID: " + instanceId + ", InstanceType: " + instanceType + ", AvailabilityZone: " + az + ", PublicDns: " + publicDnsName + ", PublicIp: " + publicIp + ", Status: " + status + ", BrokerId: " + brokerId + ".\n");
                    txtAreaClusterInfo.append("-----------------------------------------------------------\n");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLoadIngestionClusterInfoActionPerformed

    private void btnBuildProcessingClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildProcessingClusterActionPerformed

        lblBuildProcessingCluster.setText("Start creating resources for Spark cluster...");
        btnBuildProcessingCluster.setEnabled(false);
        //function call to build spark cluster ... ConfigureProcessingLayer.createEc2Instances();
        if ((comboBoxNoSparkNodes.getSelectedIndex() != 0) && (comboBoxSparkInstType.getSelectedIndex() != 0)) {
            try {
                ConfigureProcessingLayer.buildProcessingLayerCluster(Integer.parseInt(String.valueOf(comboBoxNoSparkNodes.getSelectedItem())), String.valueOf(comboBoxSparkInstType.getSelectedItem()), "", String.valueOf(comboBoxClusterType.getSelectedItem()), false);
            } catch (NumberFormatException ex) {
                lblBuildProcessingCluster.setText(ex.getMessage());
            }
        } else {
            lblBuildProcessingCluster.setText("Please select the values for no of nodes and instance type.");
        }
        lblBuildProcessingCluster.setText("Instances for Spark service cluster are created successfully.");
        btnBuildProcessingCluster.setEnabled(true);
        comboBoxNoSparkNodes.setEnabled(true);
    }//GEN-LAST:event_btnBuildProcessingClusterActionPerformed

    private void btnBuildStorageClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildStorageClusterActionPerformed
        lblBuildClusterstatus.setText("Start creating resources for Cassandra cluster...");
        if ((comboBoxNoSQLDb.getSelectedIndex() != 0) && (comboBoxNoNodes.getSelectedIndex() != 0) && (comboBoxDbInstType.getSelectedIndex() != 0)) {
            try {
                ConfigureStorageLayer.buildNoSqlStorageCluster(Integer.parseInt(String.valueOf(comboBoxNoNodes.getSelectedItem())), String.valueOf(comboBoxDbInstType.getSelectedItem()), false);
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lblBuildClusterstatus.setText("");
        lblBuildClusterstatus.setText("Instances for Cassandra cluster created successfully!");
    }//GEN-LAST:event_btnBuildStorageClusterActionPerformed

    private void btnStartInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartInstanceActionPerformed
        try {
            String instId = txtFieldInstanceId.getText().trim();
            if (!"".equals(instId)) {
                ConfigureIngestionLayer.restartKafkaBrokerNode(instId, chkBoxZookeeperNode.isSelected());
            } else {
                lblStartedInstance.setText("Enter the valid Instance ID.");
                txtFieldInstanceId.setText("");
            }
        } catch (InterruptedException ex) {
            System.out.printf("Error in instance creation " + ex.getMessage());
        }
    }//GEN-LAST:event_btnStartInstanceActionPerformed

    /**
     * Button action to perform broker node configuration
     *
     * @param evt
     */
    private void btnBuildZkServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildZkServerActionPerformed
        // TODO add your handling code here:
        if (comboBoxZkInstType.getSelectedIndex() > 0) {
            String instanceType = comboBoxZkInstType.getSelectedItem().toString();
            if (!"".equals(instanceType)) {
                try {
                    ConfigureIngestionLayer.createZkServer(instanceType);
                } catch (Exception ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                lblErrDnsName.setText("Please enter valid pubilc DNS name of the running instance.");
            }
        }


    }//GEN-LAST:event_btnBuildZkServerActionPerformed

    private void txtFieldPartitionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldPartitionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldPartitionsActionPerformed

    private void btnInstanceStorageStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInstanceStorageStartActionPerformed
        if (!"".equals(txtFieldStorageInstId.getText().trim())) {
            ConfigureStorageLayer.restartInstanceStorageLayer(txtFieldStorageInstId.getText().trim());
        }

    }//GEN-LAST:event_btnInstanceStorageStartActionPerformed

    private void btnStopInstanceStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopInstanceStorageActionPerformed
        if (!"".equals(txtFieldStorageInstId.getText().trim()) && !"".equals(txtFieldStorageInstId.getText().trim())) {
            try {
                ConfigureStorageLayer.stopInstanceStorageLayer(txtFieldStorageInstId.getText().trim(), txtFieldDnsName.getText().trim());
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnStopInstanceStorageActionPerformed

    private void btnClearAllStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllStorageActionPerformed
        // TODO add your handling code here:
        clearStorageLayerFields();
    }//GEN-LAST:event_btnClearAllStorageActionPerformed

    private void clearStorageLayerFields() {
        txtFieldStorageInstId.setText("");
        txtFieldInstIdConfigure.setText("");
        txtFieldDnsName.setText("");
        lblBuildClusterstatus.setText(":");
        txtFieldDnsNameStorage.setText("");
        comboBoxNoSQLDb.setSelectedIndex(0);
        comboBoxNoNodes.setSelectedIndex(0);
        comboBoxDbInstType.setSelectedIndex(0);
        txtAreaCassandraResourcesInfo.setText("");
        lblInstanceStatus.setText("");
        lblNonSeedMsg.setText("");
    }
    private void btnConfigureStorageNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigureStorageNodeActionPerformed
        Boolean nonSeedNode = false;
        String InstanceId = txtFieldInstIdConfigure.getText().trim();
        String seedIp = txtFieldSeedIp.getText().trim();
        if ((!"".equals(txtFieldDnsNameStorage.getText()))|| !"".equals(InstanceId)) {
            if (rdBtnNewNode.isSelected()) {
                nonSeedNode = true;
                if ("".equals(seedIp) || seedIp == null) {
                    lblNonSeedMsg.setText("Please enter the seed node IP to configure non-seed node!");
                    return;
                }
            }
            try {
                ConfigureStorageLayer.configureNoSqlServerNode(txtFieldDnsNameStorage.getText().trim(), nonSeedNode, seedIp, InstanceId);
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            lblMissingDnsNameStorage.setText("Enter DNS name or instance id.");
        }
    }//GEN-LAST:event_btnConfigureStorageNodeActionPerformed

    private void btnLoadStorageClusterDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadStorageClusterDetailsActionPerformed
        try {
            try ( // TODO add your handling code here:
                    //ConfigureStorageLayer.loadFromFileStorageClusterDetails(false);
                    ResultSet rs = ConfigureStorageLayer.loadStorageClusterInfoFromDatabase()) {
                txtAreaCassandraResourcesInfo.setText("");
                while (rs.next()) {
                    String instanceId = rs.getString("instance_id");
                    String instanceType = rs.getString("instance_type");
                    String az = rs.getString("availability_zone");
                    String publicDnsName = rs.getString("public_dnsname");
                    String publicIp = rs.getString("public_ip");
                    String privateIp = rs.getString("private_ip");
                    String status = rs.getString("status");
                    String nodeHostId = rs.getString("node_hostId");
                    //System.out.format("%s, %s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, privateIp, status, nodeHostId);
                    txtAreaCassandraResourcesInfo.append("InstanceID: " + instanceId + ", InstanceType: " + instanceType + ", AvailabilityZone: " + az + ", PublicDns: " + publicDnsName + ", PublicIp: " + publicIp + ", PrivateIp: " + privateIp + ", Status: " + status + ", HostId: " + nodeHostId + ".\n");
                    txtAreaCassandraResourcesInfo.append("-----------------------------------------------------------------------------------\n");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnLoadStorageClusterDetailsActionPerformed

    private void btnViewQoSProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewQoSProfileActionPerformed
        //load qos profile from database;
        if(tblSustainableQoSProfile.getRowCount()>0){
            return;
        }
            
        try {
            if (DatabaseConnection.con == null) {
                try {
                    DatabaseConnection.con = DatabaseConnection.getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = "SELECT * FROM sustainable_qos_profile";
            try (Statement st = DatabaseConnection.con.createStatement()) {
                ResultSet rs = st.executeQuery(query);
                //tblSustainableQoSProfile
                int columnCount = tblSustainableQoSProfile.getColumnCount();
                Vector<Vector<String>> data=new Vector<>();             
                Vector<String> columns = new Vector<>();
                
            for (int column = 0; column <columnCount; column++) {
                columns.add(tblSustainableQoSProfile.getColumnName(column));                
            }
                tblSustainableQoSProfile.setAutoCreateColumnsFromModel(false);
                DefaultTableModel model = new DefaultTableModel(data,columns);
                tblSustainableQoSProfile.setModel(model);                
                while (rs.next()) {
                    Vector<String> newRow = new Vector<>();
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        newRow.add(rs.getString(columnIndex));
                    }                   
                    model.addRow(newRow);                                       
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnViewQoSProfileActionPerformed

    private void btnStopInstanceProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopInstanceProcActionPerformed
        // TODO add your handling code here:
        Boolean isMasterNode = chkBoxMasterNode.isSelected();
        if (!"".equals(txtFieldStartRestartInstId.getText().trim())) {
            ConfigureProcessingLayer.stopSparkNode(txtFieldStartRestartInstId.getText().trim(), isMasterNode);
        }
    }//GEN-LAST:event_btnStopInstanceProcActionPerformed

    private void btnLoadProcessingDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadProcessingDetailsActionPerformed
        try {
            txtAreaSparkResourcesInfo.setText("");
            ResultSet rss = ConfigureProcessingLayer.loadMasterNodeDetails();
            while (rss.next()) {
                try {
                    String minstanceId = rss.getString("master_instance_id");
                    String mpublicDnsName = rss.getString("master_public_dnsname");
                    txtAreaSparkResourcesInfo.append("Master node Insatnce Id: " + minstanceId + ", master node DNS: " + mpublicDnsName + "\n");
                    txtAreaSparkResourcesInfo.append("-------------------------------------------------------------\n");
                } catch (SQLException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                try ( // TODO add your handling code here:
                        //ConfigureProcessingLayer.loadSparkClusterInfoFromFile();
                        ResultSet rs = ConfigureProcessingLayer.loadSparkClusterDetailsFromDb()) {
                    while (rs.next()) {
                        String instanceId = rs.getString("instance_id");
                        String instanceType = rs.getString("instance_type");
                        String az = rs.getString("availability_zone");
                        String publicDnsName = rs.getString("public_dnsname");
                        String publicIp = rs.getString("public_ip");
                        String privateIp = rs.getString("private_ip");
                        String status = rs.getString("status");
                        String nodeType = rs.getString("node_type");
                        //System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, privateIp, status);
                        txtAreaSparkResourcesInfo.append("InstanceID: " + instanceId + ", InstanceType: " + instanceType + ", AvailabilityZone: " + az + ", PublicDns: " + publicDnsName + ", PublicIp: " + publicIp + ", PrivateIp: " + privateIp + ", Status: " + status + ", Node type: " + nodeType + ".\n");
                        txtAreaSparkResourcesInfo.append("-----------------------------------------------------------------------\n");

                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnLoadProcessingDetailsActionPerformed

    private void btnConfigureSparkNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigureSparkNodeActionPerformed
        // TODO add your handling code here:
        if (!"".equals(txtFieldDnsNameConfigure.getText())) {
            ConfigureProcessingLayer.configureNewlyCreatedSparkNode(txtFieldDnsNameConfigure.getText().trim(), String.valueOf(comboBoxNodeType.getSelectedItem()));
        } else {
            lblConfigureSparkNode.setText("Enter all the fields value.");
        }
    }//GEN-LAST:event_btnConfigureSparkNodeActionPerformed

    private void btnDeleteClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteClusterActionPerformed
        // TODO add your handling code here:
        //Ask to make sure entire deletion of cluster for processing layer: to shut down all services and then instances together.
        int option = JOptionPane.showConfirmDialog(null, "Are you sure to delete the cluster?", "Delete", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            //delete entire cluster
            btnDeleteCluster.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Task task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
            // ConfigureProcessingLayer.deleteProcessingCluster();
        }
        //getActiveInstanceIds();
        //call stop instance method for all the instances in the cluster - strating from worker nodes to master node.

    }//GEN-LAST:event_btnDeleteClusterActionPerformed

    private void btnStartKafkaClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartKafkaClusterActionPerformed
        // TODO add your handling code here:
        if (!"".equals(txtFieldPartitions.getText().trim())) {
            ConfigureIngestionLayer.configureKafkaTopic(txtFieldPartitions.getText().trim());

        }
    }//GEN-LAST:event_btnStartKafkaClusterActionPerformed

    private void btnProcessingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessingActionPerformed
        // TODO add your handling code here:
        try {
            txtAreaProcessingDetails.setText("");
            txtAreaProcessingDetails.setText("Current Allocation:");
            txtAreaProcessingDetails.append("\n");
            txtAreaProcessingDetails.append("-----------------------------\n");
            try (ResultSet rs = ConfigureProcessingLayer.loadCurrentSparkClusterInfo()) {
                while (rs.next()) {
                    int clusterID = rs.getInt("cluster_id");
                    int noOfNodes = rs.getInt("no_of_nodes");
                    String instanceTypes = rs.getString("instance_types");
                    String masterNodeId = rs.getString("master_instance_id");
                    String masterNodeDns = rs.getString("master_public_dnsname");
                    String masterNodeIp = rs.getString("master_public_ip");
                    String masterNodePrivIp = rs.getString("master_private_ip");
                    int throughput = rs.getInt("throughput");
                    int latency = rs.getInt("latency");
                    int batchInterval = rs.getInt("batch_interval");

                    //System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, status, brokerId);
                    txtAreaProcessingDetails.append("NoOfNodes: " + Integer.toString(noOfNodes) + ".\n");
                    txtAreaProcessingDetails.append("Cluster Resources: " + instanceTypes + ".\n");
                    txtAreaProcessingDetails.append("Throughput: " + Integer.toString(throughput) + ".\n");
                    txtAreaProcessingDetails.append("Latency: " + Integer.toString(latency) + ".\n");
                    txtAreaProcessingDetails.append("Batch Interval: " + Integer.toString(batchInterval) + ".\n");
                    txtAreaProcessingDetails.append("----------------------------------------------------------\n");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnProcessingActionPerformed

    private void btnStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStorageActionPerformed
        try {
            // TODO add your handling code here:
            txtAreaStorageDetails.setText("");
            txtAreaStorageDetails.setText("Current Allocation:");
            txtAreaStorageDetails.append("\n");
            txtAreaStorageDetails.append("---------------------------\n");
            try (ResultSet rs = ConfigureStorageLayer.loadCurrentCassandraClusterInfo()) {
                while (rs.next()) {
                    int clusterID = rs.getInt("cluster_id");
                    int noOfNodes = rs.getInt("no_of_nodes");
                    String instanceTypes = rs.getString("instance_types");
                    int throughput = rs.getInt("throughput");
                    int latency = rs.getInt("latency");
                    //System.out.format("%s, %s, %s, %s, %s, %s, %s\n", instanceId, instanceType, az, publicDnsName, publicIp, status, brokerId);
                    txtAreaStorageDetails.append("No Of Nodes: " + Integer.toString(noOfNodes) + ".\n");
                    txtAreaStorageDetails.append("Cluster Resources: " + instanceTypes + ".\n");
                    txtAreaStorageDetails.append("Throughput: " + Integer.toString(throughput) + ".\n");
                    txtAreaStorageDetails.append("Latency: " + Integer.toString(latency) + ".\n");
                    txtAreaStorageDetails.append("--------------------------------------------------------\n");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnStorageActionPerformed

    private void btnRestartInstanceProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestartInstanceProcActionPerformed
        // TODO add your handling code here:
        ConfigureProcessingLayer.restartedSparkProcessingNode(txtFieldStartRestartInstId.getText().trim(), chkBoxMasterNode.isSelected());
    }//GEN-LAST:event_btnRestartInstanceProcActionPerformed

    private void btnCreateMasterNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateMasterNodeActionPerformed
        // TODO add your handling code here:
        ConfigureProcessingLayer.createMasterNode(comboBoxMasterNodeInstType.getSelectedItem().toString());
        //txtFieldMasterNodeDns.setText(ConfigureProcessingLayer.getMasterNodeDns(true));
    }//GEN-LAST:event_btnCreateMasterNodeActionPerformed

    private void btnClearFieldsProcessingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFieldsProcessingActionPerformed
        // TODO add your handling code here:
        clearFormFieldsProcessingTab();
    }//GEN-LAST:event_btnClearFieldsProcessingActionPerformed

    private void comboBoxClusterTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxClusterTypeItemStateChanged
        // TODO add your handling code here:
        if (comboBoxClusterType.getSelectedIndex() == 1) {
            comboBoxNoSparkNodes.setSelectedIndex(1);
            comboBoxNoSparkNodes.setEnabled(false);
        } else {
            comboBoxNoSparkNodes.setEnabled(true);
        }
    }//GEN-LAST:event_comboBoxClusterTypeItemStateChanged

    private void btnSparkSubmitAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSparkSubmitAppActionPerformed
        // TODO add your handling code here:
        ConfigureProcessingLayer.submitJobToSparkCluster();

    }//GEN-LAST:event_btnSparkSubmitAppActionPerformed

    private void btnClearAllDppLayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllDppLayersActionPerformed
        // TODO add your handling code here:
        clearDppLayerTabFields();
    }//GEN-LAST:event_btnClearAllDppLayersActionPerformed
    /**
     * Function to delete entire ingestion cluster resources
     *
     * @param evt
     */
    private void btnDeleteStorageClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteStorageClusterActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(null, "Are you sure to delete the cluster?", "Delete", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            ConfigureStorageLayer.deleteStorageCluster();
        }
    }//GEN-LAST:event_btnDeleteStorageClusterActionPerformed

    private void jMenuAboutAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutAppActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jMenuAboutAppActionPerformed

    private void jMenuAboutAppMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_jMenuAboutAppMenuKeyPressed
        // TODO add your handling code here:
        // new UsageGuideForm().setVisible(true);   
    }//GEN-LAST:event_jMenuAboutAppMenuKeyPressed

    private void menuItemUsageMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_menuItemUsageMenuKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_menuItemUsageMenuKeyPressed

    private void scaleDppResourcesFunction() {
        txtAreaIngestionResources.setText("");
        txtAreaProcessingResources.setText("");
        txtAreaStorageResources.setText("");
        if (isInitialDeployment) {
            txtAreaIngestionResources.append("Please allocate resources through Ingestion Layer module for initial deployment of service.\n");
            txtAreaProcessingResources.append("Please allocate resources through Processing Layer module for initial deployment of service.\n");
            txtAreaStorageResources.append("Please allocate resources through Storage Layer module for initial deployment of service.\n");

            btnScaleDppResources.setEnabled(false);
            ConfigureIngestionLayer.updateCurrentWorkload(txtFieldFutureWorkload.getText().trim());

        } else {
            int currInstanceCountIngestion = DatabaseConnection.getCurrentInstanceCount("ingestion");
            if (currInstanceCountIngestion <= 0) {
                txtAreaIngestionResources.append("No existing allocation for ingestion layer. Please allocate resources from ingestion module.\n");
                return;
            }
            String currInstanceTypeIngestion = DatabaseConnection.getCurrentInstanceType("ingestion");
            if (currInstanceTypeIngestion.equals(ResourceOptimizer.dppInstanceType[0])) {
                if ((currInstanceCountIngestion == ResourceOptimizer.dppResourcesCount[0]) && !isDeltaScaleStrategy) {
                    txtAreaIngestionResources.append("Current resource allocation for ingestion layer remains same for future predicted workload.");
                }
                if (isDeltaScaleStrategy) {
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) < Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        //scale-out        

                        txtAreaIngestionResources.append("\nScaling out resources for ingestion layer...\n");
                        ConfigureIngestionLayer.buildIngestionLayerCluster(ResourceOptimizer.dppResourcesCount[0], ResourceOptimizer.dppInstanceType[0]);
                        txtAreaIngestionResources.append("Resources scaled-out successfully for handling future workload.\n");

                    }
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) > Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        //scale-in
                        if (currInstanceCountIngestion == ResourceOptimizer.dppResourcesCount[0]) {
                            txtAreaIngestionResources.append("No scale-in is required for future workload.\n");
                        }
                        if (currInstanceCountIngestion > ResourceOptimizer.dppResourcesCount[0]) {
                            txtAreaIngestionResources.append("\nScaling in resources for ingestion layer...\n");
                            List<String> instanceIds = ConfigureIngestionLayer.getBrokerInstanceIds(String.valueOf(ResourceOptimizer.dppResourcesCount[0]));
                            if (instanceIds.size() > 0) {
                                instanceIds.forEach((instanceId) -> {
                                    try {
                                        ConfigureIngestionLayer.stopKafkaBrokerNode(instanceId, false);
                                        sleep(10000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                                        txtAreaIngestionResources.append("Failed to scale-in resources successfully...\n");
                                    }
                                });
                            }
                        }
                    }

                }
            } else {
                txtAreaIngestionResources.append("The instance type needed for scaling is different than the currently allocated instance type, therefore scaling is require to perform from ingestion layer module.\n");
                txtAreaIngestionResources.append("This incurs a disruption in ingestion service as well as processing service when workload is transferred to the new resource cluster.\n");
            }
            int currentInstanceCountStorage = DatabaseConnection.getCurrentInstanceCount("storage");
            String currInstanceTypeStorage = DatabaseConnection.getCurrentInstanceType("storage");
            if (currInstanceTypeStorage.equalsIgnoreCase(ResourceOptimizer.dppInstanceType[2])) {
                if ((currentInstanceCountStorage == ResourceOptimizer.dppResourcesCount[2]) && !isDeltaScaleStrategy) {
                    txtAreaStorageResources.append("Current resource allocation for storage layer remains same for the future predicted workload.");
                }
                if (isDeltaScaleStrategy) {
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) < Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        //scale-out 
                        txtAreaStorageResources.append("Scaling-out resources for storage layer...\n");
                        try {
                            ConfigureStorageLayer.buildNoSqlStorageCluster(ResourceOptimizer.dppResourcesCount[2], ResourceOptimizer.dppInstanceType[2], true);
                            txtAreaStorageResources.append("Resources scaled-out successfully for the storage layer.\n");
                        } catch (SQLException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                            txtAreaStorageResources.append("Fail to scale-out resourcess successfully.\n");

                        }

                    }
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) > Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        //scale-in
                        if (currentInstanceCountStorage == ResourceOptimizer.dppResourcesCount[2]) {
                            txtAreaStorageResources.append("No scale-in is required for the future workload.\n");
                        }
                        if (currentInstanceCountStorage > ResourceOptimizer.dppResourcesCount[2]) {
                            txtAreaStorageResources.append("Scaling in resources for storage cluster.\n");
                            List<String> instanceIds = ConfigureStorageLayer.getNonSeedNodesInstanceIds(String.valueOf(ResourceOptimizer.dppResourcesCount[2]));
                            List<String> dnsNames = ConfigureStorageLayer.getPubDnsName(String.valueOf(ResourceOptimizer.dppResourcesCount[2]));
                            int i = 0;
                            if (instanceIds.size() > 0) {
                                for (String instanceId : instanceIds) {
                                    try {
                                        ConfigureStorageLayer.stopInstanceStorageLayer(instanceId, dnsNames.get(i));
                                        sleep(10000);
                                    } catch (InterruptedException | IOException ex) {
                                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                                        txtAreaStorageResources.append("Fail to scale-in resources for storage layer..\n");
                                    }
                                    i++;
                                }
                            }
                        }

                    }
                }
            } else {
                txtAreaStorageResources.append("The instance type required for scaling is different from the currently allocated instance type therefore scaling is performed from the storage layer module.\n");
                txtAreaStorageResources.append("This incurs a disruption in the storage layer service as well as processing layer service while transferring workload to the new resource cluster.\n");

            }
            int currInstanceCountProcessing = DatabaseConnection.getCurrentInstanceCount("processing");
            String currInstanceTypeProcessing = DatabaseConnection.getCurrentInstanceType("processing");
            if (currInstanceTypeProcessing.equalsIgnoreCase(ResourceOptimizer.dppInstanceType[1])) {
                if ((currInstanceCountProcessing == ResourceOptimizer.dppResourcesCount[1]) && !isDeltaScaleStrategy) {
                    txtAreaProcessingResources.append("Current resource allocation for processing layer can handle the future predicted workload.\n");
                }
                if (isDeltaScaleStrategy) {
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) < Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        try {
                            //scale-out
                            txtAreaProcessingResources.append("Scaling out resources for the processing layer...\n");
                            List<String> dnsNames = ConfigureProcessingLayer.buildProcessingLayerCluster(ResourceOptimizer.dppResourcesCount[1], ResourceOptimizer.dppInstanceType[1], "", "Multi-node", true);
                            sleep(20000);
                            ConfigureProcessingLayer.updateMasterNode(true);
                            dnsNames.forEach((dnsName) -> {
                                ConfigureProcessingLayer.configureNewlyCreatedSparkNode(dnsName, "worker");
                            });

                            ConfigureProcessingLayer.submitJobToSparkCluster();
                            txtAreaProcessingResources.append("Resources scaled-out successfully for handling future workload.\n");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                            txtAreaProcessingResources.append("Fail to scale out resources successfully.\n");
                        }
                    }
                    if (Integer.parseInt(txtFieldCurrentWorkload.getText().trim()) > Integer.parseInt(txtFieldFutureWorkload.getText().trim())) {
                        //scale-in
                        if (currInstanceCountProcessing == ResourceOptimizer.dppResourcesCount[1]) {
                            txtAreaProcessingResources.append("No scale-in is required for future workload.\n");
                        }
                        if (currInstanceCountProcessing > ResourceOptimizer.dppResourcesCount[1]) {
                            txtAreaProcessingResources.append("Scaling in resources for processing layer...\n");
                            List<String> instanceIds = ConfigureProcessingLayer.getWorkerInstanceIds(String.valueOf(ResourceOptimizer.dppResourcesCount[1]));
                            if (instanceIds.size() > 0) {
                                instanceIds.forEach((instanceId) -> {
                                    try {
                                        ConfigureProcessingLayer.stopSparkNode(instanceId, false);
                                        sleep(10000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                                        txtAreaProcessingResources.append("Faile to Scale-in resources for processing layer...\n");
                                    }
                                });
                            }
                        }
                    }
                }
            } else {
                txtAreaProcessingResources.append("The required instance type for scaling is different than the currently allocated instance type therefore scaling should be performed from processing layer module.\n");
                txtAreaProcessingResources.append("This incurs temporary disruption in processing layer service due to transfer of workload to the newly build cluster.\n");
            }
            ConfigureIngestionLayer.updateCurrentWorkload(txtFieldFutureWorkload.getText().trim());
        }
    }
    private void menuItemUsageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemUsageActionPerformed
        // TODO add your handling code here:
        new UsageGuideForm().setVisible(true);
    }//GEN-LAST:event_menuItemUsageActionPerformed
    /**
     * Function to scale the DPP resources based on the scaling strategy and
     * obtained resources
     *
     * @param evt
     */
    @SuppressWarnings("SleepWhileInLoop")
    private void btnScaleDppResourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScaleDppResourcesActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(null, "Are you sure to Scale the DPP resource now?", "Confirm Action", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            progressBarDppScaling.setIndeterminate(true);
            btnScaleDppResources.setEnabled(false);
            ScaleTask task = new ScaleTask();
            task.addPropertyChangeListener(this);
            task.execute();
            // scaleDppResourcesFunction();           
        }

    }//GEN-LAST:event_btnScaleDppResourcesActionPerformed
    public void clearDppLayerTabFields() {
        txtAreaIngestionResources.setText("");
        txtAreaProcessingResources.setText("");
        txtAreaStorageResources.setText("");
        lblErrorMsgCompResAllocation.setText("");
        btnScaleDppResources.setEnabled(false);
        progressBarDppScaling.setValue(0);
        progressBarDppScaling.setIndeterminate(false);
        txtFieldE2eLatency.setText("");
        txtFieldFutureWorkload.setText("");
        lblTotalCost.setText("Total cost:");
        lblE2eQoS.setText("Total end-to-end latency:");

    }

    public void clearFields() {
        txtAreaIngestionResources.setText("");
        txtAreaProcessingResources.setText("");
        txtAreaStorageResources.setText("");
        lblErrorMsgCompResAllocation.setText("");
        btnScaleDppResources.setEnabled(false);
        progressBarDppScaling.setValue(0);
        progressBarDppScaling.setIndeterminate(false);
    }

    public void clearFormFieldsProcessingTab() {
        txtFieldStartRestartInstId.setText("");
        comboBoxProcFrameworks.setSelectedIndex(0);
        comboBoxSparkInstType.setSelectedIndex(0);
        comboBoxNoSparkNodes.setSelectedIndex(0);
        comboBoxMasterNodeInstType.setSelectedIndex(0);
        comboBoxClusterType.setSelectedIndex(0);
        txtAreaSparkResourcesInfo.setText("");
        txtFieldInstanceIdConfigure.setText("");
        txtFieldDnsNameConfigure.setText("");

        lblStopRestartStatus.setText("");
        lblBuildProcessingCluster.setText("");
        comboBoxNoSparkNodes.setEnabled(true);
        progressBarProcessing.setValue(0);
    }

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
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainForm().setVisible(true);
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuildIngestionCluster;
    private javax.swing.JButton btnBuildProcessingCluster;
    private javax.swing.JButton btnBuildProcessingCluster1;
    public static javax.swing.JButton btnBuildStorageCluster;
    private javax.swing.JButton btnBuildZkServer;
    private javax.swing.JButton btnClearAllDppLayers;
    private javax.swing.JButton btnClearAllStorage;
    private javax.swing.JButton btnClearFieldsProcessing;
    private javax.swing.JButton btnComputeDPPResAllocation;
    private javax.swing.JButton btnConfigureSparkNode;
    private javax.swing.JButton btnConfigureSparkNode1;
    private javax.swing.JButton btnConfigureStorageNode;
    private javax.swing.JButton btnCreateMasterNode;
    private javax.swing.JButton btnDeleteCluster;
    private javax.swing.JButton btnDeleteIngestionCluster;
    private javax.swing.JButton btnDeleteStorageCluster;
    private javax.swing.JButton btnIngestion;
    private javax.swing.JButton btnInstanceStorageStart;
    private javax.swing.JButton btnLoadIngestionClusterInfo;
    private javax.swing.JButton btnLoadProcessingDetails;
    private javax.swing.JButton btnLoadStorageClusterDetails;
    private javax.swing.JButton btnProcessing;
    private javax.swing.JButton btnRestartInstanceProc;
    private javax.swing.JButton btnScaleDppResources;
    private javax.swing.JButton btnSparkSubmitApp;
    private javax.swing.JButton btnStartInstance;
    private javax.swing.JButton btnStartKafkaCluster;
    private javax.swing.JButton btnStopInstance;
    private javax.swing.JButton btnStopInstanceProc;
    private javax.swing.JButton btnStopInstanceStorage;
    private javax.swing.JButton btnStorage;
    private javax.swing.JButton btnUpdateQoSProfile;
    private javax.swing.JButton btnViewQoSProfile;
    private javax.swing.JCheckBox chkBoxMasterNode;
    private javax.swing.JCheckBox chkBoxZookeeperNode;
    private javax.swing.JComboBox<String> comboBoxBrokersNo;
    private javax.swing.JComboBox<String> comboBoxClusterType;
    private javax.swing.JComboBox<String> comboBoxDbInstType;
    private javax.swing.JComboBox<String> comboBoxIngestionServices;
    private javax.swing.JComboBox<String> comboBoxInstanceType;
    private javax.swing.JComboBox<String> comboBoxMasterNodeInstType;
    private javax.swing.JComboBox<String> comboBoxNoNodes;
    private javax.swing.JComboBox<String> comboBoxNoSQLDb;
    private javax.swing.JComboBox<String> comboBoxNoSparkNodes;
    private javax.swing.JComboBox<String> comboBoxNoSparkNodes1;
    private javax.swing.JComboBox<String> comboBoxNodeType;
    private javax.swing.JComboBox<String> comboBoxProcFrameworks;
    private javax.swing.JComboBox<String> comboBoxProcFrameworks1;
    private javax.swing.JComboBox<String> comboBoxScalingStrategy;
    private javax.swing.JComboBox<String> comboBoxSparkInstType;
    private javax.swing.JComboBox<String> comboBoxSparkInstType1;
    private javax.swing.JComboBox<String> comboBoxZkInstType;
    private javax.swing.JTabbedPane dppLayersTab;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenuAboutApp;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel lblBuildClusterstatus;
    private javax.swing.JLabel lblBuildProcessingCluster;
    private javax.swing.JLabel lblBuildProcessingCluster1;
    public static javax.swing.JLabel lblClusterStatus;
    private javax.swing.JLabel lblConfigureSparkNode;
    private javax.swing.JLabel lblConfigureSparkNode1;
    public static javax.swing.JLabel lblE2eQoS;
    private javax.swing.JLabel lblErrDnsName;
    private javax.swing.JLabel lblErrorMsgCompResAllocation;
    public static javax.swing.JLabel lblInstanceStatus;
    public static javax.swing.JLabel lblInstanceStopMsg;
    private javax.swing.JLabel lblMissingDnsNameStorage;
    private javax.swing.JLabel lblNonSeedMsg;
    public static javax.swing.JLabel lblStartedInstance;
    public static javax.swing.JLabel lblStopInstance;
    public static javax.swing.JLabel lblStopRestartStatus;
    public static javax.swing.JLabel lblTotalCost;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemUsage;
    private javax.swing.JPanel panDPPLayers;
    private javax.swing.JPanel panIngestion;
    private javax.swing.JPanel panProcessing;
    private javax.swing.JPanel panQoSProfile;
    private javax.swing.JPanel panStorage;
    private javax.swing.JProgressBar progressBarDppScaling;
    private javax.swing.JProgressBar progressBarIngestion;
    private javax.swing.JProgressBar progressBarProcessing;
    public static javax.swing.JProgressBar progressBarStorage;
    private javax.swing.JRadioButton rdBtnNewNode;
    private javax.swing.JTable tblSustainableQoSProfile;
    public static javax.swing.JTextArea txtAreaCassandraResourcesInfo;
    public static javax.swing.JTextArea txtAreaClusterInfo;
    public static javax.swing.JTextArea txtAreaIngestionDetails;
    public static javax.swing.JTextArea txtAreaIngestionResources;
    private javax.swing.JTextArea txtAreaProcessingDetails;
    public static javax.swing.JTextArea txtAreaProcessingResources;
    public static javax.swing.JTextArea txtAreaSparkResourcesInfo;
    private javax.swing.JTextArea txtAreaStorageDetails;
    public static javax.swing.JTextArea txtAreaStorageResources;
    private javax.swing.JTextField txtFieldCurrentWorkload;
    private javax.swing.JTextField txtFieldDnsName;
    private javax.swing.JTextField txtFieldDnsNameConfigure;
    private javax.swing.JTextField txtFieldDnsNameConfigure1;
    private javax.swing.JTextField txtFieldDnsNameStorage;
    private javax.swing.JTextField txtFieldE2eLatency;
    private javax.swing.JTextField txtFieldFutureWorkload;
    public static javax.swing.JTextField txtFieldInstId;
    private javax.swing.JTextField txtFieldInstIdConfigure;
    private javax.swing.JTextField txtFieldInstTypeConfigure1;
    private javax.swing.JTextField txtFieldInstanceId;
    private javax.swing.JTextField txtFieldInstanceIdConfigure;
    private javax.swing.JTextField txtFieldInstanceIdConfigure1;
    private javax.swing.JTextField txtFieldPartitions;
    private javax.swing.JTextField txtFieldReplication;
    private javax.swing.JTextField txtFieldSeedIp;
    private javax.swing.JTextField txtFieldStartRestartInstId;
    private javax.swing.JTextField txtFieldStorageInstId;
    // End of variables declaration//GEN-END:variables
}
