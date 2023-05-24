package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;

public class Instructions extends ButtonUtilsActivity {
    private final List<String> instructions = Arrays.asList(

            "Welcome to the exhilarating game of Drinking Countdown!\n\nGet ready to embark on a thrilling journey filled with excitement, and suspense.",
            "Firstly, prepare yourself a tantalizing drink because, in this game, the loser takes a delicious shot!",
            "Gather your friends and decide how many players will be joining in on the fun.\n\nNow this is the fun part, you get to make your character! You can either take a photo of yourself, or if you're having a bad hair day, you can draw your character. \n\nDon't forget to give your creation a name!",
            "Next choose a starting number that sets the pace for the game. \n\nRemember, the lower the number, the faster the game!",
            "Decide who is player 1. It can be anyone, but probably not a dog. \n\nAlthough, who am I to judge!",
            "Let me introduce you to the two main buttons. \n\nFirst, there's the 'Generate' button, which will randomly choose a number between 0 and the one you've selected. \n\nSecond, we have the 'Wildcard' button. Here's where things get wild! With a 50/50 chance, it can either work in your favor or throw you off balance. Feel free to use it, or not, this button does not need to be clicked ever.\n\n Hint - You can change the wildcards, and the probabilities in the settings!",
            "As the game progresses, players strive to avoid reaching the dreaded number zero. \n\nOnce a player hits that mark, they're out of the game and must face the consequence of taking that deliciously sexy shot!",
            "Tada! You've completed a round of Drinking Countdown! If the thrill of the game hasn't quenched your thirst for excitement, why not play it again, and again, and again. \n\n Literally do whatever you want, you're a free person so live your gorgeous life.",
            "Within the settings you will find options to make it one screen, or make it split screen. \n\nThey both produce the same game, but split screen makes it easier if you are sitting across from someone.",
            "As I said before, you can also change the wildcards and edit them/add more. A hint about the probability is, the higher the number, the more likely it will occur.",
            "Thanks, and I hope you enjoy my game!");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c1_instructions_layout);

        final Button btnBack = findViewById(R.id.buttonReturn);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        btnUtils.setButton(btnBack, this::onBackPressed);

        InstructionPagerAdapter adapter = new InstructionPagerAdapter(instructions);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                progressBar.setProgress(position + 1);
            }
        });

        progressBar.setMax(instructions.size());
        progressBar.setProgress(1);
    }
}
