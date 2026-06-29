package com.example.countingdowngame.mainActivityRoulette;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.SharedMainActivity;
import com.example.countingdowngame.player.Player;

import java.util.List;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityRoulette extends SharedMainActivity {
    static final int BACK_PRESS_DELAY = 3000;
    int removedPlayerCount = 0;
    private GifImageView muteGif, soundGif;
    private Button btnBullshit;
    private ImageButton imageButtonExit;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_roulette_main_activity);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();
        startGame();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    Game.getInstance().endGame(MainActivityRoulette.this);
                    gotoHomeScreen();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                displayToastMessage("Press back again to go to the home screen");
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
            }
        });
    }

    public void displayToastMessage(String message) {
        StyleableToast.makeText(this, message, R.style.newToast).show();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        btnBullshit = findViewById(R.id.btnBullshit);
        imageButtonExit = findViewById(R.id.btnExitRouletteGame);
    }


    private void startGame() {
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        int chamberNumberCount = extras.getInt("chamberNumberCount");

        Log.d(TAG, "startGame: " + chamberNumberCount);

        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            Game.getInstance().setPlayers(this, playerList.size());
            Game.getInstance().setPlayerList(playerList);

            for (Player player : playerList) {
                player.setGame(Game.getInstance());
                player.setTotalChamberNumberCount(chamberNumberCount);
                player.setChamberList();
            }
        }
    }


    //-----------------------------------------------------Button Controls---------------------------------------------------//

    private void setupButtonControls() {
        btnUtils.setButton(btnBullshit, this::bullshitActivity);
        imageButtonExit.setOnClickListener(view -> {
            Log.d(TAG, "Exit button clicked");
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });
    }

    //-----------------------------------------------------Bullshit Button---------------------------------------------------//

    private void bullshitActivity() {
        List<Player> opponents = Game.getInstance().getPlayers().stream()
                .filter(p -> !p.isRemoved())
                .collect(Collectors.toList());

        mainActivityDialog.showOpponentDialog("Bullshit:", opponents, this::onPlayerViewClicked);
    }

    private void onPlayerViewClicked(Player player) {
        russianRouletteActivity(player); // Trigger the game activity
    }

    //-----------------------------------------------------Roulette Activity---------------------------------------------------//

    private void russianRouletteActivity(Player player) {
        logPlayerDetails(player);
        if (isBulletInChamber(player)) {
            handleDeath(player);
        } else {
            AudioManager.getInstance().playBlank(this);
            int bulletsLeft = player.getBulletsInChamberList().size() - 1;
            String bulletsText = (bulletsLeft == 1) ? "bullet" : "bullets";
            mainActivityDialog.showMainDialog("Safe! \n\n" + player.getName() + " dodged a bullet... Literally!\n\nYou have " + bulletsLeft + " " + bulletsText + " left!", this::handlePostDialogActions);
        }
        updateChamber(player);
    }

    // Logs player details for debugging
    private void logPlayerDetails(Player player) {
        Log.d(TAG, "Bullets in chamber list: " + player.getBulletsInChamberList());
        Log.d(TAG, "Chamber Total Number Count: " + player.getTotalChamberNumberCount());
        Log.d(TAG, "Chamber Index: " + player.getChamberIndex());
    }

    // Checks if there is a bullet in the current chamber
    private boolean isBulletInChamber(Player player) {
        int currentChamberValue = player.getBulletsInChamberList().get(player.getChamberIndex());
        return currentChamberValue == 1;
    }

    // Handles the scenario where the bullet is in the chamber
    private void handleDeath(Player player) {
        player.setRemoved(true);
        AudioManager.getInstance().playGunshot(this);
        removedPlayerCount++;

        mainActivityDialog.showDialog(
                "Eliminated!",
                player.getName() + " died! Whoopsie :(",
                R.layout.game_catastrophe_dialog_box,
                R.id.catastrophe_dialogbox_textview,
                R.id.dialogbox_textview,
                this::handlePostDialogActions
        );
    }

    // Updates the chamber list and index
    private void updateChamber(Player player) {
        List<Integer> chamberList = player.getBulletsInChamberList();
        int currentChamberIndex = player.getChamberIndex();

        // Remove the current chamber element
        chamberList.remove(currentChamberIndex);

        // Update the chamber index
        if (!chamberList.isEmpty()) {
            // Move to the next chamber (wrap around if needed)
            if (currentChamberIndex >= chamberList.size()) {
                currentChamberIndex = 0; // Loop back to the start
            }
            player.setChamberIndex(currentChamberIndex);
        }
    }

    //-----------------------------------------------------Dialogs---------------------------------------------------//

    private void handlePostDialogActions() {
        Log.d(TAG, "handlePostDialogActions: occurred");
        List<Player> playerList = Game.getInstance().getPlayers();
        int activePlayerCount = Game.getInstance().getPlayerAmount() - removedPlayerCount;

        if (activePlayerCount <= 1) {
            Player activePlayer = null;
            for (Player p : playerList) {
                if (!p.isRemoved()) {
                    activePlayer = p;
                    break;
                }
            }

            if (activePlayer != null) {
                gotoGameEndRoulette(activePlayer);
            } else {
                Log.e(TAG, "No active player found, but count indicates one should exist.");
            }
        }
    }


}



