package com.example.countingdowngame.mainActivity;

import com.example.countingdowngame.game.Game;

import java.util.ArrayList;

public class PowerUps {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static ArrayList<String> getPowerUps() {
        ArrayList<String> powerUp = new ArrayList<>();

        powerUp.add("Shield: Immune to your next drink!");
        powerUp.add("Double Trouble: The next person to drink takes double!");
        powerUp.add("Wildcard Bonus: Gain 1 extra Wildcard!");
        powerUp.add("Life Line: Automatically skip your next turn!");
        powerUp.add("Reverse: Reverse the turn order!");
        powerUp.add("Lucky Strike: The current number is halved!");
        powerUp.add("Thief: Steal 1 wildcard from a random player!");
        powerUp.add("Party Starter: Everyone but you takes a drink!");

        return powerUp;
    }
}
