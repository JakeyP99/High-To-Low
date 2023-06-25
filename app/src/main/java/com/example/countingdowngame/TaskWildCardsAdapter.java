package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class TaskWildCardsAdapter extends WildCardsAdapter {
    private final Context mContext;

    public TaskWildCardsAdapter(WildCardHeadings[] taskWildCards, Context context, WildCardType mode) {
        super("TaskPrefs", taskWildCards, context, mode);
        this.mContext = context;
        this.mMode = mode;
        loadWildCardProbabilitiesFromStorage();
    }

    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TaskWildCardViewHolder(view);
    }

    public WildCardHeadings[] getWildCards() {
        return wildCards;
    }
    public void setWildCards(WildCardHeadings[] wildCards) {
        this.wildCards = wildCards;
    }
    public boolean areAllEnabled() {
        for (WildCardHeadings wildcard : wildCards) {
            if (!wildcard.isEnabled()) {
                return false; // If any wildcard is disabled, return false
            }
        }
        return true; // All wildcards are enabled
    }
    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        WildCardHeadings wildcard = wildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return wildCards.length;
    }

    public class TaskWildCardViewHolder extends WildCardViewHolder {

        public TaskWildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
            switchEnabled = itemView.findViewById(R.id.switch_wildcard);

        }

    }
}
