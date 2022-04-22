package com.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RBG {

    float[] hub = {-105.5f,490.4125f,1086.325f,1682.2375f,2278.15f,2874.0625f,3469.975f,4065.8875f,4661.8f,5257.7125f,5853.625f,6449.5375f,7045.45f,7641.3625f,8237.275f,8833.1875f,9429.1f};
    float[] tp = {0.1f,4400.1f,8800.1f,12130.1f};//TP 24,TP 25, TP 26, TP 27
    float[] regal = {2350.1f,5660.1f,8800.1f,12130.1f,15380.1f};//LG 1, LG 2, LG 3, LG 4, LG 5

    void RBGBewegung(Connection conn, Palette palette,  int regalPosition, int regalEtage, int regalIn, int etage) throws SQLException {
        //abholen
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[regalEtage]));//hub
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(tp[regalPosition]));//pos
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        //in Lager
        palette.currenttime = new Timestamp(palette.currenttime.getTime()+1000*30);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[etage]));//hub
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(regal[regalIn]));//pos
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();


        // BLEIB STEHEN RBG!
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[etage]));//hub
        prepsInsertProduct.setString(2, String.valueOf(new Timestamp(palette.currenttime.getTime()+20)));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(regal[regalIn]));//pos
        prepsInsertProduct.setString(2, String.valueOf(new Timestamp(palette.currenttime.getTime()+20)));
        prepsInsertProduct.execute();
    }
    
    void vonLGzuTP26(Connection conn, Palette palette,  int regalPosition, int regalEtage) throws SQLException {
        //abholen bei LG [regalPostion | regalEtage]
        PreparedStatement prepsInsertProduct;
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[regalEtage]));//hub
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(regal[regalPosition]));//pos
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        //in TP
        palette.currenttime = new Timestamp(palette.currenttime.getTime()+1000*30);
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[0]));//hub
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(tp[3]));//TP 26
        prepsInsertProduct.setString(2, String.valueOf(palette.currenttime));
        prepsInsertProduct.execute();


        // BLEIB STEHEN RBG!
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (8,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(hub[0]));//hub
        prepsInsertProduct.setString(2, String.valueOf(new Timestamp(palette.currenttime.getTime()+20)));
        prepsInsertProduct.execute();
        prepsInsertProduct = conn.prepareStatement("insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (2,?,?)");
        prepsInsertProduct.setString(1, String.valueOf(tp[3]));//pos
        prepsInsertProduct.setString(2, String.valueOf(new Timestamp(palette.currenttime.getTime()+20)));
        prepsInsertProduct.execute();
    }
    
}
