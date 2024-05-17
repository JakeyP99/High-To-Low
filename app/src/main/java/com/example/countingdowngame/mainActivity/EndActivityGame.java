package com.example.countingdowngame.mainActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.Collections;

import pl.droidsonroids.gif.GifImageView;

public class EndActivityGame extends ButtonUtilsActivity {
    private GifImageView muteGif;
    private GifImageView soundGif;

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
        setContentView(R.layout.a6_end_game);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);

        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        RecyclerView statsList = findViewById(R.id.statsList);
        setupStatsList(statsList);
        ListView previousNumbers = findViewById(R.id.previousNumbers);
        setupPreviousNumbersList(previousNumbers);
    }


    private void setupStatsList(RecyclerView statsList) {
        ArrayList<String> statistics = new ArrayList<>();

        // Add the end game text
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        String playerName = currentPlayer.getName();
        int numberCounter = MainActivityGame.drinkNumberCounterInt;
        String endGameText = (numberCounter == 1) ?
                "Drink " + numberCounter + " time " + playerName + " you little baby!" :
                "Drink " + numberCounter + " times " + playerName + " you little baby!";
        statistics.add(endGameText);

        // Create a list of possible statistics
        ArrayList<String> possibleStatistics = new ArrayList<>();
        possibleStatistics.add(Game.getInstance().getPlayerWithMostWildcardsUsed());

        // Check if there are any witch players before adding witch-related statistics
        if (Game.getInstance().hasWitchClass()) {
            possibleStatistics.add(Game.getInstance().getWitchPlayerTotalDrinksHandedOut());
            possibleStatistics.add(Game.getInstance().getWitchPlayerTotalDrinksTaken());
        }

        // Shuffle the list to randomize the order
        Collections.shuffle(possibleStatistics);

        // Add the first two items from the shuffled list to the statistics list
        for (int i = 0; i < 2 && i < possibleStatistics.size(); i++) {
            statistics.add(possibleStatistics.get(i));
        }

        // Set up the adapter
        EndGameListAdapter adapter = new EndGameListAdapter(this, statistics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        statsList.setLayoutManager(layoutManager);
        statsList.setAdapter(adapter);

        // Add PagerSnapHelper for snapping effect
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(statsList);
    }


    private void setupButtonControls() {
        Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        setButtonActions(btnPlayAgain, btnNewPlayer);
    }


    private void setupPreviousNumbersList(ListView previousNumbersList) {
        ArrayList<String> previousNumbersFormatted = Game.getInstance().getPreviousNumbersFormatted();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivityGame.this,
                R.layout.list_view_end_game, R.id.previousNumbers, previousNumbersFormatted);
        previousNumbersList.setAdapter(adapter);
    }

    private void setButtonActions(Button btnPlayAgain, Button btnNewPlayer) {
        btnUtils.setButton(btnPlayAgain, () -> {
            Game.getInstance().resetPlayers(this);
            gotoNumberChoice();
        });
        btnUtils.setButton(btnNewPlayer, this::gotoPlayerNumberChoice);
    }
}
