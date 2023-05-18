package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivitySplitScreen extends AppCompatActivity {
    @Override
    public void onBackPressed() {
    }

    static Game gameInstance = new Game();
    private TextView nextPlayerText;
    private TextView nextPlayerTextPlayer2;

    private TextView numberText;
    private TextView numberTextPlayer2;

    private Button btnSkip;
    private Button btnSkipPlayer2;

    private Button btnWild;
    private Button btnWildPlayer2;


    private Map<Player, Set<WildCardProbabilities>> usedWildCard = new HashMap<>();
    private Set<WildCardProbabilities> usedWildCards = new HashSet<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        //<Player1>

        numberText = findViewById(R.id.numberText);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        View wildText = findViewById(R.id.wild_textview);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnBackWild = findViewById(R.id.btnBackWildCard);
        btnWild = findViewById(R.id.btnWild);
        btnSkip = findViewById(R.id.btnSkip);
        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);

        //<--------------------------------------------------------------------------------->
        //<Player2>

        numberTextPlayer2 = findViewById(R.id.numberTextPlayer2);
        nextPlayerTextPlayer2 = findViewById(R.id.textView_Number_TurnPlayer2);
        View wildTextPlayer2 = findViewById(R.id.wild_textviewPlayer2);
        Button btnGeneratePlayer2 = findViewById(R.id.btnGeneratePlayer2);
        Button btnBackWildPlayer2 = findViewById(R.id.btnBackWildCardPlayer2);
        btnWildPlayer2 = findViewById(R.id.btnWildPlayer2);
        btnSkipPlayer2 = findViewById(R.id.btnSkipPlayer2);
        ImageButton imageButtonExitPlayer2 = findViewById(R.id.imageBtnExitPlayer2);

        btnBackWildPlayer2.setVisibility(View.INVISIBLE);
        btnBackWild.setVisibility(View.INVISIBLE);



        //These are the button controls for Player 1
        ButtonUtils.setButton(btnGenerate, null, this, () -> {
            gameInstance.nextNumber();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);

            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);
        });

        ButtonUtils.setButton(btnBackWild, null, this, () -> {
            btnBackWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.VISIBLE);
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);

            nextPlayerText.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

            btnBackWildPlayer2.setVisibility(View.INVISIBLE);
            btnGeneratePlayer2.setVisibility(View.VISIBLE);
            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);
        });

        ButtonUtils.setButton(btnSkip, null, this, () -> {
            gameInstance.getCurrentPlayer().useSkip();

            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);

            nextPlayerText.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);

        });


        ButtonUtils.setImageButton(imageButtonExit, HomeScreen.class, this, () -> {
            gameInstance.endGame();
        });

        ButtonUtils.setButton(btnWild, null, this, () -> {
            btnWild.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnBackWild.setVisibility(View.VISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);

            nextPlayerTextPlayer2.setVisibility(View.INVISIBLE);
            btnWildPlayer2.setVisibility(View.INVISIBLE);
            wildTextPlayer2.setVisibility(View.VISIBLE);
            btnBackWildPlayer2.setVisibility(View.VISIBLE);
            btnGeneratePlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.INVISIBLE);

            gameInstance.getCurrentPlayer().useWildCard();
            Player currentPlayer = gameInstance.getCurrentPlayer();
            wildCardActivate(currentPlayer);
        });

//        These are the button controls for Player 2

        ButtonUtils.setButton(btnGeneratePlayer2, null, this, () -> {
            gameInstance.nextNumber();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);

            nextPlayerText.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);

        });

        ButtonUtils.setButton(btnBackWildPlayer2, null, this, () -> {
            btnBackWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.VISIBLE);
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);

            nextPlayerText.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

            btnBackWildPlayer2.setVisibility(View.INVISIBLE);
            btnGeneratePlayer2.setVisibility(View.VISIBLE);
            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);
        });

        ButtonUtils.setButton(btnSkipPlayer2, null, this, () -> {
            gameInstance.getCurrentPlayer().useSkip();

            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);

            nextPlayerText.setVisibility(View.VISIBLE);
            nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

            wildTextPlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.VISIBLE);

        });


        ButtonUtils.setImageButton(imageButtonExitPlayer2, HomeScreen.class, this, () -> {
            gameInstance.endGame();
        });

        ButtonUtils.setButton(btnWildPlayer2, null, this, () -> {
            btnWild.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnBackWild.setVisibility(View.VISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);

            btnWildPlayer2.setVisibility(View.INVISIBLE);
            wildTextPlayer2.setVisibility(View.VISIBLE);
            btnBackWildPlayer2.setVisibility(View.VISIBLE);
            btnGeneratePlayer2.setVisibility(View.INVISIBLE);
            numberTextPlayer2.setVisibility(View.INVISIBLE);

            gameInstance.getCurrentPlayer().useWildCard();
            Player currentPlayer = gameInstance.getCurrentPlayer();
            wildCardActivate(currentPlayer);
        });

        gameInstance.setPlayerEventListener(e -> {
            if (e.type == PlayerEventType.SKIP) {
                gameInstance.nextPlayer();
            }
        });

        gameInstance.setGameEventListener(e -> {
            switch (e.type) {
                case NEXT_PLAYER: {
                    renderPlayer();
                    break;
                }

                case GAME_END: {
                    gameInstance.endGame();
                    startActivity(new Intent(MainActivitySplitScreen.this, EndActivity.class));
                    break;
                }

                case NEXT_NUMBER:
                case GAME_START: {
                    numberText.setText(String.valueOf(gameInstance.currentNumber));
                    numberTextPlayer2.setText(String.valueOf(gameInstance.currentNumber));

                    setTextViewSizeBasedOnInt(numberText, String.valueOf(gameInstance.currentNumber));
                    setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(gameInstance.currentNumber));
                    break;
                }
            }
        });

        gameInstance.startGame(NumberChoice.startingNumber);
        numberText.setText(Integer.toString(gameInstance.currentNumber));
        numberTextPlayer2.setText(Integer.toString(gameInstance.currentNumber));

        renderPlayer();
    }

    private void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 60; // set default text size
        int minSize = 40; // minimum text size

        // Adjust text size based on the length of the text
        if (text.length() > 6) {
            textView.setTextSize(minSize); // set smaller text size for longer strings
        } else {
            textView.setTextSize(defaultTextSize); // set default text size for short strings
        }
    }


    private void renderPlayer() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> playerNamesSet = preferences.getStringSet("playerNames", null);
        String[] playerNamesArray = playerNamesSet.toArray(new String[0]);
        int currentPlayerIndex = gameInstance.currentPlayerId;

        String currentPlayerName = playerNamesArray[currentPlayerIndex];
        nextPlayerText.setText(currentPlayerName);
        nextPlayerTextPlayer2.setText(currentPlayerName);

        if (gameInstance.getCurrentPlayer().getSkipAmount() > 0) {
            btnSkip.setVisibility(View.VISIBLE);
            btnSkipPlayer2.setVisibility(View.VISIBLE);

        } else {
            btnSkip.setVisibility(View.INVISIBLE);
            btnSkipPlayer2.setVisibility(View.INVISIBLE);

        }

        if (gameInstance.getCurrentPlayer().getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
            btnWildPlayer2.setVisibility(View.VISIBLE);
        } else {
            if (btnWild != null) {
                btnWild.setVisibility(View.INVISIBLE);
            }
            if (btnWildPlayer2 != null) {
                btnWildPlayer2.setVisibility(View.INVISIBLE);
            }
        }
    }
    public void wildCardActivate(Player player) {
        Settings_WildCardChoice settings = new Settings_WildCardChoice();
        WildCardProbabilities[] activityProbabilities = settings.loadWildCardProbabilitiesFromStorage(getApplicationContext());

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        final TextView wildActivityTextViewPlayer2 = findViewById(R.id.wild_textviewPlayer2);

        player.useWildCard();

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<WildCardProbabilities> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            List<WildCardProbabilities> unusedCards = Arrays.stream(activityProbabilities)
                    .filter(WildCardProbabilities::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            if (unusedCards.isEmpty()) {
                assert usedCards != null;
                usedCards.clear();
            }

            // Calculate total weight of unused wildcards
            int totalWeight = unusedCards.stream()
                    .mapToInt(WildCardProbabilities::getProbability)
                    .sum();

            if (totalWeight <= 0) {
                wildActivityTextView.setText("No wild cards available");
                wildActivityTextViewPlayer2.setText("No wild cards available");

                return;
            }

            Random random = new Random();
            boolean foundUnusedCard = false;
            while (!foundUnusedCard) {
                int randomWeight = random.nextInt(totalWeight);
                int weightSoFar = 0;
                for (WildCardProbabilities activityProbability : unusedCards) {
                    weightSoFar += activityProbability.getProbability();
                    if (randomWeight < weightSoFar) {
                        // Check if the selected wildcard has already been used by the current player
                        assert usedCards != null;
                        if (!usedCards.contains(activityProbability)) {
                            selectedActivity = activityProbability.getText();
                            foundUnusedCard = true;
                            usedCards.add(activityProbability);
                        }
                        break;
                    }
                }
            }
            usedWildCard.put(player, usedCards);
        }

        if (selectedActivity != null) {
            wildActivityTextView.setText(selectedActivity);
            wildActivityTextViewPlayer2.setText(selectedActivity);

            for (WildCardProbabilities wc : activityProbabilities) {
                if (wc.getText().equals(selectedActivity)) {
                    player.addUsedWildCard(wc);
                    usedCards.add(wc);
                    break;
                }
            }
        }

        if (selectedActivity != null && selectedActivity.equals("Get a skip button to use on any one of your turns!")) {
            if (player.getSkipAmount() == 0) {
                player.skips++;
                btnSkip.setVisibility(View.VISIBLE);
                btnSkipPlayer2.setVisibility(View.VISIBLE);
            }
        }

        if (player.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
            btnWildPlayer2.setVisibility(View.VISIBLE);

        } else {
            btnWild.setVisibility(View.INVISIBLE);
            btnWildPlayer2.setVisibility(View.INVISIBLE);
        }
    }

}