package com.example.countingdowngame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private TruthWildCardsAdapter adapter; // Declare adapter as a field in the fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Settings_WildCard_Mode mode = Settings_WildCard_Mode.DELETABLE;

        adapter = new TruthWildCardsAdapter(truthWildCards, requireContext(), mode); // Assign the adapter to the field

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> {
            addNewWildCard();
        });

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());

        return view;
    }


    private void toggleAllWildCards() {
        boolean allEnabled = adapter.areAllEnabled();

        for (Settings_WildCard_Probabilities wildcard : adapter.getWildCards()) {
            wildcard.setEnabled(!allEnabled);
        }

        adapter.notifyDataSetChanged();
    }


    private void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Wildcard");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textInput = new EditText(requireContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Wildcard Title");
        layout.addView(textInput);

        final EditText probabilityInput = new EditText(requireContext());
        probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        probabilityInput.setHint("Probability (0-9999)");
        layout.addView(probabilityInput);

        builder.setView(layout);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("OK", (dialog, which) -> {
            int probability;
            try {
                probability = Integer.parseInt(probabilityInput.getText().toString());
            } catch (NumberFormatException e) {
                probability = 10; // Invalid input, set to a default value
            }

            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add "Quiz!" to the start of the wildcard text
            String wildcardText = "Task! " + text;

            Settings_WildCard_Probabilities newWildCard = new Settings_WildCard_Probabilities(wildcardText, probability, true, true);

            // Create a new array with increased size
            Settings_WildCard_Probabilities[] newQuizWildCards = new Settings_WildCard_Probabilities[truthWildCards.length + 1];
            System.arraycopy(truthWildCards, 0, newQuizWildCards, 0, truthWildCards.length);
            newQuizWildCards[truthWildCards.length] = newWildCard;

            // Update the quizWildCards array with the new array
            truthWildCards = newQuizWildCards;

            adapter.setWildCards(truthWildCards); // Update the adapter's dataset with the new array
            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
        });

        builder.show();
    }
}
