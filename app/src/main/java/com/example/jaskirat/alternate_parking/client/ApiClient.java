package com.example.jaskirat.alternate_parking.client;

import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.model.Sign;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public class ApiClient {
    private static ApiInterface apiInterface;

    public static ApiInterface getApiInterface() {
        if (apiInterface == null) {

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.ENDPOINT_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            apiInterface = restAdapter.create(ApiInterface.class);
        }

        return apiInterface;
    }

    public interface ApiInterface {
        @GET("/signs.json/")
        void getSigns(@QueryMap Map<String, String> params, Callback<List<Sign>> callback);
    }
}
