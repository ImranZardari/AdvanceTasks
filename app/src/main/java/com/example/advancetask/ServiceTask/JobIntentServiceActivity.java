package com.example.advancetask.ServiceTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.advancetask.R;
import com.example.advancetask.ServiceTask.Utils.DownloadJobIntentService;

import org.jetbrains.annotations.NotNull;


public class JobIntentServiceActivity extends AppCompatActivity {

    String docFile_url = "https://file-examples-com.github.io/uploads/2017/02/file-sample_100kB.doc";
    String mp3_url = "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3";
    String mp4_url = "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4";
    String jpg_url = "https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_2500kB.jpg";

    private TextView downloadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        downloadStatus = findViewById(R.id.download_status);
        Button btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(onDownloadListener());

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadJobIntentService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(JobIntentServiceActivity.this, "File downloaded!", Toast.LENGTH_LONG).show();
                    sendMyNotification("Download Completed");
                    downloadStatus.setText("Download completed!");
                } else {
                    Toast.makeText(JobIntentServiceActivity.this, "Error Downloading process!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download failed!");
                }
            }
        }
    };
    ActivityResultLauncher<Intent> downloadActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                Toast.makeText(JobIntentServiceActivity.this, "Permission Granted in Android 11", Toast.LENGTH_SHORT).show();
                            } else {
                                takePermission();
                            }
                        }

                    }
                }
            });

    private View.OnClickListener onDownloadListener() {
        return v -> {
            if (isPermissionGranted()) {
                //todo : --->  we can make another approach to pass different file types and file names
                downloadFile("file_example.doc", docFile_url);


            } else
                takePermissions();
        };
    }

    private void downloadFile(String fileName, String fileUrl) {
        Intent intent = new Intent(JobIntentServiceActivity.this, DownloadJobIntentService.class);
        intent.putExtra(DownloadJobIntentService.FILENAME, fileName);
        intent.putExtra(DownloadJobIntentService.URL, fileUrl);
        startService(intent);
        downloadStatus.setText("Downloading...");
        Toast.makeText(JobIntentServiceActivity.this, "Already got access permission", Toast.LENGTH_SHORT).show();

    }

    private void sendMyNotification(String message) {

        Intent intent = new Intent(this, JobIntentServiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //todo --> below line of code is used to  set your sound to notification from raw (asset file) <----
        //Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.correct_answer);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                //.setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            // Creating Channel
            NotificationChannel notificationChannel = new NotificationChannel("CH_ID","Testing_Audio",NotificationManager.IMPORTANCE_HIGH);
            // notificationChannel.setSound(soundUri,audioAttributes);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        mNotificationManager.notify(0, notificationBuilder.build());
    }

    private boolean isPermissionGranted() {
        //for Android 11
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        //for Android 10
        else {
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermission() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                downloadActivityResultLauncher.launch(intent);
            } catch (Exception exception) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                downloadActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == Activity.RESULT_OK) {
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage) {
                    Toast.makeText(this, "Permission Granted in android 10 or below ", Toast.LENGTH_SHORT).show();
                } else {
                    takePermission();
                }
            }
        }

    }

    private void takePermissions() {
        if (isPermissionGranted()) {
            Toast.makeText(JobIntentServiceActivity.this, "Already Permission Granted", Toast.LENGTH_SHORT).show();

        } else {
            takePermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadJobIntentService.NOTIFICATION));
    }

}