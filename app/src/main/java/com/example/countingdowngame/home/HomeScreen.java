package com.example.countingdowngame.home;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtils;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {
    private GifImageView muteGif;
    private GifImageView soundGif;
    private GifImageView drinkGif;

    private ButtonUtils buttonUtils;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().resumeBackgroundMusic();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1_home_screen);
        buttonUtils = new ButtonUtils(this);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        drinkGif = findViewById(R.id.drinkGif);
    }


    private void setupButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);

        // Set onClickListener for buttons
        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);

        drinkGif.setOnClickListener(view -> {
            GeneralSettingsLocalStore settingsStore = GeneralSettingsLocalStore.fromContext(this);
            boolean regularSoundSelected = settingsStore.shouldPlayRegularSound();
            settingsStore.setShouldPlayRegularSound(!regularSoundSelected);
            buttonUtils.playSoundEffects();
            buttonUtils.vibrateDevice();
        });
    }



}