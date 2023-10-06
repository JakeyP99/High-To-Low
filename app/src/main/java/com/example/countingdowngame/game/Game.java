package com.example.countingdowngame.game;

import android.content.Context;

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
    private boolean playerRemoved = false; // Flag to track if a player has been removed

    private Player playerToRemove; // Store the player to be removed
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
            players.add(new Player(context, null, null, null));
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

    public int nextNumber() {
        Random random = new Random();
        int nextNumber = random.nextInt(currentNumber + 1);
        currentNumber = nextNumber;
        updatedNumbers.add(nextNumber); // Add the updated number
        return nextNumber;
    }

    public void nextPlayer() {
        currentPlayerId = (currentPlayerId + 1) % players.size();

        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.incrementTurnCounter();
        }

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

    public void setPlayerToRemove(Player player) {
        if (!playerRemoved) { // Check if a player has already been removed
            players.remove(player);
            playerRemoved = true; // Set the flag to indicate that a player has been removed
        }
    }

}

