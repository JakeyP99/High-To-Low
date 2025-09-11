package com.example.countingdowngame.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_statistic, parent, false);
        }

        PlayerStatistic stat = getItem(position);

        TextView nameView = convertView.findViewById(R.id.statPlayerName);
        TextView drinksView = convertView.findViewById(R.id.statTotalDrinks);
        TextView gamesLostView = convertView.findViewById(R.id.statGamesLost);
        de.hdodenhof.circleimageview.CircleImageView playerImageView = convertView.findViewById(R.id.playerImage);

        nameView.setText(stat.getPlayerName());
        drinksView.setText("End Game Drinks: " + stat.getTotalDrinks());
        gamesLostView.setText("Games Lost: " + stat.getTotalGamesLost());

        // Load photo
        SharedPreferences prefs = getContext().getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        String keyPrefix = stat.getPlayerName().toLowerCase().replaceAll("\\s+", "_");
        String photoString = prefs.getString(keyPrefix + "_photo", null);

        if (photoString != null) {
            byte[] decodedBytes = android.util.Base64.decode(photoString, android.util.Base64.DEFAULT);
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            playerImageView.setImageBitmap(bitmap);
        } else {
            playerImageView.setImageResource(R.drawable.wine); // fallback image
        }

        return convertView;
    }



}
