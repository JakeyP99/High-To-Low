package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;

public class Settings extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b1_settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnWildCardChoice = findViewById(R.id.button_wildcardChoice);
        final Button btnWildCardSettings = findViewById(R.id.button_wildCardSettings);
        final Button btnGameModeSettings = findViewById(R.id.button_GameModeChoice);

        btnUtils.setButton(btnReturn, this::onBackPressed);

        btnUtils.setButton(btnWildCardChoice, () -> {
            startActivity(getIntentForClass(Settings_WildCard_Choice.class));
        });

        btnUtils.setButton(btnWildCardSettings, () -> {
            startActivity(getIntentForClass(Settings_WildCard_Settings.class));
        });

        btnUtils.setButton(btnGameModeSettings, () -> {
            startActivity(getIntentForClass(Settings_GameModeChoice.class));
        });

    }
}
