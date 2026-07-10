package com.mydomain.countingdowngame.mainActivity;

import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.mydomain.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleGamblerPassiveResult;
import static com.mydomain.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleSurvivorPassive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.classAbilities.ActiveAbilities;
import com.mydomain.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Handles the generation and animation of numbers in the main game activity.
 * Supports both a "Classic" digit-shuffle and a "Class Hunt" roulette-style animation.
 */
public class MainActivityNumberGenerator {

    private final MainActivityGame activity;
    private final TextView numberCounterText;
    private final RecyclerView rouletteRecyclerView;
    private final Handler shuffleHandler;

    public MainActivityNumberGenerator(MainActivityGame activity, TextView numberCounterText) {
        this.activity = activity;
        this.numberCounterText = numberCounterText;
        this.rouletteRecyclerView = activity.findViewById(R.id.rouletteRecyclerView);
        this.shuffleHandler = new Handler();
    }

    /**
     * Entry point to start the number animation sequence.
     */
    public void startNumberShuffleAnimation() {
        activity.disableButtons();
        int originalNumber = Game.getInstance().getCurrentNumber();
        int targetNumber = Game.getInstance().nextNumber();

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        boolean hasAnyClass = currentPlayer != null && !NO_CLASS.equals(currentPlayer.getClassChoice());

        boolean showRoulette = false;
        Game.GameMode mode = Game.getInstance().getGameMode();

        if (mode == Game.GameMode.CLASS_HUNT && !hasAnyClass) {
            showRoulette = true;
        } else if (mode == Game.GameMode.CRAZY) {
            int totalAvailableClasses = 10;
            if (currentPlayer != null && currentPlayer.getClassChoices().size() < totalAvailableClasses) {
                showRoulette = true;
            }
        }

        if (showRoulette) {
            runRouletteMode(originalNumber, targetNumber);
        } else {
            runClassicMode(originalNumber, targetNumber);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // CLASSIC MODE LOGIC
    //------------------------------------------------------------------------------------------------------------------

    private void runClassicMode(int originalNumber, int targetNumber) {
        // Clear any leftover class markers so they don't trigger accidentally
        Game.getInstance().getClassNumbers().clear();

        final int shuffleDuration = 1500;
        int initialShuffleInterval = originalNumber >= 1000 ? 30 : 50;
        final Random random = new Random();

        shuffleHandler.postDelayed(new ShuffleRunnable(
                random, originalNumber, targetNumber, shuffleDuration, initialShuffleInterval
        ), initialShuffleInterval);
    }

    //------------------------------------------------------------------------------------------------------------------
    // ROULETTE MODE LOGIC
    //------------------------------------------------------------------------------------------------------------------

    private void runRouletteMode(int originalNumber, int targetNumber) {
        setupRouletteUI(true);

        List<Integer> rouletteNumbers = generateUniqueRoulettePool(originalNumber, targetNumber);
        List<Integer> dynamicClassMarkers = calculateDynamicClassMarkers(rouletteNumbers, targetNumber);

        setupRouletteRecyclerView(rouletteNumbers, dynamicClassMarkers);
        startRouletteScrollAnimation(targetNumber);
    }

    private void setupRouletteUI(boolean visible) {
        int rouletteVisibility = visible ? View.VISIBLE : View.GONE;
        int normalVisibility = visible ? View.INVISIBLE : View.VISIBLE;

        numberCounterText.setVisibility(normalVisibility);
        rouletteRecyclerView.setVisibility(rouletteVisibility);
    }

    private List<Integer> generateUniqueRoulettePool(int originalNumber, int targetNumber) {
        List<Integer> result = new ArrayList<>();
        Random random = new Random();

        // Avoid OOM when originalNumber is very large (e.g. 9 digits) by sampling instead of creating a full pool.
        if (originalNumber > 1000) {
            Set<Integer> uniquePicked = new HashSet<>();
            uniquePicked.add(targetNumber);
            while (uniquePicked.size() < 60) {
                uniquePicked.add(random.nextInt(originalNumber + 1));
            }
            result.addAll(uniquePicked);
            Collections.shuffle(result);

            // Swap targetNumber to index 50 to ensure it's at the winning position and unique in the list.
            int targetIdx = result.indexOf(targetNumber);
            Collections.swap(result, targetIdx, 50);
        } else {
            List<Integer> pool = new ArrayList<>();
            for (int i = 0; i <= originalNumber; i++) {
                pool.add(i);
            }
            Collections.shuffle(pool);

            while (result.size() < 60) {
                if (pool.isEmpty()) {
                    for (int i = 0; i <= originalNumber; i++) pool.add(i);
                    Collections.shuffle(pool);
                }
                result.add(pool.remove(0));
            }
        }

        // Force winning number at the center position (50) for consistency
        result.set(50, targetNumber);
        return result;
    }

    private List<Integer> calculateDynamicClassMarkers(List<Integer> numbers, int targetNumber) {
        List<Integer> markers = Game.getInstance().getClassNumbers();
        markers.clear();
        Random r = new Random();

        for (int num : numbers) {
            if (num > 0 && r.nextInt(getProbabilityChance(num)) == 0) {
                markers.add(num);
            }
        }

        // Ensure target is included if it rolled a success (consistency check)
        if (targetNumber > 0 && r.nextInt(getProbabilityChance(targetNumber)) == 0) {
            if (!markers.contains(targetNumber)) markers.add(targetNumber);
        }

        return markers;
    }

    private int getProbabilityChance(int number) {
        if (number > 1000) return 33; // ~3% chance
        if (number >= 500) return 20; // 5% chance
        if (number >= 100) return 10; // 10% chance
        if (number >= 10) return 2;   // 50% chance
        return 1; // 100% chance for < 10
    }

    private void setupRouletteRecyclerView(List<Integer> numbers, List<Integer> classMarkers) {
        RouletteAdapter adapter = new RouletteAdapter(numbers, classMarkers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        rouletteRecyclerView.setLayoutManager(layoutManager);
        rouletteRecyclerView.setAdapter(adapter);
    }

    private void startRouletteScrollAnimation(int targetNumber) {
        float density = activity.getResources().getDisplayMetrics().density;
        int itemWidthPx = (int) (160 * density);
        int containerWidth = activity.findViewById(R.id.btnGenerate).getWidth();
        int centerOffset = containerWidth / 2;

        int totalScroll = 50 * itemWidthPx + (itemWidthPx / 2) - centerOffset;

        ValueAnimator animator = ValueAnimator.ofInt(0, totalScroll);
        animator.setDuration(5000);
        animator.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.15f, 1.0f));

        animator.addUpdateListener(animation -> {
            int currentScroll = (int) animation.getAnimatedValue();
            LinearLayoutManager lm = (LinearLayoutManager) rouletteRecyclerView.getLayoutManager();
            if (lm != null) lm.scrollToPositionWithOffset(0, -currentScroll);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setupRouletteUI(false);
                revealFinalNumber(targetNumber, 0);
            }
        });

        animator.start();
    }

    //------------------------------------------------------------------------------------------------------------------
    // SHARED FINALIZATION
    //------------------------------------------------------------------------------------------------------------------

    private void revealFinalNumber(int targetNumber, int delayMillis) {
        int previousNumber = Game.getInstance().getPreviousNumber();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        Game.getInstance().recordTurn(currentPlayer, targetNumber);

        String display = MainActivityGame.getDisplayNumber(targetNumber);
        numberCounterText.setText(display);
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, display);

        MainActivityGame.updateNumberColor();

        // Delay the logic that shows prompts/effects so the user "lands" on the number first
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            applyPassiveAbilities(currentPlayer, targetNumber, previousNumber);

            Game.GameMode mode = Game.getInstance().getGameMode();
            boolean hasNoClass = NO_CLASS.equals(currentPlayer.getClassChoice());
            boolean isClassLanded = Game.getInstance().getClassNumbers().contains(targetNumber);

            boolean shouldAwardClass = false;
            if (isClassLanded) {
                if (mode == Game.GameMode.CLASS_HUNT && hasNoClass) {
                    shouldAwardClass = true;
                } else if (mode == Game.GameMode.CRAZY) {
                    int totalClasses = 10;
                    if (currentPlayer.getClassChoices().size() < totalClasses) {
                        shouldAwardClass = true;
                    }
                }
            }

            if (shouldAwardClass) {
                handleClassAwardSequence(currentPlayer, targetNumber);
            } else {
                finalizeTurn(targetNumber);
            }
        }, delayMillis);
    }

    private void applyPassiveAbilities(Player player, int target, int previous) {
        List<String> classes = player.getClassChoices();

        if (target == 1 && previous <= 1) {
            if (classes.contains(SURVIVOR) || classes.contains(ANGRY_JIM)) {
                handleSurvivorPassive(player);
            }
        }

        if (classes.contains(GAMBLER) || (classes.contains(ANGRY_JIM) && previous < 50)) {
            handleGamblerPassiveResult(target);
        }
    }

    private void handleClassAwardSequence(Player player, int targetNumber) {
        activity.disableButtons();
        int customYellow = ContextCompat.getColor(activity, R.color.custom_yellow);
        numberCounterText.setTextColor(customYellow);

        ActiveAbilities.awardRandomClass(player);
        finalizeTurn(targetNumber);
        // Clear markers so it's only awarded once
        Game.getInstance().getClassNumbers().remove(Integer.valueOf(targetNumber));
    }

    private void finalizeTurn(int targetNumber) {
        activity.renderCurrentNumber(targetNumber, activity::gotoGameEnd, numberCounterText);
        if (targetNumber != 0) {
            activity.enableButtons();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // SHUFFLE RUNNABLE (CLASSIC)
    //------------------------------------------------------------------------------------------------------------------

    private class ShuffleRunnable implements Runnable {
        private final Random random;
        private final int originalNumber;
        private final int targetNumber;
        private final int shuffleDuration;
        private final int initialInterval;
        private int currentInterval;
        private int shuffleTime = 0;

        ShuffleRunnable(Random r, int orig, int target, int duration, int interval) {
            this.random = r;
            this.originalNumber = orig;
            this.targetNumber = target;
            this.shuffleDuration = duration;
            this.initialInterval = interval;
            this.currentInterval = interval;
        }

        @Override
        public void run() {
            shuffleTime += currentInterval;

            if (shuffleTime < shuffleDuration) {
                int randomDigit = random.nextInt(originalNumber + 1);
                String display = MainActivityGame.getDisplayNumber(randomDigit);

                numberCounterText.setText(display);
                SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, display);
                numberCounterText.setTextColor(ContextCompat.getColor(activity, R.color.bluedark));

                float progress = (float) shuffleTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 250));

                YoYo.with(Techniques.Pulse).duration(currentInterval).playOn(numberCounterText);
                shuffleHandler.postDelayed(this, currentInterval);
            } else {
                revealFinalNumber(targetNumber, 200);
            }
        }
    }
}
