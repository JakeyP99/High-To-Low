package com.example.countingdowngame.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.example.countingdowngame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class AudioManager {
    private static AudioManager instance;
    private final List<Integer> backgroundMusicList;
    public boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private int currentSongIndex = -1;
    private Context context;

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


    public void setContext(Context context) {
        this.context = context;
    }

    private final MediaPlayer.OnCompletionListener onCompletionListener = mp -> playNextSong();

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

    public void playNextSong() {
        if (context != null) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset(); // Resetting the MediaPlayer
                }
                currentSongIndex = (currentSongIndex + 1) % backgroundMusicList.size();

                int soundResourceId = backgroundMusicList.get(currentSongIndex);
                mediaPlayer = MediaPlayer.create(context, soundResourceId);

                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                    mediaPlayer.start();
                    Log.d("TAG", "Play NextSong");
                } else {
                    Log.e("AudioManager", "Failed to create MediaPlayer");
                }
            }
        } else {
            Log.e("AudioManager", "Context is null");
            // Handle the null context case, perhaps by logging an error or taking appropriate action.
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

    public void updateMuteSoundButtons(boolean isMuted, AudioManager audioManager, GifImageView muteGif, GifImageView soundGif) {
        if (audioManager != null) {
            if (isMuted) {
                Log.d(TAG, "updateMuteSoundButtons: mute gif should be visible");
                muteGif.setVisibility(View.VISIBLE);
                soundGif.setVisibility(View.INVISIBLE);

                audioManager.stopSound();
            } else if (!audioManager.isPlaying()) {
                Log.d(TAG, "updateMuteSoundButtons: sound gif should be visible");
                audioManager.playSound();

                muteGif.setVisibility(View.INVISIBLE);
                soundGif.setVisibility(View.VISIBLE);
            }
        }
    }
    public boolean isPlaying() {
        return isPlaying;
    }


}
