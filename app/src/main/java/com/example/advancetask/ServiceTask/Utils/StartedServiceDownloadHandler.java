package com.example.advancetask.ServiceTask.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.advancetask.ServiceTask.StartedServiceActivity.INTENT_KEY;

public class StartedServiceDownloadHandler extends Handler {

    public static final String BROADCAST_KEY = "RECEIVER_KEY";
    private static final String TAG = "MyTag";
    private DownloadStartedService mStartedService;
    private Context mContext;


    public StartedServiceDownloadHandler() {

    }

    @Override
    public void handleMessage(@NonNull Message msg) {

        String song = msg.obj.toString();
        int startId = msg.arg1;
        downloadSong(song);
        mStartedService.stopSelfResult(startId);
        sendMessageToActivity(song);


    }

    private void sendMessageToActivity(String song) {

        Intent intent = new Intent(BROADCAST_KEY);
        intent.putExtra(INTENT_KEY, song);
        LocalBroadcastManager.getInstance(mContext)
                .sendBroadcast(intent);


    }

    private void downloadSong(String songName) {
        Log.d(TAG, "downloadSong : starting download " + songName);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "downloadSong : song downloaded " + songName);
    }


    public void setStartedService(DownloadStartedService mStartedService) {
        this.mStartedService = mStartedService;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
