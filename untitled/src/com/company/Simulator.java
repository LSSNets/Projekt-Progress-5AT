package com.company;

import com.sun.tools.jconsole.JConsoleContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

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
        reinfolge = new ArrayList<String>(Arrays.asList("TP 1", "TP 2", "TP 3", "TP 4", "TP 5", "TP 6", "TP 7", "TP 8", "TP 9", "TP 10", "TP 12", "TP 13", "TP 14", "TP 16", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "TP 30", "TP 1"));
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
        Kran QV_2=new Kran("QV 2",new ArrayList<String>(Arrays.asList("TP 2", "TP 3")),conn,1,1,23);
        Kran QV_1=new Kran("QV 1",new ArrayList<String>(Arrays.asList("TP 1","TP 4", "TP 5")),conn,2,1,22);
        for (int i = reinfolge.size() - 2; i >= 0; i--) {
            ResultSet resultSet = null;
            selectSql = "select distinct PalNo,Timestamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=?";
            statement = conn.prepareStatement(selectSql);
            statement.setString(1, String.valueOf(timestamp2));
            statement.setString(2, reinfolge.get(i));
            resultSet = statement.executeQuery();
            Palette p=new Palette(6,"TP 4",timestamp2,this.conn);

            while (resultSet.next()) {
                palatposition = resultSet.getInt(1);
                //timestamp2 = resultSet.getTimestamp(2);
                p.currenttime=timestamp2;
                p.id=palatposition;
                break;
            }

            if (palatposition != 0) {
                resultSet = null;
                selectSql = "select distinct PalNo,TimeStamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=? order by TimeStamp desc";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, String.valueOf(timestamp2));
                statement.setString(2, reinfolge.get(i + 1));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    palatposition2 = resultSet.getInt(1);
                    break;
                }

                if (palatposition2 == 0) {

                    if (reinfolge.get(i).equals("TP 2") && false) { //todo auf QV 2 warten wenn er weg ist testen
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));
                        QV_2.kranbewegung(1,2,p,p.currenttime);
                    }
                    if(reinfolge.get(i).equals("TP 4") && false){
                        System.out.println("entered");
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60*(long) dauerstation.get(i));

                        QV_1.kranbewegung(2,3,p,p.currenttime);
                    }

                    System.out.println(reinfolge.get(i));
                    if(reinfolge.get(i).equals("TP 7")){

                        System.out.println("\n\nthis is in TP 7\n");
                        selectSql = "select PalNo, TimeStamp from dbo.LocPalHistory where LocationName = 'TP 7' order by TimeStamp DESC;";
                        statement = conn.prepareStatement(selectSql);
                        //statement.setString(1, String.valueOf(timestamp2));
                        resultSet = statement.executeQuery();

                        resultSet.next();
                        if(resultSet.getInt(1) == 0){
                            resultSet.next();
                        }
                        int palet_num_tp7 = resultSet.getInt(1);
                        System.out.println(palet_num_tp7);

                        String insert_sql;

                        if(free_station("TP 7").equals("TP 10")){

                            //station 7 leeren
                            insert_sql = "insert into dbo.LocPalHistory (LocationName, PalNo, TimeStamp) values (TP 6, 0, ?);";
                            statement = conn.prepareStatement(insert_sql);
                            statement.setString(1, String.valueOf(timestamp2));
                            statement.execute();

                            // station 10 befüllen
                            insert_sql = "insert into dbo.LocPalHistory (LocationName, PalNo, TimeStamp) values (TP 10, ?, ?);";
                            statement = conn.prepareStatement(insert_sql);
                            statement.setString(1, String.valueOf(palet_num_tp7));
                            statement.setString(2, String.valueOf(timestamp2));
                            statement.execute();

                        }

                        System.out.println("executed");
                    }
                }
            }
        }
    }


    //checks if a single station is free
    //station free: return station
    //station full: return ""
    private String free_station(String station) throws SQLException {


        String selectSql = "select PalNo, TimeStamp from dbo.LocPalHistory where LocationName = ? order by TimeStamp DESC;";
        PreparedStatement statement = conn.prepareStatement(selectSql);
        statement.setString(1, station);
        ResultSet resultSet = statement.executeQuery();


        try{
            resultSet.next();
            if(resultSet.getInt(1) == 0){
                return station;

            }

        } catch (SQLException ignored) {

        }

        return "";
    }

}
