package com.example.countingdowngame.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.content.res.ResourcesCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

public class GeneralSettings extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;
    private Button btnMute;

    private Button button_regularSound;
    private Button button_burpSound;
    //
    private AudioManager audioManager; // Instantiate AudioManager in your activity or fragment

    //-----------------------------------------------------On Pause---------------------------------------------------//

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    //-----------------------------------------------------On Create---------------------------------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_game_mode_choice);
        initializeViews();
        loadPreferences();
        setButtonListeners();
        audioManager = AudioManager.getInstance(); // Initialize AudioManager instance

    }

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        buttonHighlightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.buttonhighlight, null);
        outlineForButton = ResourcesCompat.getDrawable(getResources(), R.drawable.outlineforbutton, null);
        btnReturn = findViewById(R.id.buttonReturn);
        btnMute = findViewById(R.id.button_mute);


        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);
    }





    //-----------------------------------------------------Button Clicks---------------------------------------------------//

    @Override
    public void onClick(View view) {
        int viewId = view.getId(); // Store the view ID in a variable

      if (viewId == R.id.button_mute) {
          toggleMuteButton();

      } else if (viewId == R.id.button_normal_sound) {
          toggleButton(button_regularSound, button_burpSound);
      } else if (viewId == R.id.button_burp_sound) {
          toggleButton(button_burpSound, button_regularSound);
          //
      }

        savePreferences();
    }


    private void setButtonListeners() {
        btnUtils.setButton(btnReturn, () -> {
            savePreferences();
            super.onBackPressed();
        });

        btnMute.setOnClickListener(this);
        //copyout
        button_regularSound.setOnClickListener(this);
        button_burpSound.setOnClickListener(this);
    }

    private void toggleButton(Button selectedButton, Button unselectedButton) {
        boolean isSelected = !selectedButton.isSelected();
        selectedButton.setSelected(isSelected);
        unselectedButton.setSelected(!isSelected);

        if (isSelected) {
            selectedButton.setBackground(buttonHighlightDrawable);
            unselectedButton.setBackground(outlineForButton);
        } else {
            selectedButton.setBackground(outlineForButton);
            unselectedButton.setBackground(buttonHighlightDrawable);
        }
    }

    private void toggleMuteButton() {
        boolean isSelected = !btnMute.isSelected();
        btnMute.setSelected(isSelected);

        Drawable selectedDrawable = isSelected ? buttonHighlightDrawable : outlineForButton;
        btnMute.setBackground(selectedDrawable);

        if (audioManager != null) {
            if (isSelected) {
                audioManager.stopSound();

            } else {
                if (!audioManager.isPlaying()) {
                    audioManager.playRandomBackgroundMusic(this);
                }
            }
        }
        // Optionally, update preferences or perform other tasks related to mute state change.
    }


    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {

        //copyout
        boolean regularSoundSelected = GeneralSettingsLocalStore.fromContext(this).shouldPlayRegularSound();
        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);

        if (regularSoundSelected) {
            button_regularSound.setBackground(buttonHighlightDrawable);
            button_burpSound.setBackground(outlineForButton);
        } else {
            button_regularSound.setBackground(outlineForButton);
            button_burpSound.setBackground(buttonHighlightDrawable);
        }


        boolean isMuted = GeneralSettingsLocalStore.fromContext(this).isMuted();
        btnMute.setSelected(isMuted);

        if (isMuted) {
            btnMute.setBackground(buttonHighlightDrawable);
            if (audioManager != null) {
                audioManager.stopSound();
            }
        } else {
            btnMute.setBackground(outlineForButton);
            if (audioManager != null && !audioManager.isPlaying()) {
                audioManager.playRandomBackgroundMusic(this);
            }
        }

    }


    private void savePreferences() {
        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setIsMuted(btnMute.isSelected());
        store.setShouldPlayRegularSound(button_regularSound.isSelected());
    }
}