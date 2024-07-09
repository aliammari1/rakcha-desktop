package com.esprit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class mydatabase {
    private static mydatabase instance;
    private final String URL = "jdbc:mysql://localhost:3306/rakcha";
    private final String USER = "root";
    private final String PASSWORD = "";
    private  Connection connection;
    public mydatabase(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
           // throw new RuntimeException(e);
            System.err.println(e.getMessage());
        }
    }
    /** 
     * @return mydatabase
     */
    public static mydatabase getInstance(){
        if(instance==null)
            instance = new mydatabase();
        return instance;
    }
    /** 
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }
}
