package com.example.jaskirat.alternate_parking.util;

import com.example.jaskirat.alternate_parking.config.Constants;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class DateUtil {
    public static int getExceptDay(List<Integer> days) {
        for(int i : days) {
            if(!Constants.days.containsKey(i))
                return i;
        }

        return 0;
    }

    public static boolean areDaysConsecutive(List<Integer> days) {
        Collections.sort(days);
        for(int i = 1; i < days.size(); i++) {
            if(days.get(i) - days.get(i-1) != 1) {
                return false;
            }
        }

        return true;
    }

    public static String getMultipleDaysString(List<Integer> days) {
        Collections.sort(days);
        String output = "";

        for(int i = 0; i < days.size(); i++) {
            output += Constants.days.get(i);

            if(i != days.size() - 1) {
                output += ",";
            }
        }
        return output;
    }
}
