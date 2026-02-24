package com.example.countingdowngame.mainActivity;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleSurvivorPassive;

import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;

import java.util.Random;

public class MainActivityNumberGenerator {

    private final MainActivityGame activity;
    private final TextView numberCounterText;
    private final Handler shuffleHandler;

    public MainActivityNumberGenerator(MainActivityGame activity, TextView numberCounterText) {
        this.activity = activity;
        this.numberCounterText = numberCounterText;
        this.shuffleHandler = new Handler();
    }

    public void startNumberShuffleAnimation() {
        int originalNumber = Game.getInstance().getCurrentNumber();
        int targetNumber = Game.getInstance().nextNumber(); 
        
        final int shuffleDuration = 1500;
        int initialShuffleInterval = originalNumber >= 1000 ? 30 : 50;
        final Random random = new Random();

        shuffleHandler.postDelayed(new ShuffleRunnable(random, originalNumber, targetNumber, shuffleDuration, initialShuffleInterval), initialShuffleInterval);
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
                numberCounterText.setText(String.valueOf(randomDigit));

                float progress = (float) shuffleTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 250));

                YoYo.with(Techniques.Pulse)
                        .duration(currentInterval)
                        .playOn(numberCounterText);

                shuffleHandler.postDelayed(this, currentInterval);
            } else {
                // LAST STEP: display the actual target number and finalize
                revealFinalNumber();
            }
        }

        private void revealFinalNumber() {
            int previousNumber = Game.getInstance().getPreviousNumber();

            Game.getInstance().recordTurn(currentPlayer, targetNumber);

            // Set final number content and ensure correct size
            numberCounterText.setText(String.valueOf(targetNumber));
            SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(targetNumber));
            
            // Update color based on the final number
            MainActivityGame.updateNumberColor(targetNumber);

//            // Clear any lingering pulse and do final reveal animation
//            YoYo.with(Techniques.BounceIn)
//                    .duration(800)
//                    .playOn(numberCounterText);

            if ((targetNumber <= 2 && targetNumber > 0 && previousNumber <= 2) &&
                    (SURVIVOR.equals(currentPlayer.getClassChoice()) || ANGRY_JIM.equals(currentPlayer.getClassChoice()))) {
                handleSurvivorPassive(currentPlayer);
            }

            activity.renderCurrentNumber(targetNumber, activity::gotoGameEnd, numberCounterText);

            if (targetNumber != 0) {
                activity.enableButtons();
            }
        }
    }
}
