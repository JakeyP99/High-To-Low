package com.example.countingdowngame.mainActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
public class EndActivityGame extends ButtonUtilsActivity {

    //-----------------------------------------------------Handle back press---------------------------------------------------//

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoHomeScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a6_end_game);

        //-----------------------------------------------------Initialize UI elements---------------------------------------------------//

        TextView endGameName = findViewById(R.id.TextViewlose);
        ListView previousNumbersList = findViewById(R.id.previousNumbers);
        Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        Button btnNewPlayer = findViewById(R.id.btnNewPlayer);
        //-----------------------------------------------------Set end game text---------------------------------------------------//

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        String playerName = currentPlayer.getName();
        int numberCounter = MainActivityGame.drinkNumberCounterInt; // Access the currentNumber

        String endGameText;
        if (numberCounter == 1) {
            endGameText = "Drink " + numberCounter + " time " + playerName + " Ya Lil Baby!!";
        } else {
            endGameText = "Drink " + numberCounter + " times " + playerName + " Ya Lil Baby!!";
        }


        endGameName.setText(endGameText);
        //-----------------------------------------------------Set up previous numbers list---------------------------------------------------//

        ArrayList<String> previousNumbersFormatted = Game.getInstance().getPreviousNumbersFormatted();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivityGame.this, R.layout.list_view_end_game, R.id.previousNumbers, previousNumbersFormatted);
        previousNumbersList.setAdapter(adapter);
        //-----------------------------------------------------Button actions---------------------------------------------------//

        btnUtils.setButton(btnPlayAgain, () -> {
            Game.getInstance().resetPlayers(this);
            gotoGameStart();
        }, this);
        btnUtils.setButton(btnNewPlayer, this::gotoGameSetup, this);
    }
}
