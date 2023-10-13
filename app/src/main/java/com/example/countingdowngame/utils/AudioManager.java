package com.example.countingdowngame.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.countingdowngame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private List<Integer> backgroundMusicList;
    private Random random = new Random();

    private AudioManager() {
        backgroundMusicList = new ArrayList<>();
        backgroundMusicList.add(R.raw.backgroundmusic1);
        backgroundMusicList.add(R.raw.backgroundmusic2);
        backgroundMusicList.add(R.raw.backgroundmusic3);
        backgroundMusicList.add(R.raw.backgroundmusic4);
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void initialize(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        } else {
            Log.e("AudioManager", "Failed to create MediaPlayer");
        }
    }

    public void playRandomBackgroundMusic(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        int randomIndex = random.nextInt(backgroundMusicList.size());
        int soundResourceId = backgroundMusicList.get(randomIndex);

        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isPlaying = true;
        } else {
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
