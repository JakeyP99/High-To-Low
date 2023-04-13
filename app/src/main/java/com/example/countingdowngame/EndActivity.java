package com.example.countingdowngame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_layout);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);

        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.custom_list_item, R.id.previousNumbers, MainActivity.gameInstance.getPreviousNumbersFormatted());
        previousNumbersList.setAdapter(adapter);

        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        ButtonUtils.setButtonNoClass(btnNewPlayer, PlayerNumber.class, this, () -> {
            bop.start();
        });
        ButtonUtils.setButtonNoClass(btnPlayAgain, NumberChoice.class, this, () -> {
            MainActivity.gameInstance.playAgain();
            bop.start();
        });


    }
}
