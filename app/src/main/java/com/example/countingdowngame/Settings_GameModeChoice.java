package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Settings_GameModeChoice extends AppCompatActivity implements View.OnClickListener {
    private final ButtonUtils btnUtils = new ButtonUtils(this);

    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Button button_regularSound;
    private Button button_burpSound;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Settings_GameModeChoice.this, HomeScreen.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnUtils.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_choice);

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
            case R.id.button_normal_sound:
                toggleButton(button_regularSound, button_burpSound);
                break;
            case R.id.button_burp_sound:
                toggleButton(button_burpSound, button_regularSound);
                break;
        }
        savePreferences();
    }

    private void initializeViews() {
        button_gameModeOne = findViewById(R.id.button_gameModeOne);
        button_gameModeTwo = findViewById(R.id.button_gameModeTwo);
        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);
        buttonHighlightDrawable = getResources().getDrawable(R.drawable.buttonhighlight);
        outlineForButton = getResources().getDrawable(R.drawable.outlineforbutton);
        btnReturn = findViewById(R.id.buttonReturn);
    }

    private void loadPreferences() {
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
    }

    private void setButtonListeners() {
        button_gameModeOne.setOnClickListener(this);
        button_gameModeTwo.setOnClickListener(this);
        button_regularSound.setOnClickListener(this);
        button_burpSound.setOnClickListener(this);
        btnUtils.setButton(btnReturn, HomeScreen.class, null);
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

    private void savePreferences() {
        SharedPreferences gameModePreferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor gameModeEditor = gameModePreferences.edit();

        gameModeEditor.putBoolean("button_gameModeOne", button_gameModeOne.isSelected());
        gameModeEditor.putBoolean("button_gameModeTwo", button_gameModeTwo.isSelected());
        gameModeEditor.apply();

        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor soundEditor = soundPreferences.edit();

        soundEditor.putBoolean("button_regularSound", button_regularSound.isSelected());
        soundEditor.apply();
    }
}