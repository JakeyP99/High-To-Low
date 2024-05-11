package com.example.countingdowngame.utils;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import java.io.IOException;

public class ButtonUtils {
    private static final int NUM_SOUNDS = 4; // Number of sounds in the rotation
    private static int currentSoundIndex = 0; // Current index of the sound being played
    private final MediaPlayer[] burp = new MediaPlayer[NUM_SOUNDS];
    private final MediaPlayer bop;
    private final AppCompatActivity mContext;
    private final Drawable buttonHighlight;
    private boolean isMuted = false;

    public ButtonUtils(final AppCompatActivity context) {
        mContext = context;
        burp[0] = MediaPlayer.create(context, R.raw.burp1);
        burp[1] = MediaPlayer.create(context, R.raw.burp2);
        burp[2] = MediaPlayer.create(context, R.raw.burp3);
        burp[3] = MediaPlayer.create(context, R.raw.burp4);
        bop = MediaPlayer.create(context, R.raw.bop);
        buttonHighlight = AppCompatResources.getDrawable(mContext, R.drawable.buttonhighlight);
    }

    //-----------------------------------------------------Sound Functionality---------------------------------------------------//
    public void toggleMute() {
        isMuted = !isMuted;
    }

    private void stopAllSounds() throws IOException {
        bop.pause();  // Pause the regular sound effect

        for (MediaPlayer mediaPlayer : burp) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);  // Reset the sound to the beginning
            }
        }
    }

    public void playBurpSound() {
        if (!isMuted()) {
            Log.d("TAG", "playBurpSound: bop played");
            burp[1].start();
        }
    }

    public void playSoundEffects() {
        if (isMuted()) {
            return;
        }

        boolean soundEffectsChoice = GeneralSettingsLocalStore.fromContext(mContext).shouldPlayRegularSound();

        if (soundEffectsChoice) {
            bop.start();
            Log.d("TAG", "playSoundEffects: Bop is playing");
        } else {
            currentSoundIndex = (currentSoundIndex + 1) % NUM_SOUNDS;
            Log.d("TAG", "playSoundEffects: Burp is playing");
            try {
                stopAllSounds();
                burp[currentSoundIndex].start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void onDestroy() {
        for (MediaPlayer b : burp) {
            b.release();
        }
        bop.release();
    }


    private boolean isMuted() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isMuted", false); // Default to false if not found
    }
    //-----------------------------------------------------Onclick Functionality---------------------------------------------------//

    @SuppressLint("ClickableViewAccessibility")
    public void setButton(final Button button, final Runnable buttonAction) {
        if (button == null) {
            return;
        }

        final Drawable defaultBackground = button.getBackground(); // Store the default background

        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    button.setBackground(buttonHighlight);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    button.setBackground(defaultBackground);
                    break;
                }
            }
            return false;
        });

        button.setOnClickListener(view -> {
            if (buttonAction != null) {

                button.setEnabled(false);

                new Handler().postDelayed(() -> button.setEnabled(true), 1500);

                buttonAction.run();
            }
            vibrateDevice();
            playSoundEffects();
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setButtonWithoutEffects(final Button button, final Runnable buttonAction) {
        if (button == null) {
            return;
        }
        button.setOnClickListener(view -> {
            if (buttonAction != null) {
                buttonAction.run();
            }
            vibrateDevice();
        });
    }


    //-----------------------------------------------------Vibrate Functionality---------------------------------------------------//

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

