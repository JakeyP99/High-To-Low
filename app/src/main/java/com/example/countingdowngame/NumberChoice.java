package com.example.countingdowngame;

import static com.example.countingdowngame.R.id.btnRandomNumber;

import android.content.Intent;
import android.media.MediaPlayer;
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
        startActivity(new Intent(NumberChoice.this, PlayerNumberChoice.class));
    }

    static int startingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice);
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        Button btnRandom = findViewById(btnRandomNumber);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);


        ButtonUtils.setButton(btnSubmit,null, this, () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                originalNumberField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO); // Disable Google autofill
            }

            String inputValue = originalNumberField.getText().toString();

            if (inputValue.length() < 0 || inputValue.length() > 9) {
                bop.start();
                Toast.makeText(NumberChoice.this, "That's a lot of numbers, unfortunately too many :(", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int inputNumber = Integer.parseInt(inputValue);

                if (inputNumber <= 0) {
                    bop.start();
                    return;
                }

                startingNumber = inputNumber;

                originalNumberField.setFocusable(false);
                startActivity(new Intent(NumberChoice.this, MainActivity.class));
                bop.start();

            } catch (NumberFormatException e) {
                bop.start();
            }

        });

        ButtonUtils.setButton(btnRandom, null,this, () -> {
            randomNumberChoice();
            bop.start();

        });
    }

    public void randomNumberChoice() {
        Random random = new Random();
        int randomNum = random.nextInt(999999998) + 1; // generates a random integer between 1 and 20000
        startingNumber = randomNum;
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);


        originalNumberField.setFocusable(false);
        startActivity(new Intent(NumberChoice.this, MainActivity.class));
    };
}