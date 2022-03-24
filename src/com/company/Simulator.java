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
    Timestamp timestamp2;
    Simulator() throws SQLException {
        String dbURL = "jdbc:sqlserver://10.10.30.219\\SQLEXPRESS2019:50915;" + "trustServerCertificate=true;" + "database=ebos_Progress_Team2;";
        String user = "userTeam2";
        String pass = "KennwortTeam2";
        conn = DriverManager.getConnection(dbURL, user, pass);
        System.out.println("fadfsdafa");
        if (conn != null) {
            System.out.println("Connected");
        } else {
            System.out.println("Error connecting to db");
            System.exit(1);
        }
        reinfolge = new ArrayList<String>(Arrays.asList("TP 1", "TP 2", "TP 3", "TP 4", "QV 1", "TP 5", "TP 6", "TP 13", "TP 14", "QV 7", "TP 14.1", "TP 15", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "TP 30", "TP 1"));
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
        timestamp2 = new Timestamp(date.getTime());
        for (int i = reinfolge.size() - 2; i >= 0; i--) {
            ResultSet resultSet = null;
            selectSql = "select distinct PalNo,Timestamp from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=?) and LocationName=?";
            statement = conn.prepareStatement(selectSql);
            statement.setString(1, reinfolge.get(i));
            statement.setString(2, reinfolge.get(i));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                palatposition = resultSet.getInt(1);
                timestamp2 = resultSet.getTimestamp(2);
                System.out.println(palatposition + " ist der aktuellte wert an der position" + reinfolge.get(i));
            }
            if (palatposition != 0) {
                resultSet = null;
                selectSql = "select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=?) and LocationName=?";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, reinfolge.get(i + 1));
                statement.setString(2, reinfolge.get(i + 1));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    palatposition2 = resultSet.getInt(1);
                }
                System.out.println(palatposition2+" ist der vert an der nächsten position");
                if (palatposition2 == 0) {

                    System.out.println(i+" pos "+reinfolge.get(i)+" -- "+palatposition);
                    prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                    prepsInsertProduct.setString(1, reinfolge.get(i));
                    timestamp2=new Timestamp(timestamp2.getTime() + dauerstation.get(i)*60*1000);
                    prepsInsertProduct.setString(2, String.valueOf(timestamp2));
                    prepsInsertProduct.execute();
                    if (reinfolge.get(i).equals("TP 6")) { //todo auf QV 6 warten wenn er weg ist testen
                        int qv6pos = 0;
                        resultSet = null;
                        selectSql = "select distinct value, TimeStamp from dbo.SampleValueHistoryT where TimeStamp<=? and Value_Id_Ref=6 order by Timestamp DESC";
                        statement = conn.prepareStatement(selectSql);
                        statement.setString(1, String.valueOf(new Timestamp(timestamp2.getTime())));
                        resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            qv6pos = resultSet.getInt(1);
                            break;
                        }
                        System.out.println(qv6pos+" ist qvpos "+timestamp2);
                        if (qv6pos == 2 || qv6pos == 3) {                               // Er ist noch nicht bei TP 6
                            timestamp2 = new Timestamp(timestamp2.getTime() + 1000 * 60);
                        }
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (6, 1,?)");
                        prepsInsertProduct.setString(1, String.valueOf(new Timestamp(timestamp2.getTime())));
                        prepsInsertProduct.execute();


                        timestamp2 = new Timestamp(timestamp2.getTime() + 1000 * 60); //Fahrtzeit
                        //hingefahren
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (6, 2,?)");
                        prepsInsertProduct.setString(1, String.valueOf(new Timestamp(timestamp2.getTime())));
                        prepsInsertProduct.execute();


                        // Zurückgefahren
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (6, 1,?)");
                        prepsInsertProduct.setString(1, String.valueOf(new Timestamp(timestamp2.getTime() + 1000 * 60)));
                        prepsInsertProduct.execute();


                        set1Waitset0(prepsInsertProduct, "TP 7", String.valueOf(palatposition));
                        set1Waitset0(prepsInsertProduct, "TP 10", String.valueOf(palatposition));
                        set1Waitset0(prepsInsertProduct, "TP 12", String.valueOf(palatposition));
                    }
                    prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                    prepsInsertProduct.setString(1, reinfolge.get(i + 1));
                    prepsInsertProduct.setString(2, String.valueOf(palatposition));
                    prepsInsertProduct.setString(3, String.valueOf(new Timestamp(timestamp2.getTime())));
                    prepsInsertProduct.execute();

                }
            }
        }
    }

    private void set1Waitset0(PreparedStatement prepsInsertProduct, String pos, String palat) throws SQLException {
        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
        prepsInsertProduct.setString(1, pos);
        prepsInsertProduct.setString(2, palat);
        prepsInsertProduct.setString(3, String.valueOf(new Timestamp(timestamp2.getTime()).toLocalDateTime()));
        prepsInsertProduct.execute();

        timestamp2 = new Timestamp(timestamp2.getTime() + 10000 * 60);

        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
        prepsInsertProduct.setString(1, pos);
        prepsInsertProduct.setString(2, "0");
        prepsInsertProduct.setString(3, String.valueOf(new Timestamp(timestamp2.getTime()).toLocalDateTime()));
        prepsInsertProduct.execute();
    }

}
