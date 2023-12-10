package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.R.id.btnBackWildCard;
import static com.example.countingdowngame.R.id.btnExitGame;
import static com.example.countingdowngame.R.id.close_button;
import static com.example.countingdowngame.R.id.dialogbox_textview;
import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.R.id.textView_NumberText;
import static com.example.countingdowngame.R.id.textView_Number_Turn;
import static com.example.countingdowngame.R.id.textView_WildText;
import static com.example.countingdowngame.R.id.textView_numberCounter;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_ARCHER;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_JIM;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_SCIENTIST;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_SOLDIER;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_WITCH;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;
import com.example.countingdowngame.wildCards.wildCardTypes.ExtrasWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.QuizWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TaskWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TruthWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityGame extends SharedMainActivity {

    //-----------------------------------------------------Member Variables---------------------------------------------------//

    private TextView numberText;
    private TextView nextPlayerText;
    private TextView drinkNumberCounterTextView;
    private TextView wildText;
    private Button btnAnswer;
    private Button btnWild;
    private Button btnGenerate;
    private Button btnBackWild;
    private Button btnClassAbility;
    private Button btnQuizAnswerBL;
    private Button btnQuizAnswerBR;
    private Button btnQuizAnswerTR;
    private Button btnQuizAnswerTL;

    private ImageView playerImage;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private Handler shuffleHandler;

    private final Map<Player, Set<WildCardProperties>> usedWildCard = new HashMap<>();
    private final Set<WildCardProperties> usedWildCards = new HashSet<>();
    private final Map<Player, Integer> playerTurnCountMap = new HashMap<>();

    public static int drinkNumberCounterInt = 0;
    private Player firstPlayer;
    private boolean isFirstTurn = true;
    private boolean soldierRemoval = false;
    public WildCardProperties selectedWildCard;
    private Button[] answerButtons; // Array to hold the answer buttons
    private GifImageView confettiImageViewBL;

    private GifImageView confettiImageViewTL;
    private GifImageView confettiImageViewBR;
    private GifImageView confettiImageViewTR;


    private static final int DELAY_MILLIS = 1500;
    private static final int BUTTON_COUNT = 4;
    private static final int BUTTON_COUNT_2 = 2;

    //-----------------------------------------------------Lifecycle Methods---------------------------------------------------//
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame(this);
            gotoHomeScreen();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        displayToastMessage("Press back again to go to the home screen");
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
    }

    public void displayToastMessage(String message) {
        StyleableToast.makeText(this, message, R.style.newToast).show();
    }

    //-----------------------------------------------------Start Game Functions---------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start);
        initializeViews();
        setupButtons();
        startGame();

        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setContext(getApplicationContext()); // Set the context before calling playRandomBackgroundMusic or other methods

        // Check if the mute button is not selected before starting the music
        if (!GeneralSettingsLocalStore.fromContext(this).isMuted()) {

            AudioManager.getInstance().stopSound();

            int soundResourceId = R.raw.cartoonloop;
            AudioManager.getInstance().initialize(this, soundResourceId);


            AudioManager.getInstance().playSound();

        }


    }

    private void initializeViews() {
        playerImage = findViewById(R.id.playerImage);
        numberText = findViewById(textView_NumberText);
        drinkNumberCounterTextView = findViewById(textView_numberCounter);
        nextPlayerText = findViewById(textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        confettiImageViewBL = findViewById(R.id.confettiImageViewBL);
        confettiImageViewTL = findViewById(R.id.confettiImageViewTL);
        confettiImageViewBR = findViewById(R.id.confettiImageViewBR);
        confettiImageViewTR = findViewById(R.id.confettiImageViewTR);

        btnAnswer = findViewById(R.id.btnAnswer);
        btnClassAbility = findViewById(R.id.btnClassAbility);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackWild = findViewById(btnBackWildCard);


        btnQuizAnswerBL = findViewById(R.id.btnQuizAnswerBL);
        btnQuizAnswerBR = findViewById(R.id.btnQuizAnswerBR);
        btnQuizAnswerTL = findViewById(R.id.btnQuizAnswerTL);
        btnQuizAnswerTR = findViewById(R.id.btnQuizAnswerTR);


        shuffleHandler = new Handler();
        wildText = findViewById(textView_WildText);
    }


    private void startGame() {
        drinkNumberCounterInt = 0;


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
            firstPlayer = playerList.get(0);

        }
        for (Player player : playerList) {
            player.setClassAbility(false); // Initialize class ability to false
        }
        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });
        renderPlayer();
    }

    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView textView1) {

        if (currentNumber == 0) {
            btnGenerate.setEnabled(false);
            btnWild.setEnabled(false);
            btnClassAbility.setEnabled(false);

            textView1.setText(String.valueOf(currentNumber));
            applyPulsingEffect(textView1);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Game.getInstance().endGame(this);
                onEnd.run();
            }, 2000);
        } else {
            textView1.setText(String.valueOf(currentNumber));
            Game.getInstance().nextPlayer();
        }
    }

    //todo I need to make sure these descriptions match
    private String getClassActiveDescription(String classChoice) {
        switch (classChoice) {
            case CLASS_ARCHER:
                return CharacterClassDescriptions.archerActiveDescription;
            case CLASS_WITCH:
                return CharacterClassDescriptions.witchActiveDescription;
            case CLASS_SCIENTIST:
                return CharacterClassDescriptions.scientistActiveDescription;
            case CLASS_SOLDIER:
                return CharacterClassDescriptions.soldierActiveDescription;
            case CLASS_QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianActiveDescription;
            case CLASS_JIM:
                return CharacterClassDescriptions.jimActiveDescription;
            default:
                return "I love you cutie pie hehe. You don't have a class to show any description for.";
        }
    }

    private String getClassPassiveDescription(String classChoice) {
        switch (classChoice) {
            case CLASS_ARCHER:
                return CharacterClassDescriptions.archerPassiveDescription;
            case CLASS_WITCH:
                return CharacterClassDescriptions.witchPassiveDescription;
            case CLASS_SCIENTIST:
                return CharacterClassDescriptions.scientistPassiveDescription;
            case CLASS_SOLDIER:
                return CharacterClassDescriptions.soldierPassiveDescription;
            case CLASS_QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianPassiveDescription;
            case CLASS_JIM:
                return CharacterClassDescriptions.jimPassiveDescription;
            default:
                return "";
        }
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupButtons() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("setupButtons", " " + currentPlayer.getClassChoice());

        ImageButton imageButtonExit = findViewById(btnExitGame);

        btnBackWild.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, () -> {
            startNumberShuffleAnimation();
            isFirstTurn = false;
        });

        playerImage.setOnClickListener(v -> setupPlayerImageClickListener());


        btnUtils.setButton(btnAnswer, this::showAnswer);

        btnUtils.setButton(btnBackWild, this::wildCardContinue);

        btnUtils.setButton(btnClassAbility, this::activateActiveAbility);


        btnUtils.setButton(btnWild, () -> {
            wildCardActivate(Game.getInstance().getCurrentPlayer());
            drinkNumberCounterTextView.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
            isFirstTurn = false;
        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });
    }

    private void setupPlayerImageClickListener() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            String currentPlayerClassChoice = currentPlayer.getClassChoice();
            if (currentPlayerClassChoice != null) {
                String classDescription = currentPlayer.getClassChoice() + "'s Abilities" + "\n\n" + "Active: " + getClassActiveDescription(currentPlayerClassChoice)
                        + "\n\n" + "Passive: " + getClassPassiveDescription(currentPlayerClassChoice);
                showDialogWithFixedTextSize(classDescription, 18);
            } else {
                String loveMessage = "I love you cutie pie hehe. You don't have a class to show any description for.";
                showDialogWithFixedTextSize(loveMessage, 30);
            }
        }
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();

        characterPassiveClassAffects();
        updateClassAbilityButton(currentPlayer);
        updateDrinkNumberCounter(currentPlayer);

        if (!playerList.isEmpty()) {
            updatePlayerInfo(currentPlayer);
        }

        updateNumberText();
        logPlayerInformation(currentPlayer);

        Log.d(TAG, "renderPlayer: Is repeat turn active? " + currentPlayer.getJustUsedWildCard());
        if (currentPlayer.getJustUsedWildCard()) {
            btnWild.setVisibility(View.INVISIBLE);
            currentPlayer.setJustUsedWildCard(false);
        } else {
            updateWildCardVisibility(currentPlayer);
        }


    }

    private void updateClassAbilityButton(Player currentPlayer) {
        btnClassAbility.setText(String.format("%s's Ability", currentPlayer.getClassChoice()));

        boolean showClassAbilityButton = ("Scientist".equals(currentPlayer.getClassChoice()) ||
                "Archer".equals(currentPlayer.getClassChoice()) ||
                "Witch".equals(currentPlayer.getClassChoice()) ||
                "Quiz Magician".equals(currentPlayer.getClassChoice()) ||
                "Soldier".equals(currentPlayer.getClassChoice())) &&
                !currentPlayer.usedClassAbility();

        btnClassAbility.setVisibility(showClassAbilityButton ? View.VISIBLE : View.INVISIBLE);

        if ("Jim".equals(currentPlayer.getClassChoice()) || "No Class".equals(currentPlayer.getClassChoice())) {
            btnClassAbility.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDrinkNumberCounter(Player currentPlayer) {
        if (currentPlayer.equals(firstPlayer)) {
            drinkNumberCounterInt++;
            updateDrinkNumberCounterTextView();
        }
    }

    private void updatePlayerInfo(Player currentPlayer) {
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

    private void updateWildCardVisibility(Player currentPlayer) {
        btnWild.setVisibility(currentPlayer.getWildCardAmount() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateNumberText() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        SharedMainActivity.setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());
    }

    private void logPlayerInformation(Player currentPlayer) {
        Log.d("renderPlayer", "Current number is " + Game.getInstance().getCurrentNumber() +
                " - Player was rendered " + currentPlayer.getName() +
                " is a " + currentPlayer.getClassChoice() +
                " with " + currentPlayer.getWildCardAmount() +
                " Wildcards " +
                "and " + currentPlayer.usedClassAbility() +
                " is the class ability and are they removed ?" +
                currentPlayer.isRemoved());
    }


    private void startNumberShuffleAnimation() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);
        btnClassAbility.setEnabled(false);
        playerImage.setEnabled(false);

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
                    numberText.setText(String.valueOf(randomDigit));
                    int currentNumber = Game.getInstance().nextNumber();
                    renderCurrentNumber(currentNumber, () -> gotoGameEnd(), numberText);

                    if (currentNumber != 0) {
                        btnGenerate.setEnabled(true);
                        btnWild.setEnabled(true);
                        btnClassAbility.setEnabled(true);
                        playerImage.setEnabled(true);

                    }
                    Log.d("startNumberShuffleAnimation", "Next players turn");

                }
            }
        }, shuffleInterval);
    }

    //-----------------------------------------------------Character Class Functions---------------------------------------------------//

    private void characterPassiveClassAffects() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }
        handleQuizMagicianPassive(currentPlayer);

        handleSoldierPassive(currentPlayer);
        handleWitchPassive(currentPlayer);
        handleScientistPassive(currentPlayer);
        handleJimPassive(currentPlayer);
        handleArcherPassive(currentPlayer);
    }


    private void handleQuizMagicianPassive(Player currentPlayer) {

    }


    private void handleSoldierPassive(Player currentPlayer) {
        if ("Soldier".equals(currentPlayer.getClassChoice())) {
            new Handler().postDelayed(() -> {
                if (!currentPlayer.isRemoved()) {
                    removeCharacterFromGame();
                } else {
                    currentPlayer.useSkip();
                }
            }, 1);
        }
    }

    private void handleWitchPassive(Player currentPlayer) {
        if ("Witch".equals(currentPlayer.getClassChoice()) && !isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() % 2 == 0) {
                showDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " hand out two drinks.");
            } else {
                showDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " take a drink.");
            }
        }
    }

    private void handleScientistPassive(Player currentPlayer) {
        if ("Scientist".equals(currentPlayer.getClassChoice())) {
            Handler handler = new Handler();
            int delayMillis = 1;
            int chance = new Random().nextInt(100);

            handler.postDelayed(() -> {
                if (chance < 10) {
                    showDialog("Scientist's Passive: \n\n" + currentPlayer.getName() + " is a scientist and his turn was skipped. ");
                    currentPlayer.useSkip();
                }
            }, delayMillis);
        }
    }

    private void handleJimPassive(Player currentPlayer) {
        if ("Jim".equals(currentPlayer.getClassChoice())) {
            int turnCounter = currentPlayer.getTurnCounter();
            if (turnCounter > 0 && turnCounter % 3 == 0) {
                currentPlayer.gainWildCards(1);
            }
        }
    }

    private void handleArcherPassive(Player currentPlayer) {
        if ("Archer".equals(currentPlayer.getClassChoice())) {
            int currentPlayerTurnCount = playerTurnCountMap.getOrDefault(currentPlayer, 0);
            currentPlayerTurnCount++;

            Log.d("ArcherClass", "Turn count: " + currentPlayerTurnCount);
            playerTurnCountMap.put(currentPlayer, currentPlayerTurnCount);

            if (currentPlayerTurnCount % 3 == 0) {
                Log.d("ArcherClass", "Passive ability triggered");

                int chance = new Random().nextInt(100);
                if (chance < 60) {
                    drinkNumberCounterInt += 2;
                    updateDrinkNumberCounterTextView();
                    showDialog("Archer's Passive: \n\nDrinking number increased by 2!");
                } else {
                    drinkNumberCounterInt -= 2;
                    if (drinkNumberCounterInt < 0) {
                        drinkNumberCounterInt = 0;
                    }
                    updateDrinkNumberCounterTextView();
                    showDialog("Archer's Passive: \n\nDrinking number decreased by 2!");
                }
            }
        }
    }

    public void activateActiveAbility() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("activateActiveAbility", "Class Activated" + currentPlayer.getClassChoice());
        String classChoice = currentPlayer.getClassChoice();
        switch (classChoice) {
            case "Scientist":
                handleScientistClass(currentPlayer);
                break;
            case "Archer":
                handleArcherClass(currentPlayer);
                break;
            case "Witch":
                handleWitchClass(currentPlayer);
                break;
            case "Soldier":
                handleSoldierClass(currentPlayer);
                break;
            case "Quiz Magician":
                handleQuizMagicianClass(currentPlayer);
                break;

            default:
                break;
        }
    }
    //todo it doesnt put in the changed number in the previous numbers
    private void handleScientistClass(Player currentPlayer) {
        changeCurrentNumber();
        Game.getInstance().activateRepeatingTurn(currentPlayer);
    }

    private void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() <= 10) {
                currentPlayer.setClassAbility(true);
                Game.getInstance().activateRepeatingTurn(currentPlayer);
                drinkNumberCounterInt += 4;
                updateDrinkNumberCounterTextView();
                btnClassAbility.setVisibility(View.INVISIBLE);
            } else {
                //todo fix this toast, it covers the screen
                displayToastMessage("The +4 ability can only be activated when the number is below 10.");
            }
        } else {
            displayToastMessage("Cannot activate on the first turn.");
        }
    }

    private void handleQuizMagicianClass(Player currentPlayer) {
        Game gameInstance = Game.getInstance();
        List<Player> players = gameInstance.getPlayers();
        showDialog("Quiz Magician's Active: \n\n" + "Everyone but " + currentPlayer.getName() + " loses a wildcard!");

        for (Player player : players) {
            if (player != currentPlayer) {
                player.loseWildCards(1);
                currentPlayer.setClassAbility(true);
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void handleArcherClass(Player currentPlayer) {
        Log.d("ArcherClass", "handleArcherClass called");

        if (drinkNumberCounterInt >= 2) {
            showDialog("Archer's Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setClassAbility(true);
            drinkNumberCounterInt -= 2;
            updateDrinkNumberCounterTextView();
            btnClassAbility.setVisibility(View.INVISIBLE);
        } else {
            displayToastMessage("There must be more than two total drinks.");
        }


    }

    private void handleWitchClass(Player currentPlayer) {
        Log.d("WitchClass", "handleWitchClass called");
        currentPlayer.useSkip();
        currentPlayer.setClassAbility(true);
    }

    private void showDialog(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.mainactivity_dialog_box, null);
        TextView dialogboxtextview = dialogView.findViewById(dialogbox_textview);
        dialogboxtextview.setText(string);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageButton closeButton = dialogView.findViewById(close_button);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void removeCharacterFromGame() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        int minRange = 10;
        int maxRange = 15;

        if (!isFirstTurn) {
            if (!soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                soldierRemoval = true;
                currentPlayer.setRemoved(true);
                showDialog(currentPlayer.getName() + " has escaped the game as the soldier.");
                Handler handler = new Handler();
                int delayMillis = 1;
                handler.postDelayed(currentPlayer::useSkip, delayMillis);
            } else if (soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                showDialog("Sorry " + currentPlayer.getName() + ", a soldier has already escaped the game.");
            }
        }
    }


    private void updateDrinkNumberCounterTextView() {
        String drinkNumberText;
        if (drinkNumberCounterInt == 1) {
            drinkNumberText = "1 Drink";
        } else {
            drinkNumberText = drinkNumberCounterInt + " Drinks";
        }
        drinkNumberCounterTextView.setText(drinkNumberText);
    }

    private void changeCurrentNumber() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.character_class_change_number, null);

        EditText editCurrentNumberText = dialogView.findViewById(editCurrentNumberTextView);
        Button okButton = dialogView.findViewById(close_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(view -> {
            try {
                String userInput = editCurrentNumberText.getText().toString();
                int newNumber = Integer.parseInt(userInput);
                if (newNumber > 999999999) {
                    displayToastMessage("That number was too high!");
                    btnClassAbility.setVisibility(View.VISIBLE);
                } else if (newNumber == 0) {
                    displayToastMessage("You cannot choose 0 as your number.");
                    btnClassAbility.setVisibility(View.VISIBLE);
                } else {
                    Game.getInstance().setCurrentNumber(newNumber);
                    SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(newNumber));
                    numberText.setText(String.valueOf(newNumber));
                    renderCurrentNumber(newNumber, this::gotoGameEnd, numberText);
                    currentPlayer.setClassAbility(true);
                    updateNumber(newNumber);


                    btnClassAbility.setVisibility(View.INVISIBLE);
                    dialog.dismiss(); // Close the dialog on success
                }
            } catch (NumberFormatException e) {
                displayToastMessage("Invalid number input");
            }
        });

        dialog.show();
    }

    //-----------------------------------------------------Wild Card Functionality---------------------------------------------------//
    private void wildCardActivate(Player player) {
        Game.getInstance().getCurrentPlayer().useWildCard();

        WildCardProperties[] emptyProbabilitiesArray = new WildCardProperties[0];
        QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.QUIZ);
        TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TASK);
        TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TRUTH);
        ExtrasWildCardsAdapter extraAdapter = new ExtrasWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.EXTRAS);

        WildCardProperties[] quizProbabilities = quizAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.QUIZ_WILD_CARDS);
        WildCardProperties[] taskProbabilities = taskAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.TASK_WILD_CARDS);
        WildCardProperties[] truthProbabilities = truthAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.TRUTH_WILD_CARDS);
        WildCardProperties[] extraProbabilities = extraAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.EXTRA_WILD_CARDS);

        List<WildCardProperties> allProbabilities = new ArrayList<>();
        allProbabilities.addAll(Arrays.asList(quizProbabilities));
        allProbabilities.addAll(Arrays.asList(taskProbabilities));
        allProbabilities.addAll(Arrays.asList(truthProbabilities));
        allProbabilities.addAll(Arrays.asList(extraProbabilities));

        final TextView wildActivityTextView = findViewById(textView_WildText);

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<WildCardProperties> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());

        if (wildCardsEnabled) {
            WildCardProperties[] selectedType = new WildCardProperties[0];
            String wildCardType = "";
            boolean foundWildCardType = false;
            Random random = new Random();

            int quizProbabilitiesCount = (int) Arrays.stream(quizProbabilities)
                    .filter(WildCardProperties::isEnabled)
                    .count();

            int taskProbabilitiesCount = (int) Arrays.stream(taskProbabilities)
                    .filter(WildCardProperties::isEnabled)
                    .count();

            int truthProbabilitiesCount = (int) Arrays.stream(truthProbabilities)
                    .filter(WildCardProperties::isEnabled)
                    .count();

            int extraProbabilitiesCount = (int) Arrays.stream(extraProbabilities)
                    .filter(WildCardProperties::isEnabled)
                    .count();

            if (quizProbabilitiesCount == 0 && taskProbabilitiesCount == 0 && truthProbabilitiesCount == 0 && extraProbabilitiesCount == 0) {
                foundWildCardType = true;
            }
            while (!foundWildCardType) {
                int typeChance = random.nextInt(4);
                switch (typeChance) {
                    case 0:
                        if (quizProbabilitiesCount > 0) {
                            selectedType = quizProbabilities;
                            wildCardType = "Quiz";
                            foundWildCardType = true;
                            btnClassAbility.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 1:
                        if (taskProbabilitiesCount > 0) {
                            selectedType = taskProbabilities;
                            wildCardType = "Task";
                            foundWildCardType = true;
                            btnClassAbility.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 2:
                        if (truthProbabilitiesCount > 0) {
                            selectedType = truthProbabilities;
                            wildCardType = "Truth";
                            foundWildCardType = true;
                            btnClassAbility.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 3:
                        if (extraProbabilitiesCount > 0) {
                            selectedType = extraProbabilities;
                            wildCardType = "Extras";
                            foundWildCardType = true;
                            btnClassAbility.setVisibility(View.INVISIBLE);
                        }
                        break;
                    default:
                        selectedType = null;
                        wildCardType = "Null";
                        foundWildCardType = true;
                        break;
                }

            }

            if (selectedType == quizProbabilities) {

                if (GeneralSettingsLocalStore.fromContext(this).isMultiChoice()) {
                    btnBackWild.setVisibility(View.INVISIBLE);
                } else {
                    btnAnswer.setVisibility(View.VISIBLE);
                    btnBackWild.setVisibility(View.INVISIBLE);
                }
            } else {
                btnAnswer.setVisibility(View.INVISIBLE);
                btnBackWild.setVisibility(View.VISIBLE);
            }

            int enabledCardsCount = (int) Arrays.stream(selectedType)
                    .filter(WildCardProperties::isEnabled)
                    .count();

            Log.d("SelectedTypeEnabledCount", "SelectedType Enabled Count: " + enabledCardsCount);

            if (enabledCardsCount == 0) {
                btnAnswer.setVisibility(View.INVISIBLE);
                wildActivityTextView.setText("No wild cards available");
                return;
            }

            List<WildCardProperties> unusedCards = Arrays.stream(selectedType)
                    .filter(WildCardProperties::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            int totalTypeProbabilities = unusedCards.stream()
                    .mapToInt(WildCardProperties::getProbability)
                    .sum();

            int selectedIndex = random.nextInt(totalTypeProbabilities);
            WildCardProperties selectedCard = null;
            int cumulativeProbability = 0;

            for (WildCardProperties card : unusedCards) {
                cumulativeProbability += card.getProbability();

                if (selectedIndex < cumulativeProbability) {
                    selectedCard = card;
                    break;
                }
            }

            if (selectedCard != null) {
                selectedActivity = selectedCard.getText();
                wildActivityTextView.setText(selectedActivity);
                assert usedCards != null;
                usedCards.add(selectedCard);
                usedWildCards.add(selectedCard);
                usedWildCard.put(player, usedCards);


                int textSize = TextSizeCalculator.calculateTextSizeBasedOnCharacterCount(selectedActivity);
                wildActivityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

                selectedWildCard = selectedCard; // Update selectedWildCard to the current selected card

                if (selectedCard.hasAnswer()) {
                    if (Objects.equals(Game.getInstance().getCurrentPlayer().getClassChoice(), "Quiz Magician")) {
                        setMultiChoiceRandomizedAnswersForQuizMagician(selectedCard);
                    } else if (GeneralSettingsLocalStore.fromContext(this).isMultiChoice()) {
                        setMultiChoiceRandomizedAnswers(selectedCard);
                    } else {
                        btnAnswer.setVisibility(View.VISIBLE);
                    }
                } else {
                    btnAnswer.setVisibility(View.INVISIBLE);
                }
            } else {
                btnAnswer.setVisibility(View.INVISIBLE);
            }
            Log.d("WildCardInfo", "Type: " + wildCardType + ", " +
                    "Question: " + selectedCard.getText() + ", " +
                    "Answer: " + selectedCard.getAnswer() + ", " +
                    "Wrong Answer 1: " + selectedCard.getWrongAnswer1() + ", " +
                    "Wrong Answer 2: " + selectedCard.getWrongAnswer2() + ", " +
                    "Wrong Answer 3: " + selectedCard.getWrongAnswer3() + ", " +
                    "Category: " + selectedCard.getCategory());
        }

        performWildCardAction(selectedActivity, player);
    }

    private void performWildCardAction(String selectedActivity, Player player) {
        switch (selectedActivity) {
            case "Double the current number!":
                doubleCurrentNumber();
                break;
            case "Half the current number!":
                halveCurrentNumber();
                break;
            case "Reset the number!":
                resetNumber();
                break;
            case "Reverse the turn order!":
                reverseTurnOrder(player);
                break;
            case "Gain a couple more wildcards to use, I gotchya back!":
                gainWildCards();
                break;
            case "Lose a couple wildcards :( oh also drink 3 lol!":
                loseWildCards();
                break;
            // Add more cases for other wild card actions if needed...
            default:
                // Handle default case if needed
                break;
        }
    }

// Methods for specific wild card actions

    private void doubleCurrentNumber() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        int updatedNumber = Math.min(currentNumber * 2, 999999999);
        updateNumber(updatedNumber);
    }

    private void halveCurrentNumber() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        int updatedNumber = Math.max(currentNumber / 2, 1);
        updateNumber(updatedNumber);
    }

    private void resetNumber() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }
        int startingNumber = extras.getInt("startingNumber");
        updateNumber(startingNumber);
    }

    private void updateNumber(int updatedNumber) {
        Game.getInstance().setCurrentNumber(updatedNumber);
        Game.getInstance().addUpdatedNumber(updatedNumber);
        numberText.setText(String.valueOf(updatedNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
    }

    private void gainWildCards() {
        Game.getInstance().getCurrentPlayer().gainWildCards(3);
    }

    private void loseWildCards() {
        Game.getInstance().getCurrentPlayer().loseWildCards(2);
    }

    //-----------------------------------------------------Quiz Multi-Choice---------------------------------------------------//


    private void setMultiChoiceRandomizedAnswers(WildCardProperties selectedCard) {
        exposeQuizButtons();

        // Shuffle the answers randomly
        String[] answers = {
                selectedCard.getAnswer(),
                selectedCard.getWrongAnswer1(),
                selectedCard.getWrongAnswer2(),
                selectedCard.getWrongAnswer3()
        };

        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);
        answers = answerList.toArray(new String[0]);

        // Set answers to buttons for four buttons scenario
        setAnswersToFourButtons(answers);
    }

    private void setMultiChoiceRandomizedAnswersForQuizMagician(WildCardProperties selectedCard) {
        exposeQuizButtons();

        // Assign two random answers
        Random random = new Random();
        String[] answers = {
                selectedCard.getAnswer(),
                random.nextBoolean() ? selectedCard.getWrongAnswer1() : (random.nextBoolean() ? selectedCard.getWrongAnswer2() : selectedCard.getWrongAnswer3())
        };

        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);
        answers = answerList.toArray(new String[0]);

        // Set answers to buttons for two buttons scenario
        setAnswersToTwoButtons(answers);
    }

    private void setAnswersToFourButtons(String[] answers) {
        answerButtons = new Button[]{btnQuizAnswerTL, btnQuizAnswerTR, btnQuizAnswerBL, btnQuizAnswerBR};

        for (int i = 0; i < BUTTON_COUNT; i++) {
            Button currentButton = answerButtons[i];
            currentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, quizAnswerTextSize(answers[i]));
            currentButton.setText(answers[i]);
            setButtonClickListener(currentButton, answers[i]);
        }
    }

    private void setAnswersToTwoButtons(String[] answers) {
        answerButtons = new Button[]{btnQuizAnswerTL, btnQuizAnswerTR};

        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);

        for (int i = 0; i < BUTTON_COUNT_2; i++) {
            Button currentButton = answerButtons[i];
            currentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, quizAnswerTextSize(answers[i]));
            currentButton.setText(answers[i]);
            setButtonClickListener(currentButton, answers[i]);
        }
    }

    private void setButtonClickListener(Button button, String answer) {
        btnUtils.setButton(button, () -> handleAnswerSelection(button, answer));
    }

    private void handleAnswerSelection(Button selectedButton, String selectedAnswer) {
        disableAllButtons(answerButtons);

        String correctAnswer = selectedWildCard.getAnswer();
        boolean isCorrect = selectedAnswer.equals(correctAnswer);

        if (isCorrect) {
            handleCorrectAnswer(selectedButton, correctAnswer);
        } else {
            handleIncorrectAnswer(selectedButton, correctAnswer);
        }
    }



    private void handleCorrectAnswer(Button selectedButton, String correctAnswer) {
        selectedButton.setBackgroundResource(R.drawable.buttonhighlightgreen);


        displayConfetti(Objects.requireNonNull(getConfettiView(selectedButton.getId())));

        new Handler().postDelayed(() -> {
            resetButtonBackgrounds(answerButtons);

            handleAnswerOutcome(selectedWildCard.getAnswer().equals(correctAnswer));
            enableAllButtons(answerButtons);
        }, DELAY_MILLIS);
    }

    private void handleIncorrectAnswer(Button selectedButton, String correctAnswer) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Game.getInstance().activateRepeatingTurn(currentPlayer);

        selectedButton.setBackgroundResource(R.drawable.buttonhighlightred);
        currentPlayer.setJustUsedWildCard(true);

        // Highlight the correct answer button in green
        for (Button button : answerButtons) {
            if (button.getText().toString().equals(correctAnswer)) {
                button.setBackgroundResource(R.drawable.buttonhighlightgreen);
                break;
            }
        }

        new Handler().postDelayed(() -> {
            resetButtonBackgrounds(answerButtons);
            handleAnswerOutcome(false);
            enableAllButtons(answerButtons);
        }, DELAY_MILLIS);
    }


    private void handleAnswerOutcome(boolean isCorrect) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        if (isCorrect) {
            currentPlayer.gainWildCards(1);
            quizAnswerView(currentPlayer.getName() + " that's right! The answer was " + selectedWildCard.getAnswer() + "\n\n P.S. Give out a drink, and you get to keep your wildcard too.");
        } else {
            quizAnswerView(currentPlayer.getName() + " big ooooff! The answer actually was " + selectedWildCard.getAnswer() + "\n\n Take a drink and repeat your turn.");
        }

        hideQuizButtons();
        btnBackWild.setVisibility(View.VISIBLE);
    }


    private void wildCardContinue() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        currentPlayer.useSkip();

        btnGenerate.setVisibility(View.VISIBLE);
        drinkNumberCounterTextView.setVisibility(View.VISIBLE);
        numberText.setVisibility(View.VISIBLE);
        nextPlayerText.setVisibility(View.VISIBLE);

        wildText.setVisibility(View.INVISIBLE);
        btnBackWild.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);
    }

    private void displayConfetti(View confettiView) {
        confettiView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> confettiView.setVisibility(View.INVISIBLE), 1500);
    }

    private View getConfettiView(int buttonId) {
        switch (buttonId) {
            case R.id.btnQuizAnswerTL:
                return confettiImageViewTL;
            case R.id.btnQuizAnswerTR:
                return confettiImageViewTR;
            case R.id.btnQuizAnswerBL:
                return confettiImageViewBL;
            case R.id.btnQuizAnswerBR:
                return confettiImageViewBR;
            default:
                return null;
        }
    }

    //-----------------------------------------------------Quiz---------------------------------------------------//

    private void quizAnswerView(String string) {
        btnBackWild.setVisibility(View.VISIBLE);
        wildText.setVisibility(View.VISIBLE);
        btnWild.setVisibility(View.INVISIBLE);
        btnGenerate.setVisibility(View.INVISIBLE);
        nextPlayerText.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.INVISIBLE);
        wildText.setText(string);
    }

    private void exposeQuizButtons() {
        btnQuizAnswerBL.setVisibility(View.VISIBLE);
        btnQuizAnswerBR.setVisibility(View.VISIBLE);
        btnQuizAnswerTL.setVisibility(View.VISIBLE);
        btnQuizAnswerTR.setVisibility(View.VISIBLE);

    }

    private void hideQuizButtons() {
        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);
        btnQuizAnswerTL.setVisibility(View.INVISIBLE);
        btnQuizAnswerTR.setVisibility(View.INVISIBLE);
    }


    private void showAnswer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        TextView wildActivityTextView = findViewById(textView_WildText);
        btnQuizAnswerBL.setVisibility(View.VISIBLE);
        btnQuizAnswerBR.setVisibility(View.VISIBLE);

        btnQuizAnswerBL.setText("Were you right?");
        btnQuizAnswerBR.setText("Were you wrong?");

        if (selectedWildCard != null) {
            if (selectedWildCard.hasAnswer()) {
                String answer = selectedWildCard.getAnswer();
                wildActivityTextView.setText(answer);
                Log.d("Answer", "Quiz WildCard: " + answer);

                btnBackWild.setVisibility(View.INVISIBLE);

                btnUtils.setButton(btnQuizAnswerBL, () -> {
                    Game.getInstance().getCurrentPlayer().gainWildCards(1);
                    btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                    btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it right, give out a drink! \n\n P.S. You get to keep your wildcard too.");
                });

                btnUtils.setButton(btnQuizAnswerBR, () -> {
                    btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                    btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it wrong, take a drink! \n\n P.S. Maybe read a book once in a while.");
                });

            } else {
                wildActivityTextView.setText("No answer available");
            }
        }
        btnAnswer.setVisibility(View.INVISIBLE);
    }
}
