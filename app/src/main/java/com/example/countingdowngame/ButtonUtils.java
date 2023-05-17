package com.example.countingdowngame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class ButtonUtils {

    private static final int NUM_SOUNDS = 4; // Number of sounds in the rotation
    private static int currentSoundIndex = 0; // Current index of the sound being played
    private static MediaPlayer[] burp = new MediaPlayer[NUM_SOUNDS];

    public static void setButton(final Button button, final Class<?> activityClass, final Context context, final Runnable buttonAction) {
        burp[0] = MediaPlayer.create(context, R.raw.burp1);
        burp[1] = MediaPlayer.create(context, R.raw.burp2);
        burp[2] = MediaPlayer.create(context, R.raw.burp3);
        burp[3] = MediaPlayer.create(context, R.raw.burp4);

        final MediaPlayer bop = MediaPlayer.create(context, R.raw.bop);

        SharedPreferences preferences = context.getSharedPreferences("sound_mode_choice", Context.MODE_PRIVATE);
        boolean soundEffects = preferences.getBoolean("button_regularSound", false);

        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackground(context.getDrawable(R.drawable.buttonhighlight));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                button.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                if (activityClass != null) {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                }
                if (buttonAction != null) {
                    buttonAction.run();
                }
                vibrateDevice(context);

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
            return true;
        });
    }

    private static void stopCurrentSound() throws IOException {
        if (burp[currentSoundIndex] != null) {
            burp[currentSoundIndex].stop();
            burp[currentSoundIndex].prepare();
        }
    }
    public static void setImageButton(final ImageButton imagebutton, final Class<?> activityClass, final Context context, final Runnable buttonAction) {
        imagebutton.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                imagebutton.setBackground(context.getDrawable(R.drawable.buttonhighlight));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imagebutton.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                if (activityClass != null) {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                }
                if (buttonAction != null) {
                        buttonAction.run();
                    }
                    vibrateDevice(context);
                }
                return true;
            });
        }


        public static void vibrateDevice(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
            }
        }}

