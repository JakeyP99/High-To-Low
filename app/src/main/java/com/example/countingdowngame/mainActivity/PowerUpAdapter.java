package com.example.countingdowngame.mainActivity;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countingdowngame.R;

import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

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
        GifImageView icon = convertView.findViewById(R.id.powerup_icon);

        if (powerUp != null) {
            text.setText(powerUp);
            icon.setImageResource(PowerUps.getPowerUpIcon(powerUp));

            // Stop the gif from animating
            try {
                GifDrawable gifDrawable = (GifDrawable) icon.getDrawable();
                if (gifDrawable != null) {
                    gifDrawable.stop();
                    gifDrawable.seekTo(0);
                }
            } catch (ClassCastException e) {
                // If it's not a gif, ignore
            }

            if (PowerUps.isObtained(powerUp)) {
                // Apply grayscale filter to icon
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                icon.setColorFilter(filter);
                icon.setAlpha(0.5f);
                
                // Dim the text
                text.setTextColor(getContext().getResources().getColor(R.color.bluedarktransparent));
            } else {
                icon.clearColorFilter();
                icon.setAlpha(1.0f);
                text.setTextColor(getContext().getResources().getColor(R.color.bluedark));
            }
        }

        return convertView;
    }
}
