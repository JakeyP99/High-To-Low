package com.example.countingdowngame.createPlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
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
import com.example.countingdowngame.R;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.playerChoice.PlayerChoice;

import java.util.List;

// RecyclerView adapter for player list
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final PlayerChoice context;
    private final List<Player> players;
    private final ClickListener clickListener;

    public PlayerListAdapter(PlayerChoice context, List<Player> players, ClickListener clickListener) {
        this.context = context;
        this.players = players;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_choice_player_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.bind(player);

        holder.playerItemView.setOnLongClickListener(v -> {
            clickListener.onPlayerLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public interface ClickListener {
        void onPlayerClick(int position);
        void onPlayerLongClick(int position);
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

            playerItemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onPlayerClick(position);
                }
            });

            deletePlayerImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    context.deletePlayer(position);
                }
            });
        }

        public void bind(Player player) {
            String photoString = player.getPhoto();
            byte[] decodedBytes = Base64.decode(photoString, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            Glide.with(context)
                    .load(decodedBytes)
                    .apply(RequestOptions.circleCropTransform())
                    .into(playerPhotoImageView);

            playerNameTextView.setBackgroundResource(R.drawable.outlineforbutton);
            playerNameTextView.setText(player.getName());
            playerNameTextView.setPadding(20, 20, 20, 30);

            if (player.isSelected()) {
                playerItemView.setBackgroundResource(R.drawable.selectedplayer);
                deletePlayerImageView.setVisibility(View.INVISIBLE);
            } else {
                playerItemView.setBackgroundResource(0);
                deletePlayerImageView.setVisibility(View.VISIBLE);
            }
        }
    }
}
