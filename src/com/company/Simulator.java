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
        this.conn = conn;
        reinfolge = new ArrayList<String>(Arrays.asList("TP 1", "TP 2", "TP 3", "TP 4", "TP 5", "TP 6", "TP 7", "TP 8", "TP 9", "TP 11", "TP 10", "TP 12", "TP 13", "TP 14", "TP 14.1", "TP 15", "TP 16", "TP 16.1", "TP 17", "TP 22", "TP 24", "TP 25", "TP 26", "TP 27", "RBG", "###", "TP 30", "TP 1"));
        dauerstation = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10));
        lager = new ArrayList<ArrayList<Integer>>();
        //dauerstation.set(reinfolge.indexOf("TP 16"),1000);
    }

    void palettenrun() throws SQLException {
        int palid = 0;
        int palid2 = 0;
        String selectSql = "";
        PreparedStatement statement;
        PreparedStatement prepsInsertProduct;
        //Timestamp timestamp2 = new Timestamp(I);
        Kran QV_2 = new Kran("QV 2", new ArrayList<String>(Arrays.asList("TP 2", "TP 3")), conn, 1, 1, 23);
        Kran QV_1 = new Kran("QV 1", new ArrayList<String>(Arrays.asList("TP 1", "TP 4", "TP 5")), conn, 2, 1, 22);
        Kran QV_3 = new Kran("QV 3", new ArrayList<String>(Arrays.asList("TP 6", "TP 7", "TP 8")), conn, 2, 1, 6);
        Kran QV_4 = new Kran("QV 4", new ArrayList<String>(Arrays.asList("TP 11", "TP 12")), conn, 1, 1, 18);
        Kran QV_8 = new Kran("QV 8", new ArrayList<String>(Arrays.asList("TP 10", "TP 9")), conn, 1, 1, 1);
        Kran QV_7 = new Kran("QV 7", new ArrayList<String>(Arrays.asList("TP 14", "TP 14.1")), conn, 1, 1, 12);
        Kran QV_5 = new Kran("QV 5", new ArrayList<String>(Arrays.asList("TP 15", "TP 16", "TP 16.1", "TP 17")), conn, 1, 1, 13);
        for (int i = reinfolge.size() - 2; i >= 0; i--) {
            int ii = reinfolge.get(i).equals("TP 11") ? i + 2 : i + 1;
            try{
                int iii = reinfolge.get(ii).equals("TP 11") ? i + 3 : i + 2;
            }catch(Exception e){
                System.out.println("nicht g??ltig");
            }
            Date d = new Date();
            Timestamp timestamp2 = new Timestamp(d.getTime());
            ResultSet resultSet = null;
            //System.out.println(timestamp2 + " ist timestapm 2");
            selectSql = "select distinct PalNo,Timestamp,locationname from dbo.LocPalHistory where TimeStamp<=? and LocationName=? order by TimeStamp desc";
            statement = conn.prepareStatement(selectSql);
            statement.setString(1, String.valueOf(timestamp2));
            statement.setString(2, reinfolge.get(i));
            resultSet = statement.executeQuery();
            Palette p = new Palette(-1, "", timestamp2, this.conn);
            palid = 0;
            while (resultSet.next()) {
                //System.out.println("timestamp: " + String.valueOf(timestamp2) + " ort: " + reinfolge.get(i) + " palid: " + palid + " time: " + timestamp2);
                palid = resultSet.getInt(1);
                timestamp2 = resultSet.getTimestamp(2);
                p.currenttime = timestamp2;
                p.id = palid;
                p.currentpos = resultSet.getString(3);
                break;
            }
            if (p.id == -1) {
                continue;
            }
            if (palid != 0) {
                int id;
                int paldataid;
                if (reinfolge.get(i).equals("TP 6"))
                    paldataid = updateprocess(p);
                resultSet = null;
                selectSql = "select distinct PalNo,TimeStamp from dbo.LocPalHistory where TimeStamp<=? and LocationName=? order by TimeStamp desc";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, String.valueOf(timestamp2));
                statement.setString(2, reinfolge.get(ii));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    palid2 = resultSet.getInt(1);
                    break;
                }

                if (reinfolge.get(i).equals("TP 7")) {
                    statement = conn.prepareStatement(selectSql);
                    statement.setString(1, String.valueOf(timestamp2));
                    statement.setString(2, reinfolge.get(9));   // Check if TP 10 is free
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        palid2 = resultSet.getInt(1);
                        break;
                    }
                    if (palid2 == 0) {  // if TP 10 is free
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60 * (long) dauerstation.get(i));
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                        prepsInsertProduct.setString(1, reinfolge.get(i));
                        prepsInsertProduct.setString(2, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60);
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                        prepsInsertProduct.setString(1, "TP 10");
                        prepsInsertProduct.setString(2, String.valueOf(p.id));
                        prepsInsertProduct.setString(3, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currentpos = "TP 10";
                        continue;
                    }
                }

                if (reinfolge.get(i).equals("TP 14")) { // if TP 10 is not free it will push the palette to a passing point
                    System.out.println("abk??rzung TP 7");
                    statement = conn.prepareStatement(selectSql);
                    statement.setString(1, String.valueOf(timestamp2));
                    statement.setString(2, "TP 16");   // Check if TP 10 is free
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        palid2 = resultSet.getInt(1);
                        break;
                    }
                    if (palid2 == 0) {  // if TP 10 is free
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60 * (long) dauerstation.get(i));
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                        prepsInsertProduct.setString(1, reinfolge.get(i));
                        prepsInsertProduct.setString(2, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60);
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                        prepsInsertProduct.setString(1, "TP 16");
                        prepsInsertProduct.setString(2, String.valueOf(p.id));
                        prepsInsertProduct.setString(3, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currentpos = "TP 16";
                        continue;
                    } else {
                        System.out.println("geht normal weiter" + i);
                    }
                }

                if (reinfolge.get(i).equals("TP 9")) {
                    statement = conn.prepareStatement(selectSql);
                    statement.setString(1, String.valueOf(timestamp2));
                    statement.setString(2, reinfolge.get(9));   // Check if TP 10 is free
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        palid2 = resultSet.getInt(1);
                        break;
                    }
                    if (palid2 == 0) {  // if TP 10 is free
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60 * (long) dauerstation.get(i));
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                        prepsInsertProduct.setString(1, reinfolge.get(i));
                        prepsInsertProduct.setString(2, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60);
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                        prepsInsertProduct.setString(1, "TP 10");
                        prepsInsertProduct.setString(2, String.valueOf(p.id));
                        prepsInsertProduct.setString(3, String.valueOf(p.currenttime));
                        p.currentpos = "TP 10";
                        prepsInsertProduct.execute();
                        continue;
                    }
                }

                p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60 * (long) dauerstation.get(i));
                selectSql = "select distinct PalNo,TimeStamp from dbo.LocPalHistory where TimeStamp<? and LocationName=? order by TimeStamp desc";
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, String.valueOf(p.currenttime));
                statement.setString(2, reinfolge.get(ii));
                resultSet = statement.executeQuery();
                palid2 = 0;
                while (resultSet.next()) {
                    palid2 = resultSet.getInt(1);
                    break;
                }
                //todo warten bis nexte stelle frei wird und checken ob die stelle auch 10 min lang frei bleibt.
                if (palid2 != 0) {
                    selectSql = "select distinct PalNo,TimeStamp from dbo.LocPalHistory where TimeStamp>=? and LocationName=? and PalNo=0 order by TimeStamp asc";
                    statement = conn.prepareStatement(selectSql);
                    statement.setString(1, String.valueOf(new Timestamp(p.currenttime.getTime())));
                    statement.setString(2, reinfolge.get(ii));
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        palid2 = 0;
                        p.currenttime = resultSet.getTimestamp(2);
                        p.currenttime=new Timestamp(p.currenttime.getTime()+1000*60);
                    }
                    System.out.println("warten das davor platz frei wird");
                }
                if (palid2 == 0) {
                    updateprocessupdate(p);
                    //System.out.println("bewegen bewegen" + p.currentpos);
                    if (reinfolge.get(i).equals("TP 2")) { //todo auf QV 2 warten wenn er weg ist testen
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_2.kranbewegung(1, 2, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 4")) {
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_1.kranbewegung(2, 3, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 6")) {
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_3.kranbewegung(1, 2, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 16")) {
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_5.kranbewegung(2, 3, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 16.1")) {
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_5.kranbewegung(3, 4, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 7")) {
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        QV_3.kranbewegung(2, 3, p, p.currenttime);
                    } else if (reinfolge.get(i).equals("TP 24") || reinfolge.get(i).equals("TP 25") || reinfolge.get(i).equals("TP 26") || reinfolge.get(i).equals("TP 27")) {
                        updatestorageentry(p);
                        p.einlagern();
                        updatestorageleave(p);
                    } else {
                        System.out.println("standartpfad folgen " + reinfolge.get(i) + " " + p.id);
                        p.currenttime = new Timestamp(p.currenttime.getTime());
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
                        prepsInsertProduct.setString(1, reinfolge.get(i));
                        prepsInsertProduct.setString(2, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currenttime = new Timestamp(p.currenttime.getTime() + 1000 * 60);
                        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
                        prepsInsertProduct.setString(1, reinfolge.get(ii));
                        prepsInsertProduct.setString(2, String.valueOf(p.id));
                        prepsInsertProduct.setString(3, String.valueOf(p.currenttime));
                        prepsInsertProduct.execute();
                        p.currentpos = reinfolge.get(ii);
                    }
                }
            }
        }
    }

    void updatestorageentry(Palette p) throws SQLException {
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("update ebos_Progress_Team2.dbo.PalDataMilestonesHistory set RemovedFromDryChamber='false', EnteredInDryChamber='true',Timestamp=? where PalData_Id in (select PalData_Id from ebos_Progress_Team2.dbo.PalDataBelHistory where PalNo in (?));");
        prepsInsertProduct.setString(1, String.valueOf(p.currenttime));
        prepsInsertProduct.setString(2, String.valueOf(p.id));
        prepsInsertProduct.execute();
    }

    void updatestorageleave(Palette p) throws SQLException {
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("update ebos_Progress_Team2.dbo.PalDataMilestonesHistory set RemovedFromDryChamber='true', EnteredInDryChamber='false',Timestamp=? where PalData_Id in (select PalData_Id from ebos_Progress_Team2.dbo.PalDataBelHistory where PalNo in (?));");
        prepsInsertProduct.setString(1, String.valueOf(p.currenttime));
        prepsInsertProduct.setString(2, String.valueOf(p.id));
        prepsInsertProduct.execute();
    }

    int updateprocessupdate(Palette p) throws SQLException {
        //System.out.println("update update update update update update update");
        PreparedStatement prepsInsertProduct;
        ResultSet rs;
        if (p.currentpos.equals("TP 12")) {
            prepsInsertProduct = conn.prepareStatement("update ebos_Progress_Team2.dbo.PalDataMilestonesHistory set BarsPlaced='true',Timestamp=? where PalData_Id in (select PalData_Id from ebos_Progress_Team2.dbo.PalDataBelHistory where PalNo in (?))");
            prepsInsertProduct.setString(1, String.valueOf(p.currenttime));
            prepsInsertProduct.setString(2, String.valueOf(p.id));
            prepsInsertProduct.execute();
        }
        if (p.currentpos.equals("TP 13")) {
            prepsInsertProduct = conn.prepareStatement("update ebos_Progress_Team2.dbo.PalDataMilestonesHistory set GirdersPlaced='true',Timestamp=? where PalData_Id in (select PalData_Id from ebos_Progress_Team2.dbo.PalDataBelHistory where PalNo in (?));");
            prepsInsertProduct.setString(1, String.valueOf(p.currenttime));
            prepsInsertProduct.setString(2, String.valueOf(p.id));
            prepsInsertProduct.execute();
        }
        if (p.currentpos.equals("TP 23")) {
            prepsInsertProduct = conn.prepareStatement("update ebos_Progress_Team2.dbo.PalDataMilestonesHistory set ConcretingFinished='true',Timestamp=? where PalData_Id in (select PalData_Id from ebos_Progress_Team2.dbo.PalDataBelHistory where PalNo in (?));");
            prepsInsertProduct.setString(1, String.valueOf(p.currenttime));
            prepsInsertProduct.setString(2, String.valueOf(p.id));
            prepsInsertProduct.execute();
        }
        return 0;
    }

    int updateprocess(Palette p) throws SQLException {
        PreparedStatement prepsInsertProduct;
        ResultSet rs;
        prepsInsertProduct = conn.prepareStatement("select count(*) from ebos_Progress_Team2.dbo.PalDataBelHistory where PalData_Id in (1);");
        rs = prepsInsertProduct.executeQuery();
        prepsInsertProduct = conn.prepareStatement("insert into ebos_Progress_Team2.dbo.PalData (ProdSeqIdx , PalJob_Id , ProcessType) values (1,1,0);");
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("select max(PalData_Id) from dbo.PalData");
        rs = prepsInsertProduct.executeQuery();
        rs.next();
        int paldataid = rs.getInt(1);
        prepsInsertProduct = conn.prepareStatement("insert into ebos_Progress_Team2.dbo.PalDataBelHistory (PalData_Id, PalNo, TimeStamp) values (?, ?, ?);");
        prepsInsertProduct.setString(1, String.valueOf(paldataid));
        prepsInsertProduct.setString(2, String.valueOf(p.id));
        prepsInsertProduct.setString(3, String.valueOf(p.currenttime));

        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into ebos_Progress_Team2.dbo.PalDataMilestonesHistory (PalData_Id, TimeStamp, PalUnitAssigned," +
                "ShutteringFinished, BarsPlaced, GirdersPlaced, ConcretingFinished," +
                "EnteredInDryChamber, RemovedFromDryChamber, RemovedFromPalUnit)" +
                "values (?, ?, 'true', 'true', 'false'," +
                "'false', 'false', 'false', 'false', 'false');");
        prepsInsertProduct.setString(1, String.valueOf(paldataid));
        prepsInsertProduct.setString(2, String.valueOf(p.currenttime));
        prepsInsertProduct.execute();
        updateprocessupdate(p);
        return paldataid;
    }
}
