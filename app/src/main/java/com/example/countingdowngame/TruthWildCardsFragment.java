package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TruthWildCardsFragment extends WildCardsFragments {
    public static final WildCardHeadings[] defaultTruthWildCards = {
            new WildCardHeadings("Truth! Have you ever had a crush on a friend's significant other?", 10, true, true),
            new WildCardHeadings("Truth! What is your most regrettable romantic rejection?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever stolen something? If yes, what and why?", 10, true, true),
            new WildCardHeadings("Truth! What is your most crazy sex story?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever engaged in a naughty video chat or phone sex?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever watched adult content while in a public setting?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a sensual massage with a happy ending?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever betrayed a friend's trust and never apologized?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever knowingly ruined someone's relationship?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever stolen something valuable from a family member?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever caused someone to lose their job?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever destroyed evidence to cover up your wrongdoing?", 10, true, true),
            new WildCardHeadings("Truth! What is your biggest fear?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever cheated on a test?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done while drunk?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever lied to your partner about something significant?", 10, true, true),
            new WildCardHeadings("Truth! What is your most embarrassing nickname?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever been caught in a compromising situation?", 10, true, true),
            new WildCardHeadings("Truth! What is the strangest food combination you enjoy?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever pretended to like a gift you actually hated?", 10, true, true),
            new WildCardHeadings("Truth! What is your most embarrassing wardrobe malfunction?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a paranormal experience?", 10, true, true),
            new WildCardHeadings("Truth! What is the most ridiculous rumor you've heard about yourself?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever snooped through someone else's belongings?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done for a dare?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever eavesdropped on someone's conversation?", 10, true, true),
            new WildCardHeadings("Truth! What is the weirdest thing you've ever eaten?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an awkward encounter with a celebrity?", 10, true, true),
            new WildCardHeadings("Truth! What is your most embarrassing childhood memory?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever told a secret you promised to keep?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've posted on social media?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever accidentally sent a text to the wrong person?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done at work?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever lied about your age?", 10, true, true),
            new WildCardHeadings("Truth! What is your most embarrassing moment in front of a crush?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever been caught in a lie?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done in public?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a wardrobe malfunction in a public place?", 10, true, true),
            new WildCardHeadings("Truth! What is your most embarrassing online dating experience?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a crush on a fictional character?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done to impress someone?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever pretended to be sick to skip school or work?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've said during a job interview?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an embarrassing encounter with a pet?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done while traveling?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever accidentally injured yourself in a ridiculous way?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing mistake you've made in a relationship?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a public speaking disaster?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done at a party?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an embarrassing encounter with a teacher?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done in front of your parents?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever laughed at an inappropriate moment?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing song you secretly love?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a public fall or stumble?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done in a class?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever accidentally insulted someone without realizing it?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done on a date?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an embarrassing encounter with a neighbor?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done while exercising?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an embarrassing moment in a public restroom?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done during a job?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had an embarrassing encounter with a crush's parent?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done in front of a crowd?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever laughed at something serious or inappropriate?", 10, true, true),
            new WildCardHeadings("Truth! What is the most embarrassing thing you've done on a video call?", 10, true, true)
};

    public TruthWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.TRUTH;

        // Declare adapter as a field in the fragment
        adapter = new TruthWildCardsAdapter(defaultTruthWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setVisibility(View.GONE);

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnToggleAll.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        btnToggleAll.setLayoutParams(layoutParams);

        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }

}
