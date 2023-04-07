package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_layout);
        final Button btnBack = findViewById(R.id.nextButton);

        btnBack.setOnClickListener(v -> startActivity(new Intent(Instructions.this, HomeScreen.class)));
    }
}
