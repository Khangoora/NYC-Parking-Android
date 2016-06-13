package com.example.jaskirat.alternate_parking.client;

import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.util.ApiUtil;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class SearchRequestManager {

    public static String getPlaces(String query) {
        String data = "";
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            return data;
        }

        RequestParams params = new RequestParams();
        params.put("input", query);
        params.put("sensor", "false");
        params.put("types", "establishment|geocode");
        params.put("key", Constants.GOOGLE_PLACES_KEY);

        try {
            data = get(ApiUtil.getPlacesAutoCompleteUrl(), params);
        } catch (Exception e) {
        }

        return data;
    }

    public static String get(String urlString, RequestParams params) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString + params.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();

            bufferedReader.close();

        } catch (Exception e) {
        } finally {
            if (iStream != null)
                iStream.close();

            urlConnection.disconnect();
        }
        return data;
    }
}
