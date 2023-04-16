package com.crrepa.sdk.OSA_collector;

public class SleepData {

    // string variable for
    // storing employee name.
    private String SleepRestful;
    private String SleepLight;
    // string variable for storing
    // employee contact number
    private String time;



    // string variable for storing
    // employee address.


    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public SleepData() {

    }

    // created getter and setter methods
    // for all our variables.
    public String getSleepRestful() {
        return SleepRestful;
    }

    public void setSleepRestful(String SleepRestful) {
        this.SleepRestful = SleepRestful;
    }

    public String getSleepLight() {
        return SleepLight;
    }

    public void setSleepLight(String SleepLight) {
        this.SleepLight = SleepLight;
    }




    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}