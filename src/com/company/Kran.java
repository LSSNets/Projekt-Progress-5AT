package com.company;

import java.sql.*;
import java.util.ArrayList;

public class Kran {
    String name;
    int id;
    ArrayList<String> stationsnamen; //[TP 2,TP 3]
    int pos;
    Connection conn;
    int bewegungsdauer;
    public Kran(String name, ArrayList<String> stationsnamen, Connection conn,int pos,int bewegungsdauer,int id) throws SQLException {
        this.name = name;
        this.stationsnamen = stationsnamen;
        this.conn = conn;
        this.pos=pos;
        this.bewegungsdauer=bewegungsdauer;
        this.id=id;
    }

    void kranbewegung(int startindex,int endindex,Palette p,Timestamp t) throws SQLException {
       //System.out.println(" start: "+startindex+" endindex: "+endindex+" stationnen"+stationsnamen.get(startindex-1)+" "+stationsnamen.get(endindex-1));
        ResultSet resultSet = null;
        PreparedStatement statement = conn.prepareStatement("select distinct value, TimeStamp from dbo.SampleValueHistoryT where TimeStamp<=? and Value_Id_Ref=? order by Timestamp DESC");
        statement.setString(1, String.valueOf(new Timestamp(t.getTime())));
        statement.setString(2, String.valueOf(id));
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            this.pos = resultSet.getInt(1);
           //System.out.println("pos wurde gesetzt");
            break;
        }
       //System.out.println(pos + " ist qvpos " + t);
        if (pos != startindex) {
            t = new Timestamp(t.getTime() + ((long) bewegungsdauer) * 1000 * 60);
           //System.out.println("warten warten warten auf kran du lappen"+startindex+" ## "+pos);
        }
       //System.out.println("in kranbewegung");
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,0,?)");
        prepsInsertProduct.setString(1, stationsnamen.get(startindex-1));
        prepsInsertProduct.setString(2, String.valueOf(t));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (?, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(id));
        prepsInsertProduct.setString(2, String.valueOf(startindex));
        prepsInsertProduct.setString(3, String.valueOf(new Timestamp(t.getTime()+30*1000)));
        prepsInsertProduct.execute();
        t=new Timestamp(t.getTime() + 1000 * 60);
       //System.out.println(t+" - wird sinert into sample and locpal"+p.id+" "+endindex);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (?, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(id));
        prepsInsertProduct.setString(2, String.valueOf(endindex));
        prepsInsertProduct.setString(3, String.valueOf(t));
        prepsInsertProduct.execute();
        p.currentpos=stationsnamen.get(endindex-1);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)");
        prepsInsertProduct.setString(1, stationsnamen.get(endindex-1));
        prepsInsertProduct.setString(2, String.valueOf(p.id));
        prepsInsertProduct.setString(3, String.valueOf(t));
        prepsInsertProduct.execute();
        t=new Timestamp(t.getTime() + 1000 * 60);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (?, ?,?)");
        prepsInsertProduct.setString(1, String.valueOf(id));
        prepsInsertProduct.setString(2, String.valueOf(startindex));
        prepsInsertProduct.setString(3, String.valueOf(new Timestamp(t.getTime())));
        prepsInsertProduct.execute();
        t = new Timestamp(t.getTime() + 1000 * 60);
        p.currentpos=stationsnamen.get(endindex-1);

    }
}
