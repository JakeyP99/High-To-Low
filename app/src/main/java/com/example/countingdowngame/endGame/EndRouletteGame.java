package com.example.countingdowngame.endGame;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.Collections;

import pl.droidsonroids.gif.GifImageView;

public class EndRouletteGame extends ButtonUtilsActivity {
    private GifImageView muteGif;
    private GifImageView soundGif;
    private TextView textViewLose;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoHomeScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game_roulette);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);

        setupButtonControls();
        displayVictor(); // Display the winner's name
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        textViewLose = findViewById(R.id.TextViewWinner); // Link the TextView
    }

    private void setupButtonControls() {
        Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        setButtonActions(btnPlayAgain, btnNewPlayer);
    }

    private void setButtonActions(Button btnPlayAgain, Button btnNewPlayer) {
        btnUtils.setButton(btnPlayAgain, () -> {
            Game.getInstance().resetPlayers(this);
            gotoNumberChoice();
        });
        btnUtils.setButton(btnNewPlayer, this::gotoPlayerNumberChoice);
    }

    private void displayVictor() {
        // Retrieve victor's name from the Intent
        String victorName = getIntent().getStringExtra("VICTOR_NAME");
        if (victorName != null) {
            String victorText = "The victor is " + victorName + "!";
            textViewLose.setText(victorText);
        } else {
            textViewLose.setText("No victor found.");
        }
    }


}
