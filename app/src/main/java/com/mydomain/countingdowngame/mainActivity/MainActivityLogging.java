package com.mydomain.countingdowngame.mainActivity;

import android.util.Log;

import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;

public class MainActivityLogging {

    public static void logPlayerInformation(Player currentPlayer) {
        Log.d("renderPlayer", "Current number is " + Game.getInstance().getCurrentNumber() +
                " - Player was rendered " + currentPlayer.getName() +
                " is a " + currentPlayer.getClassChoice() +
                " with " + currentPlayer.getWildCardAmount() +
                " Wildcards " +
                "and " + currentPlayer.getUsedActiveAbility() +
                " is the class ability and are they removed ?" +
                currentPlayer.isRemoved());
    }

    protected static void logSelectedCardInfo(WildCardProperties selectedCard, String wildCardType) {
        Log.d("WildCardInfo", "Type: " + wildCardType + ", " +
                "Question: " + selectedCard.getWildCard() + ", " +
                "Answer: " + selectedCard.getAnswer() + ", " +
                "Wrong Answer 1: " + selectedCard.getWrongAnswer1() + ", " +
                "Wrong Answer 2: " + selectedCard.getWrongAnswer2() + ", " +
                "Wrong Answer 3: " + selectedCard.getWrongAnswer3() + ", " +
                "Category: " + selectedCard.getCategory());
    }
}
