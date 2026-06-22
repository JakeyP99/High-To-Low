package com.example.countingdowngame;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countingdowngame.audio.AudioManager;

public class MainApplication extends Application {
    private int activityCount = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pauseRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        AudioManager.getInstance().setContext(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (pauseRunnable != null) {
                    handler.removeCallbacks(pauseRunnable);
                    pauseRunnable = null;
                }
                activityCount++;
                if (activityCount == 1) {
                    AudioManager.getInstance().resumeBackgroundMusic();
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    pauseRunnable = () -> {
                        if (activityCount == 0) {
                            AudioManager.getInstance().pauseSound();
                        }
                    };
                    handler.postDelayed(pauseRunnable, 200); // 500ms delay to handle transitions/rotations
                }
            }

            @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
            @Override public void onActivityResumed(@NonNull Activity activity) {}
            @Override public void onActivityPaused(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }
}
