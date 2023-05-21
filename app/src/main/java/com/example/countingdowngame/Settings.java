package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;

public class Settings extends ButtonUtilsActivity {

    @Override
    public void onBackPressed() {
        startActivity(getSafeIntent(HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b1_settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnWildCardSettings = findViewById(R.id.button_wildcardSettings);
        final Button btnGameModeSettings = findViewById(R.id.button_gameModeSettings);
        final Button btnPlayerModelSettings = findViewById(R.id.button_playerSettings);

        btnUtils.setButton(btnReturn, HomeScreen.class, null);
        btnUtils.setButton(btnWildCardSettings, Settings_WildCard_Choice.class, null);
        btnUtils.setButton(btnGameModeSettings, Settings_GameModeChoice.class, null);
        btnUtils.setButton(btnPlayerModelSettings, Settings_PlayerModel.class, null);
    }
}
