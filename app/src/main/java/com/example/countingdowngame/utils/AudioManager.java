package com.example.countingdowngame.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.countingdowngame.R;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private static AudioManager instance;
    private final List<Integer> backgroundMusicList;

    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private int currentSongIndex = -1;

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

    private void setupPlayer(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPosition = 0;
        currentSongIndex = (currentSongIndex + 1) % backgroundMusicList.size();
        int soundResourceId = backgroundMusicList.get(currentSongIndex);
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
    }

    private void setupPlayer(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPosition = 0;
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
    }

    private void playNextSong(Context context) {
        if (isPlaying) {
            this.setupPlayer(context);

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(mp -> playNextSong(context));
                this.playSound();
            } else {
                Log.e("AudioManager", "Failed to create MediaPlayer");
            }
        }
    }

    public void playRandomBackgroundMusic(Context context) {
        if (isPlaying) {
            return;
        }

        if (mediaPlayer == null) {
            this.setupPlayer(context);
        } else {
            this.playSound();
        }

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> playNextSong(context));
            this.playSound();
        } else {
            Log.e("AudioManager", "Failed to create MediaPlayer");
        }
    }

    public void setupAndPlaySound(Context context, int soundResourceId) {
        if (isPlaying) {
            return;
        }

        this.setupPlayer(context, soundResourceId);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            this.playSound();
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
