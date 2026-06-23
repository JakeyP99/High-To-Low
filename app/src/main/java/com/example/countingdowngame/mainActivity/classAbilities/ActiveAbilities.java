package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.repeatedTurn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static void hideAbilityButton() {
        if (activity != null) {
            View btnClassAbility = activity.findViewById(R.id.btnClassAbility);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }


    private static void hideWildButton() {
        if (activity != null) {
            View btnWild = activity.findViewById(R.id.btnWild);
            if (btnWild != null) {
                btnWild.setVisibility(View.INVISIBLE);
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
                    currentPlayer.setUsedActiveAbility(true);
                    MainActivityGame.updateNumber(newNumber);
                    AudioManager.getInstance().playSoundEffects(activity, SCIENTIST);
                    dialog.dismiss(); // Close the dialog on success
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
                currentPlayer.setUsedActiveAbility(true);
                game.updateRepeatingTurns(currentPlayer, 1);
                activity.renderPlayerUI(false);
                repeatedTurn = true;
                activity.updateDrinkNumberCounter(4, true);
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
        AudioManager.getInstance().playSoundEffects(activity, QUIZ_MAGICIAN);
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

        activity.showClassAbilityDialog(
                GOBLIN + "'s Active: \n\n" +
                        randomPlayer.getName() + " lost two wildcards.\n\n" +
                        randomPlayer.getName() + " now has " + wildcardText + "."
        );
        currentPlayer.loseWildCards(1);
        currentPlayer.setUsedActiveAbility(true);
        activity.renderPlayerUI(true);
        AudioManager.getInstance().playSoundEffects(activity, GOBLIN);
    }

    public static void handleAngryJimClass(Player currentPlayer) {
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        game.updateRepeatingTurns(randomPlayer, 1);
        activity.showClassAbilityDialog(
                ANGRY_JIM + "'s Active: \n\n" +
                        randomPlayer.getName() + " must repeat their turn."
        );
        currentPlayer.setUsedActiveAbility(true);
        AudioManager.getInstance().playSoundEffects(activity, ANGRY_JIM);
    }

    public static void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            activity.halveCurrentNumber();
            currentPlayer.setUsedActiveAbility(true);
            AudioManager.getInstance().playSoundEffects(activity, SURVIVOR);
        }
    }

    public static void handleArcherClass(Player currentPlayer) {
        if (drinkNumberCounterInt >= 2) {
            activity.showClassAbilityDialog(
                    ARCHER + "'s Active: \n\n" +
                            currentPlayer.getName() + " hand out two drinks!"
            );
            currentPlayer.setUsedActiveAbility(true);
            activity.updateDrinkNumberCounter(-2, true);
            AudioManager.getInstance().playSoundEffects(activity, ARCHER);
        }
    }

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
    }

    private static void showTrollRiddleDialog(Player currentPlayer, List<Player> targets) {
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
                {"The more of this there is, the less you see. What is it?", "Darkness"}
        };

        int rIndex = new Random().nextInt(riddlePool.length);
        String riddleText = riddlePool[rIndex][0];
        String answerText = riddlePool[rIndex][1];

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_troll_active_riddle, null);

        // Step 1 UI (Riddle)
        View step1 = dialogView.findViewById(R.id.step2_container);
        TextView targetsTv = dialogView.findViewById(R.id.troll_targets);
        TextView riddleTv = dialogView.findViewById(R.id.troll_riddle_text);
        Button btnReveal = dialogView.findViewById(R.id.btn_reveal_answer);

        // Step 2 UI (Result)
        View step2 = dialogView.findViewById(R.id.step3_container);
        TextView answerTv = dialogView.findViewById(R.id.troll_answer_text);
        Button btnTarget1 = dialogView.findViewById(R.id.btn_target1_safe);
        Button btnTarget2 = dialogView.findViewById(R.id.btn_target2_safe);
        Button btnNoOne = dialogView.findViewById(R.id.btn_troll_noone);

        // Initial Setup
        riddleTv.setText(riddleText);
        answerTv.setText(answerText);
        String targetNames = targets.size() == 1 ? targets.get(0).getName() : targets.get(0).getName() + " & " + targets.get(1).getName();
        targetsTv.setText(targetNames);
        btnTarget1.setText(targets.get(0).getName());
        if (targets.size() > 1) {
            btnTarget2.setText(targets.get(1).getName());
        } else {
            btnTarget2.setVisibility(View.GONE);
            btnNoOne.setText("Wrong! (Drink 4)");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        // Step 1 -> Step 2
        activity.btnUtils.setButton(btnReveal, () -> {
            step1.setVisibility(View.GONE);
            step2.setVisibility(View.VISIBLE);
        });

        // Final Actions
        activity.btnUtils.setButton(btnTarget1, () -> {
            dialog.dismiss();
            finalizeTrollResult(currentPlayer, targets.get(0), targets.size() > 1 ? targets.get(1) : null);
        });

        activity.btnUtils.setButton(btnTarget2, () -> {
            dialog.dismiss();
            finalizeTrollResult(currentPlayer, targets.get(1), targets.get(0));
        });

        activity.btnUtils.setButton(btnNoOne, () -> {
            dialog.dismiss();
            currentPlayer.setUsedActiveAbility(true);
            if (targets.size() == 1) {
                activity.showGameDialog(targets.get(0).getName() + " failed! Take 4 drinks.");
            } else {
                activity.showGameDialog("Both failed! " + targets.get(0).getName() + " and " + targets.get(1).getName() + " take 4 drinks.");
            }
        });

        dialog.show();
    }

    private static void finalizeTrollResult(Player troll, Player winner, Player loser) {
        troll.setUsedActiveAbility(true);

        if (winner != null && loser != null) {
            activity.showGameDialog(winner.getName() + " was safe! " + loser.getName() + " take 4 drinks.");
        } else if (winner != null) {
            activity.showGameDialog(winner.getName() + " answered correctly! Safe!");
        }
    }

    public static void handleWitchClass(Player currentPlayer) {
        Random random = new Random();
        if (random.nextBoolean()) {
            handleWitchMathGame(currentPlayer);
        } else {
            handleWitchMemoryGame(currentPlayer);
        }
    }

    private static void handleWitchMathGame(Player currentPlayer) {
        Random random = new Random();
        int num1 = random.nextInt(900) + 100; // 3-digit
        int num2 = random.nextInt(900) + 100; // 3-digit
        int correctAnswer = num1 * num2;

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_witch_potion_math, null);

        TextView mathProblemTv = dialogView.findViewById(R.id.math_problem);
        TextView timerTv = dialogView.findViewById(R.id.timer_text);
        EditText answerEt = dialogView.findViewById(R.id.math_answer);
        Button submitBtn = dialogView.findViewById(R.id.btn_submit_potion);

        mathProblemTv.setText(num1 + " x " + num2);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        CountDownTimer timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTv.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                processPotionResult(currentPlayer, -1, correctAnswer);
            }
        }.start();

        activity.btnUtils.setButton(submitBtn, () -> {
            String input = answerEt.getText().toString();
            if (input.isEmpty()) {
                return;
            }
            try {
                int userAnswer = Integer.parseInt(input);
                timer.cancel();
                dialog.dismiss();
                processPotionResult(currentPlayer, userAnswer, correctAnswer);
            } catch (NumberFormatException ignored) {
            }
        });

        dialog.show();
        AudioManager.getInstance().playSoundEffects(activity, WITCH);
    }

    private static void handleWitchMemoryGame(Player currentPlayer) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_witch_potion_memory, null);

        TextView statusTv = dialogView.findViewById(R.id.memory_status);
        TextView timerTv = dialogView.findViewById(R.id.memory_timer);
        View[] buttons = {
                dialogView.findViewById(R.id.btn_red),
                dialogView.findViewById(R.id.btn_blue),
                dialogView.findViewById(R.id.btn_green),
                dialogView.findViewById(R.id.btn_yellow)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        List<Integer> sequence = new ArrayList<>();
        List<Integer> playerSequence = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            sequence.add(random.nextInt(4));
        }
        final boolean[] isPlayerTurn = {false};
        final int[] score = {0};

        CountDownTimer gameTimer = new CountDownTimer(300000, 1000) { // Large timer, effectively infinite until failure
            @Override
            public void onTick(long millisUntilFinished) {
                timerTv.setText("Score: " + score[0]);
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                processMemoryResult(currentPlayer, score[0]);
            }
        };

        Runnable nextRound = new Runnable() {
            @Override
            public void run() {
                isPlayerTurn[0] = false;
                playerSequence.clear();
                sequence.add(random.nextInt(4));
                statusTv.setText("Watch carefully!");

                new Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                    int step = 0;

                    @Override
                    public void run() {
                        if (step < sequence.size()) {
                            int btnIdx = sequence.get(step);
                            flashButton(buttons[btnIdx]);
                            step++;
                            new Handler(android.os.Looper.getMainLooper()).postDelayed(this, 600);
                        } else {
                            statusTv.setText("Your turn! Repeat it!");
                            isPlayerTurn[0] = true;
                        }
                    }
                }, 1000);
            }
        };

        for (int i = 0; i < 4; i++) {
            int index = i;
            buttons[i].setOnClickListener(v -> {
                if (!isPlayerTurn[0]) return;
                flashButton(buttons[index]);
                playerSequence.add(index);

                if (playerSequence.get(playerSequence.size() - 1).equals(sequence.get(playerSequence.size() - 1))) {
                    if (playerSequence.size() == sequence.size()) {
                        score[0]++;
                        new Handler(android.os.Looper.getMainLooper()).postDelayed(nextRound, 500);
                    }
                } else {
                    gameTimer.cancel();
                    dialog.dismiss();
                    processMemoryResult(currentPlayer, score[0]);
                }
            });
        }

        nextRound.run();
        gameTimer.start();
        AudioManager.getInstance().playSoundEffects(activity, WITCH);
    }

    private static void flashButton(View view) {
        view.animate().alpha(1.0f).setDuration(200).withEndAction(() -> view.animate().alpha(0.4f).setDuration(200).start()).start();
    }

    private static void processMemoryResult(Player player, int score) {
        player.setUsedActiveAbility(true);

        if (score < 2) {
            activity.showGameDialog("Failed! The potion turned into sludge (Score: " + score + ").\n\n" + player.getName() + " take 2 drinks!");
            player.incrementDrinksTakenByWitch(2);
        } else if (score <= 4) {
            activity.showGameDialog("Weak Potion (Score: " + score + ")!\n\n" + player.getName() + " hand out 1 drink.");
            player.incrementDrinksHandedOutByWitch(1);
        } else if (score <= 10) {
            activity.showGameDialog("Strong Potion (Score: " + score + ")!\n\n" + player.getName() + " hand out 3 drinks!");
            player.incrementDrinksHandedOutByWitch(3);
        } else {
            activity.showGameDialog("GODLIKE BREW! (Score: " + score + ")!\n\n" + player.getName() + " is immune to landing on 0 once!");
            PowerUps.gainPowerUp(player, PowerUps.GET_OUT_OF_JAIL + ": Immune to landing on 0 once!");
        }
    }

    private static void processPotionResult(Player player, int userAnswer, int correctAnswer) {
        player.setUsedActiveAbility(true);

        if (userAnswer == -1) {
            activity.showGameDialog("Time's up! The potion exploded. \n\n" + player.getName() + " take 2 drinks!");
            player.incrementDrinksTakenByWitch(2);
            return;
        }

        if (userAnswer == correctAnswer) {
            activity.showGameDialog("PERFECT! \n\n" + player.getName() + " is now immune to landing on 0 once!");
            PowerUps.gainPowerUp(player, PowerUps.GET_OUT_OF_JAIL + ": Immune to landing on 0 once!");
            return;
        }

        int difference = Math.abs(userAnswer - correctAnswer);
        double percentageOff = ((double) difference / correctAnswer) * 100;

        if (percentageOff <= 5) {
            activity.showGameDialog("Close enough (Within 5%)! Correct was " + correctAnswer + ".\n\n" + player.getName() + " hand out 3 drinks!");
            player.incrementDrinksHandedOutByWitch(3);
        } else if (percentageOff <= 10) {
            activity.showGameDialog("Not bad (Within 10%)! Correct was " + correctAnswer + ".\n\n" + player.getName() + " hand out 1 drink!");
            player.incrementDrinksHandedOutByWitch(1);
        } else {
            activity.showGameDialog("Way off! Correct was " + correctAnswer + ".\n\n" + player.getName() + " take 2 drinks!");
            player.incrementDrinksTakenByWitch(2);
        }
    }

    public static void handleGamblerClass() {
        List<Player> opponents = game.getPlayers().stream()
                .filter(p -> !p.equals(game.getCurrentPlayer()))
                .collect(Collectors.toList());

        if (opponents.size() == 1) {
            showBetDialog(opponents.get(0));
            return;
        }

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_select_opponent, null);

        ListView listView = dialogView.findViewById(R.id.listViewOpponents);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.setOnClickListener(v -> dialog.dismiss());

        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(activity, R.layout.game_powerup_list_item, R.id.powerup_text, opponents) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Player opponent = opponents.get(position);
                TextView textView = view.findViewById(R.id.powerup_text);
                textView.setText(opponent.getName());
                textView.setTextSize(24); // Increased text size
                GifImageView icon = view.findViewById(R.id.powerup_icon);
                icon.setVisibility(View.GONE); // Removed the symbol
                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Player selectedOpponent = opponents.get(position);
            dialog.dismiss();
            showBetDialog(selectedOpponent);
        });

        dialog.show();
    }

    private static void showBetDialog(Player opponent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_duel_bet, null);

        EditText editBetAmount = dialogView.findViewById(R.id.editBetAmount);
        
        TextView subtitle = dialogView.findViewById(R.id.bet_subtitle);
        if (subtitle != null) {
            subtitle.setText("Duel against " + opponent.getName());
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
                    dialog.dismiss();
                    showHighCardDuelUI(opponent, bet);
                }
            } catch (NumberFormatException e) {
                activity.displayToastMessage("Invalid bet!");
            }
        });
        dialog.show();
    }

    private static boolean gamblerFlipped = false;
    private static boolean opponentFlipped = false;

    private static void showHighCardDuelUI(final Player opponent, final int bet) {
        gamblerFlipped = false;
        opponentFlipped = false;

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
            currentPlayer.setUsedActiveAbility(true);
            AudioManager.getInstance().playSoundEffects(activity, GAMBLER);
        });

        dialog.show();
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
                cardText.setVisibility(View.GONE);
                if (value == 12) {
                    cardImage.setImageResource(R.drawable.queen);
                    cardImage.setVisibility(View.VISIBLE);
                } else if (value == 13) {
                    cardImage.setImageResource(R.drawable.king);
                    cardImage.setVisibility(View.VISIBLE);
                } else {
                    cardText.setText(getCardName(value));
                    cardText.setVisibility(View.VISIBLE);
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
            resultTv.setVisibility(View.VISIBLE);
            finishBtn.setVisibility(View.VISIBLE);
        }
    }

    private static String getCardName(int value) {
        if (value <= 10) return String.valueOf(value);
        if (value == 11) return "J";
        if (value == 12) return "Q";
        if (value == 13) return "K";
        return "A";
    }
}
