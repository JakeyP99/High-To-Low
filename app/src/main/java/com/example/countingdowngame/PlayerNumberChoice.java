package com.example.countingdowngame;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNumberChoice extends ButtonUtilsActivity {
    private EditText originalPlayerField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a2_player_number_choice);

        Button btnSubmitPlayers = findViewById(R.id.btnSubmitPlayers);
        originalPlayerField = findViewById(R.id.EditTextViewplayernumber);

        btnUtils.setButton(btnSubmitPlayers, this::submitPlayerNumber);
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

            if (inputValue.length() > 3) {
                Toast.makeText(PlayerNumberChoice.this, "That's way too many players.... Unless you're that popular?", Toast.LENGTH_SHORT).show();
                return;
            }

            Game.getInstance().setPlayers(inputNumber);
            startActivity(getIntentForClass(PlayerModel.class, true));

        } catch (NumberFormatException e) {
            Toast.makeText(PlayerNumberChoice.this, "Invalid player count", Toast.LENGTH_SHORT).show();
        }
    }
}