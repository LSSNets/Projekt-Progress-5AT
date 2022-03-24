package com.company;

import java.sql.*;
import java.util.ArrayList;

public class Kran {
    String name;
    ArrayList<String> stationsnamen; //[TP 2,TP 3]
    int pos;
    Connection conn;
    int bewegungsdauer;
    public Kran(String name, ArrayList<String> stationsnamen, Connection conn,int pos,int bewegungsdauer) throws SQLException {
        this.name = name;
        this.stationsnamen = stationsnamen;
        this.conn = conn;
        this.pos=pos;
        this.bewegungsdauer=bewegungsdauer;
    }

    void kranbewegung(int startindex,int endindex,Palette p,Timestamp t) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = conn.prepareStatement("select distinct value, TimeStamp from dbo.SampleValueHistoryT where TimeStamp<=? and Value_Id_Ref=23 order by Timestamp DESC");
        statement.setString(1, String.valueOf(new Timestamp(t.getTime())));
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            this.pos = resultSet.getInt(1);
            break;
        }
        System.out.println(pos + " ist qvpos " + t);
        if (pos != startindex) {
            t = new Timestamp(t.getTime() + ((long) bewegungsdauer) * 1000 * 60);
        }
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
        prepsInsertProduct.setString(1, stationsnamen.get(startindex));
        prepsInsertProduct.setString(2, String.valueOf(t));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (23, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(startindex));
        prepsInsertProduct.setString(2, String.valueOf(t));
        prepsInsertProduct.execute();
        t=new Timestamp(t.getTime() + 1000 * 60);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (23, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(endindex));
        prepsInsertProduct.setString(2, String.valueOf(t));
        prepsInsertProduct.execute();
        t=new Timestamp(t.getTime() + 1000 * 60);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (23, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(startindex));
        prepsInsertProduct.setString(2, String.valueOf(new Timestamp(t.getTime() + 2 * 1000 * 60)));
        prepsInsertProduct.execute();
        t = new Timestamp(t.getTime() + 1000 * 60);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
        prepsInsertProduct.setString(1, stationsnamen.get(startindex));
        prepsInsertProduct.setString(2, String.valueOf(p.id));
        prepsInsertProduct.setString(3, String.valueOf(t));
        prepsInsertProduct.execute();
    }
}
