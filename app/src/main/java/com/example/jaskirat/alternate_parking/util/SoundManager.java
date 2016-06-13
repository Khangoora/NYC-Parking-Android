package com.example.jaskirat.alternate_parking.util;

import android.media.AudioManager;
import android.media.SoundPool;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.application.ParkingApp;


public class SoundManager {
    private SoundPool soundPool;
    private int soundPoolId;

    public void playNotificationSound() {
        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int soundID, int status) {
                soundPoolId = soundPool.play(soundID, 1, 1, 1, -1, 1);
            }

        });
        soundPool.load(ParkingApp.getInstance(), R.raw.newrequest, 1);
        soundPool.autoResume();
    }

    public void stopNotificationSound() {
        soundPool.stop(soundPoolId);
        soundPool.release();
    }
}
