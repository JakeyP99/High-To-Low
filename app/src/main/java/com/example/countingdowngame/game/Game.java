package com.example.countingdowngame.game;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import com.example.countingdowngame.mainActivity.MainActivityGame;
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
    private final List<String> playerNames = new ArrayList<>();
    private int startingNumber = 0;
    private int currentNumber = 0;
    private int previousNumber = 0;
    private int currentPlayerId = 0;
    private int catastropheQuantity = 0;
    private GameEventListener gameEventListener;
    private ArrayList<Player> players = new ArrayList<>();
    private Boolean playerUsedWildcards = false;
    private Boolean quizWasTriggered = false;
    private Boolean gameStarted = false;
    private boolean playCards;
    private final List<GameTurns> turns = new ArrayList<>();
    private boolean reverseOrder = false;


    //-----------------------------------------------------Game Modes---------------------------------------------------//

    public boolean isPlayCards() {
        return playCards;
    }

    public void setPlayCards(boolean playCards) {
        this.playCards = playCards;
    }


    //-----------------------------------------------------Player Functions---------------------------------------------------//
    private final PlayerEventListener playerEventListener = e -> {
        if (e.type == PlayerEventType.SKIP) {
            nextPlayer();
        }
    };


    public static Game getInstance() {
        return gameInstance;
    }

    public void setPlayers(Context context, int playerAmount) {
        if (gameStarted)
            return;

        players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            players.add(new Player(context, null, null, null, null));
        }
    }

    public List<Player> getPlayers() {
        return players;
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
        previousNumber = startNum;
        startingNumber = startNum;
        currentPlayerId = 0;
        turns.clear();
    }

    public void addUpdatedName(String currentPlayerName) {
        playerNames.add(currentPlayerName);
    }


    //-----------------------------------------------------Game Event---------------------------------------------------//
    public void activateRepeatingTurnForAllPlayers(int numberOfTurns) {
        for (Player player : players) {
            repeatingTurnsMap.put(player, numberOfTurns);
            Log.d(TAG, "activateRepeatingTurnForAllPlayers: Repeating turn was activated for Player " +
                    player.getName() + ". Turns to go: " + numberOfTurns);
        }
    }

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    //-----------------------------------------------------Player---------------------------------------------------//
    public void removePlayer(Player player) {
        int removedIndex = players.indexOf(player);

        players.remove(removedIndex);
        playerNames.remove(removedIndex);
        repeatingTurnsMap.remove(player);

        // Ensure index is in range
        if (currentPlayerId >= players.size()) {
            currentPlayerId = 0;
        }
            gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));

    }


    public void recordTurn(Player player, int number) {
        turns.add(new GameTurns(player.getName(), number));
        player.addNumberPlayed(number);
    }

    private Player lastTurnPlayer;

    public Player getLastTurnPlayer() {
        return lastTurnPlayer;
    }

    public void setLastTurnPlayer(Player player) {
        this.lastTurnPlayer = player;
    }



    public Player getCurrentPlayer() {
        if (!players.isEmpty() && currentPlayerId >= 0 && currentPlayerId < players.size()) {
            return players.get(currentPlayerId);
        } else {
            return null;
        }
    }

    public int getRepeatingTurnsForPlayer(Player player) {
        return repeatingTurnsMap.getOrDefault(player, 0);
    }


    public void activateRepeatingTurn(Player player, int numberOfTurns) {
        repeatingTurnsMap.put(player, numberOfTurns);
    }

    public void updateRepeatingTurns(Player player, int numberOfTurnsToAdd) {
        int currentTurns = repeatingTurnsMap.getOrDefault(player, 0);
        repeatingTurnsMap.put(player, currentTurns + numberOfTurnsToAdd);
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
            if (!players.isEmpty()) {
                if (reverseOrder) {
                    currentPlayerId = (currentPlayerId - 1 + players.size()) % players.size();
                } else {
                    currentPlayerId = (currentPlayerId + 1) % players.size();
                }
            }
        }

        // Clear the repeating turn for the player after their last extra turn
        if (repeatingTurnsMap.containsKey(currentPlayer) && repeatingTurnsMap.get(currentPlayer) == 0) {
            repeatingTurnsMap.remove(currentPlayer);
        }
        MainActivityGame.isFirstTurn = false;

        gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
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



    public void triggerPlayerEvent(PlayerEvent event) {
        playerEventListener.onPlayerEvent(event);
    }

    public int getPlayerAmount() {
        return players.size();
    }

    public void setPlayerList(List<Player> playerList) {
        players.clear();
        players.addAll(playerList);
        Log.d(TAG, "setPlayerList: " +playerList);
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
        previousNumber = currentNumber; // Save current as previous before changing it
        int nextNumber = random.nextInt(currentNumber + 1);
        currentNumber = nextNumber;
        return nextNumber;
    }


    public int getPreviousNumber() {
        return previousNumber;
    }
    //-----------------------------------------------------Stats ---------------------------------------------------//
    public String getCatastropheQuantityString() {
        return "Catastrophes occurred: " + catastropheQuantity;
    }

    public void incrementCatastropheQuantity() {
        catastropheQuantity++;
    }
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
        return topPlayer != null ? topPlayer.getName() + " handed out " + maxDrinks + " drinks as a witch!" : "No one handed out any drinks as a witch.";
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
        return topPlayer != null ? topPlayer.getName() + " took " + maxDrinks + " drinks as a witch!" : "The witch did not take any drinks from her spell.";
    }
    //-----------------------------------------------------End Game ---------------------------------------------------//


    public void endGame(Context context) {
        gameStarted = false;
        setLastTurnPlayer(null);
        resetPlayers(context);
    }

    public ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> formatted = new ArrayList<>();
        formatted.add("Starting Number: " + startingNumber);

        for (GameTurns turn : turns) {
            formatted.add(turn.getPlayerName() + ": " + turn.getNumber());
        }

        return formatted;
    }

    public void resetPlayers(Context context) {
        for (Player player : players) {
            if (player != null) {
                player.resetWildCardAmount(context);
                repeatingTurnsMap.clear();
                player.resetPassiveAbilityTurnCounter();
            }
        }
    }

    public void reset() {
        gameStarted = false;
        players.clear();
        playerNames.clear();
        repeatingTurnsMap.clear();
        turns.clear();
        catastropheQuantity = 0;
        currentPlayerId = 0;
        playerUsedWildcards = false;
        quizWasTriggered = false;
        reverseOrder = false;
        lastTurnPlayer = null;
    }


}
