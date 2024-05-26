package com.example.countingdowngame.mainActivity;

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
//                new Catastrophe("Two drinks have been added to the counter!", 1),
//                new Catastrophe("Two drinks have been removed from the counter!", 2),
                new Catastrophe("The current number has been increased!", 3),
                new Catastrophe("The current number has been reduced!", 4),
//                new Catastrophe("Reverse the turn order!", 5),
//                new Catastrophe("Everyone gains a couple more wildcards to use!", 6),
//                new Catastrophe("Everyone loses a couple of wildcards!", 7),
//                new Catastrophe("Everyone must each make a rule, and this will last until the end of the game!", 8),
//                new Catastrophe("All players must have three turns, but the drinking number is reduced!", 9),
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

}
