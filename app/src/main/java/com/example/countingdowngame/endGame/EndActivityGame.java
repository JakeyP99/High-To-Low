package com.example.countingdowngame.endGame;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.statistics.Statistics;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class EndActivityGame extends ButtonUtilsActivity {
    private static final String TAG = "EndActivityGame";

    private GifImageView muteGif, soundGif;
    private final Game gameInstance = Game.getInstance();
    private final Player currentPlayer = gameInstance.getCurrentPlayer();
    private final String playerName = currentPlayer.getName();
    private final int drinkNumberCounter = MainActivityGame.drinkNumberCounterInt;

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

        // Save end-game stats
        Statistics.saveGlobalTotalDrinkStat(this, drinkNumberCounter, playerName);
        Statistics.saveGlobalGamesLostStat(this, playerName);
        for (Player p : gameInstance.getPlayers()) {
            Statistics.saveGlobalGamesPlayed(this, p.getName());
        }
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        setupStatsList(); // <-- updated
        setupPreviousNumbers(findViewById(R.id.previousNumbers));
    }


    private void setupStatsList() {
        ViewPager2 statsViewPager = findViewById(R.id.statsViewPager);
        DotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        List<String> statistics = buildStatistics();
        EndGameListAdapter adapter = new EndGameListAdapter(statistics);

        statsViewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(statsViewPager);
    }

    private List<String> buildStatistics() {
        List<String> statistics = new ArrayList<>();

        // End game text
        String endGameText = (drinkNumberCounter == 0)
                ? String.format("Drink up %s you litt..... Oh.. The number was 0? Well damn, lucky you I guess", playerName)
                : getResources().getQuantityString(
                R.plurals.drink_times,
                drinkNumberCounter,
                drinkNumberCounter,
                playerName
        );
        statistics.add(endGameText);

        // Additional possible stats
        List<String> possibleStats = new ArrayList<>();
        if (gameInstance.getPlayerUsedWildcards()) {
            possibleStats.add(gameInstance.getPlayerWithMostWildcardsUsed());
        }

        GeneralSettingsLocalStore settings = GeneralSettingsLocalStore.fromContext(this);
        if (settings.isQuizActivated() && gameInstance.getQuizWasTriggered()) {
            possibleStats.add(gameInstance.getPlayerWithMostQuizCorrectAnswers());
            String mostIncorrectAnswers = gameInstance.getPlayerWithMostQuizIncorrectAnswers();
            if (!mostIncorrectAnswers.isEmpty()) {
                possibleStats.add(mostIncorrectAnswers);
            }
        }

        if (gameInstance.hasWitchClass()) {
            possibleStats.add(gameInstance.getWitchPlayerTotalDrinksHandedOut());
            possibleStats.add(gameInstance.getWitchPlayerTotalDrinksTaken());
        }

        possibleStats.add(gameInstance.getCatastropheQuantityString());

        Collections.shuffle(possibleStats);
        statistics.addAll(possibleStats.subList(0, Math.min(4, possibleStats.size())));

        return statistics;
    }

    private void setupButtonControls() {
        Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        setButtonActions(btnPlayAgain, btnNewPlayer);
    }

    private void setupPreviousNumbers(ListView previousNumbersList) {
        ArrayList<String> previousNumbersFormatted = Game.getInstance().getPreviousNumbersFormatted();

        if (previousNumbersFormatted != null && !previousNumbersFormatted.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.list_view_end_game,
                    R.id.previousNumbers,
                    previousNumbersFormatted
            );
            previousNumbersList.setAdapter(adapter);
        } else {
            Log.e(TAG, "previousNumbersFormatted is null or empty");
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
