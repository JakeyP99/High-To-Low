package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class WildCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wild_activity);

        final TextView wildActivityTextView = findViewById(R.id.wild_activity_text_view);
        final Button btnNext = findViewById(R.id.nextButton);

        String[] wildActivities = {
                "Take 1 drinks.", "Take 2 drinks.", "Take 3 drinks.", "Finish your drink.",
                "Give 1 drinks.", "Give 2 drinks.", "Give 3 drinks.", "Choose a player to finish their drink.",
                "The player to the left takes a drink.",
                "The player to the right takes a drink.",
                "Take everyone's turn, you are allowed to click anything you want for any of the players.",
                "The oldest player takes 2 drinks.",
                "The youngest player takes 2 drinks.",
                "The player who last peed takes 3 drinks.",
                "The player with the oldest car takes 2 drinks.",
                "Give the phone to someone else, the game will resume with this new order.",
                "Whoever last rode on a train takes 2 drinks.",
                "Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.",
                "Double the ending drink"
        };
        Random random = new Random();
        int index = random.nextInt(wildActivities.length);
        String selectedActivity = wildActivities[index];
        wildActivityTextView.setText(selectedActivity);

        btnNext.setOnClickListener(view -> {
                    startActivity(new Intent(WildCard.this, MainActivity.class));
                    Vibrate.vibrateDevice(this);
                }
        );}}

