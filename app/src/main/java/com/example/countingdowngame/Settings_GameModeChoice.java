package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class Settings_GameModeChoice extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // Save the state of the switches in shared preferences
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("switch_gameModeOne", switch_gameModeOne.isChecked());
        editor.putBoolean("switch_gameModeTwo", switch_gameModeTwo.isChecked());
        editor.apply();

        // Call super to close the activity
        super.onBackPressed();
    }

    private Switch switch_gameModeOne;
    private Switch switch_gameModeTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_choice);

        switch_gameModeOne = findViewById(R.id.switch_gameModeOne);
        switch_gameModeTwo = findViewById(R.id.switch_gameModeTwo);

        // Retrieve the saved state of the switches from shared preferences
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean switchOneChecked = preferences.getBoolean("switch_gameModeOne", false);
        boolean switchTwoChecked = preferences.getBoolean("switch_gameModeTwo", false);

        // Set the switches accordingly
        switch_gameModeOne.setChecked(switchOneChecked);
        switch_gameModeTwo.setChecked(switchTwoChecked);

        // Add listeners to the switches to toggle the other switch off
        switch_gameModeOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_gameModeTwo.setChecked(!isChecked);
            }
        });

        switch_gameModeTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_gameModeOne.setChecked(!isChecked);
            }
        });
    }

}
