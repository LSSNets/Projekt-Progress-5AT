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
                System.out.println(this.currenttime);
                sql = "select PalNo, TimeStamp from dbo.LocPalHistory where LocationName=? and TimeStamp <= ? and TimeStamp >= ? ORDER BY TimeStamp DESC";
                statement = conn.prepareStatement(sql);
                String locName = "LG " + i + "|" + j;
                System.out.println(locName );
                statement.setString(1, locName);

                // Wenn er schon auß dem Lager draußen ist, ist es egal
                // Fall 1 : Es gibt nichts zurück, es wurde also in den letzten Zeit (lagerzeit) nichts gelagert
                // Fall 2: Es wurde in diesser Zeit aus dem Lager etwas rausgetan  (pal_id ist 0)
                // Bei diesen Fällen kann man einfügen
                // Fall 3: Es wurde in letzter Zeit eine Palette eingefügt. Aufgrund der Lagerzeit ist es noch blockiert zum Zeitpunkt
                statement.setString(2, String.valueOf(new Timestamp(this.currenttime.getTime()+ 1000*60*lagerzeit)));
                System.out.println(new Timestamp(this.currenttime.getTime()+ 1000*30));
                statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime()- 1000*60* lagerzeit )));
                System.out.println(new Timestamp(this.currenttime.getTime()- 1000*60* lagerzeit ));

                resultSet = statement.executeQuery();
                boolean b = resultSet.next();
                System.out.println(b);
                if(!b){
                    System.out.println("empty");
                }
                if( !b || resultSet.getInt(1) == 0){                // doesnt exist or was emptied again
                    //It can still be 0
                    System.out.println(locName + " is empty, adding new palette");

                    // See if RBG is in Use atm

                    do {
                        sql = "select PalNo, TimeStamp from dbo.LocPalHistory where LocationName=? and TimeStamp <= ? and TimeStamp > ? ORDER BY TimeStamp DESC";
                        statement = conn.prepareStatement(sql);
                        statement.setString(1, "RBG");
                        statement.setString(2, String.valueOf(new Timestamp(this.currenttime.getTime() + 30 * 1000)));
                        statement.setString(3, String.valueOf(new Timestamp(this.currenttime.getTime())));
                        System.out.println("Checking for RBG Conflicting times");
                        System.out.println(this.currenttime);
                        resultSet = statement.executeQuery();
                        b = resultSet.next();

                        this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*30);
                    }while(b);



                    //

                    // Currentpos to 0

                    //TP x auf 0 setzten
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, this.currentpos);
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();





                    //Put palette in rbg
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*30);

                    // RBG auf 0
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();


                    //Insert new Palette
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, locName);
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();


                    //  Statusupdate PalDataMilestoneHistory - EnteredInDryingChamber
                    sql = "insert into ebos_Progress_Team2.dbo.PalDataMilestonesHistory (PalData_Id, TimeStamp, PalUnitAssigned," +
                            "ShutteringFinished, BarsPlaced, GirdersPlaced, ConcretingFinished," +
                            "EnteredInDryChamber, RemovedFromDryChamber, RemovedFromPalUnit)" +
                            "values (?, ?, 'true', 'true', 'true'," +
                            "'true', 'true', 'true', 'false', 'true');";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, String.valueOf(this.id));
                    statement.setString(2, String.valueOf(this.currenttime));
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
                    //Dass time richtig ist
                    this.currenttime = new Timestamp(this.currenttime.getTime()-30*1000);
                    rbg.RBGBewegung(conn, this, pos, 0, i-1, j-1 );
                    this.currenttime = new Timestamp(this.currenttime.getTime()+30*1000);

                    // Clear lager
                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*60*lagerzeit + 1000*30);
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, locName);
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    // Statusupdate PalDataMilestoneHistory - RemovedFromDryingChamber
                    sql = "insert into ebos_Progress_Team2.dbo.PalDataMilestonesHistory (PalData_Id, TimeStamp, PalUnitAssigned," +
                            "ShutteringFinished, BarsPlaced, GirdersPlaced, ConcretingFinished," +
                            "EnteredInDryChamber, RemovedFromDryChamber, RemovedFromPalUnit)" +
                            "values (?, ?, 'true', 'true', 'true'," +
                            "'true', 'true', 'true', 'true', 'true');";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, String.valueOf(this.id));
                    statement.setString(2, String.valueOf(this.currenttime));
                    statement.execute();

                    //TODO RBG Animation

                    // Move the RBG from the lager to the TP 26 which is connected to TP 30 which goes to TP 1
                    rbg.RBGBewegung(conn, this, i-1, j-1, 2, 0);
                    //Put palette in rbg
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();
                    this.currentpos = "RBG";

                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*30);
                    // RBG auf 0
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "RBG");
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    // Palette zu TP 30
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "TP 30");
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    this.currenttime = new Timestamp(this.currenttime.getTime() + 1000*30);

                    // TP 30 auf 0
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "TP 30");
                    statement.setString(2, String.valueOf(0));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    // Palette zu TP 1
                    sql = "insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (?,?,?)";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, "TP 1");
                    statement.setString(2, String.valueOf(this.id));
                    statement.setString(3, String.valueOf(this.currenttime));
                    statement.execute();

                    return locName;
                }
            }
        }
        return "";
    }

} 

