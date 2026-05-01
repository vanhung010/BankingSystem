package org.vhung.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static String url = "jdbc:postgresql://localhost:5432/bankingsystem";
    private static String user = "postgres";
    private static String password = "mw500wtl";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection()  {
        try{
            return DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
