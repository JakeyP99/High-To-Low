package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
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
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class MainActivityRoulette extends ButtonUtilsActivity {
    int removedPlayerCount = 0;
    private GifImageView muteGif, soundGif;
    private Button btnBullshit;
    private ImageButton imageButtonExit;
    private ScrollView playerScrollView;
    private LinearLayout playerContainer;
    private final boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Intercept back press event and do nothing
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;  // This consumes the back press event, effectively disabling it
        }
        return super.dispatchKeyEvent(event);
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
        setContentView(R.layout.game_roulette_main_activity);

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
        setPlayerChoiceVisibility();
        List<Player> playerList = Game.getInstance().getPlayers();
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


    //-----------------------------------------------------Roulette Player Menu---------------------------------------------------//

    private View createPlayerView(Player player) {
        LinearLayout playerContainer = findViewById(R.id.playerContainer);
        // Inflate the player view layout
        View playerView = getLayoutInflater().inflate(R.layout.game_roulette_player_view, playerContainer, false);

        // Set up the player information and click listener
        setupPlayerView(player, playerView);

        return playerView;
    }

    private void setupPlayerView(Player player, View playerView) {
        ImageView playerImageView = playerView.findViewById(R.id.playerPhotoImageView);
        TextView playerNameTextView = playerView.findViewById(R.id.playerNameTextView);

        // Set player name
        String playerName = player.getName();
        playerNameTextView.setText(playerName);

        // Adjust text size based on the length of the name
        if (playerName.length() > 20) {
            playerNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else if (playerName.length() > 13) {
            playerNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else if (playerName.length() > 7) {
            playerNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            playerNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24); // Default size
        }

        // Set player image
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
        LinearLayout playerContainer = findViewById(R.id.playerContainer);
        playerContainer.setVisibility(View.INVISIBLE); // Hide the container after selection
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
            showGameDialog(player.getName() + " dodged a bullet... Literally!\n\nYou have " + bulletsLeft + " " + bulletsText + " left!");
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

    //-----------------------------------------------------Dialogs---------------------------------------------------//

    private void showGameDialog(String message) {
        showDialog(message, R.layout.game_main_dialog_box, R.id.dialogbox_textview, R.id.close_button);
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
            handlePostDialogActions();
        });

        // Handle clicks outside the dialog (if needed)
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setOnTouchListener((v, event) -> {
            dialog.dismiss();
            handlePostDialogActions();
            return true;
        });

    }


    private void handlePostDialogActions() {
        Log.d(TAG, "handlePostDialogActions: occurred");
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
            setMainScreenVisibility();
        }
    }

    private void showCatastropheDialog(String message) {
        showDialog(message, R.layout.game_roulette_death_dialog_box, R.id.dialogbox_textview, R.id.close_button);
    }

    //-----------------------------------------------------Set Visibilities---------------------------------------------------//

    // Updates the UI elements after the shot
    private void setMainScreenVisibility() {
        btnBullshit.setVisibility(View.VISIBLE);
        imageButtonExit.setVisibility(View.VISIBLE);
        playerScrollView.setVisibility(View.INVISIBLE);
        playerContainer.setVisibility(View.INVISIBLE);
    }

    private void setPlayerChoiceVisibility() {
        btnBullshit.setVisibility(View.INVISIBLE);
        playerScrollView.setVisibility(View.VISIBLE);
        playerContainer.setVisibility(View.VISIBLE);
        imageButtonExit.setVisibility(View.INVISIBLE);
    }


}



