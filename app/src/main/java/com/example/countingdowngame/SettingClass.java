package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingClass extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingClass.this, HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnWildCardSettings = findViewById(R.id.button_wildcardSettings);
        final Button btnGameModeSettings = findViewById(R.id.button_gameModeSettings);
        final Button btnPlayerModelSettings = findViewById(R.id.button_playerSettings);

        ButtonUtils.setButton(btnReturn, HomeScreen.class, this, null);
        ButtonUtils.setButton(btnWildCardSettings, Settings_WildCardChoice.class, this, null);
        ButtonUtils.setButton(btnGameModeSettings, Settings_GameModeChoice.class, this, null);
        ButtonUtils.setButton(btnPlayerModelSettings, Settings_PlayerModel.class, this, null);

    }
}
