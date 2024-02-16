package com.example.countingdowngame.mainActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {
    private GifImageView muteGif;
    private GifImageView soundGif;
    private AudioManager audioManager = AudioManager.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        audioManager.updateMuteSoundButtons(isMuted, audioManager, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1_home_screen);

        // Initialize GifImageViews after setting the content view
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        // Restore mute/sound state
        boolean isMuted = GeneralSettingsLocalStore.fromContext(this).isMuted();
        audioManager.updateMuteSoundButtons(isMuted, audioManager, muteGif, soundGif);

        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods
        setupAudioManager();
        setupButtonControls();

        Log.d("HomeScreen", "onCreate: ");

        // Check if the mute button is not selected before starting the music
        if (!isMuted) {
            audioManager.playRandomBackgroundMusic(getApplicationContext()); // Initialize and start playing music
            Log.d("HomeScreen", "Background music started");
        }
    }

    private void setupButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);
        Button btnSettings = findViewById(R.id.button_Settings);

        // Set onClickListener for buttons
        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        btnUtils.setButton(btnSettings, this::gotoSettings);
    }

    private void setupAudioManager() {
        muteGif.setOnClickListener(view -> {
            saveMuteSoundState(false); // Save the mute state
            audioManager.updateMuteSoundButtons(false, audioManager, muteGif, soundGif);
        });

        soundGif.setOnClickListener(view -> {
            saveMuteSoundState(true); // Save the sound state
            audioManager.updateMuteSoundButtons(true, audioManager, muteGif, soundGif);
        });
    }

    private void saveMuteSoundState(boolean isMuted) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isMuted", isMuted);
        editor.apply();
        Log.d("HomeScreen", "Mute state saved: " + isMuted);
    }

    private boolean getMuteSoundState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isMuted", false); // Default to false if not found
    }
}
