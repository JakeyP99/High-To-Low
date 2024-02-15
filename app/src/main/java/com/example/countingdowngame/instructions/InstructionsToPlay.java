package com.example.countingdowngame.instructions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.utils.DepthPageTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class InstructionsToPlay extends ButtonUtilsActivity {
    private final List<Integer> instructions = Arrays.asList(
            R.string.instruction_welcome,
            R.string.instruction_aim,
            R.string.instruction_create_players,
            R.string.instruction_choose_class,
            R.string.instruction_choose_starting_number,
            R.string.instruction_generate_button,
            R.string.instruction_wildcard_button,
            R.string.instruction_wildcard_choices_quiz,
            R.string.instruction_wildcard_choices_task,
            R.string.instruction_wildcard_choices_truth,
            R.string.instruction_wildcard_choices_extras,
            R.string.instruction_the_end,
            R.string.instruction_hurray,
            R.string.instruction_settings,
            R.string.instruction_tips_tricks,
            R.string.instruction_thanks
    );

    private AudioManager audioManager;
    GifImageView muteGif;
    GifImageView soundGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        setupView();

        audioManager = AudioManager.getInstance();
        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods

        // Initialize GifImageViews after setting the content view
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        // Restore mute/sound state
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().updateMuteSoundButtons(isMuted, audioManager, muteGif, soundGif);

        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods
        setupAudioManager();


        setupAudioManagerAndButtonControls();
    }

    private void setupAudioManager() {
        muteGif.setOnClickListener(view -> {

            saveMuteSoundState(true); // Save the mute state
            AudioManager.getInstance().updateMuteSoundButtons(false, audioManager, muteGif, soundGif); // Update the visibility of buttons
        });

        // Set onClickListener for sound button
        soundGif.setOnClickListener(view -> {

            saveMuteSoundState(false); // Save the sound state
            AudioManager.getInstance().updateMuteSoundButtons(true, audioManager, muteGif, soundGif); // Update the visibility of buttons
        });

    }

    private void saveMuteSoundState(boolean isMuted) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isMuted", isMuted);
        editor.apply();
    }

    // Retrieve the mute/sound state
    private boolean getMuteSoundState() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isMuted", false); // Default to false if not found
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

    private void setupView() {
        if (GeneralSettingsLocalStore.fromContext(this).isMuted()) {
            muteGif.setVisibility(View.VISIBLE);
        } else {
            soundGif.setVisibility(View.VISIBLE);
        }
    }

    private void setupAudioManagerAndButtonControls() {
        Button btnQuickPlay = findViewById(R.id.quickplay);
        Button btnInstructions = findViewById(R.id.button_Instructions);
        Button btnSettings = findViewById(R.id.button_Settings);

        // Set onClickListener for mute button

        btnUtils.setButton(btnQuickPlay, this::gotoPlayerNumberChoice);
        btnUtils.setButton(btnInstructions, this::gotoInstructions);
        btnUtils.setButton(btnSettings, this::gotoSettings);
    }
}
