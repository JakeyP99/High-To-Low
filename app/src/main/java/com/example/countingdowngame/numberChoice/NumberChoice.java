package com.example.countingdowngame.numberChoice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityRoulette;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class NumberChoice extends ButtonUtilsActivity {
    private int startingNumber;
    private EditText originalNumberField;
    private GifImageView muteGif;
    private GifImageView soundGif;

    @Override
    protected void onResume() {
        super.onResume();
        originalNumberField.setText("");
        originalNumberField.setFocusableInTouchMode(true);
        originalNumberField.setFocusable(true);

        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice_main_activity);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        resetStartingNumber();
        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        TextView gameTitle = findViewById(R.id.TextView_GameTitle);


        if (Game.getInstance().isPlayCards()) {
            gameTitle.setText("How many bullets do you want?");
            originalNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        } else {
            originalNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        }
    }

    private void setupButtonControls() {
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(R.id.btnRandomNumber);

        if (Game.getInstance().isPlayCards()) {
            btnRandom.setVisibility(View.INVISIBLE);
        } else {
            btnRandom.setVisibility(View.VISIBLE);
        }

        // Set onClickListener for buttons
        btnUtils.setButton(btnSubmit, this::onSubmitClicked);
        btnUtils.setButton(btnRandom, this::onRandomClicked);
    }

    private void onSubmitClicked() {
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            originalNumberField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        String inputValue = originalNumberField.getText().toString();

        if (inputValue.isEmpty()) {
            StyleableToast.makeText(getApplicationContext(), "Please choose a number!", R.style.newToast).show();
            return;
        }

        if (Game.getInstance().isPlayCards()) {
            if (inputValue.length() > 1) {
                StyleableToast.makeText(
                        getApplicationContext(),
                        "That's a lot of numbers, unfortunately too many :(",
                        R.style.newToast
                ).show();
            }
        } else {
            if (inputValue.length() > 9) {
                StyleableToast.makeText(
                        getApplicationContext(),
                        "That's a lot of numbers, unfortunately too many :(",
                        R.style.newToast
                ).show();
            }
        }


        int inputNumber;
        try {
            inputNumber = Integer.parseInt(inputValue);
        } catch (NumberFormatException e) {
            StyleableToast.makeText(getApplicationContext(), "Invalid number!", R.style.newToast).show();
            return;
        }

        if (inputNumber <= 0) {
            StyleableToast.makeText(getApplicationContext(), "Please choose a number greater than zero!", R.style.newToast).show();
            return;
        }

        startingNumber = inputNumber;
        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .onEnd(animator -> {
                    // Animation has ended, start the MainActivity here

                    if (Game.getInstance().isPlayCards()) {
                        goToCardGame(startingNumber);
                    } else {
                        goToInGameSettings(startingNumber);
                    }
                })
                .playOn(originalNumberField);

        originalNumberField.setFocusable(false);
    }


    private void onRandomClicked() {
        Random random = new Random();
        startingNumber = random.nextInt(99999999) + 1;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);
        goToInGameSettings(startingNumber);
    }


    public void resetStartingNumber() {
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setText(""); // Clear the input field
        originalNumberField.setFocusableInTouchMode(true); // Enable editing of the field
    }



    private void goToCardGame(int chamberNumberCount) {
        savePreferences();

        // Create an Intent to start the main game activity
        Intent intent = new Intent(this, MainActivityRoulette.class);

        // Pass the extras to the main game activity
        intent.putExtra("chamberNumberCount", chamberNumberCount);
        // Start the main game activity
        startActivity(intent);
    }

    private void savePreferences() {
        int playerChamberCount = Integer.parseInt(originalNumberField.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setChamberCount(playerChamberCount);
    }





}
