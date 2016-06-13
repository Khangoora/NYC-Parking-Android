package com.example.jaskirat.alternate_parking.application;

import android.app.Application;
import android.content.SharedPreferences;

public class ParkingApp extends Application {

    private static ParkingApp sInstance;
    private static final String PREFS = "ParkingAppPrefs";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sInstance = this;
    }

    public static ParkingApp getInstance() {
        return sInstance;
    }

    public SharedPreferences getSharedPrefs() {
        SharedPreferences settings = sInstance.getSharedPreferences(PREFS,
                MODE_PRIVATE);
        return settings;
    }

}
