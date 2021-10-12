package com.example.advancetask.ThreadingTasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.advancetask.R;
import com.example.advancetask.ServiceTask.DownloadFileActivity;


public class ThreadsActivity extends AppCompatActivity {


    private static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);
        Log.d(TAG, "onCreate: called !");
        findViewById(R.id.startThread).setOnClickListener(v -> startThread());
        findViewById(R.id.stopThread).setOnClickListener(v -> stopThread());

    }


    private void startThread() {

        Runnable runnable = () -> {
            Log.d(TAG, "onRun: Thread Called !");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };

        Thread thread = new Thread(runnable);
        thread.start();
        Log.d(TAG, "End: End of Thread !");

    }

    private void stopThread() {
        startActivity(new Intent(ThreadsActivity.this, DownloadFileActivity.class));
    }

}