package com.example.countingdowngame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TruthWildCardsFragment extends Fragment {
    Settings_WildCard_Probabilities[] truthWildCards = {
            new Settings_WildCard_Probabilities("Truth! Have you ever had a crush on a friend's significant other?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! What is your most regrettable romantic rejection?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever stolen something? If yes, what and why?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! What is your most crazy sex story?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever engaged in a naughty video chat or phone sex?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever watched adult content while in a public setting?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever had a sensual massage with a happy ending?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever betrayed a friend's trust and never apologized?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever knowingly ruined someone's relationship?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever stolen something valuable from a family member?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever caused someone to lose their job?", 10, true, true),
            new Settings_WildCard_Probabilities("Truth! Have you ever destroyed evidence to cover up your wrongdoing?", 10, true, true),
    };

    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Settings_WildCard_Mode mode = Settings_WildCard_Mode.DELETABLE;

        TruthWildCardsAdapter adapter = new TruthWildCardsAdapter(truthWildCards, requireContext(), mode);
        recyclerView.setAdapter(adapter);

        return view;
    }
}