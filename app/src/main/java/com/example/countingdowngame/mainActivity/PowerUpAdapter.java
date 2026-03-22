package com.example.countingdowngame.mainActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countingdowngame.R;

import java.util.List;

public class PowerUpAdapter extends ArrayAdapter<String> {
    public PowerUpAdapter(Context context, List<String> powerUps) {
        super(context, 0, powerUps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_powerup_list_item, parent, false);
        }

        String powerUp = getItem(position);
        TextView text = convertView.findViewById(R.id.powerup_text);
        ImageView icon = convertView.findViewById(R.id.powerup_icon);

        if (powerUp != null) {
            text.setText(powerUp);
            icon.setImageResource(PowerUps.getPowerUpIcon(powerUp));
        }

        return convertView;
    }
}
