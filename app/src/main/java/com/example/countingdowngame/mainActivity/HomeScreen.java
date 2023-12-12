package com.example.countingdowngame.mainActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        setupButtonControls();

        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods

        // Check if the mute button is not selected before starting the music
        if (!GeneralSettingsLocalStore.fromContext(this).isMuted()) {
            audioManager.playRandomBackgroundMusic(getApplicationContext()); // Initialize and start playing music
        }
    }


    private void setupView() {
        setContentView(R.layout.a1_home_screen);
    }

    private void setupButtonControls() {
        GifImageView gifDrink = findViewById(R.id.drinkGif);
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);
        Button btnSettings = findViewById(R.id.button_Settings);
        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        btnUtils.setButton(btnSettings, this::gotoSettings);

        gifDrink.setOnClickListener(view -> {
            AudioManager audioManager = AudioManager.getInstance();
            audioManager.playNextSong();
        });

    }
}




