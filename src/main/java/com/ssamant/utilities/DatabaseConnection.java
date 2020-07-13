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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunil
 */
public class DatabaseConnection {

    public DatabaseConnection() {

    }
    public static Connection con = null;

    public static Connection getConnection() throws SQLException {
        try {
            Properties prop = PropertyFileReader.readPropertyFile();
            String mysqlHost = prop.getProperty("com.ssamant.utilities.mysql.hostUrl");            
            String mysqlDb = prop.getProperty("com.ssamant.utilities.mysql.database");
            String mysqlUsr = prop.getProperty("com.ssamant.utilities.mysql.user");
            String mysqlPwd = prop.getProperty("com.ssamant.utilities.mysql.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            //con = DriverManager.getConnection("jdbc:mysql://136.186.108.219:3306/dpp_resources", "root", "dpp2020*");
            con = DriverManager.getConnection(mysqlHost+"/"+mysqlDb, mysqlUsr, mysqlPwd);
            if (con != null) {
                System.out.println("Database connection successful.");
            } else {
                System.out.println("Failed to connect to database.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("MySQL connection failed.");
        } catch (Exception ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;

    }

    public static String getServiceAmi(String serviceName) {
        String ami_id = "";
        try {
            if (con == null) {
                try {
                    con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String query = "SELECT ami_id FROM dpp_resources.ami_info WHERE service_name = ? limit 1";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, serviceName);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {

                    ami_id = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ami_id;
    }

    public static int getCurrentInstanceCount(String serviceName) {
        int instanceCount = 0;
        try {
            if (con == null) {
                try {
                    con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = null;
            if ("ingestion".equals(serviceName)) {
                query = "SELECT COUNT(*) FROM dpp_resources.ingestion_nodes_info WHERE status = ?";
            }
            if ("processing".equals(serviceName)) {
                query = "SELECT COUNT(*) FROM dpp_resources.processing_nodes_info WHERE status = ?";
            }
            if ("storage".equals(serviceName)) {
                query = "SELECT COUNT(*) FROM dpp_resources.storage_nodes_info WHERE status = ?";
            }
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, "running");
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {

                    instanceCount = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instanceCount;
    }

    public static String getCurrentInstanceType(String serviceName) {
        String instanceType = "";
        try {
            if (con == null) {
                try {
                    con = getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String query = null;
            if ("ingestion".equals(serviceName)) {
                query = "SELECT instance_type FROM dpp_resources.ingestion_nodes_info WHERE status = ? LIMIT 1";
            }
            if ("processing".equals(serviceName)) {
                query = "SELECT instance_type FROM dpp_resources.processing_nodes_info WHERE status = ? LIMIT 1";
            }
            if ("storage".equals(serviceName)) {
                query = "SELECT instance_type FROM dpp_resources.storage_nodes_info WHERE status = ? LIMIT 1";
            }
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, "running");
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {

                    instanceType = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigureStorageLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instanceType;
    }
}
