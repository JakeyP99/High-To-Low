package com.example.countingdowngame.instructions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.utils.DepthPageTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

;

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
        setLayout();
    }

    public void setLayout() {
        setContentView(R.layout.c1_instructions_layout);

        Button btnNext = findViewById(R.id.buttonNext);
        ViewPager viewPager = findViewById(R.id.viewpager);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        setupButtonControls(btnNext, viewPager);
        setupProgress(viewPager, progressBar);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        InstructionPageAdapter adapter = new InstructionPageAdapter(instructions);
        DepthPageTransformer pageTransformer = new DepthPageTransformer();
        viewPager.setPageTransformer(true, pageTransformer);
        viewPager.setAdapter(adapter);
    }

    public void setupProgress(ViewPager viewPager, ProgressBar progressBar) {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                progressBar.setProgress(position + 1);
            }
        });
        progressBar.setMax(instructions.size());
        progressBar.setProgress(1);

    }

    public void setupButtonControls(Button btnNext, ViewPager viewPager) {
        btnUtils.setButtonWithoutEffects(btnNext, () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            } else {
                gotoHomeScreen();
            }
        });
    }


}
