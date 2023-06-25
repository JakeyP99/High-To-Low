package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class TruthWildCardsAdapter extends WildCardsAdapter {
    public TruthWildCardsAdapter(WildCardHeadings[] truthWildCards, Context context, WildCardType mode) {
        super("TruthPrefs", truthWildCards, context, mode);
    }
    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TaskWildCardViewHolder(view);
    }

    public class TaskWildCardViewHolder extends WildCardViewHolder {
        public TaskWildCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}