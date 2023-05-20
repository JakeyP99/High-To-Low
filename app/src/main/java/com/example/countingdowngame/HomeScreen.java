package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;

public class HomeScreen extends ButtonUtilsActivity {

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        final Button btnQuickPlay = findViewById(R.id.quickplay);
        final Button btnInstructions = findViewById(R.id.button_Instructions);
        final Button btnSettings = findViewById(R.id.button_Settings);

        btnUtils.setButton(btnQuickPlay, PlayerSelection.class, null);
        btnUtils.setButton(btnInstructions, Instructions.class, null);
        btnUtils.setButton(btnSettings, SettingClass.class, null);
    }

}




