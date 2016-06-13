package com.example.jaskirat.alternate_parking.client;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class HttpClient {
    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, responseHandler);
    }
}
