package com.example.countingdowngame.mainActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtils;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class HomeScreen extends ButtonUtilsActivity {
    private GifImageView muteGif;
    private GifImageView soundGif;
    private GifImageView drinkGif;
    private ButtonUtils buttonUtils;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().resumeBackgroundMusic();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
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
        setContentView(R.layout.a1_home_screen);
        buttonUtils = new ButtonUtils(this);

        initializeViews();
        instructionalOverlay();

        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        drinkGif = findViewById(R.id.drinkGif);
    }


    private void setupButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);

        // Set onClickListener for buttons
        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);

        drinkGif.setOnClickListener(view -> {
            GeneralSettingsLocalStore settingsStore = GeneralSettingsLocalStore.fromContext(this);
            boolean regularSoundSelected = settingsStore.shouldPlayRegularSound();
            settingsStore.setShouldPlayRegularSound(!regularSoundSelected);
            buttonUtils.playSoundEffects();
            buttonUtils.vibrateDevice();
        });
    }


    public void instructionalOverlay() {
        // Create the dialog with the custom theme
        final Dialog dialog = new Dialog(this, R.style.WalkthroughTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set the content view and other properties of the dialog
        dialog.setContentView(R.layout.instructional_overlay);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // Find views
        Button btnPlayerImage = dialog.findViewById(R.id.btnPlayerImageInstruction);
        TextView playerImageTextView = dialog.findViewById(R.id.textview_instructions_playerimage);

        Button numberCounterButton = dialog.findViewById(R.id.btnNumberCounter);
        TextView numberCounterTextView = dialog.findViewById(R.id.textview_instructions_drinkcounter);

        Button generateButton = dialog.findViewById(R.id.btnGenerateInstruction);
        TextView generateTextView = dialog.findViewById(R.id.textview_instructions_generate);

        Button wildCardButton = dialog.findViewById(R.id.btnWildCard);
        TextView wildCardTextView = dialog.findViewById(R.id.textview_instructions_wildcard);

        // Hide unnecessary views initially
        View[] viewsToHide = {
                numberCounterButton,
                numberCounterTextView,
                generateButton,
                generateTextView,
                wildCardButton,
                wildCardTextView
        };

        for (View view : viewsToHide) {
            view.setVisibility(View.INVISIBLE);
        }

        // Set initial instructions
        setInitialInstructions(playerImageTextView);

        // Set up click listeners
        setupClickListeners(dialog, btnPlayerImage, playerImageTextView,
                numberCounterButton, numberCounterTextView,
                generateButton, generateTextView,
                wildCardButton, wildCardTextView);

        // Show the dialog
        dialog.show();
    }

    private void setInitialInstructions(TextView playerImageTextView) {
        playerImageTextView.setText("If you click on your image, you will see your abilities.");
    }

    private void setupClickListeners(Dialog dialog, Button playerImageButton, TextView playerImageTextInstruction,
                                     Button numberCounterButton, TextView numberCounterInstruction,
                                     Button generateButton, TextView generateTextView,
                                     Button wildCardInstructionButton, TextView wildCardInstructionTextView) {

        // Set up click listener for the player image button
        playerImageButton.setOnClickListener(view -> {
            playerImageButton.setVisibility(View.INVISIBLE);
            playerImageTextInstruction.setVisibility(View.INVISIBLE);

            numberCounterButton.setVisibility(View.VISIBLE);
            numberCounterInstruction.setVisibility(View.VISIBLE);

            numberCounterInstruction.setText("This is your drinks counter. It will go up every third turn. When the game ends, the player who lost needs to take the total amount of drinks.");
        });

        // Set up click listener for the number counter instruction
        numberCounterButton.setOnClickListener(view -> {
            numberCounterButton.setVisibility(View.INVISIBLE);
            numberCounterInstruction.setVisibility(View.INVISIBLE);

            generateButton.setVisibility(View.VISIBLE);
            generateTextView.setVisibility(View.VISIBLE);

            generateTextView.setText("This is the generate button. When clicked, the number on the screen will change to a random number between 0 and your number. If it lands on 0, you lose.");
        });

        // Set up click listener for the generate button instruction
        generateButton.setOnClickListener(view -> {
            generateButton.setVisibility(View.INVISIBLE);
            generateTextView.setVisibility(View.INVISIBLE);

            wildCardInstructionButton.setVisibility(View.VISIBLE);
            wildCardInstructionTextView.setVisibility(View.VISIBLE);

            wildCardInstructionTextView.setText("This is the wildcard button. Depending on the settings you choose, you may get a truth, activity, quiz, etc.");
        });

        // Set up click listener for the wildcard instruction
        wildCardInstructionButton.setOnClickListener(view -> dialog.dismiss());
    }
}