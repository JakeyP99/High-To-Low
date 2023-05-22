package com.example.countingdowngame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private static final Game gameInstance = new Game();

    public static Game getInstance() {
        return gameInstance;
    }

    private final PlayerEventListener playerEventListener = e -> {
        if (e.type == PlayerEventType.SKIP) {
            nextPlayer();
        }
    };
    private GameEventListener gameEventListener;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Integer> previousNumbers = new ArrayList<>();
    private int currentPlayerId = 0;
    private int startingNumber = 0;
    private int currentNumber = 0;
    private boolean gameStarted = false;

    public int getCurrentNumber() {
        return currentNumber;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public Player getCurrentPlayer() {
        if (!players.isEmpty() && currentPlayerId >= 0 && currentPlayerId < players.size()) {
            return players.get(currentPlayerId);
        } else {
            return null; // or handle the case when there are no players or currentPlayerId is invalid
        }
    }


    public ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> previousNumbersFormatted = new ArrayList<>();

        for (int i = previousNumbers.size() - 1; i >= 0; i--) {
            int number = previousNumbers.get(i);
            previousNumbersFormatted.add(String.valueOf(number));
        }

        previousNumbersFormatted.add(startingNumber + " (starting number)");

        return previousNumbersFormatted;
    }

    public void setPlayers(int playerAmount) {
        if (gameStarted)
            return;

        players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            players.add(new Player(null, null));
        }
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
        previousNumbers = new ArrayList<>();
    }

    public void nextNumber(final Runnable onEnd) {
        Random random = new Random();
        int nextNumber = random.nextInt(currentNumber + 1);
        previousNumbers.add(nextNumber);
        currentNumber = nextNumber;

        if (currentNumber == 0) {
            endGame();
            onEnd.run();
        } else {
            nextPlayer();
        }
    }

    public void playAgain() {
        for (Player player : players) {
            if (player != null) {
                player.resetAbilities();
            }
        }
    }

    public void endGame() {
        gameStarted = false;
    }

    public void triggerPlayerEvent(PlayerEvent event) {
        playerEventListener.onPlayerEvent(event);
    }

    private void nextPlayer() {
        currentPlayerId = (currentPlayerId + 1) % players.size();

        if (gameEventListener != null) {
            gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
        }
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
}
