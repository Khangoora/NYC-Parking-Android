package com.example.jaskirat.alternate_parking.model;

import com.google.gson.annotations.Expose;


public class Route {
    @Expose
    private OverViewPolyLine overview_polyline;

    public OverViewPolyLine getPolyLine() {
        return overview_polyline;
    }
}