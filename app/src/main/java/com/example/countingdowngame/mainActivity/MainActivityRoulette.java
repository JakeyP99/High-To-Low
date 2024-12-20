package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;

import static com.example.countingdowngame.mainActivity.MainActivityGame.BACK_PRESS_DELAY;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityRoulette extends ButtonUtilsActivity {
    int removedPlayerCount = 0;
    private GifImageView muteGif, soundGif;
    private Button btnBullshit;
    private ImageButton imageButtonExit;
    private ScrollView playerScrollView;
    private LinearLayout playerContainer;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame(this);
            gotoHomeScreen();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        StyleableToast.makeText(this, "Press back again to go to the home screen", R.style.newToast).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_roulette);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();
        startGame();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        btnBullshit = findViewById(R.id.btnBullshit);
        imageButtonExit = findViewById(R.id.btnExitRouletteGame);
        playerContainer = findViewById(R.id.playerContainer);
        playerScrollView = findViewById(R.id.playerScrollView);
    }


    private void startGame() {
        Bundle extras = getIntent().getExtras();
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


    //-----------------------------------------------------Bullshit Button---------------------------------------------------//

    private void setupButtonControls() {
        btnUtils.setButton(btnBullshit, this::bullshitActivity);
        imageButtonExit.setOnClickListener(view -> {
            Log.d(TAG, "Exit button clicked");
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });
    }

    private void bullshitActivity() {
        btnBullshit.setVisibility(View.INVISIBLE);
        playerScrollView.setVisibility(View.VISIBLE);
        playerContainer.setVisibility(View.VISIBLE);
        imageButtonExit.setVisibility(View.INVISIBLE);

        List<Player> playerList = Game.getInstance().getPlayers();
        if (playerList == null || playerList.isEmpty()) {
            StyleableToast.makeText(this, "No players available.", R.style.newToast).show();
            return;
        }

        LinearLayout playerContainer = findViewById(R.id.playerContainer);
        playerContainer.removeAllViews(); // Clear any existing player views
        playerContainer.setVisibility(View.VISIBLE);

        // Log which players are removed and which are not
        for (Player player : playerList) {
            if (player.isRemoved()) {
                Log.d(TAG, "Removed player: " + player.getName());
            } else {
                // Check if the player is already displayed
                boolean isAlreadyAdded = false;
                for (int i = 0; i < playerContainer.getChildCount(); i++) {
                    View existingPlayerView = playerContainer.getChildAt(i);
                    TextView existingPlayerName = existingPlayerView.findViewById(R.id.playerNameTextView);
                    if (existingPlayerName.getText().toString().equals(player.getName())) {
                        isAlreadyAdded = true;
                        break;
                    }
                }

                if (!isAlreadyAdded) {
                    View playerView = createPlayerView(player);
                    playerContainer.addView(playerView);
                }
            }
        }
    }

    private View createPlayerView(Player player) {
        LinearLayout playerContainer = findViewById(R.id.playerContainer);
        // Inflate the player view layout
        View playerView = getLayoutInflater().inflate(R.layout.player_view_roulette, playerContainer, false);

        // Set up the player information and click listener
        setupPlayerView(player, playerView);

        return playerView;
    }

    private void setupPlayerView(Player player, View playerView) {
        ImageView playerImageView = playerView.findViewById(R.id.playerPhotoImageView);
        TextView playerNameTextView = playerView.findViewById(R.id.playerNameTextView);

        // Set player name and image
        playerNameTextView.setText(player.getName());
        setPlayerImage(player.getPhoto(), playerImageView);

        // Set the click listener for the player view
        playerView.setOnClickListener(v -> onPlayerViewClicked(player));
    }

    private void setPlayerImage(String playerImageString, ImageView playerImageView) {
        if (playerImageString != null) {
            byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            playerImageView.setImageBitmap(decodedBitmap);
        } else {
            playerImageView.setImageResource(R.drawable.wine); // Default image
        }
    }

    private void onPlayerViewClicked(Player player) {
        Log.d("Player Clicked", "Player clicked: " + player.getName());
        LinearLayout playerContainer = findViewById(R.id.playerContainer);
        playerContainer.setVisibility(View.INVISIBLE); // Hide the container after selection

        if (player.isRemoved()) {
            showToast(player.getName() + " is already dead!!!");
        } else {
            russianRouletteActivity(player); // Trigger the game activity

        }

    }

    //-----------------------------------------------------Roulette Activity---------------------------------------------------//

    private void russianRouletteActivity(Player player) {
        Log.d(TAG, "Player: " + player.getName());
        logPlayerDetails(player);

        if (!isValidChamber(player)) {
            return;
        }

        if (isBulletInChamber(player)) {
            handleBulletShot(player);
        } else {
            showGameDialog(player.getName() + " dodged a bullet... Literally!");
        }
        updateChamber(player);
    }


    // Logs player details for debugging
    private void logPlayerDetails(Player player) {
        Log.d(TAG, "Bullets in chamber list: " + player.getBulletsInChamberList());
        Log.d(TAG, "Chamber Total Number Count: " + player.getTotalChamberNumberCount());
        Log.d(TAG, "Chamber Index: " + player.getChamberIndex());
    }

    // Validates the chamber list and index
    private boolean isValidChamber(Player player) {
        if (player.getBulletsInChamberList() == null || player.getBulletsInChamberList().isEmpty()) {
            Log.e(TAG, "Chamber is not initialized for player: " + player.getName());
            showToast("Chamber is not initialized properly for " + player.getName());
            return false;
        }

        if (player.getChamberIndex() < 0 || player.getChamberIndex() >= player.getBulletsInChamberList().size()) {
            Log.e(TAG, "Invalid chamber index for player: " + player.getName());
            showToast("Invalid chamber index for " + player.getName());
            return false;
        }

        return true;
    }

    // Checks if there is a bullet in the current chamber
    private boolean isBulletInChamber(Player player) {
        int currentChamberValue = player.getBulletsInChamberList().get(player.getChamberIndex());
        return currentChamberValue == 1;
    }

    // Handles the scenario where the bullet is in the chamber
    private void handleBulletShot(Player player) {
        player.setRemoved(true);
        AudioManager.getInstance().playGunshot(this);
        removedPlayerCount++;
        showCatastropheDialog(player.getName() + " died! Whoopsie :(");

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


    // Updates the UI elements after the shot
    private void updateUI() {
        btnBullshit.setVisibility(View.VISIBLE);
        imageButtonExit.setVisibility(View.VISIBLE);
        playerScrollView.setVisibility(View.INVISIBLE);
        playerContainer.setVisibility(View.INVISIBLE);

    }

    // Displays a styled toast message
    private void showToast(String message) {
        StyleableToast.makeText(this, message, R.style.newToast).show();
    }

    private void showGameDialog(String message) {
        showDialog(message, R.layout.game_dialog_box, R.id.dialogbox_textview, R.id.close_button);
    }

    private void showDialog(String message, int layoutId, int textViewId, int closeButtonId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);
        TextView dialogBoxTextView = dialogView.findViewById(textViewId);
        dialogBoxTextView.setText(message);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageButton closeButton = dialogView.findViewById(closeButtonId);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();

            List<Player> playerList = Game.getInstance().getPlayers();
            int activePlayerCount = Game.getInstance().getPlayerAmount() - removedPlayerCount;

            if (activePlayerCount == 1) {
                Player activePlayer = null;
                for (Player player : playerList) {
                    if (!player.isRemoved()) {
                        activePlayer = player;
                        break;
                    }
                }

                if (activePlayer != null) {
                    gotoGameEndRoulette(activePlayer);
                } else {
                    Log.e(TAG, "No active player found, but count indicates one should exist.");
                }
            } else {
                updateUI();
            }
        });
    }

    private void showCatastropheDialog(String message) {
        showDialog(message, R.layout.death_dialog_box, R.id.dialogbox_textview, R.id.close_button);
    }


}



