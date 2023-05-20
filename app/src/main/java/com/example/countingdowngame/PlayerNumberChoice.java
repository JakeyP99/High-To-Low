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

public class PlayerNumberChoice extends AppCompatActivity {
    private EditText originalPlayerField;

    private final ButtonUtils btnUtils = new ButtonUtils(this);

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlayerNumberChoice.this, HomeScreen.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnUtils.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playernumber_activity);

        Button btnSubmitPlayers = findViewById(R.id.btnSubmitPlayers);
        originalPlayerField = findViewById(R.id.EditTextViewplayernumber);

        btnUtils.setButton(btnSubmitPlayers, null, this::submitPlayerNumber);
    }

    private void submitPlayerNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            originalPlayerField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO); // Disable Google autofill
        }

        String inputValue = originalPlayerField.getText().toString();
        if (inputValue.isEmpty()) {
            return;
        }

        try {
            int inputNumber = Integer.parseInt(inputValue);
            if (inputNumber <= 0) {
                return;
            }

            SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
            boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
            if (switchOneChecked) {
                Game.getInstance().setPlayers(inputNumber);
            } else {
                MainActivitySplitScreen.gameInstance.setPlayers(inputNumber);
            }

            Intent intent = new Intent(PlayerNumberChoice.this, PlayerNameChoice.class);
            intent.putExtra("playerCount", inputNumber);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(PlayerNumberChoice.this, "That's wayyyy too many players", Toast.LENGTH_SHORT).show();
        }
    }
}

