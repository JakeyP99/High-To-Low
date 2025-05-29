package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;

import android.util.Log;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

public class passiveAbilities {
    public static void handleWitchPassive(Player currentPlayer, MainActivityGame activity) {
        Log.d("TAG", "First turn" + isFirstTurn);
        if (!isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() % 2 == 0) {
                activity.showGameDialog(WITCH + "'s Passive: \n\n" + currentPlayer.getName() + " hand out 1 drink.");
                currentPlayer.incrementDrinksHandedOutByWitch(1);
            } else {
                activity.showGameDialog(WITCH + "'s Passive: \n\n" + currentPlayer.getName() + " take 1 drink.");
                currentPlayer.incrementDrinksTakenByWitch(1);
            }
        }
    }

}
