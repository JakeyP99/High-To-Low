package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.repeatedTurn;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActiveAbilities {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static void hideAbilityButton() {
        if (activity != null) {
            Button btnClassAbility = activity.findViewById(R.id.btnClassAbility);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }


    private static void hideWildButton() {
        if (activity != null) {
            Button btnClassAbility = activity.findViewById(R.id.btnWild);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void handleScientistClass() {
        activity.scientistChangeCurrentNumber();
    }

    public static void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (game.getCurrentNumber() <= 10) {
                currentPlayer.setUsedActiveAbility(true);
                game.updateRepeatingTurns(currentPlayer, 1);
                activity.renderPlayerUI();
                repeatedTurn = true;
                activity.updateDrinkNumberCounter(4, true);
                hideAbilityButton();
                hideWildButton();
                AudioManager.getInstance().playSoundEffects(activity, SOLDIER);
            } else {
                activity.displayToastMessage("The +4 ability can only be activated when the number is below 10.");
            }
        } else {
            activity.displayToastMessage("Cannot activate on the first turn.");
        }
    }

    public static void handleQuizMagicianClass(Player currentPlayer) {
        currentPlayer.setUsedActiveAbility(true);
        currentPlayer.setJustUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, QUIZ_MAGICIAN);
    }

    public static void handleGoblinClass(Player currentPlayer) {
        List<Player> eligiblePlayers = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            if (!player.equals(currentPlayer) && player.getWildCardAmount() > 0) {
                eligiblePlayers.add(player);
            }
        }

        if (eligiblePlayers.isEmpty()) {
            Toast.makeText(activity, "All players have no wildcards", Toast.LENGTH_SHORT).show();
            return;
        }

        Player randomPlayer = eligiblePlayers.get(new Random().nextInt(eligiblePlayers.size()));
        randomPlayer.removeWildCard(randomPlayer, 2);

        int wildcardsLeft = randomPlayer.getWildCardAmount();
        String wildcardText = wildcardsLeft == 0 ? "no more wildcards" : (wildcardsLeft == 1 ? "1 wildcard left" : wildcardsLeft + " wildcards left");

        activity.showGameDialog(GOBLIN + "'s Active: \n\n" +
                randomPlayer.getName() + " lost two wildcards.\n\n" +
                randomPlayer.getName() + " now has " + wildcardText + ".");

        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, GOBLIN);
    }

    public static void handleAngryJimClass(Player currentPlayer) {
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        game.updateRepeatingTurns(randomPlayer, 1);
        activity.showGameDialog(ANGRY_JIM + "'s Active: \n\n" + randomPlayer.getName() + " must repeat their turn.");
        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, ANGRY_JIM);
    }

    public static void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            activity.halveCurrentNumber();
            currentPlayer.setUsedActiveAbility(true);
            hideAbilityButton();
            AudioManager.getInstance().playSoundEffects(activity, SURVIVOR);
        }
    }

    public static void handleArcherClass(Player currentPlayer) {
        if (drinkNumberCounterInt >= 2) {
            activity.showGameDialog(ARCHER + "'s Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setUsedActiveAbility(true);
            activity.updateDrinkNumberCounter(-2, true);
            hideAbilityButton();
            AudioManager.getInstance().playSoundEffects(activity, ARCHER);
        }
    }

    public static void handleWitchClass(Player currentPlayer) {
        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        currentPlayer.useSkip();
        AudioManager.getInstance().playSoundEffects(activity, WITCH);
    }
}
