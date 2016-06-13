package com.example.jaskirat.alternate_parking.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.example.jaskirat.alternate_parking.util.LogUtil;

public class ServiceManager {
    private static boolean serviceIsRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)){
            if(info.service.getClassName().equals(className) && info.service.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void startLocationService(Context context) {
        if(!serviceIsRunning(context, "com.example.jaskirat.alternate_parking.service.LocationService")) {
            LogUtil.d(LogUtil.LifeCycle, "starting Location Update service");
            context.startService(new Intent(context, LocationService.class));
        }
    }

    public static void startAlarmService(Context context) {
        if(!serviceIsRunning(context, "com.example.jaskirat.alternate_parking.service.AlarmService")) {
            LogUtil.d(LogUtil.LifeCycle, "Starting Alarm service");
            context.startService(new Intent(context, AlarmService.class));
        }
    }
}
