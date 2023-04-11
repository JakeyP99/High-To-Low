package com.example.countingdowngame;

import static com.example.countingdowngame.MainActivity.gameInstance;

import android.media.MediaPlayer;
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
        MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);

        String[] wildActivities = {
                "Take 1 drink.", "Take 2 drinks.", "Take 3 drinks.", "Finish your drink.",
                "Give 1 drink.", "Give 2 drinks.", "Give 3 drinks.", "Choose a player to finish their drink.",
                "The player to the left takes a drink.",
                "The player to the right takes a drink.",
                "The oldest player takes 2 drinks.",
                "The youngest player takes 2 drinks.",
                "The player who last peed takes 3 drinks.",
                "The player with the oldest car takes 2 drinks.",
                "Whoever last rode on a train takes 2 drinks.",
                "Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.",
                "Anyone who is sitting takes 2 drinks.",
                "Whoever has the longest hair takes 2 drinks.",
                "Whoever is wearing a watch takes 2 drinks.",
                "Whoever has a necklace on takes 2 drinks.",
                "Double the ending drink, whoever loses must now do double the consequence."
        };
        Random random = new Random();
        int index = random.nextInt(wildActivities.length);
        String selectedActivity = wildActivities[index];
        wildActivityTextView.setText(selectedActivity);

        ButtonUtils.setWildCardGenerate(btnNext, v -> {
            finish();
            bop.start();
            gameInstance.nextNumber();
            Vibrate.vibrateDevice(this);
        }, WildCard.this);
    }}


