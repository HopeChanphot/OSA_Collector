package com.crrepa.sdk.OSA_collector;

public class BloodOxygenData  {

    // string variable for
    // storing employee name.
    private String spo2;

    // string variable for storing
    // employee contact number
    private String time;


    private String db;
    // string variable for storing
    // employee address.


    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public BloodOxygenData() {

    }

    // created getter and setter methods
    // for all our variables.
    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDB() {
        return db;
    }

    public void setDB(String db) {
        this.db = db;
    }
}