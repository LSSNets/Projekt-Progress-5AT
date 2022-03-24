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
        if (conn != null) {
            System.out.println("Connected");
        } else {
            System.out.println("Error connecting to db");
            System.exit(1);
        }
        this.conn=conn;
        reinfolge = new ArrayList<String>(Arrays.asList("TP 1", "TP 2", "TP 3", "TP 4", "TP 5", "TP 6", "QV 3", "TP 10", "QV 8", "TP 9", "TP 11", "QV 4", "TP 12", "TP 13", "TP 14", "QV 7", "TP 14.1", "TP 15", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "TP 30", "TP 1"));
        dauerstation = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10));
        lager = new ArrayList<ArrayList<Integer>>();

    }
    void palettenrun() throws SQLException {
        int palid = 0;
        int palid2 = 0;
        String selectSql = "";
        PreparedStatement statement;
        PreparedStatement prepsInsertProduct;
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        Kran QV_2=new Kran("QV 2",new ArrayList<String>(Arrays.asList("TP 2", "TP 3")),conn,1,1,23);
        Kran QV_1=new Kran("QV 1",new ArrayList<String>(Arrays.asList("TP 1","TP 4", "TP 5")),conn,2,1,22);
        Kran QV_3=new Kran("QV 3",new ArrayList<String>(Arrays.asList("TP 6","TP 7", "TP 8")),conn,2,1,6);
        Kran QV_4=new Kran("QV 4",new ArrayList<String>(Arrays.asList("TP 11","TP 12")),conn,1,1,18);
        Kran QV_8=new Kran("QV 8",new ArrayList<String>(Arrays.asList("TP 10","TP 9")),conn,1,1,1);
        Kran QV_7=new Kran("QV 7",new ArrayList<String>(Arrays.asList("TP 14","TP 14.1")),conn,1,1,12);
        Kran QV_5=new Kran("QV 5",new ArrayList<String>(Arrays.asList("TP 15","TP 16","TP 16.1","TP 17")),conn,1,1,13);

        for (int i = reinfolge.size() - 2; i >= 0; i--) {
            ResultSet resultSet = null;
            selectSql = "select distinct PalNo,Timestamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=?";
            statement = conn.prepareStatement(selectSql);
            statement.setString(1, String.valueOf(timestamp2));
            statement.setString(2, reinfolge.get(i));
            resultSet = statement.executeQuery();
            Palette p=new Palette(6,"TP 4",timestamp2,this.conn);
            while (resultSet.next()) {
                palid = resultSet.getInt(1);
                timestamp2 = resultSet.getTimestamp(2);
                p.currenttime=timestamp2;
                p.id=palid;
                break;
            }
            if (palid != 0) {
                resultSet = null;
                selectSql = "select distinct PalNo,TimeStamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=? order by TimeStamp desc";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, String.valueOf(timestamp2));
                statement.setString(2, reinfolge.get(i + 1));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    palid2 = resultSet.getInt(1);
                    break;
                }
                if (palid2 == 0) {

                    if (reinfolge.get(i).equals("TP 2")) { //todo auf QV 2 warten wenn er weg ist testen
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));
                        QV_2.kranbewegung(1,2,p,p.currenttime);
                    }
                    if(reinfolge.get(i).equals("TP 4")){
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));
                        QV_1.kranbewegung(2,3,p,p.currenttime);
                    }
                    if (reinfolge.get(i).equals("TP 6")){
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));
                        QV_3.kranbewegung(1,2,p,p.currenttime);
                    }
                    /*if (reinfolge.get(i).equals("TP 6")){
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));
                        QV_1.kranbewegung(1,2,p,p.currenttime);
                    }*/

                }
            }
        }
    }

}
