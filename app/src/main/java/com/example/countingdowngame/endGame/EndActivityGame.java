package com.mydomain.countingdowngame.endGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager2.widget.ViewPager2;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.MainActivityGame;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.settings.GeneralSettingsLocalStore;
import com.mydomain.countingdowngame.statistics.Statistics;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;
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
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_end_main_activity);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();

        // Save end-game stats
        String splitTarget = gameInstance.getSplitTarget();
        if (splitTarget != null && !splitTarget.isEmpty()) {
            int splitAmount = Math.max(drinkNumberCounter / 2, 1);
            Statistics.saveGlobalTotalDrinkStat(this, splitAmount, playerName);
            Statistics.saveGlobalTotalDrinkStat(this, splitAmount, splitTarget);
        } else {
            Statistics.saveGlobalTotalDrinkStat(this, drinkNumberCounter, playerName);
        }

        Statistics.saveGlobalGamesLostStat(this, playerName);
        for (Player p : gameInstance.getPlayers()) {
            Statistics.saveGlobalGamesPlayed(this, p.getName());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                gotoHomeScreen();
            }
        });
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        ImageView loserImage = findViewById(R.id.loser_image);
        TextView gameOverText = findViewById(R.id.game_over_text);

        if (playerName != null) {
            gameOverText.setText("BOTTOMS UP, " + playerName.toUpperCase() + "!");
        }

        if (currentPlayer != null && currentPlayer.getPhoto() != null && !currentPlayer.getPhoto().isEmpty()) {
            byte[] decodedString = Base64.decode(currentPlayer.getPhoto(), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            loserImage.setImageBitmap(decodedBitmap);
        } else {
            loserImage.setImageResource(R.drawable.wine);
        }

        setupStatsList();
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
        String endGameText;
        if (drinkNumberCounter == 0) {
            endGameText = String.format("Drink up %s you litt..... Oh.. The number was 0? Well damn, lucky you I guess", playerName);
        } else {
            String splitTarget = gameInstance.getSplitTarget(); // I need to add this to Game class or handle it via a static variable
            if (splitTarget != null && !splitTarget.isEmpty()) {
                int splitAmount = Math.max(drinkNumberCounter / 2, 1);
                String p1Text = getResources().getQuantityString(R.plurals.drink_times, splitAmount, splitAmount, splitTarget);
                String p2Text = getResources().getQuantityString(R.plurals.drink_times, splitAmount, splitAmount, playerName);
                endGameText = p1Text.replace("!", ",") + " and " + p2Text;
            } else {
                endGameText = getResources().getQuantityString(
                        R.plurals.drink_times,
                        drinkNumberCounter,
                        drinkNumberCounter,
                        playerName
                );
            }
        }
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

        if (gameInstance.hasGamblerClass()) {
            possibleStats.add(gameInstance.getGamblerPlayerTotalDrinksHandedOut());
            possibleStats.add(gameInstance.getGamblerPlayerTotalDrinksTaken());
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
                    R.layout.game_end_previous_number_list,
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
            MainActivityGame.resetStaticState();
            Game.getInstance().resetPlayers(this);
            gotoNumberChoice();
        });
        btnUtils.setButton(btnNewPlayer, () -> {
            MainActivityGame.resetStaticState();
            this.gotoHomeScreen();
        });
    }
}
