package com.example.countingdowngame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivitySplitScreen extends ButtonUtilsActivity {
    private final Map<Player, Set<Settings_WildCard_Probabilities>> usedWildCard = new HashMap<>();
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();

    private TextView nextPlayerText;
    private TextView nextPlayerTextPlayer2;
    private TextView numberText;
    private TextView numberTextPlayer2;
    private Button btnSkip;
    private Button btnSkipPlayer2;
    private Button btnWild;
    private Button btnWildPlayer2;

    // <Player1>
    private View wildText;
    private Button btnGenerate;
    private Button btnBackWild;

    // <--------------------------------------------------------------------------------->
    // <Player2>
    private View wildTextPlayer2;
    private Button btnGeneratePlayer2;
    private Button btnBackWildPlayer2;

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start_splitscreen);

        numberText = findViewById(R.id.numberText);
        numberTextPlayer2 = findViewById(R.id.numberTextPlayer2);
        btnSkip = findViewById(R.id.btnSkip);
        btnSkipPlayer2 = findViewById(R.id.btnSkipPlayer2);
        btnWildPlayer2 = findViewById(R.id.btnWildPlayer2);
        btnWild = findViewById(R.id.btnWild);
        wildText = findViewById(R.id.wild_textview);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackWild = findViewById(R.id.btnBackWildCard);
        wildTextPlayer2 = findViewById(R.id.wild_textviewPlayer2);
        btnGeneratePlayer2 = findViewById(R.id.btnGeneratePlayer2);
        btnBackWildPlayer2 = findViewById(R.id.btnBackWildCardPlayer2);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        nextPlayerTextPlayer2 = findViewById(R.id.textView_Number_TurnPlayer2);

        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);
        ImageButton imageButtonExitPlayer2 = findViewById(R.id.imageBtnExitPlayer2);

        // These are the button controls for Player 1
        btnUtils.setButton(btnWild, null, this::ButtonWildFunction);
        btnUtils.setButton(btnGenerate, null, this::ButtonGenerateFunction);

        btnUtils.setButton(btnBackWild, null, this::ButtonContinueFunction);

        btnUtils.setButton(btnSkip, null, this::ButtonSkipFunction);

        btnUtils.setImageButton(imageButtonExit, HomeScreen.class, () -> {
            Game.getInstance().endGame();
        });

        // These are the button controls for Player 2

        btnUtils.setButton(btnWildPlayer2, null, this::ButtonWildFunction);

        btnUtils.setButton(btnGeneratePlayer2, null, this::ButtonGenerateFunction);

        btnUtils.setButton(btnBackWildPlayer2, null, this::ButtonContinueFunction);

        btnUtils.setButton(btnSkipPlayer2, null, this::ButtonSkipFunction);

        btnUtils.setImageButton(imageButtonExitPlayer2, HomeScreen.class, () -> {
            Game.getInstance().endGame();
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

    private void renderPlayer() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> playerNamesSet = preferences.getStringSet("playerNames", null);

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        int currentPlayerIndex = Game.getInstance().getCurrentPlayerId();
        String[] playerNamesArray = playerNamesSet.toArray(new String[0]);
        String currentPlayerName = playerNamesArray[currentPlayerIndex];

        nextPlayerText.setText(currentPlayerName);
        nextPlayerTextPlayer2.setText(currentPlayerName);

        if (currentPlayer.getSkipAmount() > 0) {
            btnSkip.setVisibility(View.VISIBLE);
            btnSkipPlayer2.setVisibility(View.VISIBLE);
        } else {
            btnSkip.setVisibility(View.INVISIBLE);
            btnSkipPlayer2.setVisibility(View.INVISIBLE);
        }

        if (currentPlayer.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
            btnWildPlayer2.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
            btnWildPlayer2.setVisibility(View.INVISIBLE);
        }

        numberText.setText(Integer.toString(Game.getInstance().getCurrentNumber()));
        numberTextPlayer2.setText(Integer.toString(Game.getInstance().getCurrentNumber()));
        setTextViewSizeBasedOnInt(numberText, String.valueOf(Game.getInstance().getCurrentNumber()));
        setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(Game.getInstance().getCurrentNumber()));
    }

    // This is the wildcard function.
    private void wildCardActivate(Player player) {
        Settings_WildCard_Choice settings = new Settings_WildCard_Choice();
        Settings_WildCard_Probabilities[][] probabilitiesArray = settings.loadWildCardProbabilitiesFromStorage(getApplicationContext());

        // Assuming you want to access the first set of probabilities in the array
        Settings_WildCard_Probabilities[] activityProbabilities = probabilitiesArray[0];

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        final TextView wildActivityTextViewPlayer2 = findViewById(R.id.wild_textviewPlayer2);

        player.useWildCard();

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<Settings_WildCard_Probabilities> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            List<Settings_WildCard_Probabilities> unusedCards = Arrays.stream(activityProbabilities).filter(Settings_WildCard_Probabilities::isEnabled).filter(c -> !usedWildCards.contains(c)).collect(Collectors.toList());

            if (unusedCards.isEmpty()) {
                assert usedCards != null;
                usedCards.clear();
            }

            // Calculate total weight of unused wildcards
            int totalWeight = unusedCards.stream().mapToInt(Settings_WildCard_Probabilities::getProbability).sum();

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
                for (Settings_WildCard_Probabilities activityProbability : unusedCards) {
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

            for (Settings_WildCard_Probabilities wc : activityProbabilities) {
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

    // This changes the size of the number.
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

    private void startEndActivity() {
        startActivity(getSafeIntent(EndActivity.class));
    }

    // These are my button functions.
    private void ButtonGenerateFunction() {
        Game.getInstance().nextNumber(this::startEndActivity);
        wildText.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.VISIBLE);
        nextPlayerText.setVisibility(View.VISIBLE);

        wildTextPlayer2.setVisibility(View.INVISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);
    }

    private void ButtonWildFunction() {
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

        Game.getInstance().getCurrentPlayer().useWildCard();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        wildCardActivate(currentPlayer);
    }

    private void ButtonSkipFunction() {
        Game.getInstance().getCurrentPlayer().useSkip();

        wildText.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.VISIBLE);

        nextPlayerText.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

        wildTextPlayer2.setVisibility(View.INVISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);
    }

    private void ButtonContinueFunction() {
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
    }
}