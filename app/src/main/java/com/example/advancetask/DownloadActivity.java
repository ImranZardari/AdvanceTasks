package com.example.advancetask;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.advancetask.Utils.DownloadService;

import org.jetbrains.annotations.NotNull;

public class DownloadActivity extends AppCompatActivity {

    String download_url = "https://file-examples-com.github.io/uploads/2017/02/file-sample_100kB.doc";
    private TextView downloadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        downloadStatus = (TextView) findViewById(R.id.download_status);
        Button btnDownload = (Button) findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(onDownloadListener());


    }
    private View.OnClickListener onDownloadListener() {
        return new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
               if(isPermissionGranted()){

                   //todo : --->  we can make another intentPutExtra and treat different files types

                   Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
                   intent.putExtra(DownloadService.FILENAME, "doc_file.doc");
                   intent.putExtra(DownloadService.URL, download_url);
                   startService(intent);
                   downloadStatus.setText("Downloading...");
                   Toast.makeText(DownloadActivity.this, "Already got access permission", Toast.LENGTH_SHORT).show();

               }else
                   takePermissions();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(DownloadActivity.this, "File downloaded!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download completed!");
                } else {
                    Toast.makeText(DownloadActivity.this, "Error Downloading process!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download failed!");
                }
            }
        }
    };


    private void takePermissions() {
        if (isPermissionGranted()) {
            Toast.makeText(DownloadActivity.this, "Already Permission Granted", Toast.LENGTH_SHORT).show();

        } else {
            takePermission();
        }
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
                someActivityResultLauncher.launch(intent);
            } catch (Exception exception) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                someActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }




    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                Toast.makeText(DownloadActivity.this, "Permission Granted in Android 11", Toast.LENGTH_SHORT).show();
                            } else {
                                takePermission();
                            }
                        }

                    }
                }
            });



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


}