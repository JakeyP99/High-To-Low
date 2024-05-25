package com.example.countingdowngame.numberChoice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.SettingsMenu;
import com.example.countingdowngame.utils.AudioManager;
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
        setContentView(R.layout.a4_number_choice);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        resetStartingNumber();
        setupButtonControls();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        originalNumberField = findViewById(R.id.EditTextView_numberchoice);
    }

    private void setupButtonControls() {
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(R.id.btnRandomNumber);

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

        int length = inputValue.length(); // Store the length of the string
        if (length > 9) {
            StyleableToast.makeText(getApplicationContext(), "That's a lot of numbers, unfortunately too many :(", R.style.newToast).show();
            return;
        }


        if (inputValue.isEmpty()) {
            StyleableToast.makeText(getApplicationContext(), "Please choose a number!", R.style.newToast).show();
            return;
        }

        int inputNumber = Integer.parseInt(inputValue);

        if (inputNumber <= 0) {
            StyleableToast.makeText(getApplicationContext(), "Please choose a number greater than zero!", R.style.newToast).show();
            return;
        }
        startingNumber = inputNumber;
        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .onEnd(animator -> {
                    // Animation has ended, start the MainActivity here
                    goToInGameSettings();
                })
                .playOn(originalNumberField);

        originalNumberField.setFocusable(false);

    }

    private void onRandomClicked() {
        Random random = new Random();
        startingNumber = random.nextInt(5000) + 1;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);
        goToInGameSettings();
    }


    private void goToInGameSettings() {
        Intent i = getIntentForClass(SettingsMenu.class);
        i.putExtra("startingNumber", startingNumber);
        startActivity(i);
    }

   public void resetStartingNumber() {
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setText(""); // Clear the input field
        originalNumberField.setFocusableInTouchMode(true); // Enable editing of the field
    }


}
