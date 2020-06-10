package com.example.myapplication.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.example.myapplication.R;


/**
 * Project Name:learnDaemon
 * Package Name:com.lahm.learndaemon.bgmusic
 * Created by lahm on 2018/3/5 上午9:54 .
 */

public class BgPlayService extends Service {
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                R.raw.silent);
        mMediaPlayer.setLooping(true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startPlayMusic();
            }
        }).start();
        return START_STICKY;
    }


    private void startPlayMusic() {
        if (mMediaPlayer != null) {
//            mMediaPlayer.setVolume(0,0);
            mMediaPlayer.start();
        }
    }


    private void stopPlayMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        // 重启
        Intent intent = new Intent(getApplicationContext(), BgPlayService.class);
        startService(intent);
    }

    @Override

    public void onTaskRemoved(Intent rootIntent){

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        restartServiceIntent.setPackage(getPackageName());



        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        alarmService.set(

                AlarmManager.ELAPSED_REALTIME,

                SystemClock.elapsedRealtime() + 1000,

                restartServicePendingIntent);



        super.onTaskRemoved(rootIntent);

    }
}
