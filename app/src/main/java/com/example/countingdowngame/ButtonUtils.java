package com.example.countingdowngame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class ButtonUtils {
    public static ButtonUtils create(final AppCompatActivity context) {
        return new ButtonUtils(context);
    }

    private static final int NUM_SOUNDS = 4; // Number of sounds in the rotation
    private static int currentSoundIndex = 0; // Current index of the sound being played

    private final MediaPlayer[] burp = new MediaPlayer[NUM_SOUNDS];
    private final MediaPlayer bop;
    private final AppCompatActivity mContext;


    private ButtonUtils(final AppCompatActivity context) {
        mContext = context;
        burp[0] = MediaPlayer.create(context, R.raw.burp1);
        burp[1] = MediaPlayer.create(context, R.raw.burp2);
        burp[2] = MediaPlayer.create(context, R.raw.burp3);
        burp[3] = MediaPlayer.create(context, R.raw.burp4);
        bop = MediaPlayer.create(context, R.raw.bop);
    }

    public void onDestroy() {
        for (MediaPlayer b : burp) {
            b.release();
        }
        bop.release();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setButton(final Button button, final Class<?> activityClass, final Runnable buttonAction) {
        if (button == null) {
            return;
        }
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    button.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.buttonhighlight));

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    button.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.outlineforbutton));

                    if (activityClass != null) {
                        startActivity(activityClass);
                    }

                    if (buttonAction != null) {
                        buttonAction.run();
                    }

                    vibrateDevice();

                    playSoundEffects();

                    break;
                }
            }

            return true;
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setImageButton(final ImageButton imagebutton, final Class<?> activityClass, final Runnable buttonAction) {
        imagebutton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    imagebutton.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.buttonhighlight));

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    imagebutton.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.outlineforbutton));

                    if (activityClass != null) {
                        startActivity(activityClass);
                    }

                    if (buttonAction != null) {
                        buttonAction.run();
                    }

                    vibrateDevice();

                    break;
                }
            }

            return true;
        });
    }

    private void startActivity(final Class<?> activityClass) {
        Intent intent = new Intent(mContext, activityClass);
        mContext.startActivity(intent);
        new Handler().postDelayed(mContext::finish, 1000);
    }

    private void playSoundEffects() {
        SharedPreferences preferences = mContext.getSharedPreferences("sound_mode_choice", Context.MODE_PRIVATE);
        boolean soundEffects = preferences.getBoolean("button_regularSound", true);

        if (soundEffects) {
            // Play the bop sound effect
            bop.start();
        } else {
            // Update the current sound index and handle overflow
            currentSoundIndex = (currentSoundIndex + 1) % NUM_SOUNDS;

            // Stop any previously playing sound
            try {
                stopCurrentSound();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Play the current sound
            burp[currentSoundIndex].start();
        }
    }

    private void stopCurrentSound() throws IOException {
        if (burp[currentSoundIndex] != null) {
            burp[currentSoundIndex].stop();
            burp[currentSoundIndex].prepare();
        }
    }


    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }
    }
}

