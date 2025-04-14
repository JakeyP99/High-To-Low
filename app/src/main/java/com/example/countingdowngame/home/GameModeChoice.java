package com.example.countingdowngame.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import pl.droidsonroids.gif.GifImageView;

public class GameModeChoice extends ButtonUtilsActivity {
    private GifImageView muteGif, soundGif;
    private static boolean isOnlineGame = false;

    public static boolean isOnlineGame() {
        return isOnlineGame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().resumeBackgroundMusic();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_choice);
        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
    }

    private void setupButtonControls() {
        Button btnOfflinePlay = findViewById(R.id.button_offlineGame);
        Button btnOnlinePlay = findViewById(R.id.button_onlineGame);

        // Set onClickListener for buttons
        btnUtils.setButton(btnOfflinePlay, () -> {
            isOnlineGame = false;
            Log.d("PlayerChoice", "isOnlineGame: " + isOnlineGame);
            gotoPlayerNumberChoice();
        });

        btnUtils.setButton(btnOnlinePlay, () -> {
            isOnlineGame = true;
            Log.d("PlayerChoice", "isOnlineGame: " + isOnlineGame);
            goToServerFindClass();
        });
    }


}