package com.example.countingdowngame.mainActivity.classAbilities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.TROLL;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.mainActivity.PowerUps;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifImageView;

public class ActiveAbilities extends ButtonUtilsActivity {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;
    private static boolean gamblerFlipped = false;
    private static boolean opponentFlipped = false;
    private static int currentRound = 0;
    private static int totalRounds = 0;
    private static int currentPenalty = 0;
    private static boolean startMiniGame = false;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static void resetStaticState() {
        gamblerFlipped = false;
        opponentFlipped = false;
        currentRound = 0;
        totalRounds = 0;
        currentPenalty = 0;
        startMiniGame = false;
    }


    private static void hideAbilityButton() {
        if (activity != null) {
            View btnClassAbility = activity.findViewById(R.id.btnClassAbility);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void handleScientistClass() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_scientist_change_number, null);

        EditText editCurrentNumberText = dialogView.findViewById(editCurrentNumberTextView);
        Button submitButton = dialogView.findViewById(R.id.btn_submit);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        activity.btnUtils.setButton(submitButton, () -> {
            try {
                String userInput = editCurrentNumberText.getText().toString();
                int newNumber = Integer.parseInt(userInput);
                if (newNumber > 999999999) {
                    activity.displayToastMessage("That number was too high!");
                } else if (newNumber == 0) {
                    activity.displayToastMessage("You cannot choose 0 as your number.");
                } else {

                    Player currentPlayer = game.getCurrentPlayer();

                    Game.getInstance().setCurrentNumber(newNumber);
                    markAbilityUsed(SCIENTIST, currentPlayer);
                    MainActivityGame.updateNumber(newNumber);
                    AudioManager.getInstance().playSoundEffects(activity, SCIENTIST);
                    dialog.dismiss(); // Close the dialog on success
                    hideAbilityButton();
                }
            } catch (NumberFormatException e) {
                activity.displayToastMessage("Invalid number input");
            }
        });

        dialog.show();
    }

    public static void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (game.getCurrentNumber() <= 10) {
                markAbilityUsed(SOLDIER, currentPlayer);
                game.updateRepeatingTurns(currentPlayer, 1);
                MainActivityGame.soldierActiveTurns = 2;
                activity.renderPlayerUI(false);
                activity.updateDrinkNumberCounter(4, true);
                AudioManager.getInstance().playSoundEffects(activity, SOLDIER);
                hideAbilityButton();
            } else {
                activity.displayToastMessage("The +4 ability can only be activated when the number is below 10.");
            }
        } else {
            activity.displayToastMessage("Cannot activate on the first turn.");

        }
    }

    public static void handleQuizMagicianClass(Player currentPlayer) {
        markAbilityUsed(QUIZ_MAGICIAN, currentPlayer);
        currentPlayer.setJustUsedActiveAbility(true);
        AudioManager.getInstance().playSoundEffects(activity, QUIZ_MAGICIAN);
        hideAbilityButton();
    }

    public static void handleGoblinClass(Player currentPlayer) {
        if (currentPlayer.getWildCardAmount() <= 0) {
            Toast.makeText(activity, "You need at least one wildcard to sacrifice!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Player> eligiblePlayers = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            if (!player.equals(currentPlayer) && player.getWildCardAmount() > 0) {
                eligiblePlayers.add(player);
            }
        }

        if (eligiblePlayers.isEmpty()) {
            Toast.makeText(activity, "All other players have no wildcards", Toast.LENGTH_SHORT).show();
            return;
        }

        Player randomPlayer = eligiblePlayers.get(new Random().nextInt(eligiblePlayers.size()));
        randomPlayer.removeWildCard(randomPlayer, 2);

        int wildcardsLeft = randomPlayer.getWildCardAmount();
        String wildcardText = wildcardsLeft == 0 ? "no more wildcards" : (wildcardsLeft == 1 ? "1 wildcard left" : wildcardsLeft + " wildcards left");

        activity.showClassAbilityDialog(GOBLIN + "'s Active: \n\n" + randomPlayer.getName() + " lost two wildcards.\n\n" + randomPlayer.getName() + " now has " + wildcardText + ".");
        currentPlayer.loseWildCards(1);
        markAbilityUsed(GOBLIN, currentPlayer);
        activity.renderPlayerUI(true);
        AudioManager.getInstance().playSoundEffects(activity, GOBLIN);
        hideAbilityButton();
    }

    public static void handleAngryJimClass(Player currentPlayer) {
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        game.updateRepeatingTurns(randomPlayer, 1);
        activity.showClassAbilityDialog(ANGRY_JIM + "'s Active: \n\n" + randomPlayer.getName() + " must repeat their turn.");
        markAbilityUsed(ANGRY_JIM, currentPlayer);
        AudioManager.getInstance().playSoundEffects(activity, ANGRY_JIM);
        hideAbilityButton();
    }

    public static void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            activity.halveCurrentNumber();
            markAbilityUsed(SURVIVOR, currentPlayer);
            AudioManager.getInstance().playSoundEffects(activity, SURVIVOR);
            hideAbilityButton();
        }
    }

    public static void handleArcherClass(Player currentPlayer) {
        if (drinkNumberCounterInt >= 2) {
            activity.showClassAbilityDialog(ARCHER + "'s Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            markAbilityUsed(ARCHER, currentPlayer);
            activity.updateDrinkNumberCounter(-2, true);
            AudioManager.getInstance().playSoundEffects(activity, ARCHER);
            hideAbilityButton();
        }
    }
    //-----------------------------------------------------Troll---------------------------------------------------//

    public static void handleTrollClass(Player currentPlayer) {
        List<Player> players = game.getPlayers();
        List<Player> targets = new ArrayList<>();

        if (players.size() <= 2) {
            for (Player p : players) {
                if (!p.equals(currentPlayer)) {
                    targets.add(p);
                }
            }
        } else {
            List<Player> others = new ArrayList<>(players);
            others.remove(currentPlayer);
            Random r = new Random();
            targets.add(others.remove(r.nextInt(others.size())));
            targets.add(others.remove(r.nextInt(others.size())));
        }

        if (targets.isEmpty()) return;

        showTrollRiddleDialog(currentPlayer, targets);
        AudioManager.getInstance().playSoundEffects(activity, TROLL);
        hideAbilityButton();
    }


    private static void showTrollRiddleDialog(Player currentPlayer, List<Player> targets) {
        String[] riddle = getRandomRiddle();
        View dialogView = inflateTrollDialog();

        // Step 1 UI
        View step1 = dialogView.findViewById(R.id.step1_container);
        TextView targetsTv = dialogView.findViewById(R.id.troll_targets);
        TextView riddleTv = dialogView.findViewById(R.id.troll_riddle_text);
        Button btnReveal = dialogView.findViewById(R.id.btn_reveal_answer);

        // Step 2 UI
        View step2 = dialogView.findViewById(R.id.step2_container);
        TextView answerTv = dialogView.findViewById(R.id.troll_answer_text);
        Button btnPlayer1Correct = dialogView.findViewById(R.id.btn_player1_correct);
        Button btnPlayer2Correct = dialogView.findViewById(R.id.btn_player2_correct);
        Button btnBothWrong = dialogView.findViewById(R.id.btn_both_wrong);

        setupTrollDialogInitialState(targets, riddle, targetsTv, riddleTv, answerTv, btnPlayer1Correct, btnPlayer2Correct, btnBothWrong);

        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(d -> {
            targetsTv.setSelected(true);
            targetsTv.requestFocus();
        });

        activity.btnUtils.setButton(btnReveal, () -> {
            step1.setVisibility(GONE);
            step2.setVisibility(VISIBLE);
            btnPlayer1Correct.postDelayed(() -> {
                btnPlayer1Correct.setSelected(true);
                btnPlayer1Correct.requestFocus();
            }, 100);
            if (targets.size() > 1) {
                btnPlayer2Correct.postDelayed(() -> {
                    btnPlayer2Correct.setSelected(true);
                    btnPlayer2Correct.requestFocus();
                }, 100);
            }
        });

        setupTrollResultButtons(dialog, currentPlayer, targets, btnPlayer1Correct, btnPlayer2Correct, btnBothWrong);

        dialog.show();
    }

    private static String[] getRandomRiddle() {
        String[][] riddlePool = {
                {"I speak without a mouth and hear without ears. I have no body, but I come alive with wind. What am I?", "Echo"},
                {"You measure my life in hours and I serve you by expiring. I'm quick when I'm thin and slow when I'm fat. The wind is my enemy.", "Candle"},
                {"I have cities, but no houses. I have mountains, but no trees. I have water, but no fish. What am I?", "Map"},
                {"What is seen in the middle of March and April that can’t be seen at the beginning or end of either month?", "The letter R"},
                {"You see a boat filled with people. It has not sunk, but when you look again you don’t see a single person on the boat. Why?", "All were married"},
                {"What has keys, but no locks; space, but no room; and you can enter, but never leave?", "Keyboard"},
                {"I have branches, but no fruit, trunk or leaves. What am I?", "Bank"},
                {"What can travel around the world while staying in a corner?", "Stamp"},
                {"What has a neck but no head?", "Bottle"},
                {"The more of this there is, the less you see. What is it?", "Darkness"}};
        return riddlePool[new Random().nextInt(riddlePool.length)];
    }

    private static View inflateTrollDialog() {
        return activity.getLayoutInflater().inflate(R.layout.game_troll_active_riddle, null);
    }

    private static void setupTrollDialogInitialState(List<Player> targets, String[] riddle, TextView targetsTv, TextView riddleTv, TextView answerTv, Button btnP1, Button btnP2, Button btnNone) {
        riddleTv.setText(riddle[0]);
        answerTv.setText(riddle[1]);

        String targetNames = targets.size() == 1 ? targets.get(0).getName() : targets.get(0).getName() + " & " + targets.get(1).getName();
        targetsTv.setText("This riddle is for: " + targetNames);

        btnP1.setText(targets.get(0).getName());

        if (targets.size() > 1) {
            btnP2.setText(targets.get(1).getName());
        } else {
            btnP2.setVisibility(GONE);
            btnNone.setText("No one!");
        }
    }

    private static void setupTrollResultButtons(AlertDialog dialog, Player currentPlayer, List<Player> targets, Button btnP1, Button btnP2, Button btnNone) {
        activity.btnUtils.setButton(btnP1, () -> {
            dialog.dismiss();
            finalizeTrollResult(currentPlayer, targets.get(0), targets.size() > 1 ? targets.get(1) : null);
        });

        activity.btnUtils.setButton(btnP2, () -> {
            dialog.dismiss();
            finalizeTrollResult(currentPlayer, targets.get(1), targets.get(0));
        });

        activity.btnUtils.setButton(btnNone, () -> {
            dialog.dismiss();
            markAbilityUsed(TROLL, currentPlayer);
            String description = targets.size() == 1
                    ? targets.get(0).getName() + " failed! Take 4 drinks."
                    : "Both failed! " + targets.get(0).getName() + " and " + targets.get(1).getName() + " take 4 drinks.";
            activity.showClassDialog("Troll's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
        });
    }

    private static void finalizeTrollResult(Player troll, Player winner, Player loser) {
        markAbilityUsed(TROLL, troll);
        String description;
        if (winner != null && loser != null) {
            description = winner.getName() + " was safe! " + loser.getName() + " take 4 drinks.";
            activity.showClassDialog("Troll's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
        } else if (winner != null) {
            description = winner.getName() + " answered correctly! Safe!";
            activity.showClassDialog("Troll's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
        }
    }

    private static void flashButton(View view) {
        view.animate().alpha(1.0f).setDuration(200).withEndAction(() -> view.animate().alpha(0.4f).setDuration(200).start()).start();
    }

    //-----------------------------------------------------Witch---------------------------------------------------//

    public static void handleWitchClass(Player currentPlayer) {
        Random random = new Random();
        int gameChoice = random.nextInt(3);
        AudioManager.getInstance().playSoundEffects(activity, WITCH);

        if (gameChoice == 0) {
            handleWitchMathGame(currentPlayer);
        } else if (gameChoice == 1) {
            handleWitchMemoryGame(currentPlayer);
        } else {
            handleWitchRuneGame(currentPlayer);
        }

        hideAbilityButton();
    }

    //-----------------------------------------------------Witch Math---------------------------------------------------//

    private static void handleWitchMathGame(Player currentPlayer) {
        markAbilityUsed(WITCH, currentPlayer);

        int[] problem = generateMathProblem();
        int correctAnswer = problem[2];

        View dialogView = createMathDialog();
        TextView mathProblemTv = dialogView.findViewById(R.id.math_problem);
        ProgressBar timerProgress = dialogView.findViewById(R.id.timer_progress);
        EditText answerEt = dialogView.findViewById(R.id.math_answer);
        Button actionBtn = dialogView.findViewById(R.id.btn_action);

        answerEt.setEnabled(false);

        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        final boolean[] started = {false};
        final CountDownTimer[] timer = new CountDownTimer[1];

        activity.btnUtils.setButton(actionBtn, () -> {
            if (!started[0]) {
                startWitchMathMiniGame(started, actionBtn, answerEt, mathProblemTv, problem, timerProgress, timer, dialog, currentPlayer);
            } else {
                handleMathAnswerSubmission(answerEt, timer, dialog, currentPlayer, correctAnswer);
            }
        });

        dialog.show();
    }

    private static int[] generateMathProblem() {
        Random random = new Random();
        int num1 = random.nextInt(90) + 10;
        int num2 = random.nextInt(90) + 10;
        return new int[]{num1, num2, num1 * num2};
    }

    private static View createMathDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        return inflater.inflate(R.layout.game_witch_potion_math, null);
    }

    private static void startWitchMathMiniGame(boolean[] started, Button actionBtn, EditText answerEt, TextView mathProblemTv, int[] problem, ProgressBar timerProgress, CountDownTimer[] timer, AlertDialog dialog, Player currentPlayer) {
        answerEt.setEnabled(true);
        answerEt.requestFocus();
        started[0] = true;
        actionBtn.setText("Submit");
        startMiniGame = true;

        mathProblemTv.setText(problem[0] + " x " + problem[1] + " = ?");
        timerProgress.setVisibility(VISIBLE);

        timer[0] = new CountDownTimer(15000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerProgress.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerProgress.setProgress(0);
                dialog.dismiss();
                processPotionResult(currentPlayer, -1, problem[2]);
            }
        }.start();
    }

    private static void handleMathAnswerSubmission(EditText answerEt, CountDownTimer[] timer, AlertDialog dialog, Player currentPlayer, int correctAnswer) {
        String input = answerEt.getText().toString();
        if (input.isEmpty()) return;

        try {
            int userAnswer = Integer.parseInt(input);
            if (timer[0] != null) timer[0].cancel();
            dialog.dismiss();
            processPotionResult(currentPlayer, userAnswer, correctAnswer);
        } catch (NumberFormatException ignored) {
        }
    }


    private static void processPotionResult(Player player, int userAnswer, int correctAnswer) {
        player.setUsedActiveAbility(true);
        String description;

        if (userAnswer == -1) {
            description = "Time's up! The potion exploded. \n\n" + player.getName() + " take 2 drinks!";
            activity.showClassDialog("Witch's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
            player.incrementDrinksTakenByWitch(2);
            return;
        }

        if (userAnswer == correctAnswer) {
            description = "PERFECT! \n\n" + player.getName() + " is now immune to landing on 0 once!";
            activity.showClassDialog("Witch's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
            PowerUps.gainPowerUp(player, PowerUps.GET_OUT_OF_JAIL + ": Immune to landing on 0 once!");
            return;
        }

        int difference = Math.abs(userAnswer - correctAnswer);
        double percentageOff = ((double) difference / correctAnswer) * 100;

        if (percentageOff <= 5) {
            description = "Close enough (Within 5%)! Correct was " + correctAnswer + ".\n\n" + player.getName() + " hand out 3 drinks!";
            player.incrementDrinksHandedOutByWitch(3);
        } else if (percentageOff <= 10) {
            description = "Not bad (Within 10%)! Correct was " + correctAnswer + ".\n\n" + player.getName() + " hand out 2 drinks!";
            player.incrementDrinksHandedOutByWitch(1);
        }
        else if (percentageOff <= 20) {
            description = "Not bad (Within 20%)! Correct was " + correctAnswer + ".\n\n" + player.getName() + " hand out 1 drink!";
            player.incrementDrinksHandedOutByWitch(1);
        }else {
            description = "Way off! Correct was " + correctAnswer + ".\n\n" + player.getName() + " take 2 drinks!";
            player.incrementDrinksTakenByWitch(2);
        }
        
        activity.showClassDialog("Witch's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);

    }


    //-----------------------------------------------------Witch Rune Game---------------------------------------------------//

    private static void handleWitchRuneGame(Player currentPlayer) {
        markAbilityUsed(WITCH, currentPlayer);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_witch_potion_rune, null);

        RuneTracingView runeView = dialogView.findViewById(R.id.rune_tracing_view);
        Button actionBtn = dialogView.findViewById(R.id.btn_action);
        TextView statusTv = dialogView.findViewById(R.id.rune_status);

        // Initial State: Empty board
        final Path[] targetRune = {generateRandomRune()};
        runeView.setDrawingEnabled(false);
        actionBtn.setText("Start");
        statusTv.setText("Study the magical rune!");

        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        final boolean[] canSubmit = {false};

        activity.btnUtils.setButton(actionBtn, () -> {
            if (!canSubmit[0]) {
                // Phase 1: Memorize (Rune visible for 3s)
                actionBtn.setEnabled(false);
                actionBtn.setAlpha(0.5f);
                actionBtn.setText("Watch...");
                runeView.setRune(targetRune[0]);
                runeView.setDrawingEnabled(false);
                statusTv.setText("Memorize the rune!");

                new Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    if (dialog.isShowing()) {
                        // Phase 2: Draw from memory (Rune hidden)
                        canSubmit[0] = true;
                        runeView.hideTargetRune();
                        runeView.setDrawingEnabled(true);
                        actionBtn.setEnabled(true);
                        actionBtn.setAlpha(1.0f);
                        actionBtn.setText("Cast Spell");
                        statusTv.setText("Now draw it from memory!");
                    }
                }, 3000);
            } else {
                // Phase 3: Submission
                float similarity = runeView.calculateSimilarity();
                dialog.dismiss();
                processRuneResult(currentPlayer, similarity);
            }
        });

        dialog.show();
    }

    private static Path generateRandomRune() {

        Path path = new Path();
        Random r = new Random();

        int type = r.nextInt(7);

        switch (type) {

            case 0: // Hexagram (magic star)
                path.moveTo(150, 20);
                path.lineTo(280, 250);
                path.lineTo(20, 250);
                path.close();

                path.moveTo(20, 50);
                path.lineTo(280, 50);
                path.lineTo(150, 280);
                path.close();
                break;


            case 1: // Crescent moon rune
                path.moveTo(210, 40);

                for (int i = 0; i <= 180; i++) {
                    double angle = Math.toRadians(i);

                    float x = (float)(150 + 100 * Math.cos(angle));
                    float y = (float)(150 + 100 * Math.sin(angle));

                    path.lineTo(x, y);
                }

                for (int i = 180; i >= 0; i--) {

                    double angle = Math.toRadians(i);

                    float x = (float)(180 + 70 * Math.cos(angle));
                    float y = (float)(150 + 70 * Math.sin(angle));

                    path.lineTo(x,y);
                }

                path.close();
                break;


            case 2: // Eye rune
                path.moveTo(40,150);

                for(int i=0;i<=360;i++){

                    double t=Math.toRadians(i);

                    float x=(float)(150 + 110*Math.cos(t));
                    float y=(float)(150 + 60*Math.sin(t));

                    path.lineTo(x,y);
                }

                path.close();

                // pupil
                path.addCircle(150,150,30,Path.Direction.CW);

                break;

            case 3: // Rune tree
                path.moveTo(150,280);
                path.lineTo(150,70);

                path.moveTo(150,100);
                path.lineTo(80,170);

                path.moveTo(150,140);
                path.lineTo(220,210);

                path.moveTo(150,190);
                path.lineTo(90,240);

                path.moveTo(150,220);
                path.lineTo(230,260);

                break;


            case 4: // Diamond rune
                path.moveTo(150,20);
                path.lineTo(270,150);
                path.lineTo(150,280);
                path.lineTo(30,150);
                path.close();

                path.moveTo(150,70);
                path.lineTo(210,150);
                path.lineTo(150,230);
                path.lineTo(90,150);
                path.close();

                break;


            case 5: // Lightning rune
                path.moveTo(180,20);
                path.lineTo(80,150);
                path.lineTo(150,150);
                path.lineTo(90,280);
                path.lineTo(230,120);
                path.lineTo(160,120);
                path.close();

                break;


            case 6: // Viking style rune
                path.moveTo(100,40);
                path.lineTo(100,260);

                path.moveTo(100,80);
                path.lineTo(230,80);

                path.moveTo(100,160);
                path.lineTo(200,260);

                path.moveTo(100,160);
                path.lineTo(220,40);

                break;
        }

        return path;
    }

    private static void processRuneResult(Player player, float similarity) {
        String description;
        if (similarity >= 95) {
            description = "Perfect Cast (" + String.format("%.1f", similarity) + "%)! \n\n" + player.getName() + " is now immune to landing on 0 once!";
            PowerUps.gainPowerUp(player, PowerUps.GET_OUT_OF_JAIL + ": Immune to landing on 0 once!");
        } else if (similarity >= 80) {
            description = "Strong Spell (" + String.format("%.1f", similarity) + "%)! \n\n" + player.getName() + " hand out 3 drinks!";
            player.incrementDrinksHandedOutByWitch(3);
        } else if (similarity >= 60) {
            description = "Weak Spell (" + String.format("%.1f", similarity) + "%)! \n\n" + player.getName() + " hand out 1 drink.";
            player.incrementDrinksHandedOutByWitch(1);
        } else {
            description = "Fumbled the Spell (" + String.format("%.1f", similarity) + "%)! \n\n" + player.getName() + " take 2 drinks!";
            player.incrementDrinksTakenByWitch(2);
        }
        activity.showClassDialog("Witch's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
    }

    //-----------------------------------------------------Witch Memory---------------------------------------------------//

    private static void handleWitchMemoryGame(Player currentPlayer) {
        markAbilityUsed(WITCH, currentPlayer);
        View dialogView = createMemoryDialog();
        TextView statusTv = dialogView.findViewById(R.id.memory_status);
        TextView timerTv = dialogView.findViewById(R.id.memory_timer);
        View[] buttons = getMemoryButtons(dialogView);
        AlertDialog dialog = showMemoryDialog(dialogView);
        List<Integer> sequence = generateInitialSequence();
        List<Integer> playerSequence = new ArrayList<>();
        int[] score = {0};
        boolean[] isPlayerTurn = {false};

        Button btnStart = dialogView.findViewById(R.id.btn_start);

        activity.btnUtils.setButton(btnStart, () -> {
            btnStart.setVisibility(GONE);
            startMiniGame = true;
            CountDownTimer timer = startMemoryTimer(dialog, timerTv, currentPlayer, score);
            setupMemoryButtons(buttons, sequence, playerSequence, score, isPlayerTurn, statusTv, dialog, currentPlayer);
            startNextRound(sequence, playerSequence, buttons, statusTv, isPlayerTurn);
            timer.start();
        });
    }

    private static View createMemoryDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        return inflater.inflate(R.layout.game_witch_potion_memory, null);
    }

    private static AlertDialog showMemoryDialog(View view) {
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setView(view).setCancelable(false).create();
        dialog.show();
        return dialog;
    }

    private static View[] getMemoryButtons(View view) {
        return new View[]{view.findViewById(R.id.btn_red), view.findViewById(R.id.btn_blue), view.findViewById(R.id.btn_green), view.findViewById(R.id.btn_yellow)};
    }

    private static List<Integer> generateInitialSequence() {
        List<Integer> sequence = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            sequence.add(random.nextInt(4));
        }
        return sequence;
    }

    private static CountDownTimer startMemoryTimer(AlertDialog dialog, TextView timerTv, Player currentPlayer, int[] score) {
        return new CountDownTimer(300000, 1000) {

            @Override
            public void onTick(long millis) {
                timerTv.setText("Score: " + score[0]);
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                processMemoryResult(currentPlayer, score[0]);
            }

        };
    }

    private static void processMemoryResult(Player currentPlayer, int score) {
        String description;
        if (score < 2) {
            description = "Failed! The potion turned into sludge (Score: " + score + ").\n\n" + currentPlayer.getName() + " take 2 drinks!";
            currentPlayer.incrementDrinksTakenByWitch(2);
        } else if (score <= 4) {
            description = "Weak Potion (Score: " + score + ")!\n\n" + currentPlayer.getName() + " hand out 1 drink.";
            currentPlayer.incrementDrinksHandedOutByWitch(1);
        } else if (score <= 6) {
            description = "Strong Potion (Score: " + score + ")!\n\n" + currentPlayer.getName() + " hand out 3 drinks!";
            currentPlayer.incrementDrinksHandedOutByWitch(3);
        } else {
            description = "GODLIKE BREW! (Score: " + score + ")!\n\n" + currentPlayer.getName() + " is immune to landing on 0 once!";
            PowerUps.gainPowerUp(currentPlayer, PowerUps.GET_OUT_OF_JAIL + ": Immune to landing on 0 once!");
        }
        activity.showClassDialog("Witch's Active!", description, R.layout.game_use_class_ability_dialog_box, R.id.class_textview, R.id.description_textview, null);
    }

    private static void setupMemoryButtons(View[] buttons, List<Integer> sequence, List<Integer> playerSequence, int[] score, boolean[] playerTurn, TextView status, AlertDialog dialog, Player player) {

        for (int i = 0; i < buttons.length; i++) {
            int index = i;
            buttons[i].setOnClickListener(v -> {
                if (!playerTurn[0]) return;
                flashButton(buttons[index]);
                playerSequence.add(index);
                boolean correct = sequence.get(playerSequence.size() - 1).equals(index);

                if (!correct) {
                    dialog.dismiss();
                    processMemoryResult(player, score[0]);
                    return;
                }

                if (playerSequence.size() == sequence.size()) {
                    score[0]++;
                    new Handler(android.os.Looper.getMainLooper()).postDelayed(() -> startNextRound(sequence, playerSequence, buttons, status, playerTurn), 500);
                }

            });
        }
    }

    private static void startNextRound(List<Integer> sequence, List<Integer> playerSequence, View[] buttons, TextView status, boolean[] playerTurn) {
        playerTurn[0] = false;
        playerSequence.clear();
        sequence.add(new Random().nextInt(4));
        status.setText("Watch carefully!");
        playSequence(sequence, buttons, status, playerTurn);
    }

    private static void playSequence(List<Integer> sequence, View[] buttons, TextView status, boolean[] playerTurn) {
        Handler handler = new Handler(android.os.Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            int step = 0;

            @Override
            public void run() {
                if (step < sequence.size()) {
                    flashButton(buttons[sequence.get(step)]);
                    step++;
                    handler.postDelayed(this, 600);

                } else {
                    status.setText("Your turn! Repeat it!");
                    playerTurn[0] = true;
                }
            }
        }, 1000);
    }

    //-----------------------------------------------------Gambler---------------------------------------------------//
    public static void handleGamblerClass() {

        List<Player> opponents = game.getPlayers().stream().filter(p -> !p.equals(game.getCurrentPlayer())).collect(Collectors.toList());

        if (opponents.size() == 1) {
            showBetDialog(opponents.get(0));
            return;
        }

        showOpponentDialog(opponents);
    }

    private static void showOpponentDialog(List<Player> opponents) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_grid_selection_dialog, null);
        TextView titleTextView = dialogView.findViewById(R.id.title_text_view);
        titleTextView.setText("Gambler's Active:");

        RecyclerView recyclerView = dialogView.findViewById(R.id.listViewOpponents);

        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));

        OpponentAdapter adapter = new OpponentAdapter(opponents, player -> {
            dialog.dismiss();
            showBetDialog(player);
        });

        recyclerView.setAdapter(adapter);

        dialogView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static void showBetDialog(Player opponent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_duel_bet, null);

        EditText editBetAmount = dialogView.findViewById(R.id.editBetAmount);

        TextView subtitle = dialogView.findViewById(R.id.bet_subtitle);
        if (subtitle != null) {
            subtitle.setText("Duel against " + opponent.getName() + " - Bet a drink between 1 and 5.");
        }

        Button okButton = dialogView.findViewById(R.id.btn_confirm_bet);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        activity.btnUtils.setButton(okButton, () -> {
            try {
                int bet = Integer.parseInt(editBetAmount.getText().toString());
                if (bet < 1 || bet > 5) {
                    activity.displayToastMessage("Bet must be between 1 and 5!");
                } else {
                    hideAbilityButton();
                    dialog.dismiss();
                    showGamblerGameSelection(opponent, bet);
                }
            } catch (NumberFormatException e) {
                activity.displayToastMessage("Invalid bet!");
            }
        });
        dialog.show();
    }

    private static void showGamblerGameSelection(Player opponent, int bet) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_game_selection, null);

        View btnHighCard = dialogView.findViewById(R.id.btn_high_card);
        View btnRedBlack = dialogView.findViewById(R.id.btn_red_black);

        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        activity.btnUtils.setButton(btnHighCard, () -> {
            dialog.dismiss();
            showHighCardDuelUI(opponent, bet);
        });

        activity.btnUtils.setButton(btnRedBlack, () -> {
            dialog.dismiss();
            showRedOrBlackUI(opponent, bet);
        });

        dialog.show();
    }

    private static void showHighCardDuelUI(final Player opponent, final int bet) {
        gamblerFlipped = false;
        opponentFlipped = false;
        AudioManager.getInstance().playSoundEffects(activity, GAMBLER);

        Random r = new Random();
        final int gamblerCard = r.nextInt(12) + 2; // 2-13 (10, J, Q, K)
        int tempOpponentCard = r.nextInt(12) + 2;
        while (tempOpponentCard == gamblerCard) {
            tempOpponentCard = r.nextInt(12) + 2;
        }
        final int opponentCard = tempOpponentCard;

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_high_card_duel, null);

        View gamblerCardContainer = dialogView.findViewById(R.id.gambler_card_container);
        View opponentCardContainer = dialogView.findViewById(R.id.opponent_card_container);
        TextView gamblerCardTv = dialogView.findViewById(R.id.gambler_card_text);
        TextView opponentCardTv = dialogView.findViewById(R.id.opponent_card_text);
        ImageView gamblerCardIv = dialogView.findViewById(R.id.gambler_card_image);
        ImageView opponentCardIv = dialogView.findViewById(R.id.opponent_card_image);
        TextView resultTv = dialogView.findViewById(R.id.duel_result_text);
        Button finishBtn = dialogView.findViewById(R.id.btn_finish_duel);

        final Player currentPlayer = game.getCurrentPlayer();
        gamblerCardTv.setText(currentPlayer.getName());
        opponentCardTv.setText(opponent.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        activity.btnUtils.setButton(gamblerCardContainer, () -> {
            if (!gamblerFlipped) {
                gamblerFlipped = true;
                flipCard(gamblerCardContainer, gamblerCardTv, gamblerCardIv, gamblerCard, () -> checkDuelResult(gamblerCard, opponentCard, resultTv, finishBtn, currentPlayer, opponent, bet));
            }
        });

        activity.btnUtils.setButton(opponentCardContainer, () -> {
            if (!opponentFlipped) {
                opponentFlipped = true;
                flipCard(opponentCardContainer, opponentCardTv, opponentCardIv, opponentCard, () -> checkDuelResult(gamblerCard, opponentCard, resultTv, finishBtn, currentPlayer, opponent, bet));
            }
        });

        activity.btnUtils.setButton(finishBtn, () -> {
            dialog.dismiss();
            markAbilityUsed(GAMBLER, currentPlayer);
            AudioManager.getInstance().playSoundEffects(activity, GAMBLER);
        });

        dialog.show();
    }

    private static void showRedOrBlackUI(final Player opponent, final int bet) {
        currentRound = 1;
        totalRounds = bet;
        currentPenalty = bet;
        AudioManager.getInstance().playSoundEffects(activity, GAMBLER);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_red_or_black, null);

        TextView penaltyTv = dialogView.findViewById(R.id.penalty_text);
        TextView roundTv = dialogView.findViewById(R.id.round_text);
        View cardContainer = dialogView.findViewById(R.id.card_container);
        ImageView cardIv = dialogView.findViewById(R.id.card_image);
        TextView cardValueTv = dialogView.findViewById(R.id.card_value_text);
        GifImageView confettiGif = dialogView.findViewById(R.id.confetti_gif);
        Button btnRed = dialogView.findViewById(R.id.btn_red);
        Button btnBlack = dialogView.findViewById(R.id.btn_black);
        TextView resultMsgTv = dialogView.findViewById(R.id.result_message);
        Button finishBtn = dialogView.findViewById(R.id.btn_finish);

        // Reset shading for the buttons (and mutate to prevent leakage to other gamblers)
        if (btnRed.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) btnRed.getBackground().mutate()).setColor(Color.TRANSPARENT);
        }
        if (btnBlack.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) btnBlack.getBackground().mutate()).setColor(Color.TRANSPARENT);
        }

        penaltyTv.setText("Penalty: " + currentPenalty + " drinks");
        roundTv.setText("Round " + currentRound + " of " + totalRounds);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        final Player gambler = game.getCurrentPlayer();

        activity.btnUtils.setButton(btnRed, () -> {
            boolean isRedChoice = new Random().nextBoolean();
            int cardVal = new Random().nextInt(13) + 1;
            handleGuess(true, isRedChoice, cardVal, cardIv, cardValueTv, btnRed, btnBlack, resultMsgTv, finishBtn, penaltyTv, roundTv, confettiGif, cardContainer, gambler);
        });

        activity.btnUtils.setButton(btnBlack, () -> {
            boolean isRedChoice = new Random().nextBoolean();
            int cardVal = new Random().nextInt(13) + 1;
            handleGuess(false, isRedChoice, cardVal, cardIv, cardValueTv, btnRed, btnBlack, resultMsgTv, finishBtn, penaltyTv, roundTv, confettiGif, cardContainer, gambler);
        });

        activity.btnUtils.setButton(finishBtn, () -> {
            dialog.dismiss();
            markAbilityUsed(GAMBLER, gambler);
            AudioManager.getInstance().playSoundEffects(activity, GAMBLER);
        });

        dialog.show();
    }

    private static void handleGuess(boolean guessedRed, boolean isRed, int value, ImageView cardIv, TextView cardValueTv,
                                   Button btnRed, Button btnBlack, TextView resultMsgTv, Button finishBtn,
                                   TextView penaltyTv, TextView roundTv, GifImageView confettiGif,
                                   View cardContainer, Player gambler) {
        btnRed.setEnabled(false);
        btnBlack.setEnabled(false);

        // Selection feedback on the button
        if (guessedRed) {
            if (btnRed.getBackground() instanceof GradientDrawable) {
                ((GradientDrawable) btnRed.getBackground().mutate()).setColor(Color.parseColor("#40FF0000"));
            }
        } else {
            if (btnBlack.getBackground() instanceof GradientDrawable) {
                ((GradientDrawable) btnBlack.getBackground().mutate()).setColor(Color.parseColor("#40000000"));
            }
        }
        
        flipCardForGambler(cardIv, cardValueTv, isRed, value, () -> {
            if (guessedRed == isRed) {
                // Win round
                currentPenalty--;
                penaltyTv.setText("Penalty: " + currentPenalty + " drinks");
                
                // Show confetti
                confettiGif.setVisibility(VISIBLE);
                new Handler().postDelayed(() -> confettiGif.setVisibility(GONE), 2000);

                // Hand out drink
                for (Player p : game.getPlayers()) {
                    if (!p.equals(gambler)) {
                        p.incrementDrinksTakenByGambler(1);
                        gambler.incrementDrinksHandedOutByGambler(1);
                    }
                }
                
                if (currentPenalty == 0) {
                    // Show confetti
                    confettiGif.setVisibility(VISIBLE);
                    new Handler().postDelayed(() -> confettiGif.setVisibility(GONE), 2000);

                    new Handler().postDelayed(() -> {
                        String drinksText = totalRounds == 1 ? "drink" : "drinks";
                        resultMsgTv.setText("Perfect Win!\n" + gambler.getName() + " drinks 0.\nEveryone else takes " + totalRounds + " " + drinksText + ".");
                        resultMsgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        resultMsgTv.setVisibility(VISIBLE);
                        finishBtn.setVisibility(VISIBLE);
                        penaltyTv.setVisibility(GONE);
                        roundTv.setVisibility(GONE);
                        cardContainer.setVisibility(GONE);
                        btnRed.setVisibility(GONE);
                        btnBlack.setVisibility(GONE);
                    }, 1500);
                } else {
                    currentRound++;
                    new Handler().postDelayed(() -> {
                        // Reset buttons and card for next round
                        if (btnRed.getBackground() instanceof GradientDrawable) {
                            ((GradientDrawable) btnRed.getBackground().mutate()).setColor(Color.TRANSPARENT);
                        }
                        if (btnBlack.getBackground() instanceof GradientDrawable) {
                            ((GradientDrawable) btnBlack.getBackground().mutate()).setColor(Color.TRANSPARENT);
                        }

                        cardIv.setImageResource(R.drawable.duel_card_back);
                        cardIv.setBackgroundResource(0);
                        cardValueTv.setVisibility(GONE);
                        roundTv.setText("Round " + currentRound + " of " + totalRounds);
                        btnRed.setEnabled(true);
                        btnBlack.setEnabled(true);
                    }, 2000);
                }
            } else {
                // Lose round
                gambler.incrementDrinksTakenByGambler(currentPenalty);
                int othersDrinks = totalRounds - currentPenalty;
                String gamblerDrinksText = currentPenalty == 1 ? "drink" : "drinks";
                String othersDrinksText = othersDrinks == 1 ? "drink" : "drinks";

                new Handler().postDelayed(() -> {
                    resultMsgTv.setText("Lost!\n" + gambler.getName() + " must take " + currentPenalty + " " + gamblerDrinksText + ".\nEveryone else takes " + othersDrinks + " " + othersDrinksText + ".");
                    resultMsgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                    resultMsgTv.setVisibility(VISIBLE);
                    finishBtn.setVisibility(VISIBLE);
                    penaltyTv.setVisibility(GONE);
                    roundTv.setVisibility(GONE);
                    btnRed.setVisibility(GONE);
                    btnBlack.setVisibility(GONE);
                    cardContainer.setVisibility(GONE);
                }, 1500);
            }
        });
    }

    private static void flipCardForGambler(ImageView cardIv, TextView cardValueTv, boolean isRed, int value, Runnable onEnd) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardIv, "scaleX", 1f, 0f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardIv, "scaleX", 0f, 1f);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(250);
        oa2.setDuration(250);
        
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cardIv.setImageResource(0);
                cardIv.setBackgroundResource(R.drawable.duel_card_front);
                cardValueTv.setText(getCardName(value));
                cardValueTv.setTextColor(isRed ? Color.RED : Color.BLACK);
                cardValueTv.setVisibility(VISIBLE);
                oa2.start();
            }
        });
        
        oa2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onEnd != null) onEnd.run();
            }
        });
        oa1.start();
    }

    private static void markAbilityUsed(String className, Player player) {
        player.setUsedActiveAbility(true);
        player.setClassCooldown(className, AbilityComplimentary.getClassCooldown(className));
    }

    private static void flipCard(View container, TextView cardText, ImageView cardImage, int value, Runnable onEnd) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(container, "scaleX", 1f, 0f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(container, "scaleX", 0f, 1f);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(250);
        oa2.setDuration(250);
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cardText.setVisibility(GONE);
                if (value == 12) {
                    cardImage.setImageResource(R.drawable.queen);
                    cardImage.setVisibility(VISIBLE);
                } else if (value == 13) {
                    cardImage.setImageResource(R.drawable.king);
                    cardImage.setVisibility(VISIBLE);
                } else {
                    cardText.setText(getCardName(value));
                    cardText.setVisibility(VISIBLE);
                }
                container.setBackgroundResource(R.drawable.duel_card_front);
                oa2.start();
            }
        });
        oa2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onEnd != null) onEnd.run();
            }
        });
        oa1.start();
    }

    private static void checkDuelResult(int gVal, int oVal, TextView resultTv, Button finishBtn, Player gambler, Player opponent, int bet) {
        if (gamblerFlipped && opponentFlipped) {
            String msg;
            if (gVal > oVal) {
                msg = gambler.getName() + " wins! " + opponent.getName() + " takes " + bet + " drinks.";
            } else {
                msg = opponent.getName() + " wins! " + gambler.getName() + " takes " + bet + " drinks.";
            }
            resultTv.setText(msg);
            resultTv.setVisibility(VISIBLE);
            finishBtn.setVisibility(VISIBLE);
        }
    }

    private static String getCardName(int value) {
        if (value <= 10) return String.valueOf(value);
        if (value == 11) return "J";
        if (value == 12) return "Q";
        if (value == 13) return "K";
        return "A";
    }

    public static class OpponentAdapter extends RecyclerView.Adapter<OpponentAdapter.VH> {
        private final List<Player> opponents;
        private final OnClick listener;

        public OpponentAdapter(List<Player> opponents, OnClick listener) {
            this.opponents = opponents;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_gambler_player_choice_adaptor, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Player p = opponents.get(position);
            h.name.setText(p.getName());
            h.name.postDelayed(() -> h.name.setSelected(true), 1000);
            h.clazz.setText(p.getClassChoice());
            if (p.getPhoto() != null && !p.getPhoto().isEmpty()) {
                byte[] decoded = Base64.decode(p.getPhoto(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                h.photo.setImageBitmap(bmp);
            } else {
                h.photo.setImageResource(R.drawable.wine);
            }
            h.itemView.setOnClickListener(v -> {
                h.name.setSelected(false);
                listener.onClick(p);
            });
        }

        @Override
        public int getItemCount() {
            return opponents.size();
        }

        public interface OnClick {
            void onClick(Player player);
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView photo;
            TextView name, clazz;

            VH(View v) {
                super(v);
                photo = v.findViewById(R.id.playerPhotoImageView);
                name = v.findViewById(R.id.playerNameTextView);
                clazz = v.findViewById(R.id.playerClassTextView);
            }
        }
    }


}
