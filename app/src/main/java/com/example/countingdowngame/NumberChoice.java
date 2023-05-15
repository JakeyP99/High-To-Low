package com.example.countingdowngame;

import static com.example.countingdowngame.R.id.btnRandomNumber;

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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("resetCounter", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    static int startingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice);
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(btnRandomNumber);

        ButtonUtils.setButton(btnSubmit,null, this, () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                originalNumberField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO); // Disable Google autofill
            }

            String inputValue = originalNumberField.getText().toString();

            if (inputValue.length() < 0 || inputValue.length() > 9) {
                Toast.makeText(NumberChoice.this, "That's a lot of numbers, unfortunately too many :(", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int inputNumber = Integer.parseInt(inputValue);

                if (inputNumber <= 0) {
                    return;
                }

                startingNumber = inputNumber;

                originalNumberField.setFocusable(false);

                SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
                boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
                if (switchOneChecked) {
                    startActivity(new Intent(NumberChoice.this, MainActivity.class));
                } else {
                    startActivity(new Intent(NumberChoice.this, MainActivitySplitScreen.class));
                }


            } catch (NumberFormatException e) {
            }

        });

        ButtonUtils.setButton(btnRandom, null,this, () -> {
            randomNumberChoice();
        });
    }

    public void randomNumberChoice() {
        Random random = new Random();
        int randomNum = random.nextInt(5000) + 1;
        startingNumber = randomNum;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        originalNumberField.setFocusable(false);

        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
        if (switchOneChecked) {
            startActivity(new Intent(NumberChoice.this, MainActivity.class));
        } else {
            startActivity(new Intent(NumberChoice.this, MainActivitySplitScreen.class));
        }
    };
}