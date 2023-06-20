package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class QuizWildCardsAdapter extends WildCardsAdapter {
    public QuizWildCardsAdapter(Settings_WildCard_Probabilities[] taskWildCards, Context context, Settings_WildCard_Mode mode) {
        super(taskWildCards, context, mode);
    }

    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TaskWildCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        Settings_WildCard_Probabilities wildcard = wildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return wildCards.length;
    }

    public class TaskWildCardViewHolder extends WildCardViewHolder {
        public TaskWildCardViewHolder(View itemView) {
            super(itemView);
            // Initialize any additional views specific to the TaskWildCardViewHolder if needed
        }

        @Override
        public void bind(Settings_WildCard_Probabilities wildcard) {
            textViewTitle.setText(wildcard.getText());
            textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
        }

    }

    // Additional TaskWildCardsAdapter specific methods and functionality here
}
