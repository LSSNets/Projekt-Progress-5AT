package com.company;


import java.sql.Timestamp;

public class Palette {
    int id;
    String Currentpos;
    Timestamp Currenttime;

    public Palette(int id, String currentpos, Timestamp currenttime) {
        this.id = id;
        Currentpos = currentpos;
        Currenttime = currenttime;
    }
}
