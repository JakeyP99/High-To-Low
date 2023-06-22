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

public class ExtrasWildCardsFragment extends Fragment {
    WildCardHeadings[] extrasWildCards = {
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true)
    };

    private ExtrasWildCardsAdapter adapter; // Declare adapter as a field in the fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.DELETABLE;

        adapter = new ExtrasWildCardsAdapter(extrasWildCards, requireContext(), mode); // Assign the adapter to the field

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

        for (WildCardHeadings wildcard : adapter.getWildCards()) {
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

            String inputText = probabilityInput.getText().toString().trim();
            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputText.length() > 4) {
                Toast.makeText(getContext(), "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() <=0 ) {
                Toast.makeText(getContext(), "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() > 100 ) {
                Toast.makeText(getContext(), "Sorry, way too big of a wildcard boss man, limited to 100 characters.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Add "Quiz!" to the start of the wildcard text
            String wildcardText = "Quiz! " + text;

            WildCardHeadings newWildCard = new WildCardHeadings(wildcardText, probability, true, true);

            // Create a new array with increased size
            WildCardHeadings[] newQuizWildCards = new WildCardHeadings[extrasWildCards.length + 1];
            System.arraycopy(extrasWildCards, 0, newQuizWildCards, 0, extrasWildCards.length);
            newQuizWildCards[extrasWildCards.length] = newWildCard;

            // Update the quizWildCards array with the new array
            extrasWildCards = newQuizWildCards;

            adapter.setWildCards(extrasWildCards); // Update the adapter's dataset with the new array
            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
        });

        builder.show();
    }
}

