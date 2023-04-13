package com.example.countingdowngame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerNumber extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlayerNumber.this, HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playernumber_activity);

        Button btnSubmitPlayers = findViewById(R.id.btnSubmitPlayers);
        final EditText originalPlayerField = findViewById(R.id.EditTextViewplayernumber);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);

        ButtonUtils.setButtonNoClass(btnSubmitPlayers, null, this, () -> {
            String inputValue = originalPlayerField.getText().toString();
            if (inputValue.length() <= 0) {
                bop.start();
                return;
            }
            try {
                int inputNumber = Integer.parseInt(inputValue);

                if (inputNumber <= 0) {
                    bop.start();
                    return;
                }

                MainActivity.gameInstance.setPlayers(inputNumber);

                originalPlayerField.setFocusable(false);
                startActivity(new Intent(PlayerNumber.this, NumberChoice.class));

            } catch (NumberFormatException e) {
                bop.start();
                Toast.makeText(PlayerNumber.this, "That's wayyyy too many players", Toast.LENGTH_SHORT).show();
            }


        });
    }
}
