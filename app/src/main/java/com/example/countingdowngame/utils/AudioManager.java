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
    private int currentSongIndex = -1;
    private Context context;

    private final List<Integer> backgroundMusicList;

    private AudioManager() {
        backgroundMusicList = new ArrayList<>();
        backgroundMusicList.add(R.raw.backgroundmusic1);
        backgroundMusicList.add(R.raw.backgroundmusic2);
        backgroundMusicList.add(R.raw.backgroundmusic3);
        backgroundMusicList.add(R.raw.backgroundmusic4);
    }

    private final MediaPlayer.OnCompletionListener onCompletionListener = mp -> playNextSong();

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
        currentSongIndex = new Random().nextInt(backgroundMusicList.size());
        int soundResourceId = backgroundMusicList.get(currentSongIndex);

        mediaPlayer = MediaPlayer.create(context, soundResourceId);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.start();
            isPlaying = true;
        } else {
            Log.e("AudioManager", "Failed to create MediaPlayer");
        }
    }

    private void playNextSong() {
        if (isPlaying) {
            currentSongIndex = (currentSongIndex + 1) % backgroundMusicList.size();

            int soundResourceId = backgroundMusicList.get(currentSongIndex);
            mediaPlayer = MediaPlayer.create(context, soundResourceId);
            mediaPlayer.start();
        }
    }

    public void playNextSongPublic(Context context) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset(); // Resetting the MediaPlayer
            }
            currentSongIndex = (currentSongIndex + 1) % backgroundMusicList.size();

            int soundResourceId = backgroundMusicList.get(currentSongIndex);
            mediaPlayer = MediaPlayer.create(context, soundResourceId);
            mediaPlayer.start();
            Log.d("TAG", "Play NextSong");
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
