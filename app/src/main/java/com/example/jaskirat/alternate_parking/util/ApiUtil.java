package com.example.jaskirat.alternate_parking.util;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.application.ParkingApp;


public class ApiUtil {
    public static final String getPlacesAutoCompleteUrl() {
        return ParkingApp.getInstance().getString(R.string.GooglePlacesAutoCompleteAPI);
    }

    public static final String getPlaceDetailsUrl() {
        return ParkingApp.getInstance().getString(R.string.GooglePlacesPlaceAPI);
    }

    public static final String getGeoCodeUrl() {
        return ParkingApp.getInstance().getString(R.string.GoogleGeoCode);
    }
}
