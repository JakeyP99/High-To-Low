package com.mydomain.countingdowngame.instructions;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.viewpager2.widget.ViewPager2;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class InstructionsToPlay extends ButtonUtilsActivity {
    private final List<InstructionStep> instructionSteps = Arrays.asList(
            new InstructionStep("Welcome Friend!", R.string.instruction_welcome, R.drawable.books),
            new InstructionStep("Aim of the Game!", R.string.instruction_aim, R.drawable.shots),
            new InstructionStep("Create Your Players!", R.string.instruction_create_players, R.drawable.selection),
            new InstructionStep("Choose your class!", R.string.instruction_choose_class, R.drawable.witch),
            new InstructionStep("Choose A Starting Number!", R.string.instruction_choose_starting_number, R.drawable.dice),
            new InstructionStep("Generate Button!", R.string.instruction_generate_button, R.drawable.slotmachine),
            new InstructionStep("Wildcard Button!", R.string.instruction_wildcard_button, R.drawable.playingcards),
            new InstructionStep("Wildcard Choices: Quizzes!", R.string.instruction_wildcard_choices_quiz, R.drawable.books),
            new InstructionStep("Wildcard Choices: Tasks!", R.string.instruction_wildcard_choices_task, R.drawable.trading),
            new InstructionStep("Wildcard Choices: Truths!", R.string.instruction_wildcard_choices_truth, R.drawable.toast),
            new InstructionStep("Catastrophes!", R.string.instruction_catastrophe, R.drawable.tornado),
            new InstructionStep("The End!", R.string.instruction_the_end, R.drawable.drink),
            new InstructionStep("Settings!", R.string.instruction_settings, R.drawable.swissarmyknife),
            new InstructionStep("Thank you!", R.string.instruction_thanks, R.drawable.toast)
    );
    GifImageView muteGif;
    GifImageView soundGif;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
    }

    public void setLayout() {
        setContentView(R.layout.instruction_main_activity);
        Button btnNext = findViewById(R.id.buttonNext);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        DotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        InstructionPageAdapter adapter = new InstructionPageAdapter(instructionSteps);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);

        viewPager.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1.0f - absPos);
            float scale = 0.8f + (1.0f - 0.8f) * (1.0f - absPos);
            page.setScaleX(scale);
            page.setScaleY(scale);
        });

        setupButtonControls(btnNext, viewPager);
        startPulsingAnimation(btnNext);
    }

    private void startPulsingAnimation(View view) {
        ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.05f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.05f)
        );
        pulse.setDuration(800);
        pulse.setRepeatCount(ObjectAnimator.INFINITE);
        pulse.setRepeatMode(ObjectAnimator.REVERSE);
        pulse.start();
    }

    public void setupButtonControls(Button btnNext, ViewPager2 viewPager) {
        btnUtils.setButton(btnNext, () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < instructionSteps.size() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            } else {
                gotoHomeScreen();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == instructionSteps.size() - 1) {
                    btnNext.setText(R.string.button_lets_play);
                } else {
                    btnNext.setText(R.string.button_next);
                }
            }
        });
    }
}
