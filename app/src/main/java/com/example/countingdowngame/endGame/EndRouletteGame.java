package com.example.countingdowngame.endGame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.List;

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
        setContentView(R.layout.game_roulette_end_main_activity);

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
        btnUtils.setButton(btnNewPlayer, this::gotoPlayerChoice);
    }

    private void displayVictor() {
        // Retrieve victor's name from the Intent
        String victorName = getIntent().getStringExtra("VICTOR_NAME");
            // Get the victor's chamber data
            Player victor = findPlayerByName(victorName);
                List<Integer> chamberData = victor.getChamberList();
                int shotCount = 0;
                boolean bulletFound = false;

                // Find when the first bullet appears
                for (int i = 0; i < chamberData.size(); i++) {
                    shotCount++;
                    if (chamberData.get(i) == 1) {  // Bullet found
                        bulletFound = true;
                        break;
                    }
                }

                // Construct the message
                String victorText = "The winner is " + victorName + ", they would have died in " + shotCount + " shot" + (shotCount > 1 ? "s" : "") + ".";
                textViewLose.setText(victorText);
        }


    // Helper method to find the Player object by name
    private Player findPlayerByName(String name) {
        List<Player> players = Game.getInstance().getPlayers();
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }



}
