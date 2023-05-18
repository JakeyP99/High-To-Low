package com.example.countingdowngame;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    public static Game gameInstance = new Game();

    int currentPlayerId = 0;
    ArrayList<Player> players = new ArrayList<>();

    int startingNumber = 0;
    int currentNumber = 0;
    boolean gameStarted = false;
    ArrayList<Integer> previousNumbers = new ArrayList<>();

    PlayerEventListener playerEventListener;
    GameEventListener gameEventListener;

    public void setPlayers(int playerAmount) {
        if (gameStarted) return;
        this.players = new ArrayList<>();

        for (int playerId = 0; playerId < playerAmount; playerId++) {
            this.players.add(new Player(this, playerId));
        }

    }

    public void startGame(int startingNumber) {
        if (gameStarted) {
            return;
        }
        gameStarted = true;
        if (this.players.size() <= 0) {
            return;
        }

        this.currentNumber = startingNumber;
        this.startingNumber = startingNumber;
        this.currentPlayerId = 0;
        this.previousNumbers = new ArrayList<>();

        this.gameEventListener.onGameEvent(new GameEvent(this, GameEventType.GAME_START));
    }

    public void setPlayerEventListener(PlayerEventListener listener) {
        this.playerEventListener = listener;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void nextPlayer() {
        this.currentPlayerId = this.currentPlayerId + 1;

        if (currentPlayerId > players.size() - 1) {
            currentPlayerId = 0;
        }

        this.gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_PLAYER));
    }

    public void nextNumber() {
        Random myRandom = new Random();

        int nextNumber = myRandom.nextInt(currentNumber + 1);

        previousNumbers.add(nextNumber);

        currentNumber = nextNumber;


        if (currentNumber == 0) {
            this.gameEventListener.onGameEvent(new GameEvent(this, GameEventType.GAME_END));
            return;
        }

        this.gameEventListener.onGameEvent(new GameEvent(this, GameEventType.NEXT_NUMBER));

        this.nextPlayer();
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerId);
    }

    public void playAgain() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player != null) {
                player.resetAbilities();
            }
        }
    }

    public void endGame() {
        gameStarted = false;
    }


    public void triggerPlayerEvent(PlayerEvent e) {
        this.playerEventListener.onPlayerEvent(e);
    }

    ArrayList<String> getPreviousNumbersFormatted() {
        ArrayList<String> previousNumbersFormatted = new ArrayList<>();

        for (int i = 0; i < previousNumbers.size(); i++) {
            int j = previousNumbers.size() - 1;
            int number = previousNumbers.get(j - i);
            previousNumbersFormatted.add(number + "");
        }

        previousNumbersFormatted.add(startingNumber + " (starting number)");

        return previousNumbersFormatted;
    }


}
