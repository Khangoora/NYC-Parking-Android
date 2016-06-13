package com.example.jaskirat.alternate_parking.config;

import java.util.HashMap;


public class Constants {
    public static String ENDPOINT_URL = "http://altparking.herokuapp.com";

    public static float userSpecifiedLocationAccuracy = 10;
    public static String GOOGLE_PLACES_KEY = "AIzaSyAwHrWSuBJ1ykPvE8obvqQr1LBYSNue050";

    public final static String SEARCH_AUTHORITY = "com.example.jaskirat.alternate_parking.provider.PlaceProvider";

    public static float minAccuracy = 200;

    public static HashMap<Integer, String> days;
    static
    {
        days = new HashMap<Integer, String>();
        days.put(0, "Sun");
        days.put(1, "Mon");
        days.put(2, "Tue");
        days.put(3, "Wed");
        days.put(4, "Thu");
        days.put(5, "Fri");
        days.put(6, "Sat");

    }

    public static HashMap<String, String> times;
    static
    {
        times = new HashMap<String, String>();
        times.put("0:00", "12:00AM");
        times.put("0:30", "12:30AM");
        times.put("1:00", "1:00AM");
        times.put("1:30", "1:30AM");
        times.put("2:00", "2:00AM");
        times.put("2:30", "2:30AM");
        times.put("3:00", "3:00AM");
        times.put("3:30", "3:30AM");
        times.put("4:00", "4:00AM");
        times.put("4:30", "4:30AM");
        times.put("5:00", "5:00AM");
        times.put("5:30", "5:30AM");
        times.put("6:00", "6:00AM");
        times.put("6:30", "6:30AM");
        times.put("7:00", "7:00AM");
        times.put("7:30", "7:30AM");
        times.put("8:00", "8:00AM");
        times.put("8:30", "8:30AM");
        times.put("9:00", "9:00AM");
        times.put("9:30", "9:30AM");
        times.put("10:00", "10:00AM");
        times.put("10:30", "10:30AM");
        times.put("11:00", "11:00AM");
        times.put("11:30", "11:30AM");
        times.put("12:00", "12:00PM");
        times.put("12:30", "12:30PM");
        times.put("13:00", "1:00PM");
        times.put("13:30", "1:30PM");
        times.put("14:00", "2:00PM");
        times.put("14:30", "2:30PM");
        times.put("15:00", "3:00PM");
        times.put("15:30", "3:30PM");
        times.put("16:00", "4:00PM");
        times.put("16:30", "4:30PM");
        times.put("17:00", "5:00PM");
        times.put("17:30", "5:30PM");
        times.put("18:00", "6:00PM");
        times.put("18:30", "6:30PM");
        times.put("19:00", "7:00PM");
        times.put("19:30", "7:30PM");
        times.put("20:00", "8:00PM");
        times.put("20:30", "8:30PM");
        times.put("21:00", "9:00PM");
        times.put("21:30", "9:30PM");
        times.put("22:00", "10:00PM");
        times.put("22:30", "10:30PM");
        times.put("23:00", "11:00PM");
        times.put("23:30", "11:30PM");

    }

    public static final String GOOGLE_API_URL = "http://maps.googleapis.com";
}
