package com.example.countingdowngame.game;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.player.PlayerEvent;
import com.example.countingdowngame.player.PlayerEventListener;
import com.example.countingdowngame.player.PlayerEventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {

    //-----------------------------------------------------Start Game Functions---------------------------------------------------//

    private static final Game gameInstance = new Game();
    private final Map<Player, Integer> repeatingTurnsMap = new HashMap<>();
    private final List<Integer> updatedNumbers = new ArrayList<>();
    private final List<String> playerNames = new ArrayList<>();
    int currentNumber = 0;
    private GameEventListener gameEventListener;
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerId = 0;
    //-----------------------------------------------------Player Functions---------------------------------------------------//
    private final PlayerEventListener playerEventListener = e -> {
        if (e.type == PlayerEventType.SKIP) {
            nextPlayer();
        }
    };
    private Boolean playerUsedWildcards = false;
    private Boolean quizWasTriggered = false;
    private int startingNumber = 0;
    private boolean gameStarted = false;

    public static Game getInstance() {
        return gameInstance;
    }

    public void setPlayers(Context context, int playerAmount) {
        if (gameStarted)
            return;

        players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            players.add(new Player(context, null, null, null));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setCurrentPlayerId(int playerId) {
        currentPlayerId = playerId;
    }

    //-----------------------------------------------------In Game---------------------------------------------------//
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

    public void addUpdatedNumber(int number) {
        updatedNumbers.add(number);
    }

    public void addUpdatedName(String currentPlayerName) {
        playerNames.add(currentPlayerName);
    }

    //-----------------------------------------------------Player---------------------------------------------------//

    public Player getCurrentPlayer() {
        if (!players.isEmpty() && currentPlayerId >= 0 && currentPlayerId < players.size()) {
            return players.get(currentPlayerId);
        } else {
            return null;
        }
    }

    public boolean getPlayerUsedWildcards() {
        return playerUsedWildcards;
    }

    public void setPlayerUsedWildcards(boolean didPlayerUseWildcard) {
        playerUsedWildcards = didPlayerUseWildcard;
    }

    public void nextPlayer() {
        Player currentPlayer = getCurrentPlayer();

        // Check if the current player has repeating turns left
        if (repeatingTurnsMap.containsKey(currentPlayer) && repeatingTurnsMap.get(currentPlayer) > 0) {
            repeatingTurnsMap.put(currentPlayer, repeatingTurnsMap.get(currentPlayer) - 1);
        } else {
            currentPlayerId = (currentPlayerId + 1) % players.size();
        }

        // Clear the repeating turn for the player after their last extra turn
        if (repeatingTurnsMap.containsKey(currentPlayer) && repeatingTurnsMap.get(currentPlayer) == 0) {
            repeatingTurnsMap.remove(currentPlayer);
            currentPlayer.setInRepeatingTurn(); // Clear repeating turn state
        }

        if (gameEventListener != null) {
            gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
        }
    }

    public Player getRandomPlayerExcludingCurrent() {
        Player currentPlayer = getCurrentPlayer();
        List<Player> playersExcludingCurrent = new ArrayList<>(players);
        playersExcludingCurrent.remove(currentPlayer);

        Random random = new Random();
        if (!playersExcludingCurrent.isEmpty()) {
            int randomIndex = random.nextInt(playersExcludingCurrent.size());
            return playersExcludingCurrent.get(randomIndex);
        } else {
            return null;
        }

    }

    public void activateRepeatingTurn(Player player, int numberOfTurns) {
        repeatingTurnsMap.put(player, numberOfTurns);
        player.setInRepeatingTurn();
        Log.d(TAG, "activateRepeatingTurn: Repeating turn was activated for Player " +
                player.getName() + ". Turns to go: " + numberOfTurns);
    }

    public void triggerPlayerEvent(PlayerEvent event) {
        playerEventListener.onPlayerEvent(event);
    }

    public int getPlayerAmount() {
        return players.size();
    }

    public void setPlayerList(List<Player> playerList) {
        if (gameStarted) {
            return;
        }
        players.clear();
        players.addAll(playerList);
    }
    //-----------------------------------------------------Game Number---------------------------------------------------//

    public int getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(int number) {
        currentNumber = number;
    }

    public int nextNumber() {
        Random random = new Random();
        int nextNumber = random.nextInt(currentNumber + 1);
        currentNumber = nextNumber;
        updatedNumbers.add(nextNumber); // Add the updated number
        return nextNumber;
    }
    //-----------------------------------------------------Stats ---------------------------------------------------//
    public String getPlayerWithMostWildcardsUsed() {
        Player topPlayer = null;
        int minWildCards = 0;
        for (Player player : players) {
            int usedWildcards = player.getUsedWildcards(); // Ensure this method is implemented in the Player class
            if (usedWildcards > minWildCards) {
                setPlayerUsedWildcards(true);
                minWildCards = usedWildcards;
                topPlayer = player;
            }
        }
        return topPlayer != null ? topPlayer.getName() + " used " + minWildCards + " wildcards." : "No one used any wildcards.";
    }

    public void setQuizWasTriggered(Boolean wasQuizTriggered) {
        quizWasTriggered = wasQuizTriggered;
    }

    public Boolean getQuizWasTriggered() {
        return quizWasTriggered;
    }

    public void incrementPlayerQuizCorrectAnswers(Player player) {
        if (player != null) {
            setQuizWasTriggered(true);
            player.incrementCorrectQuizAnswers();
            Log.d(TAG, "incrementPlayerQuizCorrectAnswers: " + player.getCorrectQuizAnswers());
        }
    }

    public String getPlayerWithMostQuizCorrectAnswers() {
        Player topPlayer = null;
        int minCorrectAnswers = 0;
        for (Player player : players) {
            int correctAnswers = player.getCorrectQuizAnswers();
            if (correctAnswers > minCorrectAnswers) {
                minCorrectAnswers = correctAnswers;
                topPlayer = player;
            }
        }
        return topPlayer != null ? topPlayer.getName() + " got the most quiz questions right with " + minCorrectAnswers + " correct answers." : "No one answered any quiz questions correctly.";
    }

    public void incrementPlayerQuizIncorrectAnswers(Player player) {
        if (player != null) {
            player.incrementIncorrectQuizAnswers();
            Log.d(TAG, "incrementPlayerQuizIncorrectAnswers: " + player.getIncorrectQuizAnswers());
        }
    }

    public String getPlayerWithMostQuizIncorrectAnswers() {
        Player topPlayer = null;
        int minIncorrectAnswers = 0;
        for (Player player : players) {
            int correctAnswers = player.getIncorrectQuizAnswers();
            if (correctAnswers > minIncorrectAnswers) {
                minIncorrectAnswers = correctAnswers;
                topPlayer = player;
            }
        }
        return topPlayer != null ? topPlayer.getName() + " got the most quiz questions wrong with " + minIncorrectAnswers + " incorrect answers." : "";
    }

    public boolean hasWitchClass() {
        for (Player player : players) {
            if ("Witch".equals(player.getClassChoice())) { // Assuming you have a method isWitch() in Player class
                return true;
            }
        }
        return false;
    }

    public String getWitchPlayerTotalDrinksHandedOut() {
        Player topPlayer = null;
        int maxDrinks = 0;
        for (Player player : players) {
            int totalDrinks = player.getDrinksHandedOutByWitch();
            if (totalDrinks > maxDrinks) {
                maxDrinks = totalDrinks;
                topPlayer = player;
            }
        }
        return topPlayer != null ? topPlayer.getName() + " handed out " + maxDrinks + " as a witch!" : "No one handed out any drinks as a witch.";
    }

    public String getWitchPlayerTotalDrinksTaken() {
        Player topPlayer = null;
        int maxDrinks = 0;
        for (Player player : players) {
            int totalDrinks = player.getDrinksTakenByWitch();
            if (totalDrinks > maxDrinks) {
                maxDrinks = totalDrinks;
                topPlayer = player;
            }
        }
        return topPlayer != null ? topPlayer.getName() + " took " + maxDrinks + " drinks as a witch!" : "No one took any drinks as a witch.";
    }
    //-----------------------------------------------------End Game ---------------------------------------------------//


    public void endGame(Context context) {
        gameStarted = false;
        resetPlayers(context);
    }



    public ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> previousNumbersFormatted = new ArrayList<>();
        int nameSize = playerNames.size();
        for (int i = 0; i < updatedNumbers.size(); i++) {
            String playerName = i < nameSize ? playerNames.get(i) : "Game";
            String number = String.valueOf(updatedNumbers.get(i));
            previousNumbersFormatted.add(playerName + ": " + number);
        }
        previousNumbersFormatted.add("Starting Number: " + startingNumber);
        return previousNumbersFormatted;
    }

    public void resetPlayers(Context context) {
        for (Player player : players) {
            if (player != null) {
                player.resetWildCardAmount(context);
            }
        }
    }


}

