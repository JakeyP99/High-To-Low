package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;

public class HomeScreen extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        setupButtonControls();
    }

    private void setupView() {
        setContentView(R.layout.a1_home_screen);
    }

    private void setupButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);
        Button btnSettings = findViewById(R.id.button_Settings);
        btnUtils.setButton(btnQuickPlay, this::gotoGameSetup);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        btnUtils.setButton(btnSettings, this::gotoSettings);
    }
}




