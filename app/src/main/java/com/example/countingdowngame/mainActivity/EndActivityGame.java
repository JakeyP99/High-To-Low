package com.example.countingdowngame.mainActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;

public class EndActivityGame extends ButtonUtilsActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoHomeScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a6_end_game);

        if (!GeneralSettingsLocalStore.fromContext(this).isMuted()) {
            AudioManager.getInstance().playRandomBackgroundMusic(this);
        }

        ListView previousNumbersList = findViewById(R.id.previousNumbers);
        Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        setupEndGameText();
        setupPreviousNumbersList(previousNumbersList);
        setButtonActions(btnPlayAgain, btnNewPlayer);
    }

    private void setupEndGameText() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        String playerName = currentPlayer.getName();
        int numberCounter = MainActivityGame.drinkNumberCounterInt;

        String endGameText = (numberCounter == 1) ?
                "Drink " + numberCounter + " time " + playerName + " Ya Lil Baby!!" :
                "Drink " + numberCounter + " times " + playerName + " Ya Lil Baby!!";

        TextView endGameName = findViewById(R.id.TextViewlose);
        endGameName.setText(endGameText);
    }

    private void setupPreviousNumbersList(ListView previousNumbersList) {
        ArrayList<String> previousNumbersFormatted = Game.getInstance().getPreviousNumbersFormatted();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivityGame.this,
                R.layout.list_view_end_game, R.id.previousNumbers, previousNumbersFormatted);
        previousNumbersList.setAdapter(adapter);
    }

    private void setButtonActions(Button btnPlayAgain, Button btnNewPlayer) {
        btnUtils.setButton(btnPlayAgain, () -> {
            Game.getInstance().resetPlayers(this);
            gotoNumberChoice();
        });
        btnUtils.setButton(btnNewPlayer, this::gotoPlayerNumberChoice);
    }
}
