package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EndActivity.this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_layout);

        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        // Retrieve the saved state of the switches from shared preferences
        SharedPreferences preferences = getSharedPreferences("game_mode_choice", MODE_PRIVATE);
        boolean switchOneChecked = preferences.getBoolean("button_gameModeOne", false);
        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        if (switchOneChecked) {
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.custom_list_item, R.id.previousNumbers, MainActivity.gameInstance.getPreviousNumbersFormatted());
            previousNumbersList.setAdapter(adapter);

            ButtonUtils.setButton(btnPlayAgain, NumberChoice.class, this, () -> {
                MainActivity.gameInstance.playAgain();
            });
        } else {
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.custom_list_item, R.id.previousNumbers, MainActivitySplitScreen.gameInstance.getPreviousNumbersFormatted());
            previousNumbersList.setAdapter(adapter);

            ButtonUtils.setButton(btnPlayAgain, NumberChoice.class, this, () -> {
                MainActivity.gameInstance.playAgain();
            });

        }

        ButtonUtils.setButton(btnNewPlayer,PlayerNumberChoice.class, this, null);


    }
}
