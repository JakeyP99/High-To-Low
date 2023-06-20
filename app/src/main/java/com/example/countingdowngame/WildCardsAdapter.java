package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public abstract class WildCardsAdapter extends RecyclerView.Adapter<WildCardsAdapter.WildCardViewHolder> {
    protected Settings_WildCard_Probabilities[] wildCards;
    protected Context mContext;
    protected Settings_WildCard_Mode mMode;

    public WildCardsAdapter(Settings_WildCard_Probabilities[] wildCards, Context context, Settings_WildCard_Mode mode) {
        this.wildCards = wildCards;
        this.mContext = context;
        this.mMode = mode;
        loadWildCardProbabilitiesFromStorage(mode);
    }

    // Common methods and functionality here

    public abstract class WildCardViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewTitle;
        protected TextView textViewProbabilities;
        protected Button editButton;
        protected Switch switchEnabled;

        public WildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
            switchEnabled = itemView.findViewById(R.id.switch_wildcard);
        }

        public abstract void bind(Settings_WildCard_Probabilities wildcard);
    }

    // Common methods and functionality here

    private Settings_WildCard_Probabilities[] loadWildCardProbabilitiesFromStorage(Settings_WildCard_Mode mode) {
        // Common implementation
        return new Settings_WildCard_Probabilities[0];
    }

    private void saveWildCardProbabilitiesToStorage(Settings_WildCard_Mode mode, Settings_WildCard_Probabilities[] probabilities, Fragment targetFragment) {
        SharedPreferences prefs;
        String prefsName;

        if (targetFragment instanceof TruthWildCardsFragment) {
            prefsName = "TruthTaskPrefs";
        } else if (targetFragment instanceof QuizWildCardsFragment) {
            prefsName = "QuizWildCardsPrefs";
        } else {
            // Handle other fragments if necessary
            return;
        }

        prefs = mContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.putInt("wild_card_count", probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            Settings_WildCard_Probabilities probability = probabilities[i];
            editor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
            editor.putString("wild_card_activity_" + i, probability.getText());
            editor.putInt("wild_card_probability_" + i, probability.getProbability());
        }

        editor.apply();
    }



}
