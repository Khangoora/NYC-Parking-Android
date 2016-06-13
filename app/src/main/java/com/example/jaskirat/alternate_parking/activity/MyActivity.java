package com.example.jaskirat.alternate_parking.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.event.CancelAudioServiceEvent;
import com.example.jaskirat.alternate_parking.event.LocationEvent;
import com.example.jaskirat.alternate_parking.event.StartReminderEvent;
import com.example.jaskirat.alternate_parking.service.ServiceManager;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.example.jaskirat.alternate_parking.util.UserUtil;
import com.squareup.otto. Subscribe;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_my);
        initView();
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), getString(R.string.fb_app_id));
    }

    private TextView tvTitle, tvSub;

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSub = (TextView) findViewById(R.id.tv_sub);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        checkGpsAndStartLocationService();
        BusProvider.getInstance().post(new CancelAudioServiceEvent());
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), getString(R.string.fb_app_id));
    }

    private void checkGpsAndStartLocationService() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createAlert("Enable GPS", "Please enable to use the app.", onClickGPSSettings, "Enable GPS");
        } else {
            ServiceManager.startLocationService(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void receiveLocation(LocationEvent event) {
        LogUtil.i(LogUtil.Message, "Received location from location service, start MainActivity");
        if(UserUtil.isCarParked()) {
            startParkActivity();
        } else {
            startMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public void startMainActivity() {
        Intent intent = new Intent(MyActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startParkActivity() {
        Intent intent = new Intent(MyActivity.this, ParkActivity.class);
        startActivity(intent);
    }

    private DialogInterface.OnClickListener onClickGPSSettings = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    };

    protected void createAlert(String title, String message, DialogInterface.OnClickListener listener, String positiveText) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if (title != null)
            alert.setTitle(title);

        alert.setMessage(message + "\n");
        alert.setPositiveButton(positiveText, listener);
        alert.show();
    }

    @Subscribe
    public void receiveStartReminderEvent(StartReminderEvent event) {
        BusProvider.getInstance().post(new CancelAudioServiceEvent());
    }
}
