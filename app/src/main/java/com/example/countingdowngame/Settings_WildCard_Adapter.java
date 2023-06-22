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

public class Settings_WildCard_Adapter extends ArrayAdapter<WildCardHeadings> {
    private final WildCardChoice mContext;
    private WildCardHeadings[] mProbabilities;

    public Settings_WildCard_Adapter( WildCardChoice context, WildCardHeadings[] probabilities) {
        super(context, R.layout.list_view_wild_cards, probabilities);
        mContext = context;
        mProbabilities = probabilities;
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
        WildCardHeadings wildCard = mProbabilities[position];
        textViewWildCard.setText(wildCard.getText());
        textViewProbability.setText(String.valueOf(wildCard.getProbability()));
        switchWildCard.setChecked(wildCard.isEnabled());
        String wildCardText = wildCard.getText();
        setTextViewSizeBasedOnString(textViewWildCard, wildCardText);
        String probabilityText = String.valueOf(wildCard.getProbability());
        setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

        return view;
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
        private static final int TAB_COUNT = 4;

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
                case 3:
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
                case 3:
                    return "Extras";
                default:
                    return super.getPageTitle(position);
            }
        }
    }
}