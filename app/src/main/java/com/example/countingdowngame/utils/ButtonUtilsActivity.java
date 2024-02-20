package com.example.countingdowngame.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.countingdowngame.instructions.InstructionsToPlay;
import com.example.countingdowngame.mainActivity.EndActivityGame;
import com.example.countingdowngame.mainActivity.HomeScreen;
import com.example.countingdowngame.mainActivity.NumberChoice;
import com.example.countingdowngame.mainActivity.PlayerNumberChoice;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import pl.droidsonroids.gif.GifImageView;

public abstract class ButtonUtilsActivity extends AppCompatActivity {

    //todo my pop noise doesnt work

    protected ButtonUtils btnUtils;
    protected Drawable buttonHighlightDrawable;
    protected Drawable outlineForButton;
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

    protected void gotoHomeScreen() {
        startActivity(getIntentForClass(HomeScreen.class));
    }

    protected void gotoGameEnd() {
        startActivity(getIntentForClass(EndActivityGame.class));
    }

    protected void gotoNumberChoice() {
        startActivity(getIntentForClass(NumberChoice.class));
    }

    protected void gotoPlayerNumberChoice() {
        startActivity(getIntentForClass(PlayerNumberChoice.class));
    }

    protected void gotoInstructions() {
        startActivity(getIntentForClass(InstructionsToPlay.class));
    }


    //-----------------------------------------------------Sound Functionality---------------------------------------------------//

    public void onMuteClicked(View view) {
        Button btnMute = (Button) view;
        boolean isMuted = !btnMute.isSelected();
        btnMute.setSelected(isMuted);
        Drawable selectedDrawable = isMuted ? buttonHighlightDrawable : outlineForButton;
        btnMute.setBackground(selectedDrawable);
        btnUtils.toggleMute();
    }

    public void setupAudioManagerForMuteButtons(GifImageView muteGif, GifImageView soundGif) {
        audioManager = AudioManager.getInstance();
        audioManager.setContext(getApplicationContext());
        boolean isMuted = GeneralSettingsLocalStore.fromContext(this).isMuted();
        audioManager.updateMuteSoundButtons(isMuted, audioManager, muteGif, soundGif);

        muteGif.setOnClickListener(view -> audioManager.updateMuteSoundButtons(false, audioManager, muteGif, soundGif));
        soundGif.setOnClickListener(view -> audioManager.updateMuteSoundButtons(true, audioManager, muteGif, soundGif));
        saveMuteSoundState(isMuted);

    }

    private void saveMuteSoundState(boolean isMuted) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isMuted", isMuted);
        editor.apply();
    }

    private boolean getMuteSoundState() {
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
