package com.example.countingdowngame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;

public class Instructions extends ButtonUtilsActivity {
    private final List<String> instructions = Arrays.asList(
            "Welcome to drinking countdown! In summary, you choose a number, that number will go down, and the aim of the game is not to be the person who randomly hits the number 0.",
            "Prepare yourself a beverage for the loser to drink \n\n I like to prepare a nice tasty shot.",
            "Choose how many players there will be, and choose your starting number. \n\n Hint ~ the lower the number the quicker the game.",
            "Decide who is player 1. It can be anyone, but probably not a dog.",
            "There are two main buttons you can click: \n\n 1) Generate (this will choose a number between 0 and the number of your choosing).\n\n 2) Wildcard (There is a 50/50 chance it will be good for you, or bad for you, you don't need to click this button at all).",
            "Pass the phone to player 2, they then can either generate a number, or they can skip their turn.",
            "Once a player gets the number 0 then they are out, and they lose! \n\n They can then take that sexy yummy shot.",
            "Tada, you're done! \n\n If that game didn't bore you to death then play it again, and again, and again, literally do whatever you want, you're a free person so live your gorgeous life.");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_layout);

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
