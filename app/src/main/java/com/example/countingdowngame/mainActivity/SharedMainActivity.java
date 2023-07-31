package com.example.countingdowngame.mainActivity;

import android.widget.TextView;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;

import java.util.Collections;
import java.util.List;

public class SharedMainActivity {

    public static void setNameSizeBasedOnInt(TextView textView, String text) {
        int textSize;
        if (text.length() > 24) {
            textSize = 15;
        } else if (text.length() > 18) {
            textSize = 20;
        } else if (text.length() > 14) {
            textSize = 23;
        } else if (text.length() > 8) {
            textSize = 28;
        } else {
            textSize = 38;
        }
        textView.setTextSize(textSize);
    }



    public static void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 70;
        int minSize = 47;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }

    public static void splitScreenSetTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 65;
        int minSize = 45;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }

        public static void reverseTurnOrder(Player player) {
            Game game = Game.getInstance();
            List<Player> players = game.getPlayers();
            Collections.reverse(players);

            int currentPlayerIndex = players.indexOf(player);

            if (currentPlayerIndex != -1) {
                int lastIndex = players.size() - 1;
                int newIndex = lastIndex - currentPlayerIndex;

                // Move the player to the new index
                players.remove(currentPlayerIndex);
                players.add(newIndex, player);

                // Update the current player ID if necessary
                if (game.getCurrentPlayer() == player) {
                    game.setCurrentPlayerId(newIndex);
                }
            }

            game.setPlayerList(players);
        }


}
