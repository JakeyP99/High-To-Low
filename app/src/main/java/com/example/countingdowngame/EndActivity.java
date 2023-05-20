package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class EndActivity extends ButtonUtilsActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EndActivity.this, HomeScreen.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_layout);

        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        // Retrieve the saved state of the switches from shared preferences
        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.custom_list_item, R.id.previousNumbers, Game.getInstance().getPreviousNumbersFormatted());
        previousNumbersList.setAdapter(adapter);

        btnUtils.setButton(btnPlayAgain, NumberChoice.class, () -> {
            Game.getInstance().playAgain();
        });

        btnUtils.setButton(btnNewPlayer, PlayerNumberChoice.class, null);
    }
}
