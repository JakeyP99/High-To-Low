package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.mainActivity.MainActivityGame.catastropheLimit;
import static com.example.countingdowngame.mainActivity.MainActivityGame.updateNumber;

import android.util.Log;

import com.example.countingdowngame.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivityCatastrophes {
    private final Catastrophe[] allCatastrophes;
    private List<Catastrophe> unusedCatastrophes;

    public static class Catastrophe {
        private final String message;
        private final int effect;

        public Catastrophe(String message, int effect) {
            this.message = message;
            this.effect = effect;
        }

        public String getMessage() {
            return message;
        }

        public int getEffect() {
            return effect;
        }
    }

    public MainActivityCatastrophes() {
        this.allCatastrophes = new Catastrophe[]{
                new Catastrophe("Two drinks have been added to the counter!", 1),
                new Catastrophe("Two drinks have been removed from the counter!", 2),
                new Catastrophe("The current number has been increased!", 3),
                new Catastrophe("The current number has been reduced!", 4),
                new Catastrophe("Reverse the turn order!", 5),
                new Catastrophe("Everyone gains a couple more wildcards to use!", 6),
                new Catastrophe("Everyone loses a couple of wildcards!", 7),
                new Catastrophe("Everyone must each make a rule, and this will last until the end of the game!", 8),
                new Catastrophe("All players must have three turns, but the drinking number is reduced!", 9),
        };
        this.unusedCatastrophes = new ArrayList<>(Arrays.asList(allCatastrophes)); // Initialize unusedCatastrophes
    }

    public Catastrophe deployCatastrophe() {
        if (unusedCatastrophes.isEmpty()) {
            unusedCatastrophes = new ArrayList<>(Arrays.asList(allCatastrophes));
        }
        Random random = new Random();
        int index = random.nextInt(unusedCatastrophes.size());
        return unusedCatastrophes.remove(index);
    }


    public static void increaseNumberByRandom() {
        Game game = Game.getInstance();
        int currentNumber = game.getCurrentNumber();
        Random random = new Random();
        int randomIncrease;
        if (currentNumber >= 0 && currentNumber <= 100) {
            randomIncrease = random.nextInt(11); // Random number between 0 and 10
        } else if (currentNumber > 100 && currentNumber <= 1000) {
            randomIncrease = random.nextInt(901) + 100; // Random number between 100 and 1000
        } else {
            randomIncrease = random.nextInt(9001) + 1000; // Random number between 1000 and 10000
        }
        int updatedNumber = Math.min(currentNumber + randomIncrease, 999999999);
        updateNumber(updatedNumber);
    }

    public static void decreaseNumberByRandom() {
        Game game = Game.getInstance();
        int currentNumber = game.getCurrentNumber();
        Random random = new Random();
        int randomDecrease;
        if (currentNumber >= 0 && currentNumber <= 100) {
            randomDecrease = random.nextInt(11); // Random number between 0 and 10
        } else if (currentNumber > 100 && currentNumber <= 1000) {
            randomDecrease = random.nextInt(901) + 100; // Random number between 100 and 1000
        } else {
            randomDecrease = random.nextInt(9001) + 1000; // Random number between 1000 and 10000
        }
        int updatedNumber = Math.max(currentNumber - randomDecrease, 1); // Ensure the number does not go below 1
        updateNumber(updatedNumber);
    }

    public static void setCatastropheLimit() {
        // Generate a random number between 4 and 10 for the catastrophe limit
        Random random = new Random();
        catastropheLimit = random.nextInt(4) + 4; // Generates a number between 4 and 7 (inclusive)
        Log.d(TAG, "catastropheLimit: " + catastropheLimit);
    }


}
