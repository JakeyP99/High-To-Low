package com.example.countingdowngame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class NumberChoice extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NumberChoice.this, PlayerNumber.class));
    }
    static int startingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice);

        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);


        ButtonUtils.setButtonTouchListener(btnSubmit, v -> {
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

            } catch (NumberFormatException e) {
                bop.start();
            }
        }, this);

    }
}