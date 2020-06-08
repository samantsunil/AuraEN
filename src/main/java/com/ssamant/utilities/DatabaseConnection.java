/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssamant.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dpp_resources", "root", "dpp*");
            if (con != null) {
                System.out.println("Database connection successful.");
            } else {
                System.out.println("Failed to connect to database.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("MySQL connection failed.");
            ex.printStackTrace();
        }
        return con;

    }
}
