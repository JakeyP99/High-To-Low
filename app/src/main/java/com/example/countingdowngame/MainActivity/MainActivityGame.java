package com.example.countingdowngame.MainActivity;

import static com.example.countingdowngame.MainActivity.SharedMainActivity.reverseTurnOrder;

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

import com.example.countingdowngame.AudioManager;
import com.example.countingdowngame.ButtonUtilsActivity;
import com.example.countingdowngame.ExtrasWildCardsAdapter;
import com.example.countingdowngame.ExtrasWildCardsFragment;
import com.example.countingdowngame.QuizWildCardsAdapter;
import com.example.countingdowngame.QuizWildCardsFragment;
import com.example.countingdowngame.R;
import com.example.countingdowngame.TaskWildCardsAdapter;
import com.example.countingdowngame.TaskWildCardsFragment;
import com.example.countingdowngame.TruthWildCardsAdapter;
import com.example.countingdowngame.TruthWildCardsFragment;
import com.example.countingdowngame.WildCardHeadings;
import com.example.countingdowngame.WildCardType;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.stores.PlayerModelLocalStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivityGame extends ButtonUtilsActivity {

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager.getInstance().playSound(); // Start playing the sound
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager.getInstance().stopSound(); // Stop the sound
    }

    private final Map<Player, Set<WildCardHeadings>> usedWildCard = new HashMap<>();
    private final Set<WildCardHeadings> usedWildCards = new HashSet<>();
    private TextView numberText;
    private TextView nextPlayerText;
    private Button btnAnswer;

    private Button btnWild;
    private Button btnGenerate;
    private Button btnBackWild;
    private ImageView playerImage;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private Handler shuffleHandler;
    private WildCardHeadings selectedWildCard; // Declare selectedWildCard at a higher level

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame(this);
            gotoHomeScreen();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to go to the home screen", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
    }
    //-----------------------------------------------------Create game---------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start);
        initializeViews();
        setupButtons();
        startGame();
    }

    private void initializeViews() {
        playerImage = findViewById(R.id.playerImage);
        numberText = findViewById(R.id.numberText);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackWild = findViewById(R.id.btnBackWildCard);
        shuffleHandler = new Handler();
    }

    private void startGame() {
        int soundResourceId = R.raw.cartoonloop;
        AudioManager.getInstance().initialize(this, soundResourceId);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        // Load player data from PlayerModel
        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            // Set the player list in Game class
            Game.getInstance().setPlayers(this, playerList.size());
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

        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);
        View wildText = findViewById(R.id.wild_textview);

        btnBackWild.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);


        btnUtils.setButton(btnGenerate, this::startNumberShuffleAnimation);
        btnUtils.setButton(btnAnswer, this::showAnswer);

        btnUtils.setButton(btnBackWild, () -> {
            btnGenerate.setVisibility(View.VISIBLE);
            Game.getInstance().getCurrentPlayer().useSkip();
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
            wildText.setVisibility(View.INVISIBLE);
            btnBackWild.setVisibility(View.INVISIBLE);


            btnAnswer.setVisibility(View.INVISIBLE);
        });

        btnUtils.setButton(btnWild, () -> {
            if (selectedWildCard != null && selectedWildCard.hasAnswer()) {
                btnAnswer.setVisibility(View.VISIBLE);
            } else {
                btnAnswer.setVisibility(View.INVISIBLE);
            }

            wildCardActivate(Game.getInstance().getCurrentPlayer());
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnBackWild.setVisibility(View.VISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });
    }

    //-----------------------------------------------------Button Shuffling---------------------------------------------------//

    private void startNumberShuffleAnimation() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);

        int currentNumber = Game.getInstance().getCurrentNumber();
        final int shuffleDuration = 1500;

        int shuffleInterval = currentNumber >= 1000 ? 50 : 100;

        final Random random = new Random();
        shuffleHandler.postDelayed(new Runnable() {
            int shuffleTime = 0;

            @Override
            public void run() {
                int randomDigit = random.nextInt(currentNumber + 1);
                numberText.setText(String.valueOf(randomDigit));

                shuffleTime += shuffleInterval;

                if (shuffleTime < shuffleDuration) {
                    shuffleHandler.postDelayed(this, shuffleInterval);
                } else {
                    int finalNumber = randomDigit;
                    numberText.setText(String.valueOf(finalNumber));

                    Game.getInstance().nextNumber(MainActivityGame.this, () -> gotoGameEnd());

                    btnGenerate.setEnabled(true);
                    btnWild.setEnabled(true);
                }
            }
        }, shuffleInterval);
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            String playerName = currentPlayer.getName();
            String playerImageString = currentPlayer.getPhoto();

            nextPlayerText.setText(playerName + "'s Turn");
            btnWild.setText((currentPlayer.getWildCardAmount() + "\n" + "Wild Cards"));

            if (playerImageString != null) {
                byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                playerImage.setImageBitmap(decodedBitmap);
            }
        }

        if (currentPlayer.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        SharedMainActivity.setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());

    }

    //-----------------------------------------------------Wild Card, and Skip Functionality---------------------------------------------------//

    private void wildCardActivate(Player player) {
        Game.getInstance().getCurrentPlayer().useWildCard();
        WildCardHeadings[] emptyProbabilitiesArray = new WildCardHeadings[0];
        QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.QUIZ);
        TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TASK);
        TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TRUTH);
        ExtrasWildCardsAdapter extraAdapter = new ExtrasWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.EXTRAS);

        WildCardHeadings[] quizProbabilities = quizAdapter.loadWildCardProbabilitiesFromStorage(QuizWildCardsFragment.defaultQuizWildCards);
        WildCardHeadings[] taskProbabilities = taskAdapter.loadWildCardProbabilitiesFromStorage(TaskWildCardsFragment.defaultTaskWildCards);
        WildCardHeadings[] truthProbabilities = truthAdapter.loadWildCardProbabilitiesFromStorage(TruthWildCardsFragment.defaultTruthWildCards);
        WildCardHeadings[] extraProbabilities = extraAdapter.loadWildCardProbabilitiesFromStorage(ExtrasWildCardsFragment.defaultExtrasWildCards);

        List<WildCardHeadings> allProbabilities = new ArrayList<>();
        allProbabilities.addAll(Arrays.asList(quizProbabilities));
        allProbabilities.addAll(Arrays.asList(taskProbabilities));
        allProbabilities.addAll(Arrays.asList(truthProbabilities));
        allProbabilities.addAll(Arrays.asList(extraProbabilities));

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<WildCardHeadings> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());

        if (wildCardsEnabled) {
            Random random = new Random();
            int typeChance = random.nextInt(4); // Generate a random number from 0 to 3

            WildCardHeadings[] selectedType = null;

            switch (typeChance) {
                case 0:
                    selectedType = quizProbabilities;
                    break;
                case 1:
                    selectedType = taskProbabilities;
                    break;
                case 2:
                    selectedType = truthProbabilities;
                    break;
                case 3:
                    selectedType = extraProbabilities;
                    break;
                default:
                    break;
            }

            List<WildCardHeadings> unusedCards = Arrays.stream(selectedType)
                    .filter(WildCardHeadings::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            if (unusedCards.isEmpty()) {
                wildActivityTextView.setText("No wild cards available");
                btnWild.setVisibility(View.INVISIBLE);
                return;
            }

            // Calculate the total probabilities within the selected type
            int totalTypeProbabilities = unusedCards.stream()
                    .mapToInt(WildCardHeadings::getProbability)
                    .sum();

            // Generate a random number within the total probabilities
            int selectedIndex = random.nextInt(totalTypeProbabilities);

            // Select the wildcard based on the random number and its probability
            WildCardHeadings selectedCard = null;
            int cumulativeProbability = 0;

            for (WildCardHeadings card : unusedCards) {
                cumulativeProbability += card.getProbability();

                if (selectedIndex < cumulativeProbability) {
                    selectedCard = card;
                    break;
                }
            }

            if (selectedCard != null) {
                selectedActivity = selectedCard.getText();
                wildActivityTextView.setText(selectedActivity);
                usedCards.add(selectedCard);
                usedWildCards.add(selectedCard);
                usedWildCard.put(player, usedCards);

                btnAnswer.setVisibility(selectedCard.hasAnswer() ? View.VISIBLE : View.INVISIBLE);
                selectedWildCard = selectedCard;

            }
        }

        if (selectedActivity != null) {
            if (selectedActivity.equals("Double the current number!")) {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.min(currentNumber * 2, 999999999);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
            } else if (selectedActivity.equals("Half the current number!")) {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.max(currentNumber / 2, 1);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
            } else if (selectedActivity.equals("Reset the number!")) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    throw new RuntimeException("Missing extras");
                }
                int startingNumber = extras.getInt("startingNumber");
                Game.getInstance().setCurrentNumber(startingNumber);
                Game.getInstance().addUpdatedNumber(startingNumber);
                numberText.setText(String.valueOf(startingNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(startingNumber));
            } else if (selectedActivity.equals("Reverse the turn order!")) {
                reverseTurnOrder(player);
            }
        }
    }

    private void showAnswer() {
        TextView wildActivityTextView = findViewById(R.id.wild_textview);
        btnAnswer.setVisibility(View.INVISIBLE);
        String answer = selectedWildCard.getAnswer();
        wildActivityTextView.setText(answer);
            }

}
