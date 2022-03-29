package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws SQLException {
        Simulator s=new Simulator();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        s.palettenrun();
        //var lagerlist=Arrays.asList(["LG 1|",3,16],["LG 2|",3,16],["LG 3|",1,16],["LG 4|",1,16],["LG 5|",1,16])

        /*ResultSet resultSet = null;
        String selectSql = "select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname";
        PreparedStatement statement = conn.prepareStatement(selectSql);
        statement.setString(1, "Smith");
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
        }*/
        /*
        insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (23, 1,'2022-03-23 11:00:00.000')
insert into dbo.SampleValueHistoryT(value_id_ref, value, timestamp) values (23, 2,'2022-03-23 11:01:00.000')
         */


    }

}

