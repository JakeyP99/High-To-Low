package com.example.countingdowngame.mainActivity;

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
import android.view.View;
import android.view.animation.PathInterpolator;
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
        activity.disableButtons();
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


        // Create a list of random numbers to simulate the roulette spinning effect
        List<Integer> rouletteNumbers = new ArrayList<>();
        Random r = new Random();

        // Generate 60 random numbers between 0 and the original number
        // These are the "fake" numbers the player sees before landing on the result
        for (int i = 0; i < 60; i++) {
            rouletteNumbers.add(r.nextInt(originalNumber + 1));
        }

        // Force the target/winning number into the list at position 50
        // This ensures the roulette always ends on the correct result
        rouletteNumbers.set(50, targetNumber);


        // Create adapter to display the roulette numbers
        RouletteAdapter adapter = new RouletteAdapter(rouletteNumbers, Game.getInstance().getClassNumbers());

        // Setup horizontal scrolling layout for the roulette
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        rouletteRecyclerView.setLayoutManager(layoutManager);
        rouletteRecyclerView.setAdapter(adapter);


        // Calculate the pixel width of each roulette item
        float density = activity.getResources().getDisplayMetrics().density;
        int itemWidthPx = (int) (160 * density);

        // Get the width of the container so we can center the selected number
        int containerWidth = activity.findViewById(R.id.btnGenerate).getWidth();
        int centerOffset = containerWidth / 2;


        // Calculate how far the RecyclerView needs to scroll
        // so that item 50 (the winning number) lines up with the pointer
        int totalScroll = 50 * itemWidthPx + (itemWidthPx / 2) - centerOffset;


        // Create an animation that smoothly scrolls the roulette from start to end
        ValueAnimator animator = ValueAnimator.ofInt(0, totalScroll);

        animator.setDuration(5000);
        animator.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.15f, 1.0f));
        // Update the RecyclerView position during the animation
        animator.addUpdateListener(animation -> {

            // Get current scroll position
            int currentScroll = (int) animation.getAnimatedValue();

            // Move roulette items horizontally
            layoutManager.scrollToPositionWithOffset(0, -currentScroll);
        });


        // When the roulette finishes spinning
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                // Small delay so the player can see the final number
                new Handler().postDelayed(() -> {

                    // Hide roulette UI
                    rouletteRecyclerView.setVisibility(View.GONE);
                    roulettePointer.setVisibility(View.GONE);

                    // Show the normal number display again
                    numberCounterText.setVisibility(View.VISIBLE);

                    // Reveal the actual result
                    revealFinalNumber(targetNumber);

                }, 500);
            }
        });


        // Start the roulette animation
        animator.start();
    }

    private void revealFinalNumber(int targetNumber) {
        int previousNumber = Game.getInstance().getPreviousNumber();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        Game.getInstance().recordTurn(currentPlayer, targetNumber);

        String display = MainActivityGame.getDisplayNumber(targetNumber);
        numberCounterText.setText(display);
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, display);

        MainActivityGame.updateNumberColor(targetNumber);


        if ((targetNumber == 1 && previousNumber <= 1) && (SURVIVOR.equals(currentPlayer.getClassChoice()) || ANGRY_JIM.equals(currentPlayer.getClassChoice()))) {
            handleSurvivorPassive(currentPlayer);
        }

        if (GAMBLER.equals(currentPlayer.getClassChoice()) || (ANGRY_JIM.equals(currentPlayer.getClassChoice()) && previousNumber < 50)) {
            handleGamblerPassiveResult(targetNumber);
        }

        if (Game.getInstance().getGameMode() == Game.GameMode.CLASS_HUNT && Game.getInstance().getClassNumbers().contains(targetNumber)) {
            activity.disableButtons(); // Lock buttons during the long animation
            numberCounterText.setTextColor(Color.YELLOW);

            YoYo.with(Techniques.Pulse).duration(1000).repeat(4) // 5 pulses total (1s each)
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

    private class ShuffleRunnable implements Runnable {
        private final Random random;
        private final int originalNumber;
        private final int targetNumber;
        private final int shuffleDuration;
        private final int initialInterval;
        private final Player currentPlayer = Game.getInstance().getCurrentPlayer();
        private int currentInterval;
        private int shuffleTime = 0;

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

                YoYo.with(Techniques.Pulse).duration(currentInterval).playOn(numberCounterText);

                shuffleHandler.postDelayed(this, currentInterval);
            } else {
                // LAST STEP: display the actual target number and finalize
                revealFinalNumber(targetNumber);
            }
        }
    }
}
