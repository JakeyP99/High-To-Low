package com.example.countingdowngame.settings;

import android.os.Bundle;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

public class SettingsContainer extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b1_settings_choice);

        final Button btnReturn = findViewById(R.id.buttonReturn);
        final Button btnSettings = findViewById(R.id.button_GeneralSettings);

        btnUtils.setButton(btnReturn, this::onBackPressed);

        btnUtils.setButton(btnSettings, () -> startActivity(getIntentForClass(GeneralSettings.class)));

    }
}
