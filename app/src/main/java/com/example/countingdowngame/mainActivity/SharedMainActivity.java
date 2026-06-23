package com.example.countingdowngame.mainActivity;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.animation.Animator;
import androidx.core.animation.AnimatorListenerAdapter;
import androidx.core.animation.AnimatorSet;
import androidx.core.animation.ObjectAnimator;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.instructions.InstructionalDialogPageAdapter;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharedMainActivity extends ButtonUtilsActivity {

    public static void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 70;
        int minSize = 47;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }


    public static void reverseTurnOrder() {
        Game game = Game.getInstance();
        game.setReverseOrder(!game.isReverseOrder());
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


    protected void animateTextViewBackAlive(final TextView textView, @Nullable Runnable onEnd) {
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

    public void characterClassInformationDialog(String currentPlayerClassChoice, String activeDescription, String passiveDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        int characterClassTextLength = activeDescription.length() + passiveDescription.length();
        Log.d(TAG, "characterClassInformationDialog: CharacterClassTextLength: " + characterClassTextLength);

        View dialogView = inflater.inflate(R.layout.game_character_ability_dialog_box, null);
        TextView activeAbilityDescriptionTextView = dialogView.findViewById(R.id.active_description_textview);
        TextView passiveAbilityDescriptionTextView = dialogView.findViewById(R.id.passive_description_textview);
        TextView currentPlayerClassTextView = dialogView.findViewById(R.id.class_textview);
        TextView activeTextview = dialogView.findViewById(R.id.active_textview);
        TextView passiveTextView = dialogView.findViewById(R.id.passive_textview);


        if (Objects.equals(currentPlayerClassChoice, "No Class")) {
            passiveAbilityDescriptionTextView.setVisibility(View.GONE);
            activeTextview.setVisibility(View.GONE);
            passiveTextView.setVisibility(View.GONE);
        }
        // Set text and text size separately
        activeAbilityDescriptionTextView.setText(activeDescription);
        passiveAbilityDescriptionTextView.setText(passiveDescription);
        currentPlayerClassTextView.setText(currentPlayerClassChoice);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Instructional overlay ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public void showInstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.instruction_dialog, null);
        ViewPager viewPager = dialogView.findViewById(R.id.viewpager);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_bar);
        Button btnNext = dialogView.findViewById(R.id.buttonNext);

        // Generate instructional pages
        List<Integer> layoutResIds = new ArrayList<>();
        layoutResIds.add(R.layout.instruction_dialog_1); // Replace with your layout resource IDs
        layoutResIds.add(R.layout.instruction_dialog_2); // Replace with your layout resource IDs
        layoutResIds.add(R.layout.instruction_dialog_3); // Replace with your layout resource IDs
        layoutResIds.add(R.layout.instruction_dialog_4); // Replace with your layout resource IDs

        // Create adapter and set it to the ViewPager
        InstructionalDialogPageAdapter adapter = new InstructionalDialogPageAdapter(layoutResIds);
        viewPager.setAdapter(adapter);

        // Setup progress bar
        setupProgress(viewPager, progressBar, layoutResIds);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        setupButtonControls(btnNext, viewPager, dialog);
        dialog.show();
    }

    public void setupProgress(ViewPager viewPager, ProgressBar progressBar, List<Integer> layoutResIds) {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                progressBar.setMax(layoutResIds.size());
                progressBar.setProgress(position + 1);
            }
        });
        progressBar.setMax(layoutResIds.size());
        progressBar.setProgress(1);
    }

    public void setupButtonControls(Button btnNext, ViewPager viewPager, AlertDialog dialog) {
        btnUtils.setButtonWithoutEffects(btnNext, () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            } else {
                dialog.dismiss();
            }
        });
    }
    public static class TextSizeCalculator {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();
            if (charCount <= 30) {
                textSize = 33;
            } else if (charCount <= 70) {
                textSize = 28;
            } else {
                textSize = 25;
            }
            return textSize;
        }
    }
}
