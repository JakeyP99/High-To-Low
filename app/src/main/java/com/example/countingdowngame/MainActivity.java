package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends ButtonUtilsActivity {
    private final Map<Player, Set<Settings_WildCard_Probabilities>> usedWildCard = new HashMap<>();
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();

    private TextView numberText;
    private TextView nextPlayerText;
    private Button btnSkip;
    private Button btnWild;

    @Override
    public void onBackPressed() {
        // Disable back button functionality
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start);
        ArrayList<String> playerNames = getIntent().getStringArrayListExtra("playerNames");

        if (playerNames != null) {
            // Display the player names
            TextView playerNamesTextView = findViewById(R.id.textView_Number_Turn);
            String namesText = "Selected Players:\n";
            for (String playerName : playerNames) {
                namesText += playerName + "\n";
            }
            playerNamesTextView.setText(namesText);
        }
        numberText = findViewById(R.id.numberText);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnSkip = findViewById(R.id.btnSkip);
        btnWild = findViewById(R.id.btnWild);

        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnBackWild = findViewById(R.id.btnBackWildCard);
        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);
        View wildText = findViewById(R.id.wild_textview);

        btnBackWild.setVisibility(View.INVISIBLE);

        btnGenerate.setOnClickListener(view -> {
            Game.getInstance().nextNumber(this::startEndActivity);
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
        });

        btnBackWild.setOnClickListener(view -> {
            btnBackWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.VISIBLE);
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
        });

        btnSkip.setOnClickListener(view -> {
            Game.getInstance().getCurrentPlayer().useSkip();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
            btnBackWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.VISIBLE);
        });

        btnWild.setOnClickListener(view -> {
            btnWild.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnBackWild.setVisibility(View.VISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
            Game.getInstance().getCurrentPlayer().useWildCard();
            wildCardActivate(Game.getInstance().getCurrentPlayer());
        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame();
            // Start HomeScreen activity
            startActivity(getSafeIntent(HomeScreen.class));
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });

        renderPlayer();
    }

    private void startEndActivity() {
        startActivity(getSafeIntent(EndActivity.class));
    }

    private void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 70;
        int minSize = 47;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }

    private void renderPlayer() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> playerNamesSet = preferences.getStringSet("playerNames", null);

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        int currentPlayerIndex = Game.getInstance().getCurrentPlayerId();



        if (playerNamesSet != null) {
            String[] playerNamesArray = playerNamesSet.toArray(new String[0]);
            String currentPlayerName = playerNamesArray[currentPlayerIndex];

            nextPlayerText.setText(currentPlayerName + "'s Turn");
        }

        if (currentPlayer.getSkipAmount() > 0) {
            btnSkip.setVisibility(View.VISIBLE);
        } else {
            btnSkip.setVisibility(View.INVISIBLE);
        }

        if (currentPlayer.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
    }

    private void wildCardActivate(Player player) {
        Settings_WildCard_Choice settings = new Settings_WildCard_Choice();
        Settings_WildCard_Probabilities[][] probabilitiesArray = settings.loadWildCardProbabilitiesFromStorage(getApplicationContext());

        // Assuming you want to access the first set of probabilities in the array
        Settings_WildCard_Probabilities[] activityProbabilities = probabilitiesArray[0];
        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        player.useWildCard();

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<Settings_WildCard_Probabilities> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            List<Settings_WildCard_Probabilities> unusedCards = Arrays.stream(activityProbabilities)
                    .filter(Settings_WildCard_Probabilities::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            if (unusedCards.isEmpty() && usedCards != null) {
                usedCards.clear();
            }

            // Calculate total weight of unused wildcards
            int totalWeight = unusedCards.stream()
                    .mapToInt(Settings_WildCard_Probabilities::getProbability)
                    .sum();

            if (totalWeight <= 0) {
                wildActivityTextView.setText("No wild cards available");
                return;
            }

            Random random = new Random();
            boolean foundUnusedCard = false;
            while (!foundUnusedCard) {
                int randomWeight = random.nextInt(totalWeight);
                int weightSoFar = 0;

                for (Settings_WildCard_Probabilities activityProbability : unusedCards) {
                    weightSoFar += activityProbability.getProbability();

                    if (randomWeight < weightSoFar) {
                        // Check if the selected wildcard has already been used by the current player
                        if (usedCards != null && !usedCards.contains(activityProbability)) {
                            selectedActivity = activityProbability.getText();
                            foundUnusedCard = true;
                            usedCards.add(activityProbability);
                        }
                        break;
                    }
                }
            }

            // Update the used wildcards for the current player
            usedWildCard.put(player, usedCards);
        }

        if (selectedActivity != null) {
            wildActivityTextView.setText(selectedActivity);
            for (Settings_WildCard_Probabilities wc : activityProbabilities) {
                if (wc.getText().equals(selectedActivity)) {
                    player.addUsedWildCard(wc);
                    usedCards.add(wc); // Add to usedCards set for this player
                    break;
                }
            }
        }

        if (selectedActivity != null && selectedActivity.equals("Get a skip button to use on any one of your turns!")) {
            if (player.getSkipAmount() == 0) {
                player.skips++;
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        if (player.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }
    }
}
