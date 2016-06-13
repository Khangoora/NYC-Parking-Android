package com.example.jaskirat.alternate_parking.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.activity.MyActivity;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.event.CancelAudioServiceEvent;
import com.example.jaskirat.alternate_parking.event.StartReminderEvent;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.example.jaskirat.alternate_parking.util.SoundManager;
import com.squareup.otto.Subscribe;


public class AlarmService extends Service {
    public AlarmService() {
    }

    private int notifId = -1;
    private NotificationManager notifManager;
    private Vibrator vibrator;
    private SoundManager soundManager;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(LogUtil.LifeCycle, "Alarm Service is now running.");
        BusProvider.getInstance().register(this);
        BusProvider.getInstance().post(new StartReminderEvent());
        postNotification();
        ring();
        vibrate();
    }

    private void vibrate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = new long[]{0, 100, 1000};
        vibrator.vibrate(vibrationPattern, 0);
    }

    private void stopVibrating() {
        if(vibrator != null) {
            vibrator.cancel();
        }
    }

    private void postNotification() {
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifId = (int) System.currentTimeMillis();
        notifManager.notify(notifId, buildNotification());
    }

    private void cancelNotification() {
        if(notifManager != null && notifId != -1) {
            notifManager.cancel(notifId);
        }
    }

    private Notification buildNotification() {
        String content = "Time\'s up! Move your vehicle.";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("NYC Parking")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(buildNotificationIntent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setTicker(content)
                .setOngoing(true);
        return builder.build();
    }

    private PendingIntent buildNotificationIntent() {
        Intent intent = new Intent(this, MyActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }


    private void ring() {
        soundManager = new SoundManager();
        soundManager.playNotificationSound();
    }

    private void stopRing() {
        soundManager.stopNotificationSound();
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        stopVibrating();
        stopRing();
        LogUtil.d(LogUtil.LifeCycle, "onDestroy of NotificationService hit.");
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void receiveCancelEvent(CancelAudioServiceEvent event) {
        LogUtil.i(LogUtil.Otto, "Got a notification service shutdown event. shutting down...");
        stopSelf();
    }

}
