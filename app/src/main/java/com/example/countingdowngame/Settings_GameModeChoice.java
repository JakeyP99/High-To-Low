package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Settings_GameModeChoice extends AppCompatActivity {

    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Drawable buttonHighlightDrawable;

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("button_gameModeOne", button_gameModeOne.isSelected());
        editor.putBoolean("button_gameModeTwo", button_gameModeTwo.isSelected());
        editor.apply();

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_choice);

        button_gameModeOne = findViewById(R.id.button_gameModeOne);
        button_gameModeTwo = findViewById(R.id.button_gameModeTwo);
        buttonHighlightDrawable = getResources().getDrawable(R.drawable.buttonhighlight);

        // Retrieve the saved state of the buttons from shared preferences
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean buttonOneSelected = preferences.getBoolean("button_gameModeOne", true); // Set default value to true

        // Set the buttons accordingly
        button_gameModeOne.setSelected(buttonOneSelected);
        button_gameModeTwo.setSelected(!buttonOneSelected); // Invert the selection for button_gameModeTwo

        // Apply button highlight drawable if buttons are selected
        if (buttonOneSelected) {
            button_gameModeOne.setBackground(buttonHighlightDrawable);
            button_gameModeTwo.setBackground(null);
        } else {
            button_gameModeOne.setBackground(null);
            button_gameModeTwo.setBackground(buttonHighlightDrawable);
        }

        // Add listeners to the buttons
        button_gameModeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !button_gameModeOne.isSelected();
                button_gameModeOne.setSelected(isSelected);
                button_gameModeTwo.setSelected(!isSelected);

                if (isSelected) {
                    button_gameModeOne.setBackground(buttonHighlightDrawable);
                    button_gameModeTwo.setBackground(null);
                } else {
                    button_gameModeOne.setBackground(null);
                    button_gameModeTwo.setBackground(buttonHighlightDrawable);
                }
            }
        });

        button_gameModeTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !button_gameModeTwo.isSelected();
                button_gameModeTwo.setSelected(isSelected);
                button_gameModeOne.setSelected(!isSelected);

                if (isSelected) {
                    button_gameModeTwo.setBackground(buttonHighlightDrawable);
                    button_gameModeOne.setBackground(null);
                } else {
                    button_gameModeTwo.setBackground(null);
                    button_gameModeOne.setBackground(buttonHighlightDrawable);
                }
            }
        });
    }
}