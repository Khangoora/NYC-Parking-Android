package com.example.jaskirat.alternate_parking.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.client.GoogleApiClient;
import com.example.jaskirat.alternate_parking.event.LocationEvent;
import com.example.jaskirat.alternate_parking.model.DirectionsResponse;
import com.example.jaskirat.alternate_parking.service.ServiceManager;
import com.example.jaskirat.alternate_parking.util.DecodeUtil;
import com.example.jaskirat.alternate_parking.util.DisplayUtil;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.example.jaskirat.alternate_parking.util.UserUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.otto.Subscribe;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ParkActivity extends Activity {

    private Button btnEndParking;

    private TextView tvTimeLeft;

    private GoogleMap mMap;

    protected static final float DEF_ZOOM = 18;

    private LatLng parkingLatLng;

    private RelativeLayout rlViewAvailableParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        setUpMapIfNeeded();
        populateData();
        ServiceManager.startLocationService(this);
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), getString(R.string.fb_app_id));
    }


    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }



    private void initView() {
        btnEndParking = (Button) findViewById(R.id.btn_end_parking);
        tvTimeLeft = (TextView) findViewById(R.id.tv_time_left);
        rlViewAvailableParking = (RelativeLayout) findViewById(R.id.rl_check_available_parking);

        btnEndParking.setOnClickListener(endParkingClickListener);
        rlViewAvailableParking.setOnClickListener(availableParkingClickListener);
    }

    private void populateData() {
        Date parkingEnds = new Date(UserUtil.getAlarmTime());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String dateString = sdf.format(parkingEnds);
        tvTimeLeft.setText(" " + dateString);

        parkingLatLng = new LatLng(UserUtil.getLat(), UserUtil.getLng());
        zoomToLocation(parkingLatLng);
    }

    private void zoomToLocation(LatLng location) {
        if (location != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(
                            new LatLng(location.latitude, location
                                    .longitude), DEF_ZOOM
                    );
                mMap.moveCamera(cameraUpdate);
        }
    }

    private void addSign(LatLng latLng, String message) {
        IconGenerator icon = new IconGenerator(this);
        icon.setStyle(IconGenerator.STYLE_GREEN);

        Bitmap iconBitmap = icon.makeIcon("Your parking location: \n" + message);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)));
    }

    private void addSelf(LatLng latLng) {
        zoomAll(parkingLatLng, latLng);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.park, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener availableParkingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startMainActivity();
        }
    };

    private View.OnClickListener endParkingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserUtil.setCarParking(false);
            startMainActivity();
        }
    };

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void setUpMapIfNeeded() {
        if (mMap == null) {
            LogUtil.i(LogUtil.LifeCycle, "1 Map is null");
            // Try to obtain the map from the SupportMapFragment.

            mMap = ((MapFragment) this
                    .getFragmentManager().findFragmentById(R.id.map)).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                LogUtil.i(LogUtil.LifeCycle, "2 Map is not null");
                setUpMap();
            } else {
                LogUtil.i(LogUtil.LifeCycle, "3 Map is null");
                int isEnabled = GooglePlayServicesUtil
                        .isGooglePlayServicesAvailable(this);
                if (isEnabled != ConnectionResult.SUCCESS) {
                    // TODO : Functionality : Add onCancelListener after cancel
                    // Google Play Services AlertDialog.
                    // closeApp("The application will be closed because of the device is unable to run without the Google Play Services API.");
                    GooglePlayServicesUtil.getErrorDialog(isEnabled,
                            this, 0);
                }
            }
        } else {
            setUpMap();
        }
    }

    protected void setUpMap() {
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMyLocationEnabled(true);
        }
    }

    @Subscribe
    public void receiveLocation(LocationEvent event) {
        LogUtil.i(LogUtil.Message, "Received location from location service, in ParkActivity");
        if(event.location == null)
            return;

        addSelf(new LatLng(event.location.getLatitude(), event.location.getLongitude()));
    }

    private void zoomAll(LatLng carPark, LatLng person) {
        mMap.clear();
        String parkingText = UserUtil.getAddress();
        addSign(parkingLatLng, parkingText);
        makePolyLineRequest(carPark, person);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(carPark);
        builder.include(person);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(70));
        mMap.moveCamera(cu);
    }

    public static int dpToPx(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (displayMetrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void makePolyLineRequest(final LatLng driverPosition, final LatLng pinPosition) {
        String origin = String.valueOf(driverPosition.latitude) + "," + String.valueOf(driverPosition.longitude);
        String destination = String.valueOf(pinPosition.latitude) + "," + String.valueOf(pinPosition.longitude);
        GoogleApiClient.getDriverApiInterface().getDirections(origin, destination, "true", new Callback<DirectionsResponse>() {
            @Override
            public void success(DirectionsResponse o, Response response) {
                List<LatLng> points = DecodeUtil.decodePoly(o.getRoutes().get(0).getPolyLine().getPoints());
                drawPolyLine(points);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                LogUtil.e(LogUtil.RetroFit, "Getting polyline request was a failure.");
                if(retrofitError.toString().contains("java.io.EOFException")){
                    LogUtil.e(LogUtil.RetroFit, "EOFException trying again.");
                    makePolyLineRequest(driverPosition, pinPosition);
                } else {
                    LogUtil.e(LogUtil.RetroFit, "Not an EOF error so ending.");
                }
            }
        });
    }

    private void drawPolyLine(List<LatLng> points) {
        PolylineOptions rectOptions = new PolylineOptions();
        for(LatLng point : points) {
            rectOptions.add(point);
        }
        if(mMap != null) {
            mMap.addPolyline(rectOptions);
            zoomPolyLine(points);
        }
    }

    private void zoomPolyLine(List<LatLng> points) {
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        for(LatLng point : points) {
            bc.include(point);
        }
        final LatLngBounds markerBounds = bc.build();
        mMap.setPadding(0, DisplayUtil.dpToPx(80), 0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerBounds.getCenter(), 13));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, 0));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerBounds.getCenter(), mMap.getCameraPosition().zoom - 1));
                mMap.setOnCameraChangeListener(null);
            }
        });
    }
}
