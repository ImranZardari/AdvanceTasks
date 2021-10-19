package com.example.advancetask.ThreadingTasks;

import android.os.Looper;

public class DownloadThread extends Thread {

    public DownloadHandler downloadHandler;

    public DownloadThread() {
    }

    @Override
    public void run() {

        Looper.prepare();
        downloadHandler = new DownloadHandler();
        Looper.loop();

    }


}
