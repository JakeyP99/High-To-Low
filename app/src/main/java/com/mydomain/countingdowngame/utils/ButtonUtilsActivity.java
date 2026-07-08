package com.mydomain.countingdowngame.utils;

import static com.mydomain.countingdowngame.audio.AudioManager.updateMuteButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.endGame.EndActivityGame;
import com.mydomain.countingdowngame.endGame.EndRouletteGame;
import com.mydomain.countingdowngame.home.HomeScreen;
import com.mydomain.countingdowngame.instructions.InstructionsToPlay;
import com.mydomain.countingdowngame.numberChoice.NumberChoice;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.playerChoice.PlayerChoice;
import com.mydomain.countingdowngame.settings.SettingsMenu;
import com.mydomain.countingdowngame.statistics.Statistics;

import pl.droidsonroids.gif.GifImageView;

public abstract class ButtonUtilsActivity extends AppCompatActivity {

    public ButtonUtils btnUtils;

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
        return new Intent(this, targetClass);
    }

    public void gotoHomeScreen() {
        Intent i = getIntentForClass(HomeScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void gotoGameEnd() {
        startActivity(getIntentForClass(EndActivityGame.class));
    }

    public void gotoGameEndRoulette(Player activePlayer) {
        Intent intent = new Intent(this, EndRouletteGame.class);
        intent.putExtra("VICTOR_NAME", activePlayer.getName());
        startActivity(intent);
    }

    public void gotoNumberChoice() {
        startActivity(getIntentForClass(NumberChoice.class));
    }

    public void gotoInstructions() {
        startActivity(getIntentForClass(InstructionsToPlay.class));
    }

    public void gotoStatistics() {
        startActivity(getIntentForClass(Statistics.class));
    }

    public void gotoGame(int startingNumber) {
        Intent i = new Intent(this, com.mydomain.countingdowngame.mainActivity.MainActivityGame.class);
        i.putExtra("startingNumber", startingNumber);
        startActivity(i);
    }

    public void gotoSettings() {
        startActivity(getIntentForClass(SettingsMenu.class));
    }

    public void gotoPlayerChoice() {
        startActivity(getIntentForClass(PlayerChoice.class));
    }
    //-----------------------------------------------------Sound Functionality---------------------------------------------------//

    public void setupAudioManagerForMuteButtons(GifImageView muteGif, GifImageView soundGif) {
        AudioManager audioManager = AudioManager.getInstance();
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
            AudioManager.getInstance().resumeBackgroundMusic();
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

}
