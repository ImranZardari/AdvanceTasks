package com.example.advancetask.ServiceTask.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import static com.example.advancetask.ServiceTask.StartedServiceActivity.INTENT_KEY;

public class DownloadStartedService extends Service {

    private static final String TAG = "MyTag";
    private StartedServiceDownloadThread mDownloadThread;

    public DownloadStartedService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate : called");
        mDownloadThread = new StartedServiceDownloadThread();
        mDownloadThread.start();
        while (mDownloadThread.mDownloadHandler == null) {

        }
        mDownloadThread.mDownloadHandler.setStartedService(this);
        mDownloadThread.mDownloadHandler.setContext(getApplicationContext());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand : called");

        final String songName = intent.getStringExtra(INTENT_KEY);
        Message message = Message.obtain();
        message.obj = songName;
        message.arg1 = startId;
        mDownloadThread.mDownloadHandler.sendMessage(message);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind : called");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy : called");
        super.onDestroy();
    }
}