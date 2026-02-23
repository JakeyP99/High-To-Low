package com.example.countingdowngame.utils;

import static com.example.countingdowngame.audio.AudioManager.updateMuteButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.endGame.EndRouletteGame;
import com.example.countingdowngame.instructions.InstructionsToPlay;
import com.example.countingdowngame.endGame.EndActivityGame;
import com.example.countingdowngame.home.HomeScreen;
import com.example.countingdowngame.numberChoice.NumberChoice;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.playerChoice.PlayerChoice;
import com.example.countingdowngame.settings.SettingsMenu;
import com.example.countingdowngame.statistics.Statistics;

import pl.droidsonroids.gif.GifImageView;

public abstract class ButtonUtilsActivity extends AppCompatActivity {

    protected ButtonUtils btnUtils;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnUtils = new ButtonUtils(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btnUtils != null) {
            btnUtils.onDestroy();
        }
    }

    protected Intent getIntentForClass(Class<?> targetClass) {
        Intent i = new Intent(this, targetClass);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    protected void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    protected void gotoHomeScreen() {
        startActivityWithAnimation(getIntentForClass(HomeScreen.class));
    }

    protected void gotoGameEnd() {
        startActivityWithAnimation(getIntentForClass(EndActivityGame.class));
    }

    protected void gotoGameEndRoulette(Player activePlayer) {
        Intent intent = new Intent(this, EndRouletteGame.class);
        intent.putExtra("VICTOR_NAME", activePlayer.getName());
        startActivityWithAnimation(intent);
    }

    protected void gotoNumberChoice() {
        startActivityWithAnimation(getIntentForClass(NumberChoice.class));
    }

    protected void gotoInstructions() {
        startActivityWithAnimation(getIntentForClass(InstructionsToPlay.class));
    }

    protected void gotoStatistics() {
        startActivityWithAnimation(getIntentForClass(Statistics.class));
    }

    protected void goToInGameSettings(int startingNumber) {
        Intent i = getIntentForClass(SettingsMenu.class);
        i.putExtra("startingNumber", startingNumber);
        startActivityWithAnimation(i);
    }

    protected void gotoPlayerChoice() {
        startActivityWithAnimation(getIntentForClass(PlayerChoice.class));
    }
    //-----------------------------------------------------Sound Functionality---------------------------------------------------//

    public void setupAudioManagerForMuteButtons(GifImageView muteGif, GifImageView soundGif) {
        audioManager = AudioManager.getInstance();
        audioManager.setContext(getApplicationContext());
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
        setupMuteSoundClickListeners(muteGif, soundGif);
        saveMuteSoundState(isMuted);
    }

    private void setupMuteSoundClickListeners(GifImageView muteGif, GifImageView soundGif) {
        muteGif.setOnClickListener(view -> {
            updateMuteButton(false, muteGif, soundGif);
            saveMuteSoundState(false);
            Log.d("TAG", "setupMuteSoundClickListeners: is muted is false");
        });
        soundGif.setOnClickListener(view -> {
            updateMuteButton(true, muteGif, soundGif);
            saveMuteSoundState(true);
            Log.d("TAG", "setupMuteSoundClickListeners: is muted is true");

        });
    }


    private void saveMuteSoundState(boolean isMuted) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isMuted", isMuted).apply();
    }


    public boolean getMuteSoundState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isMuted", false); // Default to false if not found
    }

    public void setupBackgroundMusic() {
        boolean isMuted = getMuteSoundState();
        if (!isMuted) {
            audioManager.playRandomBackgroundMusic(getApplicationContext());
            Log.d("HomeScreen", "Background music started");
        }
    }
}
