package com.example.advancetask.ServiceTask;

import static com.example.advancetask.Utils.Constants.BROADCAST_KEY;
import static com.example.advancetask.Utils.Constants.MyTag;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.advancetask.R;
import com.example.advancetask.ServiceTask.Utils.MusicStartedBindService;

public class MusicPlayerActivity extends AppCompatActivity {

    private Button playMusicButton, stopService;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String status = intent.getStringExtra("song_status");
            if (status.equals("Done")) {
                playMusicButton.setText("Play");
            }

        }
    };
    private boolean mBound;
    private MusicStartedBindService musicStartedBindService;
    private MusicStartedBindService.MyServiceBinder myServiceBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(MyTag, "onServiceConnected: called");
            mBound = true;
            myServiceBinder = (MusicStartedBindService.MyServiceBinder) iBinder;
            musicStartedBindService = myServiceBinder.getService();

            if (musicStartedBindService.isPlaying())
                playMusicButton.setText("Pause");
            else
                playMusicButton.setText("Play");


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(MyTag, "onServiceDisconnected: called");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        playMusicButton = findViewById(R.id.startService);
        stopService = findViewById(R.id.stopService);

        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBound) {
                    if (musicStartedBindService.isPlaying()) {
                        musicStartedBindService.pausePlaying();
                        playMusicButton.setText("Play");
                    } else {
                        Intent intent = new Intent(MusicPlayerActivity.this, MusicStartedBindService.class);
                        startService(intent);
                        musicStartedBindService.startPlaying();
                        playMusicButton.setText("Pause");
                    }
                }


            }
        });


        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayerActivity.this, MusicStartedBindService.class);
                stopService(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MusicPlayerActivity.this, MusicStartedBindService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_KEY));


    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);


    }
}