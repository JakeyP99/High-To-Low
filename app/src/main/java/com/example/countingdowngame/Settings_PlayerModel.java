package com.example.countingdowngame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Settings_PlayerModel extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingClass.class));
    }
    private static final int REQUEST_IMAGE_PICK = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private ListView playerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_player_list);

        playerListView = findViewById(R.id.playerListView);
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList);
        playerListView.setAdapter(playerListAdapter);

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(view -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Player Name");

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_name, null);
            EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS); // Restrict input to only string values

            builder.setView(dialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = nameEditText.getText().toString();
                            addNewPlayer(bitmap, name);
                        }
                    })
                    .setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }}



    private static class PlayerListAdapter extends ArrayAdapter<Player> {

        private final LayoutInflater inflater;

        public PlayerListAdapter(Context context, List<Player> players) {
            super(context, 0, players);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_view_player_name, parent, false);
                holder = new ViewHolder();
                holder.playerPhotoImageView = convertView.findViewById(R.id.playerPhotoImageView);
                holder.playerNameTextView = convertView.findViewById(R.id.playerNameTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Player player = getItem(position);
            holder.playerPhotoImageView.setImageBitmap(player.getPhoto());
            holder.playerNameTextView.setText(player.getName());

            return convertView;
        }

        private static class ViewHolder {
            ImageView playerPhotoImageView;
            TextView playerNameTextView;
        }
    }
    private void animatePlayerImage(int position) {
        View itemView = playerListView.getChildAt(position - playerListView.getFirstVisiblePosition());

        if (itemView != null) {
            ImageView playerPhotoImageView = itemView.findViewById(R.id.playerPhotoImageView);

            float startScale = 0.0f;
            float endScale = 1.0f;
            int duration = 500;

            ScaleAnimation scaleAnimation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(duration);

            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    playerPhotoImageView.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            playerPhotoImageView.startAnimation(scaleAnimation);
        }
    }

    private void addNewPlayer(Bitmap bitmap, String name) {
        Player newPlayer = new Player(bitmap, name);
        playerList.add(newPlayer);
        playerListAdapter.notifyDataSetChanged();

        int position = playerList.indexOf(newPlayer);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animate the image view
                animatePlayerImage(position);
            }
        }, 100);
    }
}
