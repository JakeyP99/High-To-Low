package com.example.countingdowngame;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNumberChoice extends ButtonUtilsActivity {
    private EditText originalPlayerField;
    private int totalPlayerCount;

    @Override
    public void onBackPressed() {
        startActivity(getSafeIntent(HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a2_player_number_choice);

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

            Intent intent = getSafeIntent(Settings_PlayerModel.class);
            intent.putExtra("playerCount", inputNumber);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(PlayerNumberChoice.this, "Invalid player count", Toast.LENGTH_SHORT).show();
        }
    }
}

