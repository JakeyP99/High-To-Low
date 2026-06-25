package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.TROLL;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.soldierRemoval;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.Random;

public class PassiveAbilities extends ButtonUtilsActivity {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static void handleWitchPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            if (game.getCurrentNumber() % 2 == 0) {
                activity.showClassAbilityDialog(
                        WITCH + "'s Passive: \n\n" +
                                currentPlayer.getName() +
                                " hand 1 drink."
                );
                currentPlayer.incrementDrinksHandedOutByWitch(1);
            } else {
                activity.showClassAbilityDialog(
                        WITCH + "'s Passive: \n\n" +
                                currentPlayer.getName() +
                                " take 1 drink."
                );
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
                activity.showClassAbilityDialog(
                        SOLDIER + "'s Passive: \n\n" +
                                currentPlayer.getName() +
                                " has escaped the game as the soldier."
                );

                currentPlayer.setRemoved(true);
                game.removePlayer(currentPlayer);
            } else if (soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                activity.showGameDialog("Sorry " + currentPlayer.getName() + ", a soldier has already escaped the game.");
            }
        }
    }

    public static void handleSurvivorPassive(Player currentPlayer) {
        String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";

        activity.showClassAbilityDialog(
                SURVIVOR + "'s Passive: \n\n" +
                         currentPlayer.getName() +
                         " survived, hand out " + drinkNumberCounterInt + " " + drinksText
        );
    }

    public static void checkGoblinPassive(Player wildcardUser, Runnable onDone) {
        boolean wildcardUserHasGoblinPassive = GOBLIN.equals(wildcardUser.getClassChoice()) ||
                (ANGRY_JIM.equals(wildcardUser.getClassChoice()) && game.getCurrentNumber() < 50);

        if (wildcardUserHasGoblinPassive) {
            onDone.run();
            return;
        }

        boolean goblinTriggered = false;
        for (Player player : game.getPlayers()) {
            boolean hasGoblinPassive = GOBLIN.equals(player.getClassChoice()) ||
                    (ANGRY_JIM.equals(player.getClassChoice()) && game.getCurrentNumber() < 50);

            if (hasGoblinPassive && !player.equals(wildcardUser)) {
                goblinTriggered = true;
                activity.showClassAbilityDialog(
                        GOBLIN + "'s Passive: \n\n" +
                                " Drink once for using a wildcard!",
                        onDone
                );
                break;
            }
        }

        if (!goblinTriggered) {
            onDone.run();
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
                    activity.showClassAbilityDialog(
                            SCIENTIST + "'s Passive: \n\n" +
                                    currentPlayer.getName() + "'s turn was skipped."
                    );
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

        if (numberBelow50 && game.getNumberWasGenerated() == true) {
            handleSoldierPassive();
            if (currentPlayer.isRemoved()) return;
            handleArcherPassive(currentPlayer);
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
                activity.showClassAbilityDialog(
                        ARCHER + "'s Passive: \n\n" +
                                "Drinking number increased by 2!"
                );
            } else {
                activity.updateDrinkNumberCounter(-2, true);
                activity.showClassAbilityDialog(
                        ARCHER + "'s Passive: \n\n" +
                                "Drinking number decreased by 2!"
                );
            }
        }
    }

    public static void handleTrollPassive(Player currentPlayer) {
        if (!isFirstTurn && !currentPlayer.hasUsedTrollPassive()) {
            int chance = new Random().nextInt(100);
            if (chance < 20) {
                currentPlayer.setTrollPassiveUsed(true);
                activity.hideNumberForTroll(currentPlayer);
                activity.showClassAbilityDialog(
                        TROLL + "'s Passive: \n\n" +
                                "The generate number is now hidden.\n\n" +
                                currentPlayer.getName() + " can still see it. Others must pay 3 drinks if they want to reveal it!"
                );
            }
        }
    }

    private static String gamblerBet = "";

    public static void showGamblerBetDialog(Runnable onBetPlaced) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_over_under, null);

        TextView title = dialogView.findViewById(R.id.dialogbox_textview);
        int currentNumber = game.getCurrentNumber();
        int middle = currentNumber / 2;
        boolean isEven = currentNumber % 2 == 0;

        String message = "Will the result be Over or Under " + middle + "?";
        if (isEven) {
            message = "Will the result be Over, Under, or Equal to " + middle + "?";
        }
        title.setText(message);
        title.setTextSize(26);

        Button overBtn = dialogView.findViewById(R.id.btn_over);
        Button underBtn = dialogView.findViewById(R.id.btn_under);
        Button equalBtn = dialogView.findViewById(R.id.btn_equal);

        if (isEven) {
            equalBtn.setVisibility(View.VISIBLE);
            equalBtn.setText("EQUAL");
        } else {
            equalBtn.setVisibility(View.GONE);
        }

        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        activity.btnUtils.setButton(overBtn, () -> {
            gamblerBet = "OVER";
            dialog.dismiss();
            onBetPlaced.run();
        });

        activity.btnUtils.setButton(underBtn, () -> {
            gamblerBet = "UNDER";
            dialog.dismiss();
            onBetPlaced.run();
        });

        activity.btnUtils.setButton(equalBtn, () -> {
            gamblerBet = "EQUAL";
            dialog.dismiss();
            onBetPlaced.run();
        });

        dialog.show();
    }

    public static void handleGamblerPassiveResult(int targetNumber) {
        if (gamblerBet.isEmpty()) return;

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null) return;

        int previousNumber = game.getPreviousNumber();
        int middle = previousNumber / 2;
        boolean won = false;
        boolean isEven = previousNumber % 2 == 0;

        if (gamblerBet.equals("OVER") && targetNumber > middle) {
            won = true;
        } else if (gamblerBet.equals("EQUAL") && targetNumber == middle) {
            won = true;
        } else if (gamblerBet.equals("UNDER")) {
            if (isEven) {
                if (targetNumber < middle) won = true;
            } else {
                if (targetNumber <= middle) won = true;
            }
        }

        String message;
        if (won) {
            if (gamblerBet.equals("EQUAL")) {
                message = currentPlayer.getName() + " won their bet! Hand out 2 drinks.";
                currentPlayer.incrementDrinksHandedOutByGambler(2);
            } else {
                message = currentPlayer.getName() + " won their bet! Hand out 1 drink.";
                currentPlayer.incrementDrinksHandedOutByGambler(1);
            }
        } else {
            message = currentPlayer.getName() + " lost their bet! Take 1 drink.";
            currentPlayer.incrementDrinksTakenByGambler(1);
        }
        activity.showClassAbilityDialog(
                GAMBLER + "'s Passive: \n\n" +
                        message
        );

        gamblerBet = "";
    }
}
