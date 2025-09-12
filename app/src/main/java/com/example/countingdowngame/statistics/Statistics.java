package com.example.countingdowngame.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class Statistics extends ButtonUtilsActivity {

    private GifImageView muteGif, soundGif;
    private ListView listViewPlayerGlobalStatistics;
    private ImageView playerImage;

    private static final String PREF_NAME = "PlayerStats";

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
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

        listViewPlayerGlobalStatistics.setAdapter(new StatisticsAdapter(this, stats));
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
