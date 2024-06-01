package com.example.countingdowngame.audio;

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
    private static AudioManager audioManager;
    private final List<Integer> backgroundMusicList;
    public boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private int currentSongIndex = -1;
    private static Context context;

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private AudioManager() {
        backgroundMusicList = new ArrayList<>();
        backgroundMusicList.add(R.raw.backgroundmusic1);
        backgroundMusicList.add(R.raw.backgroundmusic2);
        backgroundMusicList.add(R.raw.backgroundmusic3);
        backgroundMusicList.add(R.raw.backgroundmusic4);
        backgroundMusicList.add(R.raw.backgroundmusic5);
        backgroundMusicList.add(R.raw.backgroundmusic6);
        backgroundMusicList.add(R.raw.backgroundmusic7);
        backgroundMusicList.add(R.raw.backgroundmusic8);
        backgroundMusicList.add(R.raw.backgroundmusic9);
        backgroundMusicList.add(R.raw.backgroundmusic10);
    }

    public static AudioManager getInstance() {
        if (audioManager == null) {
            audioManager = new AudioManager();
        }
        return audioManager;
    }

    public void setContext(Context context) {
        AudioManager.context = context;
    }


    //-----------------------------------------------------Play Functions---------------------------------------------------//

    private final MediaPlayer.OnCompletionListener onCompletionListener = mp -> playNextSong();

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
        if (mediaPlayer != null && context != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
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
        } else {
            Log.e("AudioManager", "Context or MediaPlayer is null");
        }
    }


    //-----------------------------------------------------Pause / Stop Functions---------------------------------------------------//


    public void pauseSound() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
            Log.d(TAG, "pauseSound: Paused sound");
        }
    }


    public void resumeBackgroundMusic() {
        if (mediaPlayer != null && !isPlaying) {
            // Check if there is a current song playing
            if (currentSongIndex != -1) {
                // Resume the current song from the last known position
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                isPlaying = true;
                Log.d(TAG, "resumeBackgroundMusic: Resumed background music");
            } else {
                // If there is no current song, play a random song
                playRandomBackgroundMusic(context);
            }
        }
    }

    public void playConfettiSound(Context context) {
        MediaPlayer confettiMediaPlayer = MediaPlayer.create(context, R.raw.party);
        if (confettiMediaPlayer != null) {
            confettiMediaPlayer.start();
            confettiMediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } else {
            Log.e("AudioManager", "Failed to create MediaPlayer for confetti sound");
        }
    }

    //-----------------------------------------------------Update UI---------------------------------------------------//


    public static void updateMuteButton(boolean isMuted, GifImageView muteGif, GifImageView soundGif) {
        if (isMuted) {
            audioManager.pauseSound();
            muteGif.setVisibility(View.VISIBLE);
            soundGif.setVisibility(View.INVISIBLE);
        } else {
            if (audioManager.isNotPlaying()) {
                if (audioManager.getCurrentSongIndex() != -1) {
                    audioManager.resumeBackgroundMusic();
                    Log.d(TAG, "updateMuteButton: resume");
                } else {
                    audioManager.playRandomBackgroundMusic(context);
                    Log.d(TAG, "updateMuteButton: no song to resume");
                }
            }
            muteGif.setVisibility(View.INVISIBLE);
            soundGif.setVisibility(View.VISIBLE);
        }
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }


    public boolean isNotPlaying() {
        return !isPlaying;
    }

}
