package com.example.jaskirat.alternate_parking.util;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.application.ParkingApp;


public class UserUtil {
    public static boolean isCarParked() {
        return PrefManagerUtil.getBoolean(ParkingApp.getInstance().getString(R.string.car_parked), false);
    }

    public static void setCarParking(boolean park) {
        PrefManagerUtil.putBoolean(ParkingApp.getInstance().getString(R.string.car_parked), park);
    }

    public static double getLat() {
        String lat = PrefManagerUtil.getString(ParkingApp.getInstance().getString(R.string.lat));
        return (lat != null ? Double.valueOf(lat) : 0);
    }

    public static void setLat(double lat) {
        PrefManagerUtil.putString(ParkingApp.getInstance().getString(R.string.lat), String.valueOf(lat));
    }

    public static double getLng() {
        String lng = PrefManagerUtil.getString(ParkingApp.getInstance().getString(R.string.lng));
        return (lng != null ? Double.valueOf(lng) : 0);
    }

    public static void setLng(double lng) {
        PrefManagerUtil.putString(ParkingApp.getInstance().getString(R.string.lng), String.valueOf(lng));
    }

    public static String getAddress() {
        return PrefManagerUtil.getString(ParkingApp.getInstance().getString(R.string.address));
    }

    public static void setAddress(String address) {
        PrefManagerUtil.putString(ParkingApp.getInstance().getString(R.string.address), address);
    }

    public static long getAlarmTime() {
        return PrefManagerUtil.getLong(ParkingApp.getInstance().getString(R.string.alarm_time));
    }

    public static void setAlarmTime(long time) {
        PrefManagerUtil.putLong(ParkingApp.getInstance().getString(R.string.alarm_time), time);
    }
}
