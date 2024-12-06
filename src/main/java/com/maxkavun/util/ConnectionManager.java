package com.maxkavun.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL_KEY = "db.url";

    private ConnectionManager(){}

    public static Connection getConnection(){
        try{
            return DriverManager.getConnection(URL_KEY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
