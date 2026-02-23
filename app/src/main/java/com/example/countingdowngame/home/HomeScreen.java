package com.example.countingdowngame.home;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.playerChoice.PlayerChoice;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtils;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {

    private GifImageView muteGif;
    private GifImageView soundGif;
    private GifImageView drinkGif;
    private ButtonUtils buttonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_main_activity);

        buttonUtils = new ButtonUtils(this);

        initViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtons();
        setupDrinkGif();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.resumeBackgroundMusic();
        AudioManager.updateMuteButton(getMuteSoundState(), muteGif, soundGif);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
    }

    private void initViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        drinkGif = findViewById(R.id.drinkGif);
    }

    private void setupButtons() {
        setGameButton(R.id.quickplay, false);
        setGameButton(R.id.playCards, true);

        buttonUtils.setButton(findViewById(R.id.button_Instructions), this::gotoInstructions);
        buttonUtils.setButton(findViewById(R.id.button_Statistics), this::gotoStatistics);
    }

    private void setGameButton(int buttonId, boolean playCards) {
        Button button = findViewById(buttonId);
        buttonUtils.setButton(button, () -> {
            Game.getInstance().setPlayCards(playCards);
            gotoPlayerChoice();
        });
    }

    private void setupDrinkGif() {
        drinkGif.setOnClickListener(v -> {
            if (!getMuteSoundState()) {
                AudioManager.getInstance().playNextSong();
            }
        });

        drinkGif.setOnLongClickListener(v -> {
            if (!getMuteSoundState()) {
                toggleSoundEffects();
                return true;
            }
            return false; // no action when muted
        });
    }



    private void toggleSoundEffects() {
        GeneralSettingsLocalStore settings = GeneralSettingsLocalStore.fromContext(this);
        boolean regularSound = settings.shouldPlayRegularSound();

        settings.setShouldPlayRegularSound(!regularSound);
        buttonUtils.playSoundEffects();
        buttonUtils.vibrateDevice();

        String message = regularSound
                ? "Burp sound effects activated!"
                : "Bop sound effects activated!";

        StyleableToast.makeText(this, message, R.style.newToast).show();
    }
}
