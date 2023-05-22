package com.example.countingdowngame;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

// RecyclerView adapter for player list
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final PlayerModel context;
    private final List<Player> players;

    public PlayerListAdapter(PlayerModel context, List<Player> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_view_player_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.bind(player, position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView playerPhotoImageView;
        TextView playerNameTextView;
        ImageView deletePlayerImageView;
        View playerItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerItemView = itemView;
            playerPhotoImageView = itemView.findViewById(R.id.playerPhotoImageView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            deletePlayerImageView = itemView.findViewById(R.id.deletePlayerImageView);

            deletePlayerImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    context.deletePlayer(position);
                }
            });

            // Set click listener for player selection
            playerItemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    togglePlayerSelection(position);
                }
            });

            // Set long-click listener for player deletion
            playerItemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    context.deletePlayer(position);
                    return true;
                }
                return false;
            });
        }

        public void bind(Player player, int position) {
            String photoString = player.getPhoto();
            Glide.with(context)
                    .load(Base64.decode(photoString, Base64.DEFAULT))
                    .apply(RequestOptions.circleCropTransform())
                    .into(playerPhotoImageView);

            playerNameTextView.setBackgroundResource(R.drawable.outlineforbutton);
            playerNameTextView.setText(player.getName());
            playerNameTextView.setPadding(20, 20, 20, 20);

            // Highlight the selected player
            if (player.isSelected()) {
                playerItemView.setBackgroundResource(R.drawable.outlineforbutton);
            } else {
                playerItemView.setBackgroundResource(0);
            }
        }

        private void togglePlayerSelection(int position) {
            Player player = players.get(position);
            player.setSelected(!player.isSelected());
            notifyItemChanged(position);
            context.updatePlayerCounter();
        }
    }
}
