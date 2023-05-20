package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayerListAdapter extends ArrayAdapter<Player> {

    private List<Player> players;
    private LayoutInflater inflater;

    public PlayerListAdapter(Context context, List<Player> players) {
        super(context, 0, players);
        this.players = players;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_player, parent, false);
            holder = new ViewHolder();
            holder.playerNameTextView = convertView.findViewById(R.id.playerNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Player player = players.get(position);
        holder.playerNameTextView.setText(player.getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView playerNameTextView;
    }
}
