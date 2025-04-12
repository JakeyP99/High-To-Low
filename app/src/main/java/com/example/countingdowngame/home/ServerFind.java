package com.example.countingdowngame.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.HttpURLConnection;

import io.socket.client.IO;
import io.socket.client.Socket;
import pl.droidsonroids.gif.GifImageView;

public class ServerFind extends ButtonUtilsActivity {
    TextView connectionStatus;

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager.getInstance().resumeBackgroundMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager.getInstance().pauseSound();
    }

    private Socket mSocket;
    private static final String SERVER_URL = "http://192.168.0.148:3000"; // your server IP

    EditText playerNameInput;
    Button joinButton;

    {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("SocketIO", "Socket URI error", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_find);

        playerNameInput = findViewById(R.id.playerNameInput);
        joinButton = findViewById(R.id.joinButton);


        connectionStatus = findViewById(R.id.connectionStatus);




        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.e("SocketIO", "Connection Error: " + args[0]));

        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            connectionStatus.setText("Connected to server ✅");
            connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }));

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(() -> {
            connectionStatus.setText("Connection failed ❌");
            connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }));
        joinButton.setOnClickListener(v -> checkServerAndJoin());
    }

    private void checkServerAndJoin() {
        new Thread(() -> {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 404) { // we accept 404 as "server is alive"
                    String playerName = playerNameInput.getText().toString().trim();
                    if (!playerName.isEmpty()) {
                        JSONObject json = new JSONObject();
                        json.put("name", playerName);
                        mSocket.emit("join", playerName);

                        runOnUiThread(() -> Toast.makeText(ServerFind.this, "Joined Room!", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ServerFind.this, "Server not available", Toast.LENGTH_SHORT).show());
                }

                connection.disconnect();

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ServerFind.this, "Connection failed", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }
}
