package com.example.countingdowngame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
    private static final int NUM_SOUNDS = 4; // Number of sounds in the rotation
    private static int currentSoundIndex = 0; // Current index of the sound being played
    private final MediaPlayer[] burp = new MediaPlayer[NUM_SOUNDS];
    private final MediaPlayer bop;
    private final AppCompatActivity mContext;
    private final Drawable buttonHighlight;
    private final Drawable outlineForButton;


    public ButtonUtils(final AppCompatActivity context) {
        mContext = context;
        burp[0] = MediaPlayer.create(context, R.raw.burp1);
        burp[1] = MediaPlayer.create(context, R.raw.burp2);
        burp[2] = MediaPlayer.create(context, R.raw.burp3);
        burp[3] = MediaPlayer.create(context, R.raw.burp4);
        bop = MediaPlayer.create(context, R.raw.bop);
        buttonHighlight = AppCompatResources.getDrawable(mContext, R.drawable.buttonhighlight);
        outlineForButton = AppCompatResources.getDrawable(mContext, R.drawable.outlineforbutton);
    }

    public void onDestroy() {
        for (MediaPlayer b : burp) {
            b.release();
        }
        bop.release();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setButton(final Button button, final Runnable buttonAction) {
        if (button == null) {
            return;
        }
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    button.setBackground(buttonHighlight);

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    button.setBackground(outlineForButton);

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
    public void setImageButton(final ImageButton imagebutton, final Runnable buttonAction) {
        imagebutton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    imagebutton.setBackground(buttonHighlight);

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    imagebutton.setBackground(outlineForButton);

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

    private void playSoundEffects() {
        SharedPreferences preferences = mContext.getSharedPreferences("sound_mode_choice", Context.MODE_PRIVATE);
        boolean soundEffects = preferences.getBoolean("button_regularSound", true);

        if (soundEffects) {
            bop.start();
        } else {
            currentSoundIndex = (currentSoundIndex + 1) % NUM_SOUNDS;
            try {
                stopCurrentSound();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

