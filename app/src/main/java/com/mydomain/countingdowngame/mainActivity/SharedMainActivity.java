package com.mydomain.countingdowngame.mainActivity;


import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.animation.Animator;
import androidx.core.animation.AnimatorListenerAdapter;
import androidx.core.animation.AnimatorSet;
import androidx.core.animation.ObjectAnimator;

import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;

public class SharedMainActivity extends ButtonUtilsActivity {

    public MainActivityDialog mainActivityDialog;

    public static void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 70;
        int minSize = 47;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityDialog = new MainActivityDialog(this, btnUtils);
    }

    protected void animateTextView(final TextView textView, @Nullable Runnable onPopEnd) {
        // Shake animation
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(textView, "translationX", -5, 5);
        shakeAnimator.setDuration(100);
        shakeAnimator.setRepeatCount(7); // Adjust the repeat count as needed
        shakeAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        // Expand animation
        ObjectAnimator expandAnimatorX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 2f);
        ObjectAnimator expandAnimatorY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 2f);
        AnimatorSet expandAnimatorSet = new AnimatorSet();
        expandAnimatorSet.playTogether(expandAnimatorX, expandAnimatorY);
        expandAnimatorSet.setDuration(1300); // Adjust the duration as needed for slower expansion

        // Pop animation
        ObjectAnimator popAnimatorX = ObjectAnimator.ofFloat(textView, "scaleX", 2f, 0f);
        ObjectAnimator popAnimatorY = ObjectAnimator.ofFloat(textView, "scaleY", 2f, 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        AnimatorSet popAnimatorSet = new AnimatorSet();
        popAnimatorSet.playTogether(popAnimatorX, popAnimatorY, alphaAnimator);
        popAnimatorSet.setDuration(1300); // Adjust the duration as needed

        shakeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                super.onAnimationEnd(animation);
                expandAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        super.onAnimationEnd(animation);
                        popAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(@NonNull Animator animation) {
                                super.onAnimationEnd(animation);
                                if (onPopEnd != null) {
                                    onPopEnd.run();
                                }
                            }
                        });
                        popAnimatorSet.start();
                    }
                });
                expandAnimatorSet.start();
            }
        });

        shakeAnimator.start();
    }


    public void animateTextViewBackAlive(final TextView textView, @Nullable Runnable onEnd) {
        // Ensure TextView starts from popped state (scale 0, alpha 0)
        textView.setScaleX(0f);
        textView.setScaleY(0f);
        textView.setAlpha(0f);

        // Reverse Pop animation (from 0 to 2)
        ObjectAnimator revPopX = ObjectAnimator.ofFloat(textView, "scaleX", 0f, 2f);
        ObjectAnimator revPopY = ObjectAnimator.ofFloat(textView, "scaleY", 0f, 2f);
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        AnimatorSet revPopSet = new AnimatorSet();
        revPopSet.playTogether(revPopX, revPopY, alphaIn);
        revPopSet.setDuration(1000);

        // Shrink back to normal (from 2 to 1)
        ObjectAnimator shrinkX = ObjectAnimator.ofFloat(textView, "scaleX", 2f, 1f);
        ObjectAnimator shrinkY = ObjectAnimator.ofFloat(textView, "scaleY", 2f, 1f);
        AnimatorSet shrinkSet = new AnimatorSet();
        shrinkSet.playTogether(shrinkX, shrinkY);
        shrinkSet.setDuration(800);

        revPopSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                super.onAnimationEnd(animation);
                shrinkSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        super.onAnimationEnd(animation);
                        if (onEnd != null) onEnd.run();
                    }
                });
                shrinkSet.start();
            }
        });

        revPopSet.start();
    }

    public static class TextSizeCalculatorPlayerName {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();
            if (charCount >= 15) {
                textSize = 30;
            } else {
                textSize = 35;
            }
            return textSize;
        }
    }

    public static class TextSizeCalculatorQuizAnswers {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();
            if (charCount <= 10) {
                textSize = 25;
            } else if (charCount <= 20) {
                textSize = 20;
            } else {
                textSize = 15;
            }
            return textSize;
        }
    }

    public static class TextSizeCalculatorQuizQuestion {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();
            if (charCount <= 75) {
                textSize = 22;
            } else if (charCount <= 110) {
                textSize = 20;
            } else {
                textSize = 25;
            }
            return textSize;
        }
    }
}
