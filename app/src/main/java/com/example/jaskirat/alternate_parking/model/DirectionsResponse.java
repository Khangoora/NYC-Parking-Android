package com.example.jaskirat.alternate_parking.model;

import com.google.gson.annotations.Expose;

import java.util.List;


public class DirectionsResponse {

    @Expose
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

}