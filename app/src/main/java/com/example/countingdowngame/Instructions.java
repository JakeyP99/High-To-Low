package com.example.countingdowngame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;

public class Instructions extends AppCompatActivity {
    private MediaPlayer bop;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_layout);

        final Button btnBack = findViewById(R.id.nextButton);
        ButtonUtils.setButton(btnBack,null, HomeScreen.class, this, null);


        ViewPager viewPager = findViewById(R.id.viewPager);
        List<String> instructions = Arrays.asList(
                "Welcome to drinking countdown! In summary, you choose a number, that number will go down, and the aim of the game is not to be the person who randomly hits the number 0.",
                "Prepare yourself a beverage for the loser to drink \n\n I like to prepare a nice tasty shot.",
                "Choose how many players there will be, and choose your starting number. \n\n Hint ~ the lower the number the quicker the game.",
                "Decide who is player 1. It can be anyone, but probably not a dog.",
                "There are three buttons you can click: \n\n 1) Generate (this will choose a number between 0 and the number of your choosing). \n\n 2) Skip (I think you know what this is you silly goose). \n\n 3) Wildcard (Dude it's a wildcard, I am sure you know what that is). \n\n Watch out though, you only get one skip and one wild card per game (really spooky)!",
                "Pass the phone to player 2, they then can either generate a number, or they can skip their turn.",
                "Once a player gets the number 0 then they are out, and they lose! \n\n They can then take that sexy yummy shot.",
                "Tada, you're done! \n\n If that game didn't bore you to death then play it again, and again, and again, literally do whatever you want, you're a free person so live your gorgeous life.");

        InstructionPagerAdapter adapter = new InstructionPagerAdapter(instructions);
        viewPager.setAdapter(adapter);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(instructions.size());
        progressBar.setProgress(1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                progressBar.setProgress(position + 1);
            }
        });
    }

    public static class InstructionPagerAdapter extends PagerAdapter {
        private final List<String> instructions;

        public InstructionPagerAdapter(List<String> instructions) {
            this.instructions = instructions;
        }

        @Override
        public int getCount() {
            return instructions.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.instructions_layout, container, false);
            TextView textView = view.findViewById(R.id.info_text);
            textView.setText(instructions.get(position));
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}

