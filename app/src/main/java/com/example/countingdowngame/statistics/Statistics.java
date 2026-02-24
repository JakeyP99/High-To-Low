package com.example.countingdowngame.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class Statistics extends ButtonUtilsActivity implements StatisticsAdapter.OnLongClickListener {

    private GifImageView muteGif, soundGif;
    private ListView listViewPlayerGlobalStatistics;
    private ImageView playerImage;

    private static final String PREF_NAME = "PlayerStats";
    private int debugClickCount = 0;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_main_activity);
        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setPlayerStatistics();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        listViewPlayerGlobalStatistics = findViewById(R.id.playerGlobalStatistics);
    }

    private void setPlayerStatistics() {
        List<PlayerStatistic> stats = new ArrayList<>();
        SharedPreferences prefs = getPrefs(this);

        List<String> knownPlayerNames = Player.getSavedPlayerNames(this);

        for (String playerName : knownPlayerNames) {
            String keyPrefix = getKeyPrefix(playerName);

            int totalDrinks = prefs.getInt(keyPrefix + "_drinks", 0);
            int totalGamesLost = prefs.getInt(keyPrefix + "_gameslost", 0);
            int totalGamesPlayed = prefs.getInt(keyPrefix + "_gamesplayed", 0);

            stats.add(new PlayerStatistic(playerName, totalDrinks, totalGamesLost, totalGamesPlayed));
        }

        listViewPlayerGlobalStatistics.setAdapter(new StatisticsAdapter(this, stats, this));
    }

    @Override
    public void onLongClick(PlayerStatistic stat, int position) {
        showOptionsDialog(stat);
    }

    private void showOptionsDialog(PlayerStatistic stat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.statistics_options_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        dialogTitle.setText("Options for " + stat.getPlayerName());

        Button btnEditValues = dialogView.findViewById(R.id.btnEditValues);
        Button btnDeletePlayer = dialogView.findViewById(R.id.btnDeletePlayer);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        debugClickCount = 0;
        dialogTitle.setOnClickListener(v -> {
            debugClickCount++;
            if (debugClickCount >= 5) {
                btnEditValues.setEnabled(true);
                btnEditValues.setAlpha(1.0f);
            }
        });

        AlertDialog dialog = builder.setView(dialogView).create();

        btnEditValues.setOnClickListener(v -> {
            showEditDialog(stat);
            dialog.dismiss();
        });

        btnDeletePlayer.setOnClickListener(v -> {
            showDeleteConfirmationDialog(stat.getPlayerName());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showEditDialog(PlayerStatistic stat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.statistics_edit_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.editDialogTitle);
        dialogTitle.setText("Edit Stats: " + stat.getPlayerName());

        final EditText editDrinks = dialogView.findViewById(R.id.editDrinks);
        final EditText editGamesPlayed = dialogView.findViewById(R.id.editGamesPlayed);
        final EditText editGamesLost = dialogView.findViewById(R.id.editGamesLost);

        editDrinks.setText(String.valueOf(stat.getTotalDrinks()));
        editGamesPlayed.setText(String.valueOf(stat.getTotalGamesPlayed()));
        editGamesLost.setText(String.valueOf(stat.getTotalGamesLost()));

        Button btnSave = dialogView.findViewById(R.id.btnSaveStats);
        Button btnCancel = dialogView.findViewById(R.id.btnEditCancel);

        AlertDialog dialog = builder.setView(dialogView).create();

        btnSave.setOnClickListener(v -> {
            try {
                int drinks = Integer.parseInt(editDrinks.getText().toString());
                int played = Integer.parseInt(editGamesPlayed.getText().toString());
                int lost = Integer.parseInt(editGamesLost.getText().toString());

                saveManualStats(stat.getPlayerName(), drinks, lost, played);
                setPlayerStatistics(); // Refresh list
                dialog.dismiss();
            } catch (NumberFormatException e) {
                // Handle invalid input if necessary
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmationDialog(String playerName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.statistics_delete_dialog, null);

        TextView dialogMessage = dialogView.findViewById(R.id.deleteDialogMessage);
        dialogMessage.setText("Are you sure you want to delete all statistics for " + playerName + "?");

        Button btnConfirmDelete = dialogView.findViewById(R.id.btnConfirmDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnDeleteCancel);

        AlertDialog dialog = builder.setView(dialogView).create();

        btnConfirmDelete.setOnClickListener(v -> {
            deletePlayerStats(playerName);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveManualStats(String playerName, int drinks, int lost, int played) {
        SharedPreferences.Editor editor = getPrefs(this).edit();
        String prefix = getKeyPrefix(playerName);
        editor.putInt(prefix + "_drinks", drinks);
        editor.putInt(prefix + "_gameslost", lost);
        editor.putInt(prefix + "_gamesplayed", played);
        editor.apply();
    }

    private void deletePlayerStats(String playerName) {
        SharedPreferences.Editor editor = getPrefs(this).edit();
        String prefix = getKeyPrefix(playerName);
        editor.remove(prefix + "_drinks");
        editor.remove(prefix + "_gameslost");
        editor.remove(prefix + "_gamesplayed");
        editor.remove(prefix + "_photo");
        editor.apply();
        setPlayerStatistics(); // Refresh list
    }

    // ====== Helpers for SharedPreferences ======
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getKeyPrefix(String playerName) {
        return playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
    }

    private static void updateStat(Context context, String playerName, String suffix, int increment) {
        SharedPreferences prefs = getPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();

        String key = getKeyPrefix(playerName) + suffix;
        int current = prefs.getInt(key, 0);
        editor.putInt(key, current + increment);
        editor.apply();
    }

    // ====== Public Save Methods ======
    public static void saveGlobalTotalDrinkStat(Context context, int drinkNumberCounter, String playerName) {
        updateStat(context, playerName, "_drinks", drinkNumberCounter);
    }

    public static void saveGlobalGamesLostStat(Context context, String playerName) {
        updateStat(context, playerName, "_gameslost", 1);
    }

    public static void saveGlobalGamesPlayed(Context context, String playerName) {
        updateStat(context, playerName, "_gamesplayed", 1);
    }

    public static void savePlayerPhoto(Context context, String playerName, String photoString) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(getKeyPrefix(playerName) + "_photo", photoString);
        editor.apply();
    }
}
