package com.ducksabervn.projects.ketchup.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLService {
    private static final String USERNAME = "root"; //connection username
    private static final String PASSWORD = "123456789"; //connection password
    private static final String CONN_STRING = "jdbc:mysql://localhost/Ketchup"; //database target to connect

    /**
     * This method is used to establish a connection with a database
     *
     * @return {@code java.sql.Connection}
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD); //idk what this is, this is part of java convention
    }

    public static Connection getFirstConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:mysql://localhost/", USERNAME, PASSWORD);
    }
}
