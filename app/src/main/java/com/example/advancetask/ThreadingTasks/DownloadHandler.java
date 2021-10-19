package com.example.advancetask.ThreadingTasks;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.advancetask.ThreadingTasks.Interfaces.FileDownloaderListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DownloadHandler extends Handler {

    private static final String TAG = "MyTag";
    private FileDownloaderListener downloadListener;

    DownloadHandler() {

    }

    @Override
    public void handleMessage(@NonNull Message msg) {

        Playlist playlist = (Playlist) msg.obj;
        downloadSong(playlist.getSongName());
        downloadListener.onDownloadCompleteListener(playlist);


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

    private void downloadRealSong(Playlist playlist) {
        Log.d(TAG, "downloadSong : ---> started downloading :" + playlist.getSongName());
        String urlPath = playlist.getSongUrl();
        String fileName = playlist.getSongName();

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

            } finally {
                output.close();
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "downloadSong: ---> finished downloading :" + playlist.getSongName());
    }

    public void setDownloadListener(FileDownloaderListener downloadListener) {
        this.downloadListener = downloadListener;
    }


}

