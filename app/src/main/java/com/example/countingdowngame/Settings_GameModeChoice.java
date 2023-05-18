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
    private Drawable outlineforbutton;


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
        outlineforbutton = getResources().getDrawable(R.drawable.outlineforbutton);

        btnReturn = findViewById(R.id.buttonReturn);

        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean buttonOneSelected = preferences.getBoolean("button_gameModeOne", true);

        button_gameModeOne.setSelected(buttonOneSelected);
        button_gameModeTwo.setSelected(!buttonOneSelected);


        ButtonUtils.setButton(btnReturn, HomeScreen.class, this, null);


        if (buttonOneSelected) {
            button_gameModeOne.setBackground(buttonHighlightDrawable);
            button_gameModeTwo.setBackground(outlineforbutton);
        } else {
            button_gameModeOne.setBackground(outlineforbutton);
            button_gameModeTwo.setBackground(buttonHighlightDrawable);
        }

        button_gameModeOne.setOnClickListener(view -> {
            toggleButton(button_gameModeOne, button_gameModeTwo);
            savePreferences();
        });

        button_gameModeTwo.setOnClickListener(view -> {
            toggleButton(button_gameModeTwo, button_gameModeOne);
            savePreferences();
        });

        SharedPreferences soundPreferences = getSharedPreferences("sound_mode_choice", MODE_PRIVATE);
        boolean regularSoundSelected = soundPreferences.getBoolean("button_regularSound", true);

        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);

        if (regularSoundSelected) {
            button_regularSound.setBackground(buttonHighlightDrawable);
            button_burpSound.setBackground(outlineforbutton);
            savePreferences();

        } else {
            button_regularSound.setBackground(outlineforbutton);
            button_burpSound.setBackground(buttonHighlightDrawable);
            savePreferences();

        }

        button_regularSound.setOnClickListener(view -> {
            toggleButton(button_regularSound, button_burpSound);
            savePreferences();
        });

        button_burpSound.setOnClickListener(view -> {
            toggleButton(button_burpSound, button_regularSound);
            savePreferences();
        });
    }


    private void toggleButton(Button selectedButton, Button unselectedButton) {
        boolean isSelected = !selectedButton.isSelected();
        selectedButton.setSelected(isSelected);
        unselectedButton.setSelected(!isSelected);

        if (isSelected) {
            selectedButton.setBackground(buttonHighlightDrawable);
            unselectedButton.setBackground(outlineforbutton);
        } else {
            selectedButton.setBackground(outlineforbutton);
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
