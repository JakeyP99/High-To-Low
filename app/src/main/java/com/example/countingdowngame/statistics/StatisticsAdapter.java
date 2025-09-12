package com.example.countingdowngame.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.countingdowngame.R;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticsAdapter extends ArrayAdapter<PlayerStatistic> {

    private final LayoutInflater inflater;
    private final SharedPreferences prefs;

    public StatisticsAdapter(Context context, List<PlayerStatistic> statistics) {
        super(context, 0, statistics);
        this.inflater = LayoutInflater.from(context);
        this.prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_statistic, parent, false);
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

            // Load photo from SharedPreferences
            String keyPrefix = stat.getPlayerName()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("\\s+", "_");

            String photoString = prefs.getString(keyPrefix + "_photo", null);

            if (photoString != null) {
                byte[] decodedBytes = Base64.decode(photoString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.playerImageView.setImageBitmap(bitmap);
            } else {
                holder.playerImageView.setImageResource(R.drawable.wine); // fallback image
            }
        }

        return convertView;
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
