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

        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final Button btnQuickPlay = findViewById(R.id.quickplay);
        final Button btnInstructions = findViewById(R.id.button_Instructions);
        final Button btnSettings = findViewById(R.id.button_Settings);



        ButtonUtils.setButton(btnQuickPlay, null, PlayerNumberChoice.class, this, null);
        ButtonUtils.setButton(btnInstructions,null, Instructions.class, this, null);
//        ButtonUtils.setButton(btnSettings,null, WildCardChoice.class, this, null);


    }
}




