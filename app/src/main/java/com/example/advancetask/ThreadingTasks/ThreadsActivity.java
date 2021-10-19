package com.example.advancetask.ThreadingTasks;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.advancetask.R;
import com.example.advancetask.ThreadingTasks.Interfaces.FileDownloaderListener;

import org.jetbrains.annotations.NotNull;


public class ThreadsActivity extends AppCompatActivity implements FileDownloaderListener {


    private static final String TAG = "MyTag";
    public ProgressDialog progressDialog;
    ActivityResultLauncher<Intent> downloadActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                Toast.makeText(ThreadsActivity.this, "Permission Granted for Android 11", Toast.LENGTH_SHORT).show();
                            } else {
                                takePermission();
                            }
                        }

                    }
                }
            });
    private DownloadThread mDownloadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);

        progressDialog = new ProgressDialog(this);
        findViewById(R.id.startThread).setOnClickListener(v -> {
            if (isPermissionGranted()) {
                startThread();

            } else
                takePermissions();
        });


        mDownloadThread = new DownloadThread();
        mDownloadThread.setName("Nano_DownloadThread");
        mDownloadThread.start();


    }

    @Override
    public void onDownloadCompleteListener(Playlist playlist) {
        Log.d(TAG, "downloadCompleteListener : Song " + playlist.getSongName() + " Downloaded");
        progressDialog.dismiss();
    }

    private void startThread() {

        for (int i = 0; i < 2; i++) {
            Playlist playlist = new Playlist("file sample song " + i + ".mp3",
                    "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3");
            progressDialog.show();
            Message songsData = Message.obtain();
            songsData.obj = playlist;
            mDownloadThread.downloadHandler.sendMessage(songsData);
            mDownloadThread.downloadHandler.setDownloadListener(this);

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

    private void takePermissions() {
        if (isPermissionGranted()) {
            Toast.makeText(ThreadsActivity.this, "Already Permission Granted", Toast.LENGTH_SHORT).show();

        } else {
            takePermission();
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
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


}