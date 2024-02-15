package com.example.countingdowngame.mainActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {
    GifImageView muteGif;
    GifImageView soundGif;
    AudioManager audioManager = AudioManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1_home_screen);

        // Initialize GifImageViews after setting the content view
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        // Restore mute/sound state
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().updateMuteSoundButtons(isMuted, audioManager, muteGif, soundGif);

        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods
        setupAudioManager();
        setupAudioManagerAndButtonControls();

        // Check if the mute button is not selected before starting the music
        if (!isMuted) {
            if (!audioManager.isPlaying()) {
                audioManager.playRandomBackgroundMusic(getApplicationContext()); // Initialize and start playing music
            }
        }
    }


    private void setupAudioManagerAndButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);
        Button btnSettings = findViewById(R.id.button_Settings);

        // Set onClickListener for mute button

        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        btnUtils.setButton(btnSettings, this::gotoSettings);
    }

    private void setupAudioManager() {
        muteGif.setOnClickListener(view -> {

            saveMuteSoundState(true); // Save the mute state
            AudioManager.getInstance().updateMuteSoundButtons(false, audioManager, muteGif, soundGif); // Update the visibility of buttons
        });

        // Set onClickListener for sound button
        soundGif.setOnClickListener(view -> {

            saveMuteSoundState(false); // Save the sound state
            AudioManager.getInstance().updateMuteSoundButtons(true, audioManager, muteGif, soundGif); // Update the visibility of buttons
        });

    }

    private void saveMuteSoundState(boolean isMuted) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isMuted", isMuted);
        editor.apply();
    }

    // Retrieve the mute/sound state
    private boolean getMuteSoundState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isMuted", false); // Default to false if not found
    }

    // Update the visibility of mute/sound buttons based on the mute/sound state

}