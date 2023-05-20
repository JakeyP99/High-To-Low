package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class NumberChoice extends AppCompatActivity {
    int startingNumber;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("resetCounter", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice);
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(R.id.btnRandomNumber);

        ButtonUtils.setButton(btnSubmit, null, this, this::onSubmitClicked);
        ButtonUtils.setButton(btnRandom, null, this, this::onRandomClicked);
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

        try {
            int inputNumber = Integer.parseInt(inputValue);

            if (inputNumber <= 0) {
                Toast.makeText(NumberChoice.this, "Please choose a number greater than zero!", Toast.LENGTH_SHORT).show();
                return;
            }

            startingNumber = inputNumber;

            originalNumberField.setFocusable(false);

            Intent intent;
            SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
            boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
            Class<?> targetClass = switchOneChecked ? MainActivity.class : MainActivitySplitScreen.class;
            intent = new Intent(NumberChoice.this, targetClass);

// Add the startingNumber as an extra to the intent
            intent.putExtra("startingNumber", startingNumber);

// Start the MainActivity or MainActivitySplitScreen
            startActivity(intent);
        } catch (NumberFormatException e) {
        }
    }

    private void onRandomClicked() {
        Random random = new Random();
        int randomNum = random.nextInt(5000) + 1;
        startingNumber = randomNum;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);

        Intent intent;
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
        Class<?> targetClass = switchOneChecked ? MainActivity.class : MainActivitySplitScreen.class;
        intent = new Intent(NumberChoice.this, targetClass);

// Add the startingNumber as an extra to the intent
        intent.putExtra("startingNumber", startingNumber);

// Start the MainActivity or MainActivitySplitScreen
        startActivity(intent);
    }
}
