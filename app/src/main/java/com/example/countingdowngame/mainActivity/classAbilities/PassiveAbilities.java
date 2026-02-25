package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.soldierRemoval;

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
        if (currentPlayer == null) return;

        int currentNumber = game.getCurrentNumber();
        int minRange = 10;
        int maxRange = 15;

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
            currentPlayer.gainWildCards(1);
        }
    }

    public static void handleScientistPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            Handler handler = new Handler();
            int currentNumber = game.getCurrentNumber();
            int skipChance = (currentNumber < 10) ? 20 : (currentNumber < 100 ? 15 : 10);
            int chance = new Random().nextInt(100);

            handler.postDelayed(() -> {
                if (chance < skipChance) {
                    activity.showGameDialog(SCIENTIST + "'s Passive: \n\n" + currentPlayer.getName() + " is a scientist and their turn was skipped.");
                    currentPlayer.useSkip();
                }
            }, 1);
        }
    }

    public static void handleAngryJimPassive(Player currentPlayer) {
        boolean numberBelow50 = game.getCurrentNumber() < 50;
        Player lastPlayer = game.getLastTurnPlayer();
        boolean isFirstAngryJimTurn = lastPlayer == null || !lastPlayer.equals(currentPlayer);

        if (numberBelow50 && isFirstAngryJimTurn) {
            game.updateRepeatingTurns(currentPlayer, 1);
        }

        if (numberBelow50) {
            handleSoldierPassive();
            handleArcherPassive(currentPlayer);
            handleGoblinPassive(currentPlayer);
            handleWitchPassive(currentPlayer);
            handleScientistPassive(currentPlayer);
        }
    }

    public static void handleArcherPassive(Player currentPlayer) {
        if (!ANGRY_JIM.equals(currentPlayer.getClassChoice())) {
            currentPlayer.incrementPassiveAbilityTurnCounter();
        }

        if (currentPlayer.getPassiveAbilityTurnCounter() == 3) {
            currentPlayer.resetPassiveAbilityTurnCounter();
            int chance = new Random().nextInt(100);
            if (chance < 60) {
                activity.updateDrinkNumberCounter(2, true);
                activity.showGameDialog(ARCHER + "'s Passive: \n\nDrinking number increased by 2!");
            } else {
                activity.updateDrinkNumberCounter(-2, true);
                activity.showGameDialog(ARCHER + "'s Passive: \n\nDrinking number decreased by 2!");
            }
        }
    }
}
