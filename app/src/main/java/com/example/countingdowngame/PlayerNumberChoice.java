package com.example.countingdowngame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerNumberChoice extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlayerNumberChoice.this, HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playernumber_activity);

        Button btnSubmitPlayers = findViewById(R.id.btnSubmitPlayers);
        final EditText originalPlayerField = findViewById(R.id.EditTextViewplayernumber);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);

        ButtonUtils.setButton(btnSubmitPlayers, null, this, () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                originalPlayerField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO); // Disable Google autofill
            }
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

                MainActivitySplitScreen.gameInstance.setPlayers(inputNumber);

                Intent intent = new Intent(PlayerNumberChoice.this, PlayerNameChoice.class);
                intent.putExtra("playerCount", inputNumber);
                startActivity(intent);

                bop.start();

            } catch (NumberFormatException e) {
                bop.start();
                Toast.makeText(PlayerNumberChoice.this, "That's wayyyy too many players", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
