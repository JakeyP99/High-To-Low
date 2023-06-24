package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

public class ExtrasWildCardsFragment extends Fragment {
    WildCardHeadings[] extrasWildCards = {
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true),
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true)

    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.DELETABLE;
        ExtrasWildCardsAdapter adapter = new ExtrasWildCardsAdapter(extrasWildCards, requireContext(), mode);
        recyclerView.setAdapter(adapter);

        loadWildCardsFromPreferences();
        return view;
    }

    private void loadWildCardsFromPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE);
        String wildCardsJson = sharedPreferences.getString("extrasWildCardsKey", null);
        if (wildCardsJson != null) {
            new Gson().fromJson(wildCardsJson, WildCardHeadings[].class);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        saveWildCardsToPreferences();
    }

    private void saveWildCardsToPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String wildCardsJson = new Gson().toJson(extrasWildCards);
        editor.putString("extrasWildCardsKey", wildCardsJson);
        editor.apply();
    }
}