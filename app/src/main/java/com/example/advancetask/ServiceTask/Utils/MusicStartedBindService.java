package com.example.advancetask.ServiceTask.Utils;

import static com.example.advancetask.Utils.Constants.BROADCAST_KEY;
import static com.example.advancetask.Utils.Constants.MyTag;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.advancetask.R;

public class MusicStartedBindService extends Service {

    private final Binder iBinder = new MyServiceBinder();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MyTag, "onCreate: called");
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(BROADCAST_KEY);
                intent.putExtra("song_status", "Done");
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
            }
        });


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MyTag, "onStartCommand: called");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MyTag, "onBind: called");
        return iBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(MyTag, "onDestroy: called");
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(MyTag, "onUnbind: called");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(MyTag, "onRebind: called");
        super.onRebind(intent);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void startPlaying() {
        mediaPlayer.start();
    }

    public void pausePlaying() {
        mediaPlayer.pause();
    }

    public class MyServiceBinder extends Binder {

        public MusicStartedBindService getService() {
            return MusicStartedBindService.this;
        }

    }


}
