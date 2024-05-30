package com.example.countingdowngame.endGame;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.audio.AudioManager;
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
        Game gameInstance = Game.getInstance();
        Player currentPlayer = gameInstance.getCurrentPlayer();
        String playerName = currentPlayer.getName();
        int numberCounter = MainActivityGame.drinkNumberCounterInt;

        // Add the end game text
        String endGameText;
        if (numberCounter == 0) {
            endGameText = String.format("Drink up %s you litt..... Oh.. The number was 0? Well damn, lucky you I guess", playerName);
        } else {
            endGameText = String.format("Drink %d time%s %s you little baby!",
                    numberCounter, numberCounter == 1 ? "" : "s", playerName);
        }
        statistics.add(endGameText);
        // Generate possible statistics
        ArrayList<String> possibleStatistics = new ArrayList<>();
        if (gameInstance.getPlayerUsedWildcards()) {
            possibleStatistics.add(gameInstance.getPlayerWithMostWildcardsUsed());
        }

        GeneralSettingsLocalStore settings = GeneralSettingsLocalStore.fromContext(this);
        if (settings.isQuizActivated() && gameInstance.getQuizWasTriggered()) {
            possibleStatistics.add(gameInstance.getPlayerWithMostQuizCorrectAnswers());
            String mostIncorrectAnswers = gameInstance.getPlayerWithMostQuizIncorrectAnswers();
            if (!mostIncorrectAnswers.isEmpty()) {
                possibleStatistics.add(mostIncorrectAnswers);
            }
        }

        if (gameInstance.hasWitchClass()) {
            possibleStatistics.add(gameInstance.getWitchPlayerTotalDrinksHandedOut());
            possibleStatistics.add(gameInstance.getWitchPlayerTotalDrinksTaken());
        }

        possibleStatistics.add(gameInstance.getCatastropheQuantityString());


        // Shuffle and select up to 3 random statistics
        Collections.shuffle(possibleStatistics);
        statistics.addAll(possibleStatistics.subList(0, Math.min(4, possibleStatistics.size())));

        // Set up the RecyclerView with the adapter
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

        // Check if the list is not null and has valid indices
        if (previousNumbersFormatted != null && !previousNumbersFormatted.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivityGame.this,
                    R.layout.list_view_end_game, R.id.previousNumbers, previousNumbersFormatted);
            previousNumbersList.setAdapter(adapter);
        } else {
            // Handle the case where the list is null or empty
            System.err.println("Error: previousNumbersFormatted is null or empty");
        }
    }

    private void setButtonActions(Button btnPlayAgain, Button btnNewPlayer) {
        btnUtils.setButton(btnPlayAgain, () -> {
            Game.getInstance().resetPlayers(this);
            gotoNumberChoice();
        });
        btnUtils.setButton(btnNewPlayer, this::gotoPlayerNumberChoice);
    }
}
