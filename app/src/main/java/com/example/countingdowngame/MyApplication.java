package com.example.countingdowngame;

import android.app.Application;

public class MyApplication extends Application {

    private boolean isAppRunning = true;

    public boolean isAppRunning() {
        return isAppRunning;
    }

    public void setAppRunning(boolean isAppRunning) {
        this.isAppRunning = isAppRunning;
    }
}