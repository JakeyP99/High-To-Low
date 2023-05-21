//package com.example.countingdowngame;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//
//public class PlayerNameChoice extends ButtonUtilsActivity {
//    private static final int REQUEST_CODE_RESET_COUNTER = 1;
//
//    private ListView playerListView;
//    private EditText nameEditText;
//    private ArrayList<String> playerNames;
//    private int playerCounter;
//
//    @Override
//    public void onBackPressed() {
//        startActivityForResult(getSafeIntent(PlayerNumberChoice.class), REQUEST_CODE_RESET_COUNTER);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_RESET_COUNTER && resultCode == RESULT_OK) {
//            boolean shouldResetCounter = data.getBooleanExtra("resetCounter", false);
//            if (shouldResetCounter) {
//                playerCounter = 0;
//                playerNames.clear();
//                updatePlayerList();
//            }
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.a3_player_name_choice);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//
//        playerListView = findViewById(R.id.list_view_player_names);
//        nameEditText = findViewById(R.id.edit_text_name);
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PlayerNameChoice.this);
//
//        playerNames = new ArrayList<>(preferences.getStringSet("playerNames", new HashSet<>()));
//        playerCounter = playerNames.size();
//
//        Button addButton = findViewById(R.id.button_add_name);
//        btnUtils.setButton(addButton, null, this::addPlayerName);
//
//        Button doneButton = findViewById(R.id.button_done);
//        btnUtils.setButton(doneButton, null, this::checkPlayerNames);
//
//        updatePlayerList();
//    }
//
//    private void addPlayerName() {
//        String name = nameEditText.getText().toString().trim();
//        if (!name.isEmpty()) {
//            playerNames.add(name);
//            nameEditText.setText("");
//            updatePlayerList();
//            playerCounter++;
//        }
//    }
//
//    private void checkPlayerNames() {
//        int playerCount = getIntent().getIntExtra("playerCount", 0);
//
//        if (playerNames.size() == playerCount) {
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PlayerNameChoice.this).edit();
//            editor.putStringSet("playerNames", new HashSet<>(playerNames));
//            editor.apply();
//            startActivity(getSafeIntent(NumberChoice.class));
//        } else if (playerNames.size() < playerCount) {
//            Toast.makeText(PlayerNameChoice.this, "Please add more player names.", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(PlayerNameChoice.this, "Please remove player names.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updatePlayerList() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames) {
//            @Override
//            public View getView(final int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_delete_player, parent, false);
//                }
//
//                TextView playerNameView = convertView.findViewById(R.id.player_name);
//                String playerName = playerNames.get(position);
//                playerNameView.setText(playerName);
//                setTextViewSizeBasedOnString(playerNameView, playerName);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    playerNameView.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
//                }
//
//                btnUtils.setButton(convertView.findViewById(R.id.delete_button), null, () -> {
//                    playerNames.remove(position);
//                    playerCounter--;
//                    updatePlayerList();
//                });
//
//                return convertView;
//            }
//        };
//        playerListView.setAdapter(adapter);
//
//        playerCounter = playerNames.size();
//
//        int playerCount = getIntent().getIntExtra("playerCount", 0);
//        int remainingPlayers = playerCount - playerCounter;
//        int extraPlayers = playerCounter - playerCount;
//
//        TextView remainingPlayersView = findViewById(R.id.text_view_counter);
//        if (remainingPlayers > 0) {
//            remainingPlayersView.setText("Enter " + playerCount + " Player Names!");
//        } else if (remainingPlayers == 0) {
//            remainingPlayersView.setText("You're good to play!");
//        } else if (remainingPlayers == -1) {
//            remainingPlayersView.setText("Remove " + extraPlayers + " Player Name!");
//        } else {
//            remainingPlayersView.setText("Remove " + extraPlayers + " Player Names!");
//        }
//    }
//
//    private void setTextViewSizeBasedOnString(TextView textView, String text) {
//        int textSize = 23; // Set default text size
//        if (text.length() > 23) {
//            textSize = 16; // Set smaller text size for longer strings
//        } else if (text.length() > 20) {
//            textSize = 18; // Set slightly smaller text size for medium length strings
//        }
//        textView.setTextSize(textSize);
//    }
//}
