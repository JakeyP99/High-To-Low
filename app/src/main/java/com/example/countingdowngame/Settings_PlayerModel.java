package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings_PlayerModel extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_photos);

        avatarImageView = findViewById(R.id.avatarImageView);
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
            Bitmap circularBitmap = ImageUtil.getCircularBitmap(bitmap);
            avatarImageView.setImageBitmap(circularBitmap);
        }
    }
}
