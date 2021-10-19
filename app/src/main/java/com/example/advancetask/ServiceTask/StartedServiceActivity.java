package com.example.advancetask.ServiceTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.advancetask.R;
import com.example.advancetask.ServiceTask.Utils.DownloadStartedService;
import com.example.advancetask.ServiceTask.Utils.StartedServiceDownloadHandler;

public class StartedServiceActivity extends AppCompatActivity {

    public static final String INTENT_KEY = "SONG_NAME";
    private static final String TAG = "MyTag";
    public ProgressDialog progressDialog;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onBroadCastReceive: songDownloaded : " + intent.getStringExtra(INTENT_KEY));
            Log.d(TAG, "onBroadCastReceive: " + Thread.currentThread().getName());
            progressDialog.dismiss();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(StartedServiceDownloadHandler.BROADCAST_KEY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_service);

        progressDialog = new ProgressDialog(this);
        findViewById(R.id.startStartedServiceButton).setOnClickListener(v -> {


            progressDialog.show();
            for (int i = 0; i < 2; i++) {
                Intent intent = new Intent(StartedServiceActivity.this, DownloadStartedService.class);
                intent.putExtra(INTENT_KEY, "Dil Dil Pakistan " + i);
                startService(intent);
            }


        });
        findViewById(R.id.stopStartedServiceButton).setOnClickListener(v -> {
            Intent intent = new Intent(StartedServiceActivity.this, DownloadStartedService.class);
            stopService(intent);
        });

    }


}