package com.example.countingdowngame.mainActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.Collections;
import java.util.List;

public class SharedMainActivity extends ButtonUtilsActivity {



    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView textView1, TextView textView2) {
        if (currentNumber == 0) {
            textView1.setText(String.valueOf(currentNumber));
            textView2.setText(String.valueOf(currentNumber));

            applyPulsingEffect(textView1);
            applyPulsingEffect(textView2);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Game.getInstance().endGame(this);
                onEnd.run();
            }, 2000);
        } else {
            textView1.setText(String.valueOf(currentNumber));
            textView2.setText(String.valueOf(currentNumber));
            Game.getInstance().nextPlayer();
        }
    }

    void applyPulsingEffect(TextView textView) {
        // Apply pulsing effect to textView
        ObjectAnimator pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
                textView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f, 1.0f)
        );
        pulseAnimation.setDuration(1000); // Adjust the pulsing duration (in milliseconds) as per your preference
        pulseAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        pulseAnimation.start();
    }

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

    public static class TextSizeCalculator {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();

            if (charCount <= 30) {
                textSize = 33; // Set text size to 20sp for short texts
            } else if (charCount <= 70) {
                textSize = 28; // Set text size to 16sp for moderately long texts
            } else {
                textSize = 25; // Set text size to 12sp for long texts (adjust as needed)
            }

            return textSize;
        }
    }

    public void disableAllButtons(Button[] buttons) {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    public void enableAllButtons(Button[] buttons) {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }

    public void resetButtonBackgrounds(Button[] buttons) {
        for (Button button : buttons) {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.outlineforbutton));
        }
    }

    public void showDialogWithFixedTextSize(String text, int textSizeSp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.mainactivity_dialog_box, null);
        TextView dialogTextView = dialogView.findViewById(R.id.dialogbox_textview);

        // Set the fixed text size
        dialogTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);

        dialogTextView.setText(text);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }


    public static int quizAnswerTextSize(String answer) {
        int textSize;
        int answerLength = answer.length();

        if (answerLength > 16) {
            textSize = 15; // Set text size to 15sp for answers longer than 20 characters
        } else if (answerLength > 13) {
            textSize = 18; // Set text size to 18sp for answers longer than 15 characters
        } else if (answerLength > 10) {
            textSize = 20; // Set text size to 20sp for answers longer than 10 characters
        } else {
            textSize = 23; // Set default text size to 23sp for shorter answers
        }

        return textSize;
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

    //todo fix reverse turn
    public static void reverseTurnOrder(Player player) {
        Game currentGame = Game.getInstance();
        List<Player> players = currentGame.getPlayers();
        Collections.reverse(players);

        // Find the current player's index
        int currentPlayerIndex = players.indexOf(player);

        if (currentPlayerIndex != -1) {
            // Calculate the new positions for all players
            int numPlayers = players.size();
            for (int i = 0; i < numPlayers; i++) {
                int newPlayerIndex = (currentPlayerIndex + i) % numPlayers;
                Player currentPlayer = players.get(newPlayerIndex);
                currentPlayer.setPosition(i);
            }

            // Log the updated order of players' turns
            logTurnOrder(players);

            // Update the current player's ID if necessary
            int previousPlayerIndex = (currentPlayerIndex - 1 + numPlayers) % numPlayers;
            currentGame.setCurrentPlayerId(previousPlayerIndex);

            // Log the current player and the next player
            Player currentPlayer = players.get(currentPlayerIndex);
            int nextPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
            Player nextPlayer = players.get(nextPlayerIndex);

            logCurrentAndNextPlayer(currentPlayer, nextPlayer);
        }

        currentGame.setPlayerList(players);
    }


    private static void logTurnOrder(List<Player> players) {
        System.out.println("Turn order:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println("Player " + players.get(i).getName() + " - Position " + i);
        }
    }

    private static void logCurrentAndNextPlayer(Player currentPlayer, Player nextPlayer) {
        System.out.println("Current Player: " + currentPlayer.getName());
        System.out.println("Next Player: " + nextPlayer.getName());
    }


}
