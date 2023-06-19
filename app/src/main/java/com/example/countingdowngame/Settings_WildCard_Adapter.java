package com.example.countingdowngame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Settings_WildCard_Adapter extends ArrayAdapter<Settings_WildCard_Probabilities> {
    private final Settings_WildCard_Choice mContext;
    private final Settings_WildCard_Mode mMode;
    private Settings_WildCard_Probabilities[] mProbabilities;

    public Settings_WildCard_Adapter(Settings_WildCard_Mode mode, Settings_WildCard_Choice context, Settings_WildCard_Probabilities[] probabilities) {
        super(context, R.layout.list_view_wild_cards, probabilities);
        mContext = context;
        mProbabilities = probabilities;
        mMode = mode;
    }

    @Override
    public int getCount() {
        return mProbabilities.length;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_view_wild_cards, null);

        TextView textViewWildCard = view.findViewById(R.id.textview_wildcard);
        TextView textViewProbability = view.findViewById(R.id.textview_probability);
        Switch switchWildCard = view.findViewById(R.id.switch_wildcard);
        Settings_WildCard_Probabilities wildCard = mProbabilities[position];
        textViewWildCard.setText(wildCard.getText());
        textViewProbability.setText(String.valueOf(wildCard.getProbability()));
        switchWildCard.setChecked(wildCard.isEnabled());
        String wildCardText = wildCard.getText();
        setTextViewSizeBasedOnString(textViewWildCard, wildCardText);
        String probabilityText = String.valueOf(wildCard.getProbability());
        setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

        return view;
    }

    public Settings_WildCard_Probabilities[] getWildCardProbabilities() {
        return mProbabilities;
    }

    public boolean areAllEnabled() {
        for (Settings_WildCard_Probabilities probability : mProbabilities) {
            if (!probability.isEnabled()) {
                return false; // Return false if any wild card is not enabled
            }
        }
        return true;
    }

    public void setAllEnabled(boolean enabled) {
        for (Settings_WildCard_Probabilities probability : mProbabilities) {
            probability.setEnabled(enabled);
        }
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    private void setTextViewSizeBasedOnString(TextView textView, String text) {
        int textSize = 20;
        if (text.length() > 20) {
            textSize = 14;
        } else if (text.length() > 10) {
            textSize = 16;
        }
        textView.setTextSize(textSize);
    }

    private void setProbabilitySizeBasedOnString(TextView textView, String text) {
        int textSize = 18;
        if (text.length() > 2) {
            textSize = 12;
        } else if (text.length() > 0) {
            textSize = 18;
        }
        textView.setTextSize(textSize);
    }

    public static class WildCardsPagerAdapter extends FragmentPagerAdapter {
        private static final int TAB_COUNT = 3;

        public WildCardsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new QuizWildCardsFragment();
                case 1:
                    return new TaskWildCardsFragment();
                case 2:
                    return new TruthWildCardsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Quiz";
                case 1:
                    return "Task";
                case 2:
                    return "Truth";
                default:
                    return super.getPageTitle(position);
            }
        }
    }
}