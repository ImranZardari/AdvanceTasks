package com.example.advancetask.ServiceTask.Utils;

import android.os.Looper;

public class StartedServiceDownloadThread extends Thread {

    public StartedServiceDownloadHandler mDownloadHandler;

    @Override
    public void run() {
        Looper.prepare();
        mDownloadHandler = new StartedServiceDownloadHandler();
        Looper.loop();
    }
}
