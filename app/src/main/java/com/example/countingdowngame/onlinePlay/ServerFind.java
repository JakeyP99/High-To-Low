package com.example.countingdowngame.onlinePlay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.numberChoice.PlayerNumberChoice;
import com.example.countingdowngame.playerChoice.PlayerChoice;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import org.json.JSONObject;

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
    private Button hostButton;
    private Button joinButton;



    private final boolean isHost = false;


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

        hostButton = findViewById(R.id.hostButton);
        joinButton = findViewById(R.id.joinButton);
        startConnectingAnimation();

        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("SocketIO", "Socket URI error", e);
        }

        if (mSocket != null) {
            connectionLogic();
            mSocket.connect();  // Ensure the socket is connected first
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                // After socket is connected, start listening for events
                listenForHostAssignment();
                listenForPlayerCountUpdate();
            });
        }
    }



    private void connectionLogic() {
        mSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            isConnected = true;
            stopDotAnimation = true;
            connectionStatus.setText("Connected!");

            // Only show buttons if connected
            hostButton.setVisibility(View.VISIBLE);
            joinButton.setVisibility(View.VISIBLE);

            // Update button state after connection
            if (isHost) {
                hostButton.setEnabled(true);
                joinButton.setEnabled(false);
            } else {
                hostButton.setEnabled(false);
                joinButton.setEnabled(true);
            }
        }));
    }
    private void listenForHostAssignment() {
        mSocket.on("hostAssigned", args -> runOnUiThread(() -> {
            if (args.length > 0 && args[0] instanceof Boolean) {
                boolean isHost = (boolean) args[0];  // Get boolean value from server

                if (isHost) {
                    Log.d("HostAssignment", "Host has been assigned to this user.");
                    // Handle Host UI state
                    setHostUIState(true);
                    btnUtils.setButton(hostButton, this::gotoPlayerNumberChoice);
                } else {
                    Log.d("HostAssignment", "User is not the host.");
                    // Handle Non-Host UI state
                    setHostUIState(false);
                }
            }
        }));
    }

    private void setHostUIState(boolean isHost) {
        if (isHost) {
            hostButton.setAlpha(1f);
            hostButton.setEnabled(true);  // Enable the button for the host

        }}

    private void listenForPlayerCountUpdate() {
        mSocket.on("playerCountUpdate", args -> runOnUiThread(() -> {
            if (args.length > 1) {
                // Safely handle conversion by checking the type
                Object currentObj = args[0];
                Object totalObj = args[1];

                int currentPlayers = 0;
                int totalPlayers = 0;

                if (currentObj instanceof Double) {
                    currentPlayers = ((Double) currentObj).intValue();
                } else if (currentObj instanceof Integer) {
                    currentPlayers = (Integer) currentObj;
                }

                if (totalObj instanceof Double) {
                    totalPlayers = ((Double) totalObj).intValue();
                } else if (totalObj instanceof Integer) {
                    totalPlayers = (Integer) totalObj;
                }

                Log.d("ServerFind", "Player count: " + currentPlayers + "/" + totalPlayers);

                if (totalPlayers > 0) {
                    btnUtils.setButton(joinButton, () -> {
                        Intent intent = new Intent(this, PlayerChoice.class);
                        intent.putExtra("resetPlayers", true);
                        startActivity(intent);
                    });

                    // Game is in pre-start phase
                    if (!isHost) {
                        hostButton.setAlpha(0.5f);
                        joinButton.setAlpha(1f);
                        joinButton.setEnabled(true);
                    } else {
                        hostButton.setAlpha(1f);
                        hostButton.setEnabled(true);
                        joinButton.setAlpha(0.5f);
                        joinButton.setEnabled(false);
                    }
                } else {
                    // Waiting for host to set player count
                    if (!isHost) {
                        joinButton.setAlpha(0.5f);
                        joinButton.setEnabled(false);
                    }
                }
            }
        }));
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