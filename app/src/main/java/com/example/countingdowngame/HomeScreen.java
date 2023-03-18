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

         Button btnQuickPlay = findViewById(R.id.quickplay);

        btnQuickPlay.setOnClickListener(v -> startActivity(new Intent(HomeScreen.this, PlayerNumber.class)));


    }
}




