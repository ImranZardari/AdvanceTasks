package com.example.advancetask.ServiceTask.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DownloadJobIntentService extends JobIntentService {

    public static final String URL = "url";
    public static final String FILENAME = "name";
    public static final String FILEPATH = "path";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "notification";


    public DownloadJobIntentService() {
        super();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, DownloadJobIntentService.class, 123, work);
    }


    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        int result = Activity.RESULT_CANCELED;

        try {
            java.net.URL url = new URL(urlPath);
            InputStream input = url.openStream();

            File storagePath = new File(Environment.getExternalStorageDirectory() + "/Download");
            OutputStream output = new FileOutputStream(new File(storagePath, fileName));
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
                result = Activity.RESULT_OK;
            } finally {
                output.close();
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        publishResults(result);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

}
