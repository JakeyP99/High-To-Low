package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class EndActivity extends ButtonUtilsActivity {
    @Override
    public void onBackPressed() {
        gotoHomeScreen();
    }
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a6_end_game);

        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.list_view_end_game, R.id.previousNumbers, Game.getInstance().getPreviousNumbersFormatted());
        previousNumbersList.setAdapter(adapter);

        btnUtils.setButton(btnPlayAgain, () -> {

            Game.getInstance().playAgain();
            gotoGameStart();
        });

        btnUtils.setButton(btnNewPlayer, this::gotoGameSetup);
    }
}
