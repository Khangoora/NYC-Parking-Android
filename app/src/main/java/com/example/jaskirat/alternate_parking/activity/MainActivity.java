package com.example.jaskirat.alternate_parking.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.jaskirat.alternate_parking.OmniModel;
import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.adapter.LocationSearchAdapter;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.client.ApiClient;
import com.example.jaskirat.alternate_parking.client.RequestManager;
import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.event.CancelAudioServiceEvent;
import com.example.jaskirat.alternate_parking.event.GeocodeEvent;
import com.example.jaskirat.alternate_parking.event.PlaceEvent;
import com.example.jaskirat.alternate_parking.event.StartReminderEvent;
import com.example.jaskirat.alternate_parking.model.Sign;
import com.example.jaskirat.alternate_parking.service.AlarmService;
import com.example.jaskirat.alternate_parking.util.FieldUtil;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.example.jaskirat.alternate_parking.util.UserUtil;
import com.example.jaskirat.alternate_parking.util.ViewUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {

    private static GoogleMap mMap;
    private HashMap<String, Sign> mapMarkers = new HashMap<String, Sign>();
    private List<Sign> currentSigns = new ArrayList<Sign>();

    protected static final float DEF_ZOOM = 19;

    private boolean userModifiedLocation;

    private boolean listenToGps;

    private LatLng lastLatLng;

    private Button btnSetReminder, btnReminder;

    private ImageButton ibLocateMe, ibSearchClear, ibBannerClear;

    private AutoCompleteTextView etSearch;

    private LocationSearchAdapter searchAdapter;

    private float accuracy;

    private boolean animate;

    private boolean search;

    private TextView tvDesc1, tvDesc2, tvDesc3, tvTimeDesc;

    private RelativeLayout rlBanner;

    private Marker lastOpened;

    private DatePicker dpReminderDate;

    private TimePicker tpReminderTime;

    private Date lastPingDate;

    private Dialog dialog;

    private ImageButton ibShare;


    PendingIntent pi;
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setUpMapIfNeeded();
        placeMarkerCurrentLocation();
    }

    private void initView() {
        btnSetReminder = (Button) findViewById(R.id.btn_set_reminder);
        ibLocateMe = (ImageButton) findViewById(R.id.ib_locate_me);
        ibSearchClear = (ImageButton) findViewById(R.id.ib_search_clear);
        ibBannerClear = (ImageButton) findViewById(R.id.ib_banner_clear);
        etSearch = (AutoCompleteTextView) findViewById(R.id.et_search);
        rlBanner = (RelativeLayout) findViewById(R.id.rl_banner);
        tvDesc1 = (TextView) findViewById(R.id.tv_desc_1);
        tvDesc2 = (TextView) findViewById(R.id.tv_desc_2);
        tvDesc3 = (TextView) findViewById(R.id.tv_desc_3);
        ibShare = (ImageButton) findViewById(R.id.ib_share);


        ColorDrawable dropDownBackgroundDrawable = new ColorDrawable(Color.parseColor("#f2ffffff"));

        btnSetReminder.setOnClickListener(setReminderClickListener);
        ibSearchClear.setOnClickListener(searchClearListener);
        ibBannerClear.setOnClickListener(bannerClearListener);
        ibLocateMe.setOnClickListener(setLocateMeClickListener);
        ibShare.setOnClickListener(shareClickListener);

        etSearch.setDropDownBackgroundDrawable(dropDownBackgroundDrawable);
        etSearch.setAdapter(getSearchAdapter());
        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                setLocation(position);
            }
        });
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(etSearch.isFocused()) {
                    ibSearchClear.setVisibility(View.VISIBLE);
                } else {
                    ibSearchClear.setVisibility(View.INVISIBLE);
                }

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && ibSearchClear.getVisibility() != View.VISIBLE && etSearch.isFocused()) {
                    ibSearchClear.setVisibility(ImageButton.VISIBLE);
                } else if (s.length() == 0 && ibSearchClear.getVisibility() != View.INVISIBLE) {
                    ibSearchClear.setVisibility(ImageButton.INVISIBLE);
                }
            }
        });

    }

    private LocationSearchAdapter getSearchAdapter() {
        if (searchAdapter == null) {
            searchAdapter = new LocationSearchAdapter(this, R.layout.location_search_item);
        }
        return searchAdapter;
    }

    private View.OnClickListener setReminderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Do stuff for setting reminder
            if(UserUtil.isCarParked()) {
                startParkActivity();
            } else {
                setReminder();
            }
        }
    };

    private View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startShareActivity();
        }
    };

    private View.OnClickListener searchClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etSearch.setText("");
        }
    };


    private View.OnClickListener bannerClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setBannerInVisible();
        }
    };

    private View.OnClickListener setLocateMeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setAnimate(true);
            setListenToGps(true);
        }
    };

    public void setLocation(int position) {
        LocationSearchAdapter.PlaceResult result = getSearchAdapter().getItem(position);

        String address = result.getDescription();
        if (address != null) {
            etSearch.setText(address);
        }

        etSearch.dismissDropDown();
        etSearch.setSelection(etSearch.length());
        String reference = result.getReference();
        RequestManager.requestPlaceDetails(reference);
    }


    private void placeMarkerCurrentLocation() {
        setAccuracy(OmniModel.currentLocation.getAccuracy());
        zoomToLocation(OmniModel.currentLocation);
    }

    private void zoomToLocation(Location location) {
        zoomToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void zoomToLocation(LatLng location) {
        if (location != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(
                            new LatLng(location.latitude, location
                                    .longitude), DEF_ZOOM
                    );
            if (isAnimate()) {
                mMap.animateCamera(cameraUpdate);
                setAnimate(false);
            } else {
                mMap.moveCamera(cameraUpdate);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
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
            mMap.setOnCameraChangeListener(mCameraChangeListener);
            mMap.setOnMarkerClickListener(mSingleMarkerClickListener);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    if (isListenToGps() && (lastPingDate == null || new Date().getTime() - lastPingDate.getTime() > 2000)) {
                        lastPingDate = new Date();
                        setAccuracy(arg0.getAccuracy());
                        setUserModifiedLocation(false);
                        zoomToLocation(arg0);
                    }

                }
            });
        }
    }

    private void getNearbySigns(double lat, double lng, float accuracy) {
        OmniModel.currentLatLng = new LatLng(lat, lng);
        ApiClient.getApiInterface().getSigns(FieldUtil.getSignsMap(lat, lng, accuracy), new Callback<List<Sign>>() {
            @Override
            public void success(List<Sign> signs, Response response) {
                LogUtil.i(LogUtil.RetroFit, "Getting signs was a success with size: " + signs.size());
                drawParkingSigns(signs);
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtil.e(LogUtil.RetroFit, "Getting signs was a failure");
            }
        });
    }

    private void drawParkingSigns(List<Sign> signs) {
        currentSigns = signs;
        for (Sign sign : signs) {
            if (!mapMarkers.containsKey(sign.getObjectId())) {
                mapMarkers.put(sign.getObjectId(), sign);
                if (!sign.isNoParkingTime() && !sign.isMeterParking() && !(sign.getRestrictions() != null && sign.getRestrictions().size() > 0))
                    continue;

                if (sign.isNoParkingTime()) {
                    addSign(sign.getLocation(), sign.getObjectId(), R.drawable.no_parking);
                } else if (sign.isRestrictionValid()) {
                    addSign(sign.getLocation(), sign.getObjectId(), R.drawable.maybe_parking);
                } else {
                    addSign(sign.getLocation(), sign.getObjectId(), R.drawable.yes_parking);
                }
            }
        }
    }


    private void addSign(LatLng latLng, String message, int imageSign) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(imageSign))
                .snippet(message));
    }

    private GoogleMap.OnCameraChangeListener mCameraChangeListener = new GoogleMap.OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            LogUtil.i(LogUtil.LifeCycle, "Camera moved to: " + cameraPosition.target.latitude + " "
                    + cameraPosition.target.longitude);
            if (isUserModifiedLocation()) {
                getNearbySigns(cameraPosition.target.latitude, cameraPosition.target.longitude, Constants.userSpecifiedLocationAccuracy);
                setLastLatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                setListenToGps(false);
                performSearch(cameraPosition);
            } else if (getLastLatLng() == null || SphericalUtil.computeDistanceBetween(getLastLatLng(), new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)) > 5) {
                setUserModifiedLocation(true);
                getNearbySigns(cameraPosition.target.latitude, cameraPosition.target.longitude, getAccuracy());
                setLastLatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                performSearch(cameraPosition);
            }
        }
    };

    private void performSearch(CameraPosition cameraPosition) {
        if (!isSearch()) {
            setDestinationText("Getting address...");
            RequestManager.requestAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
        }
        setSearch(false);
    }

    private void setDestinationText(String text) {
        etSearch.setFocusable(false);
        etSearch.setFocusableInTouchMode(false);
        etSearch.setText(text);
        etSearch.setFocusable(true);
        etSearch.setFocusableInTouchMode(true);
    }

    protected GoogleMap.OnMarkerClickListener mSingleMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        public boolean onMarkerClick(Marker marker) {
            setBanner(marker.getSnippet());
            if (lastOpened != null) {
                lastOpened.hideInfoWindow();
                if (lastOpened.equals(marker)) {
                    lastOpened = null;
                    return true;
                }
            }
            marker.showInfoWindow();
            lastOpened = marker;
            return true;
        }
    };

    private void setBanner(String markerId) {
        Sign currentSign = mapMarkers.get(markerId);
        Set<String> currentRestrictions = new HashSet<String>();
        setBannerInVisible();
        setBannerVisibility(currentRestrictions);
        if (currentSign.isNoParkingTime()) {
            currentRestrictions.add("No Parking Anytime");
        } else {
            currentRestrictions = currentSign.getRestrictionStrings();
            if (currentSign.isMeterParking()) {
                currentRestrictions.add(String.valueOf(currentSign.getMeterParkingLimit()) + "-HOUR PARKING ONLY");
            }
        }
        setBannerTextColor(currentSign);
        setBannerVisibility(currentRestrictions);
    }

    private void setBannerTextColor(Sign sign) {
        if (sign.isNoParkingTime()) {
            ViewUtil.setTextColor(getResources().getColor(R.color.red), tvDesc1, tvDesc2, tvDesc3);
        } else if (sign.isRestrictionValid()) {
            ViewUtil.setTextColor(getResources().getColor(R.color.orange), tvDesc1, tvDesc2, tvDesc3);
        } else {
            ViewUtil.setTextColor(getResources().getColor(R.color.green), tvDesc1, tvDesc2, tvDesc3);
        }

    }

    private void setBannerVisibility(Set<String> messages) {
        String[] currentRestrictions = messages.toArray(new String[messages.size()]);

        switch (messages.size()) {
            case 0:
                setBannerInVisible();
                break;
            case 1:
                setBannerVisible();
                ViewUtil.setVisibility(View.VISIBLE, tvDesc1);
                ViewUtil.setVisibility(View.GONE, tvDesc2, tvDesc3);
                tvDesc1.setText(currentRestrictions[0]);
                break;
            case 2:
                setBannerVisible();
                ViewUtil.setVisibility(View.VISIBLE, tvDesc1, tvDesc2);
                ViewUtil.setVisibility(View.GONE, tvDesc3);
                tvDesc1.setText(currentRestrictions[0]);
                tvDesc2.setText(currentRestrictions[1]);
                break;
            default:
                setBannerVisible();
                ViewUtil.setVisibility(View.VISIBLE, tvDesc1, tvDesc2, tvDesc3);
                tvDesc1.setText(currentRestrictions[0]);
                tvDesc2.setText(currentRestrictions[1]);
                tvDesc3.setText(currentRestrictions[2]);
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        etSearch.requestFocus();
        BusProvider.getInstance().post(new CancelAudioServiceEvent());
        setParkingBtnText();
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), getString(R.string.fb_app_id));
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void receivePlacesEvent(PlaceEvent event) {
        setAnimate(true);
        setUserModifiedLocation(true);
        setSearch(true);
        zoomToLocation(new LatLng(event.lat, event.lng));
    }

    @Subscribe
    public void receiveGeoCodeEvent(GeocodeEvent event) {
        if (event == null || event.shortAddress == null)
            return;

        setDestinationText(event.shortAddress);
    }

    public boolean isUserModifiedLocation() {
        return userModifiedLocation;
    }

    public void setUserModifiedLocation(boolean userModifiedLocation) {
        this.userModifiedLocation = userModifiedLocation;
    }

    public boolean isListenToGps() {
        return listenToGps;
    }

    public void setListenToGps(boolean listenToGps) {
        this.listenToGps = listenToGps;
    }

    public void setLastLatLng(double lat, double lng) {
        this.lastLatLng = new LatLng(lat, lng);
    }

    public LatLng getLastLatLng() {
        return lastLatLng;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void closeKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public boolean isSearch() {
        return search;
    }

    protected void createAlert(String title, String message, DialogInterface.OnClickListener listener, String positiveText) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if (title != null)
            alert.setTitle(title);

        alert.setMessage(message + "\n");
        alert.setPositiveButton(positiveText, listener);
        alert.show();
    }

    private DialogInterface.OnClickListener onErrorClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Do something when they click
        }
    };

    private void showDatePicker(int year, int month, int day, int hour, int minutes) {
        dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_date_time_picker);
        dialog.setTitle("Set Parking Reminder");
        tpReminderTime = (TimePicker) dialog.findViewById(R.id.timePicker1);
        dpReminderDate = (DatePicker) dialog.findViewById(R.id.datePicker1);
        dpReminderDate.updateDate(year, month - 1, day);
        tpReminderTime.setCurrentHour(hour);
        tpReminderTime.setCurrentMinute(minutes);
        tvTimeDesc = (TextView) dialog.findViewById(R.id.tv_time_desc);
        btnReminder = (Button) dialog.findViewById(R.id.btn_reminder);
        btnReminder.setOnClickListener(reminderClickListener);

        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker1);
        dp.setCalendarViewShown(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                btnSetReminder.setVisibility(View.VISIBLE);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                btnSetReminder.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    private Sign findNearestSign() {
        double low = 1000;
        Sign nearestSign = null;
        for (Sign sign : currentSigns) {
            double distance = SphericalUtil.computeDistanceBetween(OmniModel.currentLatLng, sign.getLocation());
            if (distance < low && !sign.isNoParkingTime() && sign.getRestrictions() != null && sign.getRestrictions().size() > 0) {
                nearestSign = sign;
                low = distance;
            }
        }

        return nearestSign;
    }

    private void setReminder() {
        Sign nearestSign = findNearestSign();

        DateTime now = new DateTime();
        DateTime next;

        if (nearestSign == null) {
            next = now.plusHours(1);
        } else if (nearestSign.isMeterParking()) {
            next = now.plusHours(nearestSign.getMeterParkingLimit());
        } else {
            next = nearestSign.getNextDateTimeRestriction();
        }

        showDatePicker(next.getYear(), next.getMonthOfYear(), next.getDayOfMonth(), next.getHourOfDay(), next.getMinuteOfHour());

    }

    protected void setBannerVisible() {
        rlBanner.setAnimation(AnimationUtils.loadAnimation(
                this, R.anim.slide_down));
        rlBanner.setVisibility(View.VISIBLE);
    }

    protected void setBannerInVisible() {
        rlBanner.clearAnimation();
        rlBanner.setVisibility(View.GONE);
    }

    private void setAlarm(DateTime alarmTime) {
        pi = PendingIntent.getBroadcast(this, 0, new Intent("com.example.jaskirat.alternate_parking"),
                0);

        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        long millis = alarmTime.getMillis() - new DateTime().getMillis();
        LogUtil.e(LogUtil.Test, "The millis is: " + millis);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + millis, pintent);

    }

    private void setAlarmDetails(DateTime alarmTime) {
        if(alarmTime == null || getLastLatLng() == null) {
            createAlert("Error", "Something went wrong, please try again.", onErrorClickListener, "ok");
            return;
        }

        UserUtil.setCarParking(true);
        UserUtil.setLat(getLastLatLng().latitude);
        UserUtil.setLng(getLastLatLng().longitude);
        if(etSearch.getText() != null) {
            UserUtil.setAddress(etSearch.getText().toString());
        } else {
            UserUtil.setAddress("Address n/a");
        }
        UserUtil.setAlarmTime(alarmTime.getMillis());
        setAlarm(alarmTime);
        startParkActivity();
    }

    public void startParkActivity() {
        Intent intent = new Intent(MainActivity.this, ParkActivity.class);
        startActivity(intent);
    }

    public void startShareActivity() {
        Intent intent = new Intent(MainActivity.this, ShareActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void receiveStartReminderEvent(StartReminderEvent event) {
        BusProvider.getInstance().post(new CancelAudioServiceEvent());
    }

    private View.OnClickListener reminderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setAlarmDetails(new DateTime(dpReminderDate.getYear(), dpReminderDate.getMonth() + 1, dpReminderDate.getDayOfMonth(), tpReminderTime.getCurrentHour(), tpReminderTime.getCurrentMinute()));
            if(dialog != null)
                dialog.dismiss();

        }
    };

    private void setParkingBtnText() {
        if(UserUtil.isCarParked()) {
            btnSetReminder.setText(R.string.view_location_text);
        } else {
            btnSetReminder.setText(R.string.set_reminder_text);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
