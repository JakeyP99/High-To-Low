package com.example.countingdowngame.mainActivity.classAbilities;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.repeatedTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.soldierRemoval;

import android.content.ContentValues;
import android.os.Handler;
import android.util.Log;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

import java.util.Random;

public class PassiveAbilities {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static void handleWitchPassive(Player currentPlayer) {
        Log.d("TAG", "First turn: " + isFirstTurn);
        if (!isFirstTurn) {
            if (game.getCurrentNumber() % 2 == 0) {
                activity.showGameDialog(WITCH + "'s Passive: \n\n" + currentPlayer.getName() + " hand out 1 drink.");
                currentPlayer.incrementDrinksHandedOutByWitch(1);
            } else {
                activity.showGameDialog(WITCH + "'s Passive: \n\n" + currentPlayer.getName() + " take 1 drink.");
                currentPlayer.incrementDrinksTakenByWitch(1);
            }
        }
    }

    public static void handleSoldierPassive() {

        Player currentPlayer = game.getCurrentPlayer();
        int currentNumber = game.getCurrentNumber();

        Log.d("TAG", "Current number: " + currentNumber);
        int minRange = 10;
        int maxRange = 15;

        Log.d("TAG", "First turn: " + isFirstTurn);
        Log.d("TAG", "Soldier removal: " + soldierRemoval);

        if (!isFirstTurn) {
            if (!soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                soldierRemoval = true;
                activity.showGameDialog(currentPlayer.getName() + " has escaped the game as the soldier.");
                currentPlayer.setRemoved(true);
                game.removePlayer(currentPlayer);
            } else if (soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                activity.showGameDialog("Sorry " + currentPlayer.getName() + ", a soldier has already escaped the game.");
            }
        }
    }


    public static void handleSurvivorPassive(Player currentPlayer) {
        int currentNumber = Game.getInstance().getCurrentNumber();
        Log.d(ContentValues.TAG, "handleSurvivorPassive: current number = " + currentNumber);
        String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";
        activity.showGameDialog(SURVIVOR + "'s Passive: \n\n" + currentPlayer.getName()
                + " survived, hand out " + drinkNumberCounterInt + " " + drinksText);

    }


    public static void handleGoblinPassive(Player currentPlayer) {
        if (!ANGRY_JIM.equals(currentPlayer.getClassChoice())) {
            currentPlayer.incrementPassiveAbilityTurnCounter();
        }
        if (currentPlayer.getPassiveAbilityTurnCounter() == 3) {
            currentPlayer.resetPassiveAbilityTurnCounter();
            Log.d(TAG, "handleGoblinPassive: reset turn");
            currentPlayer.gainWildCards(1);
        }
    }

    public static void handleScientistPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            Handler handler = new Handler();
            int delayMillis = 1;

            int currentNumber = Game.getInstance().getCurrentNumber();
            int skipChance;

            if (currentNumber < 10) {
                skipChance = 20; // 20% chance
            } else if (currentNumber < 100) {
                skipChance = 15; // 15% chance
            } else {
                skipChance = 10; // Default 10% chance
            }

            int chance = new Random().nextInt(100); // 0–99

            handler.postDelayed(() -> {
                if (chance < skipChance) {
                    activity.showGameDialog(SCIENTIST + "'s Passive: \n\n" + currentPlayer.getName() + " is a scientist and their turn was skipped.");
                    currentPlayer.useSkip();
                }
            }, delayMillis);
        }
    }

}
