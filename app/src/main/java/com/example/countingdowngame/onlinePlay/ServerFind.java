package com.example.countingdowngame.onlinePlay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.playerChoice.PlayerChoice;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerFind extends ButtonUtilsActivity {
    private static final String SERVER_URL = "http://192.168.0.148:3000"; // your server IP
    private static Socket mSocket;
    private final Handler handler = new Handler();
    Button connectionStatus;
    boolean isConnected = false; // add this at the top of the class
    private int dotCount = 0;
    private boolean stopDotAnimation = false;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

    public static Socket getSocket() {
        return mSocket;
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

        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("SocketIO", "Socket URI error", e);
            handleConnectionFailure();
        }

        if (mSocket != null) {
            connectionLogic();
            mSocket.connect();
            startConnectingAnimation();
        }

        connectionStatus.setOnClickListener(v -> {
            if (isConnected) {
                btnUtils.setButton(connectionStatus, () -> {
                    Intent intent = new Intent(this, PlayerChoice.class);
                    intent.putExtra("resetPlayers", true);
                    startActivity(intent);
                });
            } else {
                retryConnection();
            }
        });
    }

    private void retryConnection() {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            stopDotAnimation = false;
            startConnectingAnimation();
            
            try {
                mSocket = IO.socket(SERVER_URL);
                connectionLogic();
                mSocket.connect();
            } catch (URISyntaxException e) {
                Log.e("SocketIO", "Socket URI error", e);
                handleConnectionFailure();
            }
        } else {
            connectionStatus.setText("Connection failed ❌");
            retryCount = 0; // Reset retry count for next attempt
        }
    }

    private void handleConnectionFailure() {
        runOnUiThread(() -> {
            isConnected = false;
            stopDotAnimation = true;
            connectionStatus.setText("Connection failed ❌");
        });
    }

    private void connectionLogic() {
        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            isConnected = true;
            stopDotAnimation = true;
            connectionStatus.setText("Connected!");
            retryCount = 0; // Reset retry count on successful connection
        }));

        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(this::handleConnectionFailure));


    }

    private void startConnectingAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stopDotAnimation) return;

                dotCount = (dotCount + 1) % 4; // cycle 0 -> 1 -> 2 -> 3 -> 0
                String dots = new String(new char[dotCount]).replace("\0", ".");
                connectionStatus.setText("Connecting to server" + dots);

                handler.postDelayed(this, 700); // update every 500ms
            }
        }, 500);
    }
}