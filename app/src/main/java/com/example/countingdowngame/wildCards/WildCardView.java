package com.example.countingdowngame.wildCards;

import android.content.Context;
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

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.wildCardTypes.ExtrasWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.ExtrasWildCardsFragment;
import com.example.countingdowngame.wildCards.wildCardTypes.QuizWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.QuizWildCardsFragment;
import com.example.countingdowngame.wildCards.wildCardTypes.TaskWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TaskWildCardsFragment;
import com.example.countingdowngame.wildCards.wildCardTypes.TruthWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TruthWildCardsFragment;

public class WildCardView extends ArrayAdapter<WildCardHeadings> {
    private final WildCardChoice mContext;
    private final WildCardHeadings[] mProbabilities;

    public WildCardView(WildCardChoice context, WildCardHeadings[] probabilities) {
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
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_view_wild_cards, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewWildCard = convertView.findViewById(R.id.textview_wildcard);
            viewHolder.switchWildCard = convertView.findViewById(R.id.switch_wildcard);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        WildCardHeadings wildCard = mProbabilities[position];
        viewHolder.textViewWildCard.setText(wildCard.getText());
        viewHolder.textViewProbability.setText(String.valueOf(wildCard.getProbability()));
        viewHolder.switchWildCard.setChecked(wildCard.isEnabled());

        String wildCardText = wildCard.getText();
        setTextViewSizeBasedOnString(viewHolder.textViewWildCard, wildCardText);

        String probabilityText = String.valueOf(wildCard.getProbability());
        setProbabilitySizeBasedOnString(viewHolder.textViewProbability, probabilityText);

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewWildCard;
        TextView textViewProbability;
        Switch switchWildCard;
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
        }
        textView.setTextSize(textSize);
    }

    public static class WildCardsPagerAdapter extends FragmentPagerAdapter {
        private static final int TAB_COUNT = 4;

        Context mContext;

        public WildCardsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            WildCardHeadings[] emptyProbabilitiesArray = new WildCardHeadings[0];
            QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(emptyProbabilitiesArray, mContext, WildCardType.QUIZ);
            TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(emptyProbabilitiesArray, mContext, WildCardType.TASK);
            TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(emptyProbabilitiesArray, mContext, WildCardType.TRUTH);
            ExtrasWildCardsAdapter extraAdapter = new ExtrasWildCardsAdapter(emptyProbabilitiesArray, mContext, WildCardType.EXTRAS);

            switch (position) {
                case 0:
                    return new QuizWildCardsFragment(mContext, quizAdapter);
                case 1:
                    return new TaskWildCardsFragment(mContext, taskAdapter);
                case 2:
                    return new TruthWildCardsFragment(mContext, truthAdapter);
                case 3:
                    return new ExtrasWildCardsFragment(mContext, extraAdapter);
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
                    return null;
            }
        }
    }
}