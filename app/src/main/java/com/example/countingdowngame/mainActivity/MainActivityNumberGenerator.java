package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleGamblerPassiveResult;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleSurvivorPassive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivityNumberGenerator {

    private final MainActivityGame activity;
    private final TextView numberCounterText;
    private final RecyclerView rouletteRecyclerView;
    private final View roulettePointer;
    private final Handler shuffleHandler;

    public MainActivityNumberGenerator(MainActivityGame activity, TextView numberCounterText) {
        this.activity = activity;
        this.numberCounterText = numberCounterText;
        this.rouletteRecyclerView = activity.findViewById(R.id.rouletteRecyclerView);
        this.roulettePointer = activity.findViewById(R.id.roulettePointer);
        this.shuffleHandler = new Handler();
    }

    public void startNumberShuffleAnimation() {
        int originalNumber = Game.getInstance().getCurrentNumber();
        int targetNumber = Game.getInstance().nextNumber();

        if (Game.getInstance().getGameMode() == Game.GameMode.CLASS_HUNT) {
            startRouletteAnimation(originalNumber, targetNumber);
        } else {
            final int shuffleDuration = 1500;
            int initialShuffleInterval = originalNumber >= 1000 ? 30 : 50;
            final Random random = new Random();
            shuffleHandler.postDelayed(new ShuffleRunnable(random, originalNumber, targetNumber, shuffleDuration, initialShuffleInterval), initialShuffleInterval);
        }
    }

    private void startRouletteAnimation(int originalNumber, int targetNumber) {
        activity.disableButtons();
        numberCounterText.setVisibility(View.INVISIBLE);
        rouletteRecyclerView.setVisibility(View.VISIBLE);
        roulettePointer.setVisibility(View.VISIBLE);

        List<Integer> rouletteNumbers = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 60; i++) {
            rouletteNumbers.add(r.nextInt(originalNumber + 1));
        }
        // Set the winning number at position 50
        rouletteNumbers.set(50, targetNumber);

        RouletteAdapter adapter = new RouletteAdapter(rouletteNumbers, Game.getInstance().getClassNumbers());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        rouletteRecyclerView.setLayoutManager(layoutManager);
        rouletteRecyclerView.setAdapter(adapter);

        float density = activity.getResources().getDisplayMetrics().density;
        int itemWidthPx = (int) (100 * density);
        int containerWidth = activity.findViewById(R.id.btnGenerate).getWidth();
        int centerOffset = containerWidth / 2;

        int totalScroll = 50 * itemWidthPx + (itemWidthPx / 2) - centerOffset;

        ValueAnimator animator = ValueAnimator.ofInt(0, totalScroll);
        animator.setDuration(4000);
        animator.setInterpolator(new DecelerateInterpolator(1.2f));
        animator.addUpdateListener(animation -> {
            int currentScroll = (int) animation.getAnimatedValue();
            layoutManager.scrollToPositionWithOffset(0, -currentScroll);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(() -> {
                    rouletteRecyclerView.setVisibility(View.GONE);
                    roulettePointer.setVisibility(View.GONE);
                    numberCounterText.setVisibility(View.VISIBLE);
                    revealFinalNumber(targetNumber);
                }, 1000);
            }
        });
        animator.start();
    }

    private class ShuffleRunnable implements Runnable {
        private final Random random;
        private final int originalNumber;
        private final int targetNumber;
        private final int shuffleDuration;
        private int currentInterval;
        private final int initialInterval;
        private int shuffleTime = 0;
        private final Player currentPlayer = Game.getInstance().getCurrentPlayer();

        ShuffleRunnable(Random random, int originalNumber, int targetNumber, int shuffleDuration, int initialInterval) {
            this.random = random;
            this.originalNumber = originalNumber;
            this.targetNumber = targetNumber;
            this.shuffleDuration = shuffleDuration;
            this.initialInterval = initialInterval;
            this.currentInterval = initialInterval;
        }

        @Override
        public void run() {
            shuffleTime += currentInterval;

            if (shuffleTime < shuffleDuration) {
                // Still shuffling: display a random number
                int randomDigit = random.nextInt(originalNumber + 1);
                String display = MainActivityGame.getDisplayNumber(randomDigit);
                numberCounterText.setText(display);
                SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, display);
                numberCounterText.setTextColor(Color.BLACK); // Always black during shuffle

                float progress = (float) shuffleTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 250));

                YoYo.with(Techniques.Pulse)
                        .duration(currentInterval)
                        .playOn(numberCounterText);

                shuffleHandler.postDelayed(this, currentInterval);
            } else {
                // LAST STEP: display the actual target number and finalize
                revealFinalNumber(targetNumber);
            }
        }
    }

    private void revealFinalNumber(int targetNumber) {
        int previousNumber = Game.getInstance().getPreviousNumber();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        Game.getInstance().recordTurn(currentPlayer, targetNumber);

        String display = MainActivityGame.getDisplayNumber(targetNumber);
        numberCounterText.setText(display);
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, display);

        MainActivityGame.updateNumberColor(targetNumber);


        if ((targetNumber == 1 && previousNumber <= 1) &&
                (SURVIVOR.equals(currentPlayer.getClassChoice()) || ANGRY_JIM.equals(currentPlayer.getClassChoice()))) {
            handleSurvivorPassive(currentPlayer);
        }

        if (GAMBLER.equals(currentPlayer.getClassChoice()) || (ANGRY_JIM.equals(currentPlayer.getClassChoice()) && previousNumber < 50)) {
            handleGamblerPassiveResult(targetNumber);
        }

        if (Game.getInstance().getGameMode() == Game.GameMode.CLASS_HUNT && Game.getInstance().getClassNumbers().contains(targetNumber)) {
            activity.disableButtons(); // Lock buttons during the long animation
            numberCounterText.setTextColor(Color.YELLOW);

            YoYo.with(Techniques.Pulse)
                    .duration(1000)
                    .repeat(4) // 5 pulses total (1s each)
                    .playOn(numberCounterText);

            new Handler().postDelayed(() -> {
                activity.awardRandomClass(currentPlayer, targetNumber);
                activity.renderCurrentNumber(targetNumber, activity::gotoGameEnd, numberCounterText);
                activity.enableButtons(); // Re-enable after award
            }, 5000); // Wait for 5 seconds of pulsing

            Game.getInstance().getClassNumbers().remove(Integer.valueOf(targetNumber)); // Only award once
        } else {
            activity.renderCurrentNumber(targetNumber, activity::gotoGameEnd, numberCounterText);
            if (targetNumber != 0) {
                activity.enableButtons();
            }
        }
    }
}
