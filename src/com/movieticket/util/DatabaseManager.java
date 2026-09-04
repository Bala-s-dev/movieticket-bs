package com.movieticket.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {
    DbConfig config;
    Connection connection;

    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public DatabaseManager(){
        config = new DbConfig();
        try{
            connection = DriverManager.getConnection(config.getUrl(),config.getUser(),config.getPassword());
            System.out.println("Db connected success");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Connection getConnection(){
        return connection;
    }
    public void closeConnection(){
        try{
            connection.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
