package com.example.countingdowngame.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
        SharedPreferences prefs = getSharedPreferences("PlayerStats", MODE_PRIVATE);

        List<String> knownPlayerNames = Player.getSavedPlayerNames(this);

        for (String playerName : knownPlayerNames) {
            String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
            int totalDrinks = prefs.getInt(keyPrefix + "_drinks", 0);  // read global saved stats
            int totalGamesLost = prefs.getInt(keyPrefix + "_gameslost", 0);  // read global saved stats
            int totalGamesPlayed = prefs.getInt(keyPrefix + "_gamesplayed", 0);  // read global saved stats

            stats.add(new PlayerStatistic(playerName, totalDrinks, totalGamesLost, totalGamesPlayed));
        }
        StatisticsAdapter adapter = new StatisticsAdapter(this, stats);
        listViewPlayerGlobalStatistics.setAdapter(adapter);
    }

    public static void saveGlobalTotalDrinkStat(Context context, int drinkNumberCounter, String playerName) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");

        int savedDrinks = prefs.getInt(keyPrefix + "_drinks", 0);
        int newTotalDrinks = savedDrinks + drinkNumberCounter;

        editor.putInt(keyPrefix + "_drinks", newTotalDrinks);
        editor.apply();
    }

    public static void saveGlobalGamesLostStat(Context context, String playerName) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");

        int lostGames = prefs.getInt(keyPrefix + "_gameslost", 0);
        int newLostGames = lostGames + 1;

        editor.putInt(keyPrefix + "_gameslost", newLostGames);
        editor.apply();
    }


    public static void saveGlobalGamesPlayed(Context context, String playerName) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");

        int gamesPlayed = prefs.getInt(keyPrefix + "_gamesplayed", 0);
        int newGamesPlayed = gamesPlayed + 1;

        editor.putInt(keyPrefix + "_gamesplayed", newGamesPlayed);
        editor.apply();
    }


    public static void savePlayerPhoto(Context context, String playerName, String photoString) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        editor.putString(keyPrefix + "_photo", photoString);
        editor.apply();
    }

}

