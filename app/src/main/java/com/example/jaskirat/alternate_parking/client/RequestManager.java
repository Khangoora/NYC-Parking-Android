package com.example.jaskirat.alternate_parking.client;

import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.json.JsonParser;
import com.example.jaskirat.alternate_parking.util.ApiUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class RequestManager {
    public static void requestPlaceDetails(String reference) {
        try {
            RequestParams params = new RequestParams();
            params.put("reference", reference);
            params.put("sensor", "false");
            params.put("key", Constants.GOOGLE_PLACES_KEY);
            HttpClient.get(ApiUtil.getPlaceDetailsUrl(), params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject arg0) {
                            super.onSuccess(statusCode, headers, arg0);
                            JsonParser parser = new JsonParser();
                            try {
                                parser.parsePlaceDetails(arg0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestAddress(double lat, double lng) {
        try {
            RequestParams params = new RequestParams();
            params.put("sensor", "true");
            params.put("latlng", Double.toString(lat) + "," + Double.toString(lng));
            HttpClient.get(ApiUtil.getGeoCodeUrl(), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject arg0) {
                    super.onSuccess(statusCode, headers, arg0);
                    JsonParser parser = new JsonParser();
                    try {
                        parser.parseAddress(arg0);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
