package com.example.countingdowngame.createPlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Base64;
import android.util.Log;
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
import com.example.countingdowngame.game.Player;

import java.util.List;

// RecyclerView adapter for player list
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final PlayerChoice context;
    private final List<Player> players;
    private final ClickListener clickListener;

    public PlayerListAdapter(PlayerChoice context, List<Player> players, ClickListener clickListener) {
        this.context = context;
        this.players = players;
        this.clickListener = clickListener; // Initialize the clickListener with the provided parameter
    }


    public interface ClickListener {
        void onPlayerClick(int position);
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
        holder.bind(player);
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

            playerItemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onPlayerClick(position);
                }
                Log.d("Player Clicked", "ViewHolder: Playerclicked");

            });


            deletePlayerImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    context.deletePlayer(position);
                }
            });


            //todo put this functionality in classSelection once you choose your class
//            // Set click listener for player selection
//            playerItemView.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    togglePlayerSelection(position);
//                }
//            });

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

        public void bind(Player player) {
            String photoString = player.getPhoto();
            byte[] decodedBytes = Base64.decode(photoString, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Create a circular bitmap
            Bitmap circleBitmap = Bitmap.createBitmap(decodedBitmap.getWidth(), decodedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(decodedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(circleBitmap);
            float radius = decodedBitmap.getWidth() / 2f;
            canvas.drawCircle(radius, radius, radius, paint);

            Glide.with(context)
                    .load(Base64.decode(photoString, Base64.DEFAULT))
                    .apply(RequestOptions.circleCropTransform())
                    .into(playerPhotoImageView);

            playerNameTextView.setBackgroundResource(R.drawable.outlineforbutton);
            playerNameTextView.setText(player.getName());
            playerNameTextView.setPadding(20, 20, 20, 30);

            // Highlight the selected player
            if (player.isSelected()) {
                playerItemView.setBackgroundResource(R.drawable.selectedplayer);
                playerItemView.setPadding(20, 20, 20, 30);
                deletePlayerImageView.setVisibility(View.INVISIBLE);

            } else {
                playerItemView.setBackgroundResource(0);
                deletePlayerImageView.setVisibility(View.VISIBLE);

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
