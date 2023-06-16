package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings_GameModeChoice extends ButtonUtilsActivity implements View.OnClickListener {
    private Button button_gameModeOne;
    private Button button_gameModeTwo;

    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;
    private Button btnMute;

    private static int wildcardAmount; // Change to static variable
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
    public static int getWildcardAmount() {
        return wildcardAmount;
    }
    //copyout
    private Button button_regularSound;
    private Button button_burpSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_game_mode_choice);
        initializeViews();
        loadPreferences();
        setButtonListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_gameModeOne:
                toggleButton(button_gameModeOne, button_gameModeTwo);
                break;
            case R.id.button_gameModeTwo:
                toggleButton(button_gameModeTwo, button_gameModeOne);
                break;

            //copyout
            case R.id.button_normal_sound:
                toggleButton(button_regularSound, button_burpSound);
                break;
            case R.id.button_burp_sound:
                toggleButton(button_burpSound, button_regularSound);
                break;
            case R.id.button_mute:
                toggleMuteButton();
                break;
        }
        savePreferences();
    }

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

    private void loadPreferences() {
        //copyout
        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        boolean regularSoundSelected = soundPreferences.getBoolean("button_regularSound", true);
        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);

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

        // Load wildcard amount
        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        wildcardAmount = wildcardPreferences.getInt("wildcardAmount", 1);
        EditText wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        wildcardPerPlayerEditText.setText(String.valueOf(wildcardAmount));

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

        // Save wildcard amount
        EditText wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        int wildcardAmount = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());

        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        SharedPreferences.Editor wildcardEditor = wildcardPreferences.edit();
        wildcardEditor.putInt("wildcardAmount", wildcardAmount);
        wildcardEditor.apply();

        // Update the wildcard amount for all players in the game
        Player player = new Player(null, null); // Create a player instance
        player.setWildCardAmount(wildcardAmount); // Call the method on the player instance

    }

}
