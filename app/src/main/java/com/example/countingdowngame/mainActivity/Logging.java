package com.example.countingdowngame.mainActivity;

import android.util.Log;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.wildCards.WildCardProperties;

public class Logging {

    protected static void logPlayerInformation(Player currentPlayer) {
        Log.d("renderPlayer", "Current number is " + Game.getInstance().getCurrentNumber() +
                " - Player was rendered " + currentPlayer.getName() +
                " is a " + currentPlayer.getClassChoice() +
                " with " + currentPlayer.getWildCardAmount() +
                " Wildcards " +
                "and " + currentPlayer.getUsedClassAbility() +
                " is the class ability and are they removed ?" +
                currentPlayer.isRemoved());
    }

    protected static void logSelectedCardInfo(WildCardProperties selectedCard, String wildCardType) {
        Log.d("WildCardInfo", "Type: " + wildCardType + ", " +
                "Question: " + selectedCard.getText() + ", " +
                "Answer: " + selectedCard.getAnswer() + ", " +
                "Wrong Answer 1: " + selectedCard.getWrongAnswer1() + ", " +
                "Wrong Answer 2: " + selectedCard.getWrongAnswer2() + ", " +
                "Wrong Answer 3: " + selectedCard.getWrongAnswer3() + ", " +
                "Category: " + selectedCard.getCategory());
    }
}
