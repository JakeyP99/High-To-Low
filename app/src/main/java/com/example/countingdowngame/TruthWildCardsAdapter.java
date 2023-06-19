package com.example.countingdowngame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TruthWildCardsAdapter extends RecyclerView.Adapter<TruthWildCardsAdapter.TruthWildCardViewHolder> {
    private Settings_WildCard_Probabilities[] truthWildCards;

    public TruthWildCardsAdapter(Settings_WildCard_Probabilities[] truthWildCards) {
        this.truthWildCards = truthWildCards;
    }

    @Override
    public TruthWildCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TruthWildCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruthWildCardViewHolder holder, int position) {
        Settings_WildCard_Probabilities wildcard = truthWildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return truthWildCards.length;
    }

    public static class TruthWildCardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewProbabilities;


        public TruthWildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);

            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
        }

        public void bind(Settings_WildCard_Probabilities wildcard) {
            textViewTitle.setText(wildcard.getText());
            textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
        }
    }
}
