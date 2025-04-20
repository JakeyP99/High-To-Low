package com.example.countingdowngame.home;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtils;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {
    private GifImageView muteGif, soundGif, drinkGif;
    private ButtonUtils buttonUtils;

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
        Button btnPlayCards = findViewById(R.id.playCards);
        Button btnInstructions = findViewById(R.id.button_Instructions);

        // Set onClickListener for buttons
        btnUtils.setButton(btnQuickPlay, () -> {
            Game.getInstance().setPlayCards(false);
            goToGameModeChoice();
        });

        btnUtils.setButton(btnPlayCards, () -> {
            Game.getInstance().setPlayCards(true);
            gotoPlayerNumberChoice();
        });
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        drinkGifFunctionality();
    }


    public void drinkGifFunctionality() {
        drinkGif.setOnClickListener(view -> {
            AudioManager audioManager = AudioManager.getInstance();
            audioManager.playNextSong();
        });

        drinkGif.setOnLongClickListener(view -> {
            GeneralSettingsLocalStore settingsStore = GeneralSettingsLocalStore.fromContext(this);
            boolean regularSoundSelected = settingsStore.shouldPlayRegularSound();
            settingsStore.setShouldPlayRegularSound(!regularSoundSelected);
            buttonUtils.playSoundEffects();
            buttonUtils.vibrateDevice();

            String message = regularSoundSelected ? "Burp sound effects activated!" : "Bop sound effects activated!";
            StyleableToast.makeText(this, message, R.style.newToast).show();

            return true;
        });
    }

}