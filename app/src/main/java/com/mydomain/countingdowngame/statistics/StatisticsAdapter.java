package com.mydomain.countingdowngame.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mydomain.countingdowngame.R;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticsAdapter extends ArrayAdapter<PlayerStatistic> {

    private final LayoutInflater inflater;
    private final OnLongClickListener longClickListener;

    public StatisticsAdapter(Context context, List<PlayerStatistic> statistics, OnLongClickListener longClickListener) {
        super(context, 0, statistics);
        this.inflater = LayoutInflater.from(context);
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.statistic_main_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PlayerStatistic stat = getItem(position);
        if (stat != null) {
            holder.nameView.setText(stat.getPlayerName());
            holder.drinksView.setText("End Game Drinks: " + stat.getTotalDrinks());
            holder.gamesLostView.setText("Games Lost: " + stat.getTotalGamesLost());
            holder.gamesPlayedView.setText("Games Played: " + stat.getTotalGamesPlayed());

            if (stat.getPlayerPhoto() != null) {
                holder.playerImageView.setImageBitmap(stat.getPlayerPhoto());
            } else {
                holder.playerImageView.setImageResource(R.drawable.wine); // fallback image
            }

            convertView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onLongClick(stat, position);
                    return true;
                }
                return false;
            });
        }

        return convertView;
    }

    public interface OnLongClickListener {
        void onLongClick(PlayerStatistic stat, int position);
    }

    private static class ViewHolder {
        final TextView nameView;
        final TextView drinksView;
        final TextView gamesLostView;
        final TextView gamesPlayedView;
        final CircleImageView playerImageView;

        ViewHolder(View view) {
            nameView = view.findViewById(R.id.statPlayerName);
            drinksView = view.findViewById(R.id.statTotalDrinks);
            gamesLostView = view.findViewById(R.id.statGamesLost);
            gamesPlayedView = view.findViewById(R.id.statGamesPlayed);
            playerImageView = view.findViewById(R.id.playerImage);
        }
    }
}
