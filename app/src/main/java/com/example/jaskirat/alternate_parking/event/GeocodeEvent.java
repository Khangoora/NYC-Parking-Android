package com.example.jaskirat.alternate_parking.event;


public class GeocodeEvent {
    public String shortAddress;

    public boolean status;

    public GeocodeEvent(boolean status, String shortAddress) {
        this.status = status;
        this.shortAddress = shortAddress;
    }
}
