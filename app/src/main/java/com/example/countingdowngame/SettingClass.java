package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;

public class SettingClass extends ButtonUtilsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnWildCardSettings = findViewById(R.id.button_wildcardSettings);
        final Button btnGameModeSettings = findViewById(R.id.button_gameModeSettings);
        final Button btnPlayerModelSettings = findViewById(R.id.button_playerSettings);

        btnUtils.setButton(btnReturn, this::onBackPressed);

        btnUtils.setButton(btnWildCardSettings, () -> {
            startActivity(getIntentForClass(Settings_WildCardChoice.class));
        });

        btnUtils.setButton(btnGameModeSettings, () -> {
            startActivity(getIntentForClass(Settings_GameModeChoice.class));
        });

        btnUtils.setButton(btnPlayerModelSettings, () -> {
            startActivity(getIntentForClass(Settings_PlayerModel.class));
        });
    }
}
