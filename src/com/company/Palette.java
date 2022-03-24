package com.company;


import java.sql.*;

public class Palette {
    int id;
    String currentpos;//useless
    Timestamp currenttime;
    Connection conn;
    public Palette(int id, String currentpos, Timestamp currenttime, Connection conn) throws SQLException {
        this.id = id;
        this.conn=conn;
        this.currentpos = currentpos;
        this.currenttime = currenttime;
        //String selectSql = "insert into dbo.LocPalHistory (LocationName,PalNo,TimeStamp) values ('TP 2',6,current_timestamp)";
        //PreparedStatement statement = this.conn.prepareStatement(selectSql);
       // statement.execute();
    }
}
