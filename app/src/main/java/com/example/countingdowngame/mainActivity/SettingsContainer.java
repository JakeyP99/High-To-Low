package com.example.countingdowngame.mainActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.GeneralSettings;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.wildCards.WildCardChoice;

public class SettingsContainer extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b1_settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnWildCardChoice = findViewById(R.id.button_wildcardChoice);
        final Button btnGameModeSettings = findViewById(R.id.button_GameModeChoice);

        btnUtils.setButton(btnReturn, this::onBackPressed);

        btnUtils.setButton(btnWildCardChoice, () -> {
            startActivity(getIntentForClass(WildCardChoice.class));
        });

        btnUtils.setButton(btnGameModeSettings, () -> {
            startActivity(getIntentForClass(GeneralSettings.class));
        });

    }
}
