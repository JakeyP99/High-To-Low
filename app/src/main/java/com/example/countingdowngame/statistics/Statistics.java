package com.example.countingdowngame.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtils;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class Statistics extends ButtonUtilsActivity {

    private GifImageView muteGif, soundGif;
    private ListView listViewPlayerGlobalStatistics;
    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().resumeBackgroundMusic();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
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

        List<String> knownPlayers = Player.getSavedPlayerNames(this);
        for (String playerName : knownPlayers) {
            String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
            int totalDrinks = prefs.getInt(keyPrefix + "_drinks", 0);  // read global saved stats
            stats.add(new PlayerStatistic(playerName, totalDrinks));
        }

        StatisticsAdapter adapter = new StatisticsAdapter(this, stats);
        listViewPlayerGlobalStatistics.setAdapter(adapter);
    }

    public static void saveGlobalStats(Context context, int drinkNumberCounter, String playerName) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String keyPrefix = playerName.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");

        int savedDrinks = prefs.getInt(keyPrefix + "_drinks", 0);
        int newTotalDrinks = savedDrinks + drinkNumberCounter;

        editor.putInt(keyPrefix + "_drinks", newTotalDrinks);
        editor.apply();
    }

}

