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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.instructions.InstructionalDialogPageAdapter;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

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

    public void characterClassInformationDialog(Player player) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(
                R.layout.game_character_ability_dialog_box,
                null
        );

        ViewPager viewPager = dialogView.findViewById(R.id.abilityViewPager);
        DotsIndicator dotsIndicator = dialogView.findViewById(R.id.dotsIndicator);

        List<String> classes = player.getClassChoices();
        if (classes.isEmpty()) {
            classes = new ArrayList<>();
            classes.add(CharacterClassDescriptions.NO_CLASS);
        }

        AbilityPagerAdapter adapter = new AbilityPagerAdapter(classes, inflater);
        viewPager.setAdapter(adapter);

        if (classes.size() > 1) {
            dotsIndicator.setVisibility(View.VISIBLE);
            dotsIndicator.setViewPager(viewPager);
        } else {
            dotsIndicator.setVisibility(View.GONE);
        }

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class AbilityPagerAdapter extends PagerAdapter {
        private final List<String> classes;
        private final LayoutInflater inflater;

        public AbilityPagerAdapter(List<String> classes, LayoutInflater inflater) {
            this.classes = classes;
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return classes.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull android.view.ViewGroup container, int position) {
            View itemView = inflater.inflate(R.layout.game_character_ability_item, container, false);
            String classChoice = classes.get(position);

            TextView activeDescTv = itemView.findViewById(R.id.active_description_textview);
            TextView passiveDescTv = itemView.findViewById(R.id.passive_description_textview);
            TextView classTv = itemView.findViewById(R.id.class_textview);
            TextView activeLabelTv = itemView.findViewById(R.id.active_textview);
            TextView passiveLabelTv = itemView.findViewById(R.id.passive_textview);

            classTv.setText(classChoice);
            activeDescTv.setText(getClassActiveDescription(classChoice));
            passiveDescTv.setText(getClassPassiveDescription(classChoice));

            boolean isNoClass = CharacterClassDescriptions.NO_CLASS.equals(classChoice);
            if (isNoClass) {
                passiveDescTv.setVisibility(View.GONE);
                activeLabelTv.setVisibility(View.GONE);
                passiveLabelTv.setVisibility(View.GONE);
            }

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull android.view.ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    protected String getClassActiveDescription(String classChoice) {
        if (classChoice == null) return "";
        switch (classChoice) {
            case CharacterClassDescriptions.ARCHER:
                return CharacterClassDescriptions.archerActiveDescription;
            case CharacterClassDescriptions.WITCH:
                return CharacterClassDescriptions.witchActiveDescription;
            case CharacterClassDescriptions.SCIENTIST:
                return CharacterClassDescriptions.scientistActiveDescription;
            case CharacterClassDescriptions.SOLDIER:
                return CharacterClassDescriptions.soldierActiveDescription;
            case CharacterClassDescriptions.QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianActiveDescription;
            case CharacterClassDescriptions.SURVIVOR:
                return CharacterClassDescriptions.survivorActiveDescription;
            case CharacterClassDescriptions.ANGRY_JIM:
                return CharacterClassDescriptions.angryJimActiveDescription;
            case CharacterClassDescriptions.GOBLIN:
                return CharacterClassDescriptions.goblinActiveDescription;
            case CharacterClassDescriptions.GAMBLER:
                return CharacterClassDescriptions.gamblerActiveDescription;
            case CharacterClassDescriptions.TROLL:
                return CharacterClassDescriptions.trollActiveDescription;
            default:
                return CharacterClassDescriptions.noClassDescription;
        }
    }

    protected String getClassPassiveDescription(String classChoice) {
        if (classChoice == null) return "";
        switch (classChoice) {
            case CharacterClassDescriptions.ARCHER:
                return CharacterClassDescriptions.archerPassiveDescription;
            case CharacterClassDescriptions.WITCH:
                return CharacterClassDescriptions.witchPassiveDescription;
            case CharacterClassDescriptions.SCIENTIST:
                return CharacterClassDescriptions.scientistPassiveDescription;
            case CharacterClassDescriptions.SOLDIER:
                return CharacterClassDescriptions.soldierPassiveDescription;
            case CharacterClassDescriptions.QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianPassiveDescription;
            case CharacterClassDescriptions.SURVIVOR:
                return CharacterClassDescriptions.survivorPassiveDescription;
            case CharacterClassDescriptions.ANGRY_JIM:
                return CharacterClassDescriptions.angryJimPassiveDescription;
            case CharacterClassDescriptions.GOBLIN:
                return CharacterClassDescriptions.goblinPassiveDescription;
            case CharacterClassDescriptions.GAMBLER:
                return CharacterClassDescriptions.gamblerPassiveDescription;
            case CharacterClassDescriptions.TROLL:
                return CharacterClassDescriptions.trollPassiveDescription;
            default:
                return "";
        }
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
             if (charCount <= 70) {
                textSize = 25;
            } else {
                textSize = 20;
            }
            return textSize;
        }
    }

    public static class TextSizeCalculatorPlayerName {
        public static int calculateTextSizeBasedOnCharacterCount(String text) {
            int textSize;
            int charCount = text.length();
            if (charCount >= 15) {
                textSize = 30;
            }
            else {
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
}
