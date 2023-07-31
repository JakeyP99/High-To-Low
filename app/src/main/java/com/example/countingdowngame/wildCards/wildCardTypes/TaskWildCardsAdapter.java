package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.WildCardHeadings;
import com.example.countingdowngame.wildCards.WildCardType;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardsAdapter;

public class TaskWildCardsAdapter extends WildCardsAdapter {

    public TaskWildCardsAdapter(WildCardHeadings[] taskWildCards, Context context, WildCardType mode) {
        super("TaskPrefs", taskWildCards, context, mode);
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
