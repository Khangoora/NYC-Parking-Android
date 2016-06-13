package com.example.jaskirat.alternate_parking.client;

import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.model.DirectionsResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;


public class GoogleApiClient {
    private static DriverApiInterface sDriverApiInterface;

    public static DriverApiInterface getDriverApiInterface() {
        if (sDriverApiInterface == null) {

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.GOOGLE_API_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            sDriverApiInterface = restAdapter.create(DriverApiInterface.class);
        }

        return sDriverApiInterface;
    }

    public interface DriverApiInterface {
        @GET("/maps/api/directions/json")
        void getDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("sensor") String sensor, Callback<DirectionsResponse> callback);
    }
}