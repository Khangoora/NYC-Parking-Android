package com.example.jaskirat.alternate_parking.util;

import android.util.Log;

/**
 * Created by pchekuri on 7/29/14.
 */
public class LogUtil {

    public static final String Message = "my_message";
    public static final String LifeCycle = "my_lifecycle";
    public static final String Test = "my_testing";
    public static final String RetroFit = "my_retrofit";
    public static final String Otto = "my_otto";

    public static void w(String tag, String msg) {
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
            Log.d(tag, msg);
    }
}
