package com.example.countingdowngame.mainActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.util.Log;
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
        closeButton.setOnClickListener(v -> dialog.dismiss());
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


    static void logPlayerInformation(Player currentPlayer) {
        Log.d("renderPlayer", "Current number is " + Game.getInstance().getCurrentNumber() +
                " - Player was rendered " + currentPlayer.getName() +
                " is a " + currentPlayer.getClassChoice() +
                " with " + currentPlayer.getWildCardAmount() +
                " Wildcards " +
                "and " + currentPlayer.usedClassAbility() +
                " is the class ability and are they removed ?" +
                currentPlayer.isRemoved());
    }

}
