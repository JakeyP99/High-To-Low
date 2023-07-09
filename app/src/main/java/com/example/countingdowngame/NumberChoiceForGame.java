package com.example.countingdowngame;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.stores.GeneralSettingsLocalStore;

import java.util.Random;

public class NumberChoiceForGame extends ButtonUtilsActivity {
    private int startingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_number_choice);
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(R.id.btnRandomNumber);
        resetStartingNumber();
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
            Toast.makeText(NumberChoiceForGame.this, "That's a lot of numbers, unfortunately too many :(", Toast.LENGTH_SHORT).show();
            return;
        }


        if (inputValue.isEmpty()) {
            Toast.makeText(NumberChoiceForGame.this, "Please choose a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputNumber = Integer.parseInt(inputValue);

        if (inputNumber <= 0) {
            Toast.makeText(NumberChoiceForGame.this, "Please choose a number greater than zero!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        startingNumber = inputNumber;

        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .onEnd(animator -> {
                    // Animation has ended, start the MainActivity here
                    startMainActivity();
                })
                .playOn(originalNumberField);

        originalNumberField.setFocusable(false);

    }

    private void onRandomClicked() {
        Random random = new Random();
        startingNumber = random.nextInt(5000) + 1;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);
        startMainActivity();
    }

    private void startMainActivity() {
        boolean switchOneChecked = GeneralSettingsLocalStore.fromContext(this).isSingleScreen();
        Class<?> targetClass = switchOneChecked ? MainActivityGame.class : MainActivitySplitScreen.class;
        Intent i = getIntentForClass(targetClass, true);
        i.putExtra("startingNumber", startingNumber);
        startActivity(i);
    }

   public void resetStartingNumber() {
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setText(""); // Clear the input field
        originalNumberField.setFocusableInTouchMode(true); // Enable editing of the field
    }


}
