package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Simulator {
    Connection conn = null;
    ArrayList<String> reinfolge;
    ArrayList<Integer> dauerstation;
    ArrayList<ArrayList<Integer>> lager;
    Simulator() throws SQLException {
        String dbURL = "jdbc:sqlserver://10.10.30.219\\SQLEXPRESS2019:50915;" + "trustServerCertificate=true;" + "database=ebos_Progress_Team2;";
        String user = "userTeam2";
        String pass = "KennwortTeam2";
        Connection conn = DriverManager.getConnection(dbURL, user, pass);
        System.out.println("fadfsdafa");
        if (conn != null) {
            System.out.println("Connected");
        } else {
            System.out.println("Error connecting to db");
            System.exit(1);
        }
        reinfolge = new ArrayList<String>(Arrays.asList("TP 1", "TP 2", "TP 3", "TP 4", "QV 1", "TP 5", "TP 6", "QV 3", "TP 10", "QV 8", "TP 9", "TP 11", "QV 4", "TP 12", "TP 13", "TP 14", "QV 7", "TP 14.1", "TP 15", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "TP 30", "TP 1"));
        dauerstation = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10));
        lager = new ArrayList<ArrayList<Integer>>();
    }
    void palettenrun() throws SQLException {
        int palatposition = 0;
        int palatposition2 = 0;
        String selectSql = "";
        PreparedStatement statement;
        PreparedStatement prepsInsertProduct;
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        for (int i = reinfolge.size() - 2; i >= 0; i--) {
            ResultSet resultSet = null;
            selectSql = "select distinct PalNo,Timestamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=?";
            statement = conn.prepareStatement(selectSql);
            statement.setString(1, String.valueOf(timestamp2));
            statement.setString(2, reinfolge.get(i));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                palatposition = resultSet.getInt(1);
                timestamp2 = resultSet.getTimestamp(2);
                System.out.println(palatposition + " ist der aktuellte wert an der position" + reinfolge.get(i));
                break;
            }
            if (palatposition != 0) {
                resultSet = null;
                selectSql = "select distinct PalNo from dbo.LocPalHistory where TimeStamp<=? and LocationName=? order by TimeStamp desc";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, reinfolge.get(i + 1));
                statement.setString(2, reinfolge.get(i + 1));
                statement.setString(3, String.valueOf(timestamp2));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    palatposition2 = resultSet.getInt(1);
                    break;
                }
                System.out.println(palatposition2+" ist der vert an der nächsten position");
                if (palatposition2 == 0) {
                    System.out.println(i+" pos "+reinfolge.get(i)+" -- "+palatposition);
                    prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                    prepsInsertProduct.setString(1, reinfolge.get(i));
                    timestamp2=new Timestamp(timestamp2.getTime() + dauerstation.get(i)*60*1000);
                    prepsInsertProduct.setString(2, String.valueOf(timestamp2));
                    prepsInsertProduct.execute();
                    if (reinfolge.get(i).equals("TP 2")) { //todo auf QV 2 warten wenn er weg ist testen

                    }
                    if(reinfolge.get(i).equals("TP 9")){

                    }
                    prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                    prepsInsertProduct.setString(1, reinfolge.get(i + 1));
                    prepsInsertProduct.setString(2, String.valueOf(palatposition));
                    prepsInsertProduct.setString(3, String.valueOf(new Timestamp(timestamp2.getTime()).toLocalDateTime()));
                    prepsInsertProduct.execute();
                }
            }
        }
    }

}
