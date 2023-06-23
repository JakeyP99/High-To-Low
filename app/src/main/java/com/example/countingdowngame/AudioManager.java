package com.example.countingdowngame;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying = false;


      public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void initialize(Context context, String soundFileName) {
        int soundResourceId = context.getResources().getIdentifier(soundFileName, "raw", context.getPackageName());
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        mediaPlayer.setLooping(true);
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
