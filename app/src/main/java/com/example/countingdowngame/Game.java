package com.example.countingdowngame;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private static Game gameInstance = new Game();
    private int currentPlayerId = 0;
    private ArrayList<Player> players = new ArrayList<>();
    private int startingNumber = 0;
    private int currentNumber = 0;
    private boolean gameStarted = false;
    private ArrayList<Integer> previousNumbers = new ArrayList<>();
    private PlayerEventListener playerEventListener;
    private GameEventListener gameEventListener;

    public static Game getInstance() {
        return gameInstance;
    }

    public void setPlayers(int playerAmount) {
        if (gameStarted) return;

        players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            players.add(new Player(this, playerId));
        }
    }

    public void startGame(int startingNumber) {
        if (gameStarted) return;
        gameStarted = true;

        if (players.isEmpty()) return;

        currentNumber = startingNumber;
        this.startingNumber = startingNumber;
        currentPlayerId = 0;
        previousNumbers = new ArrayList<>();

        gameEventListener.onGameEvent(new GameEvent(this, GameEventType.GAME_START));
    }

    public void setPlayerEventListener(PlayerEventListener listener) {
        playerEventListener = listener;
    }

    public void setGameEventListener(GameEventListener listener) {
        gameEventListener = listener;
    }

    public void nextPlayer() {
        currentPlayerId = (currentPlayerId + 1) % players.size();
        gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
    }

    public void nextNumber() {
        Random random = new Random();
        int nextNumber = random.nextInt(currentNumber + 1);
        previousNumbers.add(nextNumber);
        currentNumber = nextNumber;

        if (currentNumber == 0) {
            gameEventListener.onGameEvent(new GameEvent(this, GameEventType.GAME_END));
            return;
        }
        nextPlayer();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerId);
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

    public ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> previousNumbersFormatted = new ArrayList<>();

        for (int i = previousNumbers.size() - 1; i >= 0; i--) {
            int number = previousNumbers.get(i);
            previousNumbersFormatted.add(String.valueOf(number));
        }

        previousNumbersFormatted.add(startingNumber + " (starting number)");

        return previousNumbersFormatted;
    }
}
