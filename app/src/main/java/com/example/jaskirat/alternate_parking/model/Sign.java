package com.example.jaskirat.alternate_parking.model;

import android.location.Location;

import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Sign {

    @SerializedName("object_id")
    @Expose
    private String objectId;

    @Expose
    private String name;

    @Expose
    private List<Double> location = new ArrayList<Double>();

    @SerializedName("no_parking_anytime")
    @Expose
    private boolean noParkingTime;

    @Expose
    private List<Restriction> restrictions;

    @SerializedName("meter_parking")
    @Expose
    private boolean meterParking;

    @SerializedName("meter_parking_limit")
    @Expose
    private int meterParkingLimit;

    public boolean isNoParkingTime() {
        return noParkingTime;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return new LatLng(location.get(1), location.get(0));
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public int getMeterParkingLimit() {
        return meterParkingLimit;
    }

    public boolean isMeterParking() {
        return meterParking;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public boolean isRestrictionValid() {
        for(Restriction r : restrictions) {
            if(r.isRestrictionValidRightNow()) {
                return true;
            }
        }

        return false;
    }

    public Set<String> getRestrictionStrings() {
        Set<String> output = new HashSet<String>();
        for(Restriction r: restrictions) {
            String str = "No parking " + r.getRestrictionDayString() + " from " + r.getRestrictionDayTimes();
            output.add(str);
        }

        return output;
    }

    public DateTime getNextDateTimeRestriction() {
        DateTime dt = new DateTime().plusDays(10);
        for(Restriction r : restrictions) {
            if(dt.getMillis() > r.getNearestDateTime().getMillis()) {
                dt = r.getNearestDateTime();
            }
        }
        return dt;
    }

}
