package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        String[] wildActivities = {"take 3 drinks", "the player to the left takes 3 drinks", "Sing a song", "Tell a joke", "Take a selfie"};
        Random random = new Random();
        int index = random.nextInt(wildActivities.length);
        String selectedActivity = wildActivities[index];
        wildActivityTextView.setText(selectedActivity);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WildCard.this, MainActivity.class));
            }
        });
    }
};


