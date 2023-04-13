package com.example.countingdowngame;

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

        ButtonUtils.setButtonNoClass(btnQuickPlay, PlayerNumber.class, this, null);
        ButtonUtils.setButtonNoClass(btnInstructions, Instructions.class, this, null);


    }
}




