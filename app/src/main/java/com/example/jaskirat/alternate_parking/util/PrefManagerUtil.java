package com.example.jaskirat.alternate_parking.util;

import android.content.SharedPreferences;

import com.example.jaskirat.alternate_parking.application.ParkingApp;

public class PrefManagerUtil {

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs()
                .edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs()
                .edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs()
                .edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs()
                .edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putFloat(String key, Float value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void putString(int key, String value) {
        SharedPreferences.Editor editor = ParkingApp.getInstance().getSharedPrefs()
                .edit();
        editor.putString(ParkingApp.getInstance().getString(key), value);
        editor.commit();
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defValue) {
        return ParkingApp.getInstance().getSharedPrefs().getString(key, defValue);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return ParkingApp.getInstance().getSharedPrefs().getBoolean(key, defValue);
    }

    public static long getLong(String key) {
        return getLong(key, 0);
    }

    public static long getLong(String key, long defValue) {
        return ParkingApp.getInstance().getSharedPrefs().getLong(key, defValue);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defValue) {
        return ParkingApp.getInstance().getSharedPrefs().getInt(key, defValue);
    }

}