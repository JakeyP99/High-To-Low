package com.example.countingdowngame.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import android.os.Handler;


public class ServerFind extends ButtonUtilsActivity {
    private static final String SERVER_URL = "http://192.168.0.148:3000"; // your server IP
    Button connectionStatus;
    boolean isConnected = false; // add this at the top of the class
    private Socket mSocket;
    private Handler handler = new Handler();
    private int dotCount = 0;
    private boolean stopDotAnimation = false;

    {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("SocketIO", "Socket URI error", e);
        }
    }

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_find);

        connectionStatus = findViewById(R.id.connectionStatus);
        connectionStatus.setText("Connecting to server");

        startConnectingAnimation(); // Start the animation

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            stopDotAnimation = true; // stop animating dots
            isConnected = true;
            connectionStatus.setText("Connected!");
        }));

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(() -> {
            stopDotAnimation = true; // stop animating dots
            connectionStatus.setText("Connection failed ❌");
        }));

        connectionStatus.setOnClickListener(v -> {
            if (isConnected) {
                btnUtils.setButton(connectionStatus, this::gotoPlayerNumberChoice);
            } else {
                Toast.makeText(this, "Still connecting...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startConnectingAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stopDotAnimation) return;

                dotCount = (dotCount + 1) % 4; // cycle 0 -> 1 -> 2 -> 3 -> 0
                String dots = new String(new char[dotCount]).replace("\0", ".");
                connectionStatus.setText("Connecting to server" + dots);

                handler.postDelayed(this, 500); // update every 500ms
            }
        }, 500);
    }


}
