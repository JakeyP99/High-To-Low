package com.example.countingdowngame.game;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    //-----------------------------------------------------Start Game Functions---------------------------------------------------//

    private static final Game gameInstance = new Game();
    private GameEventListener gameEventListener;
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerId = 0;
    private int startingNumber = 0;
    int currentNumber = 0;
    private boolean gameStarted = false;

    private final ArrayList<Integer> updatedNumbers = new ArrayList<>();


    public static Game getInstance() {
        return gameInstance;
    }
    public void setPlayers(Context context, int playerAmount) {
        if (gameStarted)
            return;

        players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            players.add(new Player(context, null, null));
        }
    }
    public List<Player> getPlayers() {
        return players;
    }

    public void setCurrentPlayerId(int playerId) {
        currentPlayerId = playerId;
    }

    public void startGame(int startNum, GameEventListener listener) {
        if (gameStarted)
            return;

        if (players.isEmpty())
            return;

        gameStarted = true;
        gameEventListener = listener;
        currentNumber = startNum;
        startingNumber = startNum;
        currentPlayerId = 0;
        updatedNumbers.clear();
    }

    //-----------------------------------------------------In Game---------------------------------------------------//

    public int getCurrentNumber() {
        return currentNumber;
    }

    public Player getCurrentPlayer() {
        if (!players.isEmpty() && currentPlayerId >= 0 && currentPlayerId < players.size()) {
            return players.get(currentPlayerId);
        } else {
            return null;
        }
    }

    public void nextNumber(Context context, final Runnable onEnd, TextView textView1, TextView textView2) {
        Random random = new Random();
        int nextNumber = random.nextInt(currentNumber + 1);
        currentNumber = nextNumber;

        updatedNumbers.add(nextNumber); // Add the updated number

        if (currentNumber == 0) {
            textView1.setText(String.valueOf(currentNumber)); // Update the textView with the new number
            textView2.setText(String.valueOf(currentNumber)); // Update the textView with the new number

            applyPulsingEffect(textView1);
            applyPulsingEffect(textView2);


            Handler handler = new Handler();
            handler.postDelayed(() -> {
                endGame(context);
                onEnd.run();
            }, 2000); // Adjust the delay time (in milliseconds) as per your preference
        } else {
            textView1.setText(String.valueOf(currentNumber)); // Update the textView with the new number
            textView2.setText(String.valueOf(currentNumber)); // Update the textView with the new number
            nextPlayer();
        }
    }


    private void applyPulsingEffect(TextView textView) {
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


    private void nextPlayer() {
        currentPlayerId = (currentPlayerId + 1) % players.size();

        if (gameEventListener != null) {
            gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
        }
    }

    //-----------------------------------------------------Player Functions---------------------------------------------------//
    private final PlayerEventListener playerEventListener = e -> {
        if (e.type == PlayerEventType.SKIP) {
            nextPlayer();
        }
    };

    public void triggerPlayerEvent(PlayerEvent event) {
        playerEventListener.onPlayerEvent(event);
    }
    public int getPlayerAmount(){
        return players.size();
    }

    public void setPlayerList(List<Player> playerList) {
        if (gameStarted) {
            return;
        }
        players.clear();
        players.addAll(playerList);
    }

    //-----------------------------------------------------End Game ---------------------------------------------------//

    public void endGame(Context context) {
        gameStarted = false;
        resetPlayers(context);
    }


    public void addUpdatedNumber(int number) {
        updatedNumbers.add(number);
    }

    public ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> previousNumbersFormatted = new ArrayList<>();

        for (int i = updatedNumbers.size() - 1; i >= 0; i--) {
            int number = updatedNumbers.get(i);
            previousNumbersFormatted.add(String.valueOf(number));
        }
        previousNumbersFormatted.add(startingNumber + " (Starting Number)");
        return previousNumbersFormatted;
    }

    public void setCurrentNumber(int number) {
        currentNumber = number;
    }
    public void resetPlayers(Context context) {
        for (Player player : players) {
            if (player != null) {
                player.resetWildCardAmount(context);
            }
        }
    }


}

