package com.example.countingdowngame.mainActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.instructions.InstructionalDialogPageAdapter;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtils;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivityDialog {

    private final AppCompatActivity activity;
    private final ButtonUtils btnUtils;
    private final List<AlertDialog> dialogQueue = new ArrayList<>();

    public MainActivityDialog(AppCompatActivity activity, ButtonUtils btnUtils) {
        this.activity = activity;
        this.btnUtils = btnUtils;
    }

    public void showDialog(String message, int layoutId, int textViewId) {
        showDialog(message, layoutId, textViewId, null);
    }

    public void showDialog(String message, int layoutId, int textViewId, Runnable onDismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);
        TextView dialogBoxTextView = dialogView.findViewById(textViewId);

        if (dialogBoxTextView != null) {
            dialogBoxTextView.setText(message);
        }

        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(d -> {
            dialogQueue.remove(dialog);

            if (!dialogQueue.isEmpty()) {
                dialogQueue.get(0).show();
            }

            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        // Add special handling for immediate dismissal on click (like in Roulette)
        dialogView.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Set transparent window like in Roulette
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogQueue.add(dialog);
        if (dialogQueue.size() == 1) {
            dialog.show();
        }
    }

    public void showCombinedPassivesDialog(CharSequence content, Runnable onDismiss) {
        showClassDialog(
                "Messages:",
                content,
                R.layout.game_combined_passives_dialog,
                R.id.title_textview,
                R.id.content_textview,
                onDismiss
        );
    }

    public void showClassDialog(String title, CharSequence description, int layoutId,
                                int classTextViewId, int descriptionTextViewId,
                                Runnable onDismiss) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);

        TextView classTextView = dialogView.findViewById(classTextViewId);
        TextView descriptionTextView = dialogView.findViewById(descriptionTextViewId);

        if (classTextView != null) {
            classTextView.setText(title);
        }

        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }

        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(d -> {
            dialogQueue.remove(dialog);

            if (!dialogQueue.isEmpty()) {
                dialogQueue.get(0).show();
            }

            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogQueue.add(dialog);
        if (dialogQueue.size() == 1) {
            dialog.show();
        }
    }

    public void showClassAbilityDialog(String message) {
        showClassAbilityDialog(message, null);
    }

    public void showClassAbilityDialog(String message, Runnable onDismiss) {

        String title;
        String description = "";

        if (message.contains("\n\n")) {
            title = message.substring(0, message.indexOf("\n\n"));
            description = message.substring(message.indexOf("\n\n") + 2);
        } else {
            title = message;
        }

        showClassDialog(
                title,
                description,
                R.layout.game_use_class_ability_dialog_box,
                R.id.class_textview,
                R.id.description_textview,
                onDismiss
        );
    }

    public void showGameDialog(String message) {
        showDialog(message, R.layout.game_main_dialog_box, R.id.dialogbox_textview);
    }

    public void showGameDialog(String message, Runnable onDismiss) {
        showDialog(message, R.layout.game_main_dialog_box, R.id.dialogbox_textview, onDismiss);
    }

    public void characterClassInformationDialog(Player player) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(
                R.layout.game_character_ability_dialog_box,
                null
        );

        ViewPager viewPager = dialogView.findViewById(R.id.abilityViewPager);
        DotsIndicator dotsIndicator = dialogView.findViewById(R.id.dotsIndicator);

        List<String> classes = new ArrayList<>(player.getClassChoices());
        classes.remove(CharacterClassDescriptions.NO_CLASS);

        if (classes.isEmpty()) {
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

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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

    public void showInstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.instruction_dialog, null);
        ViewPager viewPager = dialogView.findViewById(R.id.viewpager);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_bar);
        Button btnNext = dialogView.findViewById(R.id.buttonNext);

        List<Integer> layoutResIds = new ArrayList<>();
        layoutResIds.add(R.layout.instruction_dialog_1);
        layoutResIds.add(R.layout.instruction_dialog_2);
        layoutResIds.add(R.layout.instruction_dialog_3);
        layoutResIds.add(R.layout.instruction_dialog_4);

        InstructionalDialogPageAdapter adapter = new InstructionalDialogPageAdapter(layoutResIds);
        viewPager.setAdapter(adapter);

        setupProgress(viewPager, progressBar, layoutResIds);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setupButtonControls(btnNext, viewPager, dialog);
        dialog.show();
    }

    private void setupProgress(ViewPager viewPager, ProgressBar progressBar, List<Integer> layoutResIds) {
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

    private void setupButtonControls(Button btnNext, ViewPager viewPager, AlertDialog dialog) {
        btnUtils.setButtonWithoutEffects(btnNext, () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            } else {
                dialog.dismiss();
            }
        });
    }
}
