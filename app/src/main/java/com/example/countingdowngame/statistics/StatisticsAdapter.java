package com.example.countingdowngame.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.countingdowngame.R;

import java.util.List;

public class StatisticsAdapter extends ArrayAdapter<PlayerStatistic> {

    public StatisticsAdapter(Context context, List<PlayerStatistic> statistics) {
        super(context, 0, statistics);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_statistic, parent, false);
        }

        PlayerStatistic stat = getItem(position);

        TextView nameView = convertView.findViewById(R.id.statPlayerName);
        TextView drinksView = convertView.findViewById(R.id.statTotalDrinks);

        nameView.setText(stat.getPlayerName());
        drinksView.setText("End Game Drinks: " + stat.getTotalDrinks());

        return convertView;
    }
}
