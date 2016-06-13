package com.example.jaskirat.alternate_parking.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.example.jaskirat.alternate_parking.OmniModel;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.event.LocationEvent;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;



public class LocationService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    protected LocationClient mLocationClient;
    private LocationListener listener;
    private Runnable listeningRunnable;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
        initLocationObjects();
    }

    private void initLocationObjects() {
        listener = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                OmniModel.currentLocation = location;
                BusProvider.getInstance().post(new LocationEvent(location));
                endService();
            }
        };
        mLocationClient = new LocationClient(this, this, this);
        listeningRunnable = new Runnable() {
            @Override
            public void run() {
                mLocationClient.connect();
            }
        };
        listeningRunnable.run();
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        OmniModel.currentLocation = mLocationClient.getLastLocation();
        if(OmniModel.currentLocation != null) {
            BusProvider.getInstance().post(new LocationEvent(OmniModel.currentLocation));
            endService();
        } else {
            mLocationClient.requestLocationUpdates(NORMAL_REQUEST, listener);
        }
    }

    private void endService() {
        mLocationClient.disconnect();
        stopSelf();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: log mixpanel when this fails.
    }

    public static final LocationRequest NORMAL_REQUEST = LocationRequest
            .create().setInterval(1000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

}
