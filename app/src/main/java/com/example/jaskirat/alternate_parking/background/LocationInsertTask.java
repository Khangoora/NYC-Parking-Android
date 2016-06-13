package com.example.jaskirat.alternate_parking.background;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.jaskirat.alternate_parking.application.ParkingApp;
import com.example.jaskirat.alternate_parking.provider.PlaceProvider;


public class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
    @Override
    protected Void doInBackground(ContentValues... contentValues) {
        if (ParkingApp.getInstance().getContentResolver() != null)
            ParkingApp.getInstance().getContentResolver().insert(PlaceProvider.CONTENT_URI, contentValues[0]);

        return null;
    }
}
