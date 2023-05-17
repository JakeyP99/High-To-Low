package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Settings_GameModeChoice extends AppCompatActivity {

    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Button button_regularSound;
    private Button button_burpSound;

    Button btnReturn;


    private Drawable buttonHighlightDrawable;

    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_choice);

        button_gameModeOne = findViewById(R.id.button_gameModeOne);
        button_gameModeTwo = findViewById(R.id.button_gameModeTwo);
        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);
        buttonHighlightDrawable = getResources().getDrawable(R.drawable.buttonhighlight);
        btnReturn = findViewById(R.id.buttonReturn);

        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean buttonOneSelected = preferences.getBoolean("button_gameModeOne", true);

        button_gameModeOne.setSelected(buttonOneSelected);
        button_gameModeTwo.setSelected(!buttonOneSelected);


        ButtonUtils.setButton(btnReturn, HomeScreen.class, this, () ->
        {
            savePreferences();
            super.onBackPressed();
        });


        if (buttonOneSelected) {
            button_gameModeOne.setBackground(buttonHighlightDrawable);
            button_gameModeTwo.setBackground(null);
        } else {
            button_gameModeOne.setBackground(null);
            button_gameModeTwo.setBackground(buttonHighlightDrawable);
        }

        button_gameModeOne.setOnClickListener(v -> toggleButton(button_gameModeOne, button_gameModeTwo));
        button_gameModeTwo.setOnClickListener(v -> toggleButton(button_gameModeTwo, button_gameModeOne));

        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        boolean regularSoundSelected = soundPreferences.getBoolean("button_regularSound", true);

        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);

        if (regularSoundSelected) {
            button_regularSound.setBackground(buttonHighlightDrawable);
            button_burpSound.setBackground(null);
        } else {
            button_regularSound.setBackground(null);
            button_burpSound.setBackground(buttonHighlightDrawable);
        }

        button_regularSound.setOnClickListener(v -> toggleButton(button_regularSound, button_burpSound));
        button_burpSound.setOnClickListener(v -> toggleButton(button_burpSound, button_regularSound));
    }

    private void toggleButton(Button selectedButton, Button unselectedButton) {
        boolean isSelected = !selectedButton.isSelected();
        selectedButton.setSelected(isSelected);
        unselectedButton.setSelected(!isSelected);

        if (isSelected) {
            selectedButton.setBackground(buttonHighlightDrawable);
            unselectedButton.setBackground(null);
        } else {
            selectedButton.setBackground(null);
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
