package com.example.countingdowngame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private TextView numberText;
    private TextView numberTextPlayer2;
    private TextView nextPlayerText;
    private TextView nextPlayerTextPlayer2;
    private Button btnSkip;
    private Button btnSkipPlayer2;
    private Button btnWild;
    private Button btnWildPlayer2;
    private ImageView playerImage;
    private ImageView playerImagePlayer2;
    Button btnGenerate;
    Button btnGeneratePlayer2;
    Button btnBackWild;
    Button btnBackWildPlayer2;
    View wildText;
    View wildTextPlayer2;
    ImageButton imageButtonExit;
    ImageButton imageButtonExitPlayer2;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame();
            gotoHomeScreen();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to go to the home screen", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start_splitscreen);
        initializeViews();
        setupButtons();
        startGame();
    }

    private void initializeViews() {
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGeneratePlayer2 = findViewById(R.id.btnGeneratePlayer2);
        btnBackWild = findViewById(R.id.btnBackWildCard);
        btnBackWildPlayer2 = findViewById(R.id.btnBackWildCardPlayer2);
        wildText = findViewById(R.id.wild_textview);
        wildTextPlayer2 = findViewById(R.id.wild_textviewPlayer2);
        imageButtonExit = findViewById(R.id.imageBtnExit);
        imageButtonExitPlayer2 = findViewById(R.id.imageBtnExitPlayer2);
        playerImage = findViewById(R.id.playerImage);
        playerImage.setBackgroundResource(R.drawable.circle_background);
        playerImagePlayer2 = findViewById(R.id.playerImagePlayer2);
        playerImagePlayer2.setBackgroundResource(R.drawable.circle_background);
        btnSkip = findViewById(R.id.btnSkip);
        btnSkipPlayer2 = findViewById(R.id.btnSkipPlayer2);
        btnWild = findViewById(R.id.btnWild);
        btnWildPlayer2 = findViewById(R.id.btnWildPlayer2);
        numberText = findViewById(R.id.numberText);
        numberTextPlayer2 = findViewById(R.id.numberTextPlayer2);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        nextPlayerTextPlayer2 = findViewById(R.id.textView_Number_TurnPlayer2);
    }

    private void startGame() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        // Load player data from PlayerModel
        List<Player> playerList = PlayerModel.loadSelectedPlayers(this);
        if (playerList != null && !playerList.isEmpty()) {
            // Set the player list in Game class
            Game.getInstance().setPlayers(playerList.size());
            Game.getInstance().setPlayerList(playerList);

            // Set the game object for each player
            for (Player player : playerList) {
                player.setGame(Game.getInstance());
            }
        }

        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });

        renderPlayer();
    }
    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupButtons() {

        btnBackWild.setVisibility(View.INVISIBLE);
        btnBackWildPlayer2.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, this::ButtonGenerateFunction);

        btnUtils.setButton(btnWild, this::ButtonWildFunction);

        btnUtils.setButton(btnSkip, this::ButtonSkipFunction);

        btnUtils.setButton(btnBackWild, this::ButtonContinueFunction);

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame();
            gotoHomeScreen();
        });

        btnUtils.setButton(btnGeneratePlayer2, this::ButtonGenerateFunction);

        btnUtils.setButton(btnWildPlayer2, this::ButtonWildFunction);

        btnUtils.setButton(btnSkipPlayer2, this::ButtonSkipFunction);

        btnUtils.setButton(btnBackWildPlayer2, this::ButtonContinueFunction);

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame();
            gotoHomeScreen();
        });


        imageButtonExitPlayer2.setOnClickListener(view -> {
            Game.getInstance().endGame();
            gotoHomeScreen();
        });
    }
    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        List<Player> playerList = PlayerModel.loadSelectedPlayers(this);

        if (!playerList.isEmpty()) {
            String playerName = currentPlayer.getName();
            String playerImageString = currentPlayer.getPhoto();

            nextPlayerText.setText(playerName);
            nextPlayerTextPlayer2.setText(playerName);

            if (playerImageString != null) {
                byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                playerImage.setImageBitmap(decodedBitmap);
                playerImagePlayer2.setImageBitmap(decodedBitmap);

            }
        }

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

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        numberTextPlayer2.setText(String.valueOf(currentNumber));

        setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(currentNumber));

        setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());
        setNameSizeBasedOnInt(nextPlayerTextPlayer2, nextPlayerTextPlayer2.getText().toString());

    }

    //-----------------------------------------------------Wild Card, and Skip Functionality---------------------------------------------------//

    private void wildCardActivate(Player player) {
        Settings_WildCard_Choice settings = new Settings_WildCard_Choice();
        Settings_WildCard_Probabilities[][] probabilitiesArray = settings.loadWildCardProbabilitiesFromStorage(getApplicationContext());

        Settings_WildCard_Probabilities[] deletableProbabilities = probabilitiesArray[0];
        Settings_WildCard_Probabilities[] nonDeletableProbabilities = probabilitiesArray[1];

        List<Settings_WildCard_Probabilities> allProbabilities = new ArrayList<>();
        allProbabilities.addAll(Arrays.asList(deletableProbabilities));
        allProbabilities.addAll(Arrays.asList(nonDeletableProbabilities));

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        final TextView wildActivityTextViewPlayer2 = findViewById(R.id.wild_textviewPlayer2);

        player.useWildCard();


        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<Settings_WildCard_Probabilities> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            List<Settings_WildCard_Probabilities> unusedCards = allProbabilities.stream()
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
            wildActivityTextViewPlayer2.setText(selectedActivity);

            for (Settings_WildCard_Probabilities wc : allProbabilities) {
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

    private void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 65;
        int minSize = 45;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }

    //-----------------------------------------------------Button Functionality---------------------------------------------------//

    private void ButtonGenerateFunction() {
        Game.getInstance().nextNumber(this::gotoGameEnd);
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

        playerImage.setVisibility(View.INVISIBLE);
        playerImagePlayer2.setVisibility(View.INVISIBLE);


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
        btnGenerate.setVisibility(View.VISIBLE);
        btnGeneratePlayer2.setVisibility(View.VISIBLE);

        playerImage.setVisibility(View.VISIBLE);
        playerImagePlayer2.setVisibility(View.VISIBLE);

        wildText.setVisibility(View.INVISIBLE);
        wildTextPlayer2.setVisibility(View.INVISIBLE);

        numberText.setVisibility(View.VISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);

        nextPlayerText.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

        btnBackWild.setVisibility(View.INVISIBLE);
        btnBackWildPlayer2.setVisibility(View.INVISIBLE);
    }

    private void ButtonContinueFunction() {
        btnBackWild.setVisibility(View.INVISIBLE);
        btnGenerate.setVisibility(View.VISIBLE);
        wildText.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.VISIBLE);

        playerImage.setVisibility(View.VISIBLE);
        playerImagePlayer2.setVisibility(View.VISIBLE);

        nextPlayerText.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);

        btnBackWildPlayer2.setVisibility(View.INVISIBLE);
        btnGeneratePlayer2.setVisibility(View.VISIBLE);
        wildTextPlayer2.setVisibility(View.INVISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);
    }

    private void setNameSizeBasedOnInt(TextView textView, String text) {
        int textSize;

        if (text.length() > 18) {
            textSize = 20;
        } else if (text.length() > 14) {
            textSize = 23;
        } else if (text.length() > 8) {
            textSize = 28;
        } else {
            textSize = 38;
        }
        textView.setTextSize(textSize);
    }

}
