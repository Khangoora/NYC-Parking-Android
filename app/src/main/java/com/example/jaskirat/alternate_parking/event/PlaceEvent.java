package com.example.jaskirat.alternate_parking.event;


public class PlaceEvent {
    public double lat;
    public double lng;
    public String shortAddress;
    public String longAddress;

    public PlaceEvent(double lat, double lng, String shortAddress, String longAddress) {
        this.lat = lat;
        this.lng = lng;
        this.shortAddress = shortAddress;
        this.longAddress = longAddress;
    }
}
