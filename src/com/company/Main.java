package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws SQLException {
        Simulator s=new Simulator();
        int counter=0;

        for (int i = 0; i < 26; i++) {
            System.out.println("run"+counter++);
            s.palettenrun();
        }
    }
}

