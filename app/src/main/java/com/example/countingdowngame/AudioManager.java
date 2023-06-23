package com.example.countingdowngame;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying = false;


    private AudioManager() {
        // Private constructor to enforce singleton pattern
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    public void initialize(Context context, int soundResourceId) {
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        } else {
            // Handle the case when MediaPlayer creation fails
            // For example, you can log an error message
            Log.e("AudioManager", "Failed to create MediaPlayer");
        }
    }


    public void playSound() {
        if (mediaPlayer != null && !isPlaying) {
            if (currentPosition == 0) {
                mediaPlayer.start();
            } else {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
            isPlaying = true;
        }
    }

    public void stopSound() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }
    }

}
