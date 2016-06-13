package com.example.jaskirat.alternate_parking.model;


import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.util.DateUtil;
import com.example.jaskirat.alternate_parking.util.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.Collections;
import java.util.List;


public class Restriction {

    @Expose
    private RestrictionObject restriction;

    public RestrictionObject getRestriction() {
        return restriction;
    }

    public List<Integer> getDays() {
        return getRestriction().days;
    }

    public String getStartTime() {
        return getRestriction().startTime;
    }

    public String getEndTime() {
        return getRestriction().endTime;
    }

    public boolean isRestrictionValidRightNow() {
        DateTime now = new DateTime();

        for(int i : getDays()) {
            int currentDay = (i == 0) ? 7 : i;
            if(now.getDayOfWeek() == currentDay)
                return isRestrictionTimeValid();
        }

        return false;
    }

    public boolean isRestrictionTimeValid() {
        String[] startTime = StringUtil.getTimes(getStartTime());
        String[] endTime = StringUtil.getTimes(getEndTime());
        DateTime start = new DateTime().withTime(Integer.valueOf(startTime[0]), Integer.valueOf(startTime[1]), 0, 0);
        DateTime end = new DateTime().withTime(Integer.valueOf(endTime[0]), Integer.valueOf(endTime[1]), 0, 0);
        Interval interval = new Interval(start, end);
        return interval.containsNow();
    }

    public DateTime getNearestDateTime() {
        DateTime now = new DateTime();
        String[] startTime = StringUtil.getTimes(getStartTime());
        DateTime start = new DateTime().withTime(Integer.valueOf(startTime[0]), Integer.valueOf(startTime[1]), 0, 0);
        int dayOfWeek = now.getDayOfWeek();
        int startHour = Integer.valueOf(startTime[0]);
        if(now.getHourOfDay() > startHour) {
            dayOfWeek += 1;
            start = start.plusDays(1);
        }
        int closestDayOfWeek = getClosestDay(dayOfWeek);
        if(start.getDayOfWeek() > closestDayOfWeek) {
            start = start.plusDays(7);
        }
        start = start.withDayOfWeek(closestDayOfWeek);

        return start;
    }

    private int getClosestDay(int i) {
        Collections.sort(getDays());
        for(int x : getDays()) {
            if(x >= i)
                return x;
        }

        return getDays().get(0);
    }

    public String getRestrictionDayString() {
        if(getDays().size() == 0)
            return "N/A";

        if(getDays().size() == 2) {
            return Constants.days.get(getDays().get(0)) + " & " + Constants.days.get(getDays().get(1));
        } else if(getDays().size() == 6) {
            return "Except " + Constants.days.get(DateUtil.getExceptDay(getDays()));
        } else if(getDays().size() == 1){
            return Constants.days.get(getDays().get(0));
        }else if(DateUtil.areDaysConsecutive(getDays())) {
            return Constants.days.get(getDays().get(0)) + " - " + Constants.days.get(getDays().get(getDays().size() - 1));
        } else {
            return DateUtil.getMultipleDaysString(getDays());
        }
    }

    public String getRestrictionDayTimes() {
        return Constants.times.get(getStartTime()) + " TO " + Constants.times.get(getEndTime());
    }



    public class RestrictionObject {
        @Expose
        public List<Integer> days;

        @SerializedName("start_time")
        @Expose
        public String startTime;

        @SerializedName("end_time")
        @Expose
        public String endTime;

    }
}
