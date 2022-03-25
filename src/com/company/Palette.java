package com.company;

import java.sql.*;
import java.util.Objects;

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
    }

    String einlagern() throws SQLException {
        // Todo Handle wenn es keinen Freien Platz mehr gibt ( Momentan gibt es einfach empty String zurück)
        // return pos in lager, add to freiem Platz
        int lagerzeit = 10; // 10 minuten
        ResultSet resultSet;
        String sql;
        PreparedStatement statement;
        for(int i = 1; i <6; i++){


            for(int j = (i<4) ? 3 : 1; j<17; j++){
                // MAche abfrage

                resultSet = null;
                sql = "select PalNo, TimeStamp from dbo.LocPalHistory where LocationName=? and TimeStamp <= ? and TimeStamp >= ? ORDER BY TimeStamp DESC";
                statement = conn.prepareStatement(sql);
                String locName = "LG " + i + "|" + j;
                statement.setString(1, locName);

                // Wenn er schon auß dem Lager draußen ist, ist es egal
                // Fall 1 : Es gibt nichts zurück, es wurde also in den letzten Zeit (lagerzeit) nichts gelagert
                // Fall 2: Es wurde in diesser Zeit aus dem Lager etwas rausgetan  (pal_id ist 0)
                // Bei diesen Fällen kann man einfügen
                // Fall 3: Es wurde in letzter Zeit eine Palette eingefügt. Aufgrund der Lagerzeit ist es noch blockiert zum Zeitpunkt
                statement.setString(2, String.valueOf(new Timestamp(this.currenttime.getTime()+ 1000*30)));
                statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime()- 1000*60* lagerzeit )));
                System.out.println(new Timestamp(this.currenttime.getTime()- 1000*60* lagerzeit ));

                resultSet = statement.executeQuery();
                boolean b = resultSet.next();
                System.out.println(b);

                if( !b || resultSet.getInt(1) == 0){                // doesnt exist or was emptied again
                    //It can still be 0
                    System.out.println(locName + " is empty, adding new palette");

                    //Put palette in rbg
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime())));
                    statement.execute();

                    // RBG auf 0
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime() + 1000*30)));
                    statement.execute();


                    //Insert new Palette
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, locName);
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime() + 1000*30)));
                    statement.execute();


                    RBG rbg = new RBG();

                    int pos = 0;
                    String[] s = this.currentpos.split(" ");
                    if (s[0].equals("TP")) {
                        int num=Integer.parseInt(s[1]);
                        pos = num-24;                                       //TP 24,TP 25, TP 26, TP 27

                    }else{
                        System.out.println(this.currentpos + " Is not a valid position for the Palete "+ this.id);
                        return "ERROR WHILE ADDING PALET TO LAGER";
                    }
                    // ANIMATION
                    // -1 da es bei Datenbank aus irgendein Grund bei 1 startet und ich bei for schleife deshalb auch bei 1 starte
                    rbg.RBGBewegung(conn, this, j-1, pos , i-1 );

                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*60*lagerzeit + 1000*30);
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, locName);
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    return locName;
                }
            }
        }
        return "";
    }

}

