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
import android.graphics.Typeface;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PassiveAbilities extends ButtonUtilsActivity {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;
    private static final List<PassiveMessage> pendingPassiveMessages = new ArrayList<>();
    private static final List<Runnable> pendingActions = new ArrayList<>();

    private static class PassiveMessage {
        String className;
        String message;

        PassiveMessage(String className, String message) {
            this.className = className;
            this.message = message;
        }
    }

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static void addPassiveMessage(String className, String message) {
        pendingPassiveMessages.add(new PassiveMessage(className, message));
    }

    private static void addPassiveAction(Runnable action) {
        pendingActions.add(action);
    }

    public static void showCombinedPassives() {
        showCombinedPassives(null);
    }

    public static void showCombinedPassives(Runnable onDone) {
        if (pendingPassiveMessages.isEmpty()) {
            runPendingActions(onDone);
            return;
        }

        if (pendingPassiveMessages.size() == 1) {
            PassiveMessage pm = pendingPassiveMessages.get(0);
            activity.showClassAbilityDialog(pm.className + "'s Passive:\n\n" + pm.message, () -> {
                pendingPassiveMessages.clear();
                runPendingActions(onDone);
            });
            return;
        }

        SpannableStringBuilder combined = new SpannableStringBuilder();
        for (int i = 0; i < pendingPassiveMessages.size(); i++) {
            PassiveMessage pm = pendingPassiveMessages.get(i);
            String title = pm.className + "'s Passive:\n";
            int startTitle = combined.length();
            combined.append(title);
            int endTitle = combined.length();

            combined.setSpan(new AbsoluteSizeSpan(25, true), startTitle, endTitle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            combined.setSpan(new StyleSpan(Typeface.BOLD), startTitle, endTitle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            int startMsg = combined.length();
            combined.append(pm.message);
            int endMsg = combined.length();
            combined.setSpan(new AbsoluteSizeSpan(20, true), startMsg, endMsg, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (i < pendingPassiveMessages.size() - 1) {
                combined.append("\n\n--------------------\n\n");
            }
        }

        activity.showCombinedPassivesDialog(combined, () -> {
            pendingPassiveMessages.clear();
            runPendingActions(onDone);
        });
    }

    private static void runPendingActions(Runnable onDone) {
        List<Runnable> actionsToRun = new ArrayList<>(pendingActions);
        pendingActions.clear();
        for (Runnable action : actionsToRun) {
            action.run();
        }
        if (onDone != null) onDone.run();
    }

    public static void handleWitchPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            if (game.getCurrentNumber() % 2 == 0) {
                addPassiveMessage(WITCH, currentPlayer.getName() + " hand 1 drink.");
                currentPlayer.incrementDrinksHandedOutByWitch(1);
            } else {
                addPassiveMessage(WITCH, currentPlayer.getName() + " take 1 drink.");
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
                addPassiveMessage(SOLDIER, currentPlayer.getName() + " has escaped the game as the soldier.");
                currentPlayer.setRemoved(true);
                addPassiveAction(() -> game.removePlayer(currentPlayer));
            } else if (soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                activity.showGameDialog("Sorry " + currentPlayer.getName() + ", a soldier has already escaped the game.");
            }
        }
    }

    public static void handleSurvivorPassive(Player currentPlayer) {
        String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";
        addPassiveMessage(SURVIVOR, currentPlayer.getName() + " survived, hand out " + drinkNumberCounterInt + " " + drinksText);
    }

    public static void checkGoblinPassive(Player wildcardUser, Runnable onDone) {
        boolean wildcardUserHasGoblinPassive = wildcardUser.getClassChoices().contains(GOBLIN) ||
                (wildcardUser.getClassChoices().contains(ANGRY_JIM) && game.getCurrentNumber() < 50);

        if (wildcardUserHasGoblinPassive) {
            onDone.run();
            return;
        }

        for (Player player : game.getPlayers()) {
            boolean hasGoblinPassive = player.getClassChoices().contains(GOBLIN) ||
                    (player.getClassChoices().contains(ANGRY_JIM) && game.getCurrentNumber() < 50);

            if (hasGoblinPassive && !player.equals(wildcardUser)) {
                addPassiveMessage(GOBLIN, "Drink once for using a wildcard!");
                break;
            }
        }

        showCombinedPassives(onDone);
    }

    public static void handleScientistPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            int currentNumber = game.getCurrentNumber();
            int skipChance = (currentNumber < 10) ? 20 : (currentNumber < 100 ? 15 : 10);
            int chance = new Random().nextInt(100);

            if (chance < skipChance) {
                addPassiveMessage(SCIENTIST, currentPlayer.getName() + "'s turn was skipped.");
                addPassiveAction(currentPlayer::useSkip);
            }
        }
    }

    public static void handleAngryJimPassive(Player currentPlayer) {
        boolean numberBelow50 = game.getCurrentNumber() < 50;
        Player lastPlayer = game.getLastTurnPlayer();
        boolean isFirstAngryJimTurn = lastPlayer == null || !lastPlayer.equals(currentPlayer);

        if (numberBelow50 && isFirstAngryJimTurn) {
            game.updateRepeatingTurns(currentPlayer, 1);
        }

        if (numberBelow50 && game.getNumberWasGenerated()) {
            handleSoldierPassive();
            if (currentPlayer.isRemoved()) return;
            handleArcherPassive(currentPlayer);
            handleWitchPassive(currentPlayer);
            handleScientistPassive(currentPlayer);
        }
    }

    public static void handleArcherPassive(Player currentPlayer) {
        if (!currentPlayer.getClassChoices().contains(ANGRY_JIM)) {
            currentPlayer.incrementPassiveAbilityTurnCounter();
        }

        if (currentPlayer.getPassiveAbilityTurnCounter() == 3) {
            currentPlayer.resetPassiveAbilityTurnCounter();
            int chance = new Random().nextInt(100);
            if (chance < 60) {
                activity.updateDrinkNumberCounter(2, true);
                addPassiveMessage(ARCHER, "Drinking number increased by 2!");
            } else {
                activity.updateDrinkNumberCounter(-2, true);
                addPassiveMessage(ARCHER, "Drinking number decreased by 2!");
            }
        }
    }

    public static void handleTrollPassive(Player currentPlayer) {
        if (!isFirstTurn && !currentPlayer.hasUsedTrollPassive()) {
            int chance = new Random().nextInt(100);
            if (chance < 20) {
                currentPlayer.setTrollPassiveUsed(true);
                activity.hideNumberForTroll(currentPlayer);
                addPassiveMessage(TROLL, "The generate number is now hidden.\n\n" +
                        currentPlayer.getName() + " can still see it. Others must pay 3 drinks if they want to reveal it!");
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
        
        addPassiveMessage(GAMBLER, message);
        gamblerBet = "";
    }
}
