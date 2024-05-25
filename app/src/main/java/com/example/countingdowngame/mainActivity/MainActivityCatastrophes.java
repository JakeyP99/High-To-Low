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
                new Catastrophe("Two drinks have been added!", 1),
                new Catastrophe("Two drinks have been removed!", 2),
                new Catastrophe("Double the current number!", 3),
                new Catastrophe("Half the current number!", 4),
                new Catastrophe("Reverse the turn order!", 5),
                new Catastrophe("Everyone gains a couple more wildcards to use!", 6),
                new Catastrophe("Everyone loses a couple of wildcards!", 7),
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
