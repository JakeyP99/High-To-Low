package com.mydomain.countingdowngame.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.createPlayer.CharacterClassPagerAdapter;
import com.mydomain.countingdowngame.createPlayer.CharacterClassStore;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.playerChoice.playerChoiceComplimentary;
import com.mydomain.countingdowngame.settings.GeneralSettingsLocalStore;
import com.mydomain.countingdowngame.utils.ButtonUtils;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import com.mydomain.countingdowngame.wildCards.api.TriviaSessionManager;
import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends playerChoiceComplimentary {

    private GifImageView muteGif;
    private GifImageView soundGif;
    private GifImageView drinkGif;
    private GifImageView infoGif;
    private ButtonUtils buttonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_main_activity);

        buttonUtils = new ButtonUtils(this);

        initViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtons();
        setupDrinkGif();

        // Preload trivia questions from API
        TriviaSessionManager.getInstance().preloadQuestions(20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager.updateMuteButton(getMuteSoundState(), muteGif, soundGif);
    }

    private void initViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        drinkGif = findViewById(R.id.drinkGif);
        infoGif = findViewById(R.id.informationGif);
    }

    private void setupButtons() {
        setGameButton(R.id.quickplay, false);
        setGameButton(R.id.playCards, true);

        buttonUtils.setButton(findViewById(R.id.btn_settings), this::gotoSettings);
        buttonUtils.setButton(findViewById(R.id.button_Statistics), this::gotoStatistics);
        buttonUtils.setButton(infoGif, this::gotoInstructions);
    }

    private void browseClasses() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.home_screen_browse_classes, null);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        List<CharacterClassStore> characterClasses = generateCharacterClasses();

        List<List<CharacterClassStore>> pages = new ArrayList<>();
        for (int i = 0; i < characterClasses.size(); i++) {
            pages.add(characterClasses.subList(i, i + 1));
        }

        ViewPager viewPager = dialogView.findViewById(R.id.classRecyclerView);
        DotsIndicator dotsIndicator = dialogView.findViewById(R.id.dots_indicator);

        CharacterClassPagerAdapter pagerAdapter = new CharacterClassPagerAdapter(pages);
        viewPager.setAdapter(pagerAdapter);
        dotsIndicator.setViewPager(viewPager);

        AlertDialog dialog = builder.setView(dialogView).create();
        buttonUtils.setButton(btnClose, dialog::dismiss);
        dialog.show();
    }

    private void setGameButton(int buttonId, boolean playCards) {
        View button = findViewById(buttonId);
        buttonUtils.setButton(button, () -> {
            Game.getInstance().setPlayCards(playCards);
            if (playCards) {
                gotoPlayerChoice();
            } else {
                showGameModeDialog();
            }
        });
    }

    private void showGameModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.home_screen_choose_mode, null);
        View btnClassic = dialogView.findViewById(R.id.btn_mode_classic);
        View btnHunt = dialogView.findViewById(R.id.btn_mode_class_hunt);
        View btnCrazy = dialogView.findViewById(R.id.btn_mode_crazy);

        AlertDialog dialog = builder.setView(dialogView).create();

        buttonUtils.setButton(btnClassic, () -> {
            Game.getInstance().setGameMode(Game.GameMode.CLASSIC);
            dialog.dismiss();
            gotoPlayerChoice();
        });

        buttonUtils.setButton(btnHunt, () -> {
            Game.getInstance().setGameMode(Game.GameMode.CLASS_HUNT);
            dialog.dismiss();
            gotoPlayerChoice();
        });

        buttonUtils.setButton(btnCrazy, () -> {
            Game.getInstance().setGameMode(Game.GameMode.CRAZY);
            dialog.dismiss();
            gotoPlayerChoice();
        });

        dialog.show();
    }

    private void setupDrinkGif() {
        drinkGif.setOnClickListener(v -> {
            if (!getMuteSoundState()) {
                AudioManager.getInstance().playNextSong();
            }
        });

        drinkGif.setOnLongClickListener(v -> {
            if (!getMuteSoundState()) {
                toggleSoundEffects();
                return true;
            }
            return false; // no action when muted
        });
    }



    private void toggleSoundEffects() {
        GeneralSettingsLocalStore settings = GeneralSettingsLocalStore.fromContext(this);
        boolean regularSound = settings.shouldPlayRegularSound();

        settings.setShouldPlayRegularSound(!regularSound);
        buttonUtils.playSoundEffects();
        buttonUtils.vibrateDevice();

        String message = regularSound
                ? "Burp sound effects activated!"
                : "Bop sound effects activated!";

        StyleableToast.makeText(this, message, R.style.newToast).show();
    }

}
