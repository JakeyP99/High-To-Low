package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings_GameModeChoice extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;
    private Button btnMute;

    //copyout
    private Button button_regularSound;
    private Button button_burpSound;
    //

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
    }

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        button_gameModeOne = findViewById(R.id.button_gameModeOne);
        button_gameModeTwo = findViewById(R.id.button_gameModeTwo);
        buttonHighlightDrawable = getResources().getDrawable(R.drawable.buttonhighlight);
        outlineForButton = getResources().getDrawable(R.drawable.outlineforbutton);
        btnReturn = findViewById(R.id.buttonReturn);
        btnMute = findViewById(R.id.button_mute);
        //copyout
        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);
    }

    //-----------------------------------------------------Button Clicks---------------------------------------------------//

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_gameModeOne:
                toggleButton(button_gameModeOne, button_gameModeTwo);
                break;
            case R.id.button_gameModeTwo:
                toggleButton(button_gameModeTwo, button_gameModeOne);
                break;
            case R.id.button_mute:
                toggleMuteButton();
                break;
            //copyout
            case R.id.button_normal_sound:
                toggleButton(button_regularSound, button_burpSound);
                break;
            case R.id.button_burp_sound:
                toggleButton(button_burpSound, button_regularSound);
                break;
            //
        }
        savePreferences();
    }

    private void setButtonListeners() {
        button_gameModeOne.setOnClickListener(this);
        button_gameModeTwo.setOnClickListener(this);
        btnUtils.setButton(btnReturn, this::onBackPressed);
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

        btnUtils.toggleMute(); // Toggle the mute state
    }


    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {
        //copyout
        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        boolean regularSoundSelected = soundPreferences.getBoolean("button_regularSound", true);
        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);
//
        if (regularSoundSelected) {
            button_regularSound.setBackground(buttonHighlightDrawable);
            button_burpSound.setBackground(outlineForButton);
        } else {
            button_regularSound.setBackground(outlineForButton);
            button_burpSound.setBackground(buttonHighlightDrawable);
        }

        SharedPreferences gameModePreferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean buttonOneSelected = gameModePreferences.getBoolean("button_gameModeOne", true);
        button_gameModeOne.setSelected(buttonOneSelected);
        button_gameModeTwo.setSelected(!buttonOneSelected);

        if (buttonOneSelected) {
            button_gameModeOne.setBackground(buttonHighlightDrawable);
            button_gameModeTwo.setBackground(outlineForButton);
        } else {
            button_gameModeOne.setBackground(outlineForButton);
            button_gameModeTwo.setBackground(buttonHighlightDrawable);
        }

        SharedPreferences mutePreferences = getSharedPreferences("mute_state", MODE_PRIVATE);
        boolean isMuted = mutePreferences.getBoolean("isMuted", false);
        btnMute.setSelected(isMuted);

        if (isMuted) {
            btnMute.setBackground(buttonHighlightDrawable);
        } else {
            btnMute.setBackground(outlineForButton);
        }


    }


    private void savePreferences() {
        SharedPreferences gameModePreferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor gameModeEditor = gameModePreferences.edit();

        gameModeEditor.putBoolean("button_gameModeOne", button_gameModeOne.isSelected());
        gameModeEditor.putBoolean("button_gameModeTwo", button_gameModeTwo.isSelected());
        gameModeEditor.apply();

        SharedPreferences mutePreferences = getSharedPreferences("mute_state", MODE_PRIVATE);
        SharedPreferences.Editor muteEditor = mutePreferences.edit();

        boolean isMuted = btnMute.isSelected();
        muteEditor.putBoolean("isMuted", isMuted);
        muteEditor.apply();

        //copyout
        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor soundEditor = soundPreferences.edit();
        soundEditor.putBoolean("button_regularSound", button_regularSound.isSelected());
        soundEditor.apply();

    }
}