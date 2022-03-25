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

                    //Insert new Palette
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, locName);
                    statement.setString(2, String.valueOf(this.id));
                    //todo ADD ANIMATION


                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*30);
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*60*lagerzeit);
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