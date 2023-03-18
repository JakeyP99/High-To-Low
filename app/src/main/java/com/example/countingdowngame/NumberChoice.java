package com.example.countingdowngame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class NumberChoice extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        // Do nothing
    }
    static int startingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_choice);

        Button btnSubmit = findViewById(R.id.btnSubmitNumbers);
        final EditText originalNumberField = findViewById(R.id.EditTextView_numberchoice);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);
        final TextView nextPlayerText = findViewById(R.id.TextView_nextplayer);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputValue = originalNumberField.getText().toString();

                if (inputValue.length() <= 0 || inputValue.length() > 9) {
                    bop.start();
                    return;
                }

                int inputNumber = Integer.parseInt(inputValue);

                if (inputNumber <= 0) {
                    bop.start();
                    return;
                }

                startingNumber = inputNumber;

                originalNumberField.setFocusable(false);
                startActivity(new Intent(NumberChoice.this, MainActivity.class));
            }
        });


    }

}