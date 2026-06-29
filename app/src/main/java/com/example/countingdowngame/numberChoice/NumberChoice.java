package com.example.countingdowngame.numberChoice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivityRoulette.MainActivityRoulette;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class NumberChoice extends ButtonUtilsActivity {
    private int startingNumber;
    private EditText originalNumberField;
    private pl.droidsonroids.gif.GifImageView muteGif;
    private pl.droidsonroids.gif.GifImageView soundGif;
    private boolean isGenerating = false;

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isGenerating) {
                    finish();
                }
            }
        });
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
            btnRandom.setVisibility(View.GONE);
        } else {
            btnRandom.setVisibility(View.VISIBLE);
        }

        // Set onClickListener for buttons
        btnUtils.setButton(btnSubmit, this::onSubmitClicked);
        btnUtils.setButton(btnRandom, this::onRandomClicked);
    }

    private void onSubmitClicked() {
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
        isGenerating = true;
        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .onEnd(animator -> {
                    // Animation has ended, start the MainActivity here
                    isGenerating = false;

                    if (Game.getInstance().isPlayCards()) {
                        goToCardGame(startingNumber);
                    } else {
                        gotoGame(startingNumber);
                    }
                })
                .playOn(originalNumberField);

        originalNumberField.setFocusable(false);
    }


    private void onRandomClicked() {
        Button btnRandom = findViewById(R.id.btnRandomNumber);
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        btnRandom.setEnabled(false);
        btnSubmit.setEnabled(false);
        originalNumberField.setFocusable(false);
        isGenerating = true;

        Random random = new Random();
        int range = 5000;
        int targetNumber = random.nextInt(range) + 1;

        final int[] count = {0};
        final long[] currentDelay = {30};
        final int totalSteps = 13;

        Handler handler = new Handler();
        Runnable rouletteRunnable = new Runnable() {
            @Override
            public void run() {
                if (count[0] < totalSteps) {
                    int tempNumber = random.nextInt(range) + 1;
                    originalNumberField.setText(String.valueOf(tempNumber));
                    count[0]++;
                    currentDelay[0] = (long) (currentDelay[0] * 1.2); // Slow down
                    handler.postDelayed(this, currentDelay[0]);
                } else {
                    originalNumberField.setText(String.valueOf(targetNumber));
                    startingNumber = targetNumber;

                    YoYo.with(Techniques.Bounce)
                            .duration(600)
                            .onEnd(animator -> {
                                isGenerating = false;
                                btnRandom.setEnabled(true);
                                btnSubmit.setEnabled(true);
                                if (Game.getInstance().isPlayCards()) {
                                    goToCardGame(startingNumber);
                                } else {
                                    goToInGameSettings(startingNumber);
                                }
                            })
                            .playOn(originalNumberField);
                }
            }
        };
        handler.post(rouletteRunnable);
    }


    public void resetStartingNumber() {
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
        try {
            int playerChamberCount = Integer.parseInt(originalNumberField.getText().toString());
            GeneralSettingsLocalStore.fromContext(this).setChamberCount(playerChamberCount);
        } catch (NumberFormatException e) {
            // If empty or invalid, we don't save
        }
    }

}
