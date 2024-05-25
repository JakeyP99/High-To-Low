package com.example.countingdowngame.mainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivityCatastrophes {

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

    ////////////////////////////////////////////////////////////////////////////

    private final Catastrophe[] allCatastrophes;
    private List<Catastrophe> unusedCatastrophes;

    // Default constructor with predefined catastrophes
    public MainActivityCatastrophes() {
        this.allCatastrophes = new Catastrophe[]{
//                new Catastrophe("The drinking number is doubled!", 1),
//                new Catastrophe("The drinking number is halved!", 2),
//                new Catastrophe("Double the current number!", 3),
//                new Catastrophe("Half the current number!", 4),
//                new Catastrophe("Reverse the turn order!", 5),
                new Catastrophe("Everyone gains a couple more wildcards to use!", 6),
//                new Catastrophe("Everyone loses a couple of wildcards!", 7),
        };
        this.unusedCatastrophes = new ArrayList<>(Arrays.asList(allCatastrophes)); // Initialize unusedCatastrophes
    }

    public Catastrophe deployCatastrophe() {
        // Check if there are any unused catastrophes left
        if (unusedCatastrophes.isEmpty()) {
            // If all catastrophes have been used, reset the list
            unusedCatastrophes = new ArrayList<>(Arrays.asList(allCatastrophes));
        }

        // Randomly select an unused catastrophe to deploy
        Random random = new Random();
        int index = random.nextInt(unusedCatastrophes.size());

        return unusedCatastrophes.remove(index);
    }

}
