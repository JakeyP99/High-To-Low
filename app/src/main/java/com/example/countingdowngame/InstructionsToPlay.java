package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InstructionsToPlay extends ButtonUtilsActivity {
    private final List<Integer> instructions = Arrays.asList(
            R.string.instruction_welcome,
            R.string.instruction_aim,
            R.string.instruction_choose_drink,
            R.string.instruction_create_players,
            R.string.instruction_choose_starting_number,
            R.string.instruction_generate_button,
            R.string.instruction_wildcard_button,
            R.string.instruction_the_end,
            R.string.instruction_hurray,
            R.string.instruction_settings,
            R.string.instruction_tips_tricks,
            R.string.instruction_thanks
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c1_instructions_layout);

        final Button btnNext = findViewById(R.id.buttonNext);
        final ViewPager viewPager = findViewById(R.id.viewpager);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        btnUtils.setButtonWithoutEffects(btnNext, () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }else
            {
                gotoHomeScreen();
            }
        });

        InstructionPageAdapter adapter = new InstructionPageAdapter(instructions);

        DepthPageTransformer pageTransformer = new DepthPageTransformer();
        viewPager.setPageTransformer(true, pageTransformer);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                progressBar.setProgress(position + 1);
                if (position == adapter.getCount() - 1) {
                    btnNext.setText("Finish"); // Update button text on the last instruction
                } else {
                    btnNext.setText("Next"); // Reset button text on other instructions
                }
            }
        });

        progressBar.setMax(instructions.size());
        progressBar.setProgress(1);
    }}
