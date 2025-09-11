package com.example.countingdowngame.statistics;

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
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        listViewPlayerGlobalStatistics = findViewById(R.id.playerGlobalStatistics);
    }

    private void setPlayerStatistics() {
        List<PlayerStatistic> stats = new ArrayList<>();
        for (Player player : Game.getInstance().getPlayers()) {
            stats.add(new PlayerStatistic(
                    player.getName(),
                    player.getTotalDrinksConsumed()
            ));
        }

        StatisticsAdapter adapter = new StatisticsAdapter(this, stats);
        listViewPlayerGlobalStatistics.setAdapter(adapter);
    }

}

