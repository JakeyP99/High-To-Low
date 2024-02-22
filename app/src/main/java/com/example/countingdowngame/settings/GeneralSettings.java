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
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;

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


        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);
    }





    //-----------------------------------------------------Button Clicks---------------------------------------------------//

    @Override
    public void onClick(View view) {
        savePreferences();
    }


    private void setButtonListeners() {
        btnUtils.setButton(btnReturn, () -> {
            savePreferences();
            super.onBackPressed();
        });

        button_regularSound.setOnClickListener(this);
        button_burpSound.setOnClickListener(this);
    }


    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {
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
        }


    private void savePreferences() {
        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setShouldPlayRegularSound(button_regularSound.isSelected());
    }
}