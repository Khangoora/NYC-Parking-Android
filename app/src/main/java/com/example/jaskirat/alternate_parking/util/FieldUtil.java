package com.example.jaskirat.alternate_parking.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by pchekuri on 7/29/14.
 */
public class FieldUtil {
    public static Map<String, String> getSignsMap(double lat, double lng, float accuracy) {
        Map<String, String> map = new Hashtable();
        map.put("lon", String.valueOf(lng));
        map.put("lat", String.valueOf(lat));
        map.put("accuracy", String.valueOf(accuracy));

        return map;
    }
}
