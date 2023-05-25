package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;


public class NumberChoice extends ButtonUtilsActivity {
    private int startingNumber;
    private static NumberChoice instance;

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

        if (inputValue.isEmpty()) {
            Toast.makeText(NumberChoice.this, "Please choose a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputNumber = Integer.parseInt(inputValue);

        if (inputNumber <= 0) {
            Toast.makeText(NumberChoice.this, "Please choose a number greater than zero!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        startingNumber = inputNumber;

        originalNumberField.setFocusable(false);

        startMainActivity();
    }

    private void onRandomClicked() {
        Random random = new Random();
        startingNumber = random.nextInt(5000) + 1;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);
        startMainActivity();
    }

    private void startMainActivity() {
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", true);
        Class<?> targetClass = switchOneChecked ? MainActivity.class : MainActivitySplitScreen.class;
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
