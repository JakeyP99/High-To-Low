package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreen extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // Do nothing
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        final Button btnQuickPlay = findViewById(R.id.quickplay);
        final Button btnInstructions = findViewById(R.id.instructions_button);


        btnQuickPlay.setOnClickListener(view -> {
            Vibrate.vibrateDevice(this);
            startActivity(new Intent(HomeScreen.this, PlayerNumber.class));
        });


        btnInstructions.setOnClickListener(view ->{
            Vibrate.vibrateDevice(this);
            startActivity(new Intent(HomeScreen.this, Instructions.class));
                });
    }}




