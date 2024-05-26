package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.R.id.btnBackWildCard;
import static com.example.countingdowngame.R.id.btnExitGame;
import static com.example.countingdowngame.R.id.close_button;
import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.R.id.textView_NumberText;
import static com.example.countingdowngame.R.id.textView_Number_Turn;
import static com.example.countingdowngame.R.id.textView_WildText;
import static com.example.countingdowngame.R.id.textView_numberCounter;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_ARCHER;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_GOBLIN;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_SCIENTIST;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_SOLDIER;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_SURVIVOR;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_WITCH;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;
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

    //-----------------------------------------------------Constants---------------------------------------------------//
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private static final int BUTTON_COUNT = 4;
    private static final int BUTTON_COUNT_2 = 2;
    private static final int DELAY_MILLIS = 1500;


    //-----------------------------------------------------Public ---------------------------------------------------//
    public static int drinkNumberCounterInt = 0;
    //-----------------------------------------------------Maps and Sets---------------------------------------------------//
    private final Map<Player, Set<WildCardProperties>> usedWildCard = new HashMap<>();
    public WildCardProperties selectedWildCard;
    private int turnCounter = 0;
    private int catastropheTurnCounter = 0;
    private int catastropheLimit;

    //-----------------------------------------------------Views---------------------------------------------------//
    private Button btnAnswer;
    private Button btnWildContinue;
    private Button btnClassAbility;
    private Button btnGenerate;
    private Button btnQuizAnswerBL;
    private Button btnQuizAnswerBR;
    private Button btnQuizAnswerTL;
    private Button btnQuizAnswerTR;
    private Button btnWild;
    private GifImageView confettiImageViewBL;
    private GifImageView confettiImageViewBR;
    private GifImageView confettiImageViewTL;
    private GifImageView confettiImageViewTR;
    private GifImageView infoGif;
    private GifImageView muteGif;
    private GifImageView soundGif;
    private ImageView playerImage;
    private TextView drinkNumberTextView;
    private TextView nextPlayerText;
    private TextView numberCounterText;

    private TextView wildActivityTextView;

    private TextView wildText;

    //-----------------------------------------------------Booleans---------------------------------------------------//
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isFirstTurn = true;
    private boolean soldierRemoval = false;
    private boolean repeatedTurn = false;


    //-----------------------------------------------------Array---------------------------------------------------//
    private Button[] answerButtons; // Array to hold the answer buttons
    private Handler shuffleHandler;
    private MainActivityCatastrophes catastrophesManager;

    //-----------------------------------------------------Lifecycle Methods---------------------------------------------------//
    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
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
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtons();
        initializeCatastrophe();
        startGame();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        infoGif = findViewById(R.id.informationGif);


        playerImage = findViewById(R.id.playerImage);
        numberCounterText = findViewById(textView_NumberText);
        drinkNumberTextView = findViewById(textView_numberCounter);
        nextPlayerText = findViewById(textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        confettiImageViewBL = findViewById(R.id.confettiImageViewBL);
        confettiImageViewTL = findViewById(R.id.confettiImageViewTL);
        confettiImageViewBR = findViewById(R.id.confettiImageViewBR);
        confettiImageViewTR = findViewById(R.id.confettiImageViewTR);

        btnAnswer = findViewById(R.id.btnAnswer);
        btnClassAbility = findViewById(R.id.btnClassAbility);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnWildContinue = findViewById(btnBackWildCard);


        btnQuizAnswerBL = findViewById(R.id.btnQuizAnswerBL);
        btnQuizAnswerBR = findViewById(R.id.btnQuizAnswerBR);
        btnQuizAnswerTL = findViewById(R.id.btnQuizAnswerTL);
        btnQuizAnswerTR = findViewById(R.id.btnQuizAnswerTR);
        wildActivityTextView = findViewById(textView_WildText);


        shuffleHandler = new Handler();
        wildText = findViewById(textView_WildText);
    }

    private void startGame() {
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
                player.resetWildCardAmount(this); // Reset wild card count using GeneralSettingsLocalStore
                player.setGame(Game.getInstance());
                player.setUsedClassAbility(false); // Initialize class ability to false
            }

        }

        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });
        renderPlayer();
        drinkNumberCounterInt = 1;
        updateDrinkNumberCounterTextView();
    }


    public void initializeCatastrophe() {
        // Initialize the catastrophes manager using the default constructor
        catastrophesManager = new MainActivityCatastrophes();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupButtons() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("setupButtons", " " + currentPlayer.getClassChoice());

        ImageButton imageButtonExit = findViewById(btnExitGame);

        btnWildContinue.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, () -> {
            startNumberShuffleAnimation();
            isFirstTurn = false;
        });

        playerImage.setOnClickListener(v -> characterClassDescriptions());
        btnUtils.setButton(btnAnswer, this::showAnswer);
        btnUtils.setButton(btnWildContinue, this::wildCardContinue);
        btnUtils.setButton(btnClassAbility, this::activateActiveAbility);


        btnUtils.setButton(btnWild, () -> {
            wildCardActivate(Game.getInstance().getCurrentPlayer());
            drinkNumberTextView.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberCounterText.setVisibility(View.INVISIBLE);
            isFirstTurn = false;
        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });

        infoGif.setOnClickListener(view -> showInstructionDialog());

    }

    private void characterClassDescriptions() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            String currentPlayerClassChoice = currentPlayer.getClassChoice();
            if (currentPlayerClassChoice != null) {
                characterClassInformationDialog(currentPlayerClassChoice, getClassActiveDescription(currentPlayerClassChoice), getClassPassiveDescription(currentPlayerClassChoice));
            }
        }
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        updateAbilitiesAfterThreeTurns(currentPlayer);
        updateClassAbilityButton(currentPlayer);
        updatePlayerInfo(currentPlayer);
        updateNumberText();
        updateTurnCounter();
        logPlayerInformation(currentPlayer);
        updateWildCardVisibilityIfNeeded(currentPlayer);
        setCatastropheLimit();
        updateCatastropheTurnCounter();
    }

    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView generatedNumberTextView) {
        if (currentNumber == 0) {
            btnGenerate.setEnabled(false);
            btnWild.setEnabled(false);
            btnClassAbility.setEnabled(false);

            generatedNumberTextView.setText(String.valueOf(currentNumber));
            animateTextView(generatedNumberTextView);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                btnUtils.playSoundEffects();
                Game.getInstance().endGame(this);
                onEnd.run();
            }, 3300);
        } else {
            generatedNumberTextView.setText(String.valueOf(currentNumber));
            Game.getInstance().nextPlayer();
        }
    }

    //-----------------------------------------------------Update Player's Info---------------------------------------------------//

    private void updateTurnCounter() {
        turnCounter++;
        if (turnCounter == 4) {
            updateDrinkNumberCounter(1, false);
            turnCounter = 0;
        }
    }


    private void updateCatastropheTurnCounter() {
        MainActivityCatastrophes.Catastrophe catastrophe = catastrophesManager.deployCatastrophe();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        catastropheTurnCounter++;
        Log.d(TAG, "updateCatastropheTurnCounter: " + catastropheTurnCounter);

        Log.d("Before switch", "drinkNumberCounterInt: " + drinkNumberCounterInt);

        if (catastropheTurnCounter == catastropheLimit) {
            switch (catastrophe.getEffect()) {
                case 1:
                    drinkNumberCounterInt += 2;
                    updateDrinkNumberCounterTextView();
                    break;
                case 2:
                    drinkNumberCounterInt -= 2;
                    updateDrinkNumberCounterTextView();
                case 3:
                    doubleCurrentNumber();
                    break;
                case 4:
                    halveCurrentNumber();
                    break;
                case 5:
                    reverseTurnOrder(currentPlayer);
                    break;
                case 6:
                    for (Player player : Game.getInstance().getPlayers()) {
                        player.gainWildCards(2);
                    }
                    renderPlayer();
                    break;
                case 7:
                    for (Player player : Game.getInstance().getPlayers()) {
                        player.loseWildCards(2);
                    }
                    renderPlayer();
                    break;
                case 9:
                    Game.getInstance().activateRepeatingTurnForAllPlayers(2);
                    drinkNumberCounterInt -= 2;
                    updateDrinkNumberCounterTextView();
                default:
                    break;
            }
            showCatastropheDialog(catastrophe.getMessage());
            catastropheTurnCounter = 0; // Reset the turn counter after reaching the limit

            // Generate a new random catastrophe limit
            Random random = new Random();
            catastropheLimit = random.nextInt(4) + 4; // Generates a number between 4 and 7 (inclusive)
        }
    }

    private void setCatastropheLimit() {
        // Generate a random number between 4 and 10 for the catastrophe limit
        Random random = new Random();
        catastropheLimit = random.nextInt(4) + 4; // Generates a number between 4 and 7 (inclusive)
    }

    private void updateWildCardVisibilityIfNeeded(Player currentPlayer) {
        if (repeatedTurn) {
            btnWild.setVisibility(View.INVISIBLE);
            repeatedTurn = false;
        } else {
            if (!currentPlayer.getJustUsedWildCard()) {
                updateWildCardVisibility(currentPlayer);
                characterPassiveClassAffects();
            }
        }
        if (currentPlayer.getJustUsedWildCard()) {
            btnWild.setVisibility(View.INVISIBLE);
            currentPlayer.setJustUsedWildCard(false);
        }
    }

    private void updateClassAbilityButton(Player currentPlayer) {
        btnClassAbility.setText(String.format("%s's Ability", currentPlayer.getClassChoice()));

        boolean showClassAbilityButton = ("Scientist".equals(currentPlayer.getClassChoice()) ||
                "Archer".equals(currentPlayer.getClassChoice()) ||
                "Witch".equals(currentPlayer.getClassChoice()) ||
                "Quiz Magician".equals(currentPlayer.getClassChoice()) ||
                "Survivor".equals(currentPlayer.getClassChoice()) ||
                "Goblin".equals(currentPlayer.getClassChoice()) ||
                "Angry Jim".equals(currentPlayer.getClassChoice()) ||
                "Soldier".equals(currentPlayer.getClassChoice())) &&
                !currentPlayer.getUsedClassAbility();

        Log.d(TAG, "updateClassAbilityButton: " + currentPlayer.getUsedClassAbility());

        btnClassAbility.setVisibility(showClassAbilityButton ? View.VISIBLE : View.INVISIBLE);
        if ("No Class".equals(currentPlayer.getClassChoice())) {
            btnClassAbility.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePlayerInfo(Player currentPlayer) {
        String playerName = currentPlayer.getName();
        String playerImageString = currentPlayer.getPhoto();
        Game.getInstance().addUpdatedName(playerName);

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

    //-----------------------------------------------------Update Drink Number Counter---------------------------------------------------//

    private void updateDrinkNumberCounter(int drinkNumberCounterInput, boolean activatedByAbility) {
        int maxTotalDrinkAmount = GeneralSettingsLocalStore.fromContext(this).totalDrinkAmount();
        int potentialNewValue = drinkNumberCounterInt + drinkNumberCounterInput;
        // Increment the counter
        if (drinkNumberCounterInput > 0) {
            if (!activatedByAbility & potentialNewValue <= maxTotalDrinkAmount) {
                drinkNumberCounterInt = potentialNewValue;
            } else if (activatedByAbility) {
                drinkNumberCounterInt += drinkNumberCounterInput;

            }
        }
        // Decrement the counter
        else if (drinkNumberCounterInput < 0) {
            if (!activatedByAbility) {
                drinkNumberCounterInt = Math.max(potentialNewValue, 0);
            } else {
                drinkNumberCounterInt = Math.max(drinkNumberCounterInt + drinkNumberCounterInput, 0);
            }
        }

        updateDrinkNumberCounterTextView();
    }


    private void updateDrinkNumberCounterTextView() {
        int maxTotalDrinkAmount = GeneralSettingsLocalStore.fromContext(this).totalDrinkAmount();

        String drinkNumberText;
        if (drinkNumberCounterInt <= maxTotalDrinkAmount) {
            if (drinkNumberCounterInt == 1) {
                drinkNumberText = "1 Drink";
            } else {
                drinkNumberText = drinkNumberCounterInt + " Drinks";
            }
        } else {
            drinkNumberText = maxTotalDrinkAmount + " (+" + (drinkNumberCounterInt - maxTotalDrinkAmount) + ") Drinks";
        }

        drinkNumberTextView.setText(drinkNumberText);
    }


    private void updateNumberText() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        numberCounterText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(currentNumber));
        SharedMainActivity.setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());
    }


    //-----------------------------------------------------Shuffler---------------------------------------------------//

    private void startNumberShuffleAnimation() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);
        btnClassAbility.setEnabled(false);
        playerImage.setEnabled(false);
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        int originalNumber = Game.getInstance().getCurrentNumber(); // Store the original number
        final int shuffleDuration = 1500;

        int shuffleInterval = originalNumber >= 1000 ? 50 : 100;

        final Random random = new Random();
        shuffleHandler.postDelayed(new Runnable() {
            int shuffleTime = 0;

            @Override
            public void run() {
                int randomDigit = random.nextInt(originalNumber + 1);
                numberCounterText.setText(String.valueOf(randomDigit));

                shuffleTime += shuffleInterval;

                if (shuffleTime < shuffleDuration) {
                    shuffleHandler.postDelayed(this, shuffleInterval);
                } else {
                    numberCounterText.setText(String.valueOf(randomDigit));
                    int currentNumber = Game.getInstance().nextNumber();
                    Log.d(TAG, "NextNumber = " + currentNumber);

                    if ("Survivor".equals(currentPlayer.getClassChoice()) && originalNumber == 1 && currentNumber == 1) {
                        handleSurvivorPassive(currentPlayer);
                    }

                    renderCurrentNumber(currentNumber, () -> gotoGameEnd(), numberCounterText);

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

    //-----------------------------------------------------Active Effects---------------------------------------------------//
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
            case CLASS_SURVIVOR:
                return CharacterClassDescriptions.survivorActiveDescription;
            case CLASS_ANGRY_JIM:
                return CharacterClassDescriptions.angryJimActiveDescription;
            case CLASS_GOBLIN:
                return CharacterClassDescriptions.goblinActiveDescription;
            default:
                return "I love you cutie pie hehe. You don't have a class to show any description for.";
        }
    }

    public void activateActiveAbility() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("activateActiveAbility", "Class Activated: " + currentPlayer.getClassChoice());
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
            case "Survivor":
                handleSurvivorClass(currentPlayer);
                break;
            case "Goblin":
                handleGoblinClass(currentPlayer);
                break;
            case "Angry Jim":
                handleAngryJimClass(currentPlayer);
                break;
            default:
                break;
        }
    }

    private void handleScientistClass(Player currentPlayer) {
        scientistChangeCurrentNumber();
        Game.getInstance().activateRepeatingTurn(currentPlayer, 1);
    }

    private void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() <= 10) {
                currentPlayer.setUsedClassAbility(true);
                Game.getInstance().activateRepeatingTurn(currentPlayer, 1);
                repeatedTurn = true;
                updateDrinkNumberCounter(4, true);
                updateDrinkNumberCounterTextView();
                btnWild.setVisibility(View.INVISIBLE);
                btnClassAbility.setVisibility(View.INVISIBLE);
            } else {
                displayToastMessage("The +4 ability can only be activated when the number is below 10.");
            }
        } else {
            displayToastMessage("Cannot activate on the first turn.");
        }
    }

    private void handleQuizMagicianClass(Player currentPlayer) {
        currentPlayer.setUsedClassAbility(true);
        currentPlayer.setJustUsedClassAbility(true);
        btnClassAbility.setVisibility(View.INVISIBLE);
    }

    private void handleGoblinClass(Player currentPlayer) {
        Game game = Game.getInstance();
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        randomPlayer.removeWildCard(randomPlayer, 1);
        currentPlayer.setUsedClassAbility(true);
        showGameDialog("Goblin's Active: \n\n" + randomPlayer.getName() + " lost a wildcard!");
        btnClassAbility.setVisibility(View.INVISIBLE);
    }

    private void handleAngryJimClass(Player currentPlayer) {
        Game game = Game.getInstance();
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        if (randomPlayer != null) {
            game.activateRepeatingTurn(randomPlayer, 1); // Assuming 1 turn for repeating
            showGameDialog("Angry Jim's Active: \n\n" + randomPlayer.getName() + " must repeat their turn.");
            btnClassAbility.setVisibility(View.INVISIBLE);
            currentPlayer.setUsedClassAbility(true);
        } else {
            Log.e(TAG, "No players available to select for repeating turn.");
        }
    }


    private void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            halveCurrentNumber();
            currentPlayer.setUsedClassAbility(true);
            btnClassAbility.setVisibility(View.INVISIBLE);
        } else {
            displayToastMessage("You can only halve the number when it is greater than 1.");
        }
    }

    private void updateAbilitiesAfterThreeTurns(Player currentPlayer) {
        if (("Survivor".equals(currentPlayer.getClassChoice()) ||
                "Witch".equals(currentPlayer.getClassChoice()) ||
                "Goblin".equals(currentPlayer.getClassChoice())) &&
                currentPlayer.getUsedClassAbility()) {
            currentPlayer.incrementAbilityTurnCounter();
            Log.d(TAG, "increment counter: " + currentPlayer.getAbilityTurnCounter());
            if (currentPlayer.getAbilityTurnCounter() == 4) {
                Log.d(TAG, "updateAbilitiesAfterThreeTurns: " + currentPlayer.getName());
                currentPlayer.setUsedClassAbility(false);
                currentPlayer.resetSpecificTurnCounter();
            }
        }
    }


    private void handleArcherClass(Player currentPlayer) {
        Log.d("ArcherClass", "handleArcherClass called");

        if (drinkNumberCounterInt >= 2) {
            showGameDialog("Archer's Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setUsedClassAbility(true);
            updateDrinkNumberCounter(-2, true);
            updateDrinkNumberCounterTextView();
            btnClassAbility.setVisibility(View.INVISIBLE);
        } else {
            displayToastMessage("There must be more than two total drinks.");
        }
    }

    private void handleWitchClass(Player currentPlayer) {
        Log.d("WitchClass", "handleWitchClass called");
        currentPlayer.setUsedClassAbility(true);
        currentPlayer.useSkip();
    }

    //-----------------------------------------------------Passive Effects---------------------------------------------------//
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
            case CLASS_SURVIVOR:
                return CharacterClassDescriptions.survivorPassiveDescription;
            case CLASS_ANGRY_JIM:
                return CharacterClassDescriptions.angryJimPassiveDescription;
            case CLASS_GOBLIN:
                return CharacterClassDescriptions.goblinPassiveDescription;
            default:
                return "";
        }
    }

    private void characterPassiveClassAffects() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        // Check the class choice of the current player
        String classChoice = currentPlayer.getClassChoice();

        // Handle passive effects based on the player's class choice
        if ("Soldier".equals(classChoice)) {
            handleSoldierPassive(currentPlayer);
        } else if ("Witch".equals(classChoice)) {
            handleWitchPassive(currentPlayer);
        } else if ("Scientist".equals(classChoice)) {
            handleScientistPassive(currentPlayer);
        } else if ("Angry Jim".equals(classChoice)) {
            handleAngryJimPassive(currentPlayer);
        } else if ("Archer".equals(classChoice)) {
            handleArcherPassive(currentPlayer);
        } else if ("Goblin".equals(classChoice)) {
            handleGoblinPassive(currentPlayer);
        }
    }


    private void handleSoldierPassive(Player currentPlayer) {
        new Handler().postDelayed(() -> {
            if (!currentPlayer.isRemoved()) {
                removeCharacterFromGame();
            } else {
                currentPlayer.useSkip();
            }
        }, 1);
    }


    private void handleWitchPassive(Player currentPlayer) {
        if (!isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() % 2 == 0) {
                showGameDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " hand out two drinks.");
                currentPlayer.incrementDrinksHandedOutByWitch(2);
            } else {
                showGameDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " take two drinks.");
                currentPlayer.incrementDrinksTakenByWitch(2);
            }
        }
    }


    private void handleScientistPassive(Player currentPlayer) {
        Handler handler = new Handler();
        int delayMillis = 1;
        int chance = new Random().nextInt(100);

        handler.postDelayed(() -> {
            if (chance < 10) {
                showGameDialog("Scientist's Passive: \n\n" + currentPlayer.getName() + " is a scientist and their turn was skipped. ");
                currentPlayer.useSkip();
            }
        }, delayMillis);
    }

    private void handleAngryJimPassive(Player currentPlayer) {
        // Increment Angry Jim's specific turn counter

        // Check if the current number is below 50
        if (Game.getInstance().getCurrentNumber() < 50 && currentPlayer.getAngryJimTurnCounter() != 1) {
            // Execute each player's passive abilities
            handleWitchPassive(currentPlayer);
            handleScientistPassive(currentPlayer);
            handleArcherPassive(currentPlayer);
            handleGoblinPassive(currentPlayer);
            handleGoblinPassive(currentPlayer);
            Game.getInstance().activateRepeatingTurn(currentPlayer, 1);

            currentPlayer.incrementAbilityTurnCounter();
            currentPlayer.incrementAngryJimTurnCounter();

            Log.d(TAG, "handleAngryJimPassive: is on");
        } else if (currentPlayer.getAngryJimTurnCounter() == 1) {
            currentPlayer.resetAngryJimTurnCounter();
        }
    }


    private void handleGoblinPassive(Player currentPlayer) {
        // Check if the current player is Angry Jim
        if (!"Angry Jim".equals(currentPlayer.getClassChoice())) {
            // Increment the turn counter for non-Angry Jim players
            currentPlayer.incrementAbilityTurnCounter();
        }
        if (currentPlayer.getAbilityTurnCounter() == 3) {
            currentPlayer.resetSpecificTurnCounter();
            currentPlayer.gainWildCards(1);
        }
    }

    private void handleArcherPassive(Player currentPlayer) {
        // Check if the current player is Angry Jim
        if (!"Angry Jim".equals(currentPlayer.getClassChoice())) {
            // Increment the turn counter for non-Angry Jim players
            currentPlayer.incrementAbilityTurnCounter();
        }

        if (currentPlayer.getAbilityTurnCounter() == 3) {
            currentPlayer.resetSpecificTurnCounter();
            Log.d("ArcherClass", "Passive ability triggered");

            int chance = new Random().nextInt(100);
            if (chance < 60) {
                updateDrinkNumberCounter(2, true);
                updateDrinkNumberCounterTextView();
                showGameDialog("Archer's Passive: \n\nDrinking number increased by 2!");
            } else {
                updateDrinkNumberCounter(-2, true);
                if (drinkNumberCounterInt < 0) {
                    drinkNumberCounterInt = 0;
                }
                updateDrinkNumberCounterTextView();
                showGameDialog("Archer's Passive: \n\nDrinking number decreased by 2!");
            }
        }
    }

    private boolean currentPlayerHasQuizMagicianPassive(Player currentPlayer) {
        return "Quiz Magician".equals(currentPlayer.getClassChoice()) || ("Angry Jim".equals(currentPlayer.getClassChoice()) && Game.getInstance().getCurrentNumber() < 50);
    }

    private void handleSurvivorPassive(Player currentPlayer) {
        int currentNumber = Game.getInstance().getCurrentNumber();
        Log.d(TAG, "handleSurvivorPassive: current number = " + currentNumber);

        String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";

        if ("Survivor".equals(currentPlayer.getClassChoice()) && currentNumber == 1) {
            showGameDialog("Survivor's Passive: \n\n" + currentPlayer.getName() + " survived a 1, hand out " + drinkNumberCounterInt + " " + drinksText);
        }
    }


    //-----------------------------------------------------External Class Effects---------------------------------------------------//

    private void showDialog(String message, int layoutId, int textViewId, int closeButtonId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);
        TextView dialogBoxTextView = dialogView.findViewById(textViewId);
        dialogBoxTextView.setText(message);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageButton closeButton = dialogView.findViewById(closeButtonId);
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showGameDialog(String message) {
        showDialog(message, R.layout.game_dialog_box, R.id.dialogbox_textview, R.id.close_button);
    }

    private void showCatastropheDialog(String message) {
        showDialog(message, R.layout.catastrophe_dialog_box, R.id.dialogbox_textview, R.id.close_button);
    }

    private void removeCharacterFromGame() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        int currentNumber = Game.getInstance().getCurrentNumber();

        int minRange = 10;
        int maxRange = 15;

        if (!isFirstTurn) {
            if (!soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                soldierRemoval = true;
                currentPlayer.setRemoved(true);
                showGameDialog(currentPlayer.getName() + " has escaped the game as the soldier.");
                Handler handler = new Handler();
                int delayMillis = 1;
                handler.postDelayed(currentPlayer::useSkip, delayMillis);
            } else if (soldierRemoval && currentNumber >= minRange && currentNumber <= maxRange) {
                showGameDialog("Sorry " + currentPlayer.getName() + ", a soldier has already escaped the game.");
            }
        }
    }


    private void scientistChangeCurrentNumber() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.character_class_change_number, null);

        EditText editCurrentNumberText = dialogView.findViewById(editCurrentNumberTextView);
        Button okButton = dialogView.findViewById(close_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);

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
                    Player currentPlayer = Game.getInstance().getCurrentPlayer();
                    Game.getInstance().setCurrentNumber(newNumber);
                    SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(newNumber));
                    numberCounterText.setText(String.valueOf(newNumber));
                    renderCurrentNumber(newNumber, this::gotoGameEnd, numberCounterText);
                    currentPlayer.setUsedClassAbility(true);
                    updateNumber(newNumber);
                    updatePlayerInfo(currentPlayer);

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
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Game.getInstance().getCurrentPlayer().useWildCard();
        currentPlayer.incrementUsedWildcards();

        WildCardProperties[] emptyProbabilitiesArray = new WildCardProperties[0];
        QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.QUIZ);
        TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TASK);
        TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TRUTH);

        WildCardProperties[] quizProbabilities = quizAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.QUIZ_WILD_CARDS);
        WildCardProperties[] taskProbabilities = taskAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.TASK_WILD_CARDS);
        WildCardProperties[] truthProbabilities = truthAdapter.loadWildCardProbabilitiesFromStorage(WildCardData.TRUTH_WILD_CARDS);

        WildCardProperties[] selectedType = selectWildCardType(currentPlayer, quizProbabilities, taskProbabilities, truthProbabilities);
        if (selectedType == null) {
            wildActivityTextView.setText("No wild cards available");
            return;
        }

        WildCardProperties selectedCard = selectRandomCard(player, selectedType);
        handleSelectedCard(selectedCard, getWildCardType(selectedType, quizProbabilities, taskProbabilities, truthProbabilities), player);
        btnClassAbility.setVisibility(View.INVISIBLE);

    }

    private WildCardProperties[] selectWildCardType(Player currentPlayer, WildCardProperties[] quizProbabilities, WildCardProperties[] taskProbabilities, WildCardProperties[] truthProbabilities) {
        if ("Quiz Magician".equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedClassAbility()) {
            btnClassAbility.setVisibility(View.INVISIBLE);
            return quizProbabilities;
        }

        List<WildCardProperties[]> enabledTypes = new ArrayList<>();
        addIfEnabled(enabledTypes, quizProbabilities);
        addIfEnabled(enabledTypes, taskProbabilities);
        addIfEnabled(enabledTypes, truthProbabilities);

        if (enabledTypes.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return enabledTypes.get(random.nextInt(enabledTypes.size()));
    }

    private void addIfEnabled(List<WildCardProperties[]> enabledTypes, WildCardProperties[] probabilities) {
        if (Arrays.stream(probabilities).anyMatch(WildCardProperties::isEnabled)) {
            enabledTypes.add(probabilities);
        }
    }

    private WildCardProperties selectRandomCard(Player player, WildCardProperties[] selectedType) {
        Set<WildCardProperties> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        List<WildCardProperties> unusedCards = Arrays.stream(selectedType)
                .filter(WildCardProperties::isEnabled)
                .filter(c -> {
                    assert usedCards != null;
                    return !usedCards.contains(c);
                })
                .collect(Collectors.toList());

        int totalTypeProbabilities = unusedCards.stream().mapToInt(WildCardProperties::getProbability).sum();
        int selectedIndex = new Random().nextInt(totalTypeProbabilities);
        int cumulativeProbability = 0;

        for (WildCardProperties card : unusedCards) {
            cumulativeProbability += card.getProbability();
            if (selectedIndex < cumulativeProbability) {
                return card;
            }
        }
        return null;
    }

    private String getWildCardType(WildCardProperties[] selectedType, WildCardProperties[] quizProbabilities, WildCardProperties[] taskProbabilities, WildCardProperties[] truthProbabilities) {
        if (selectedType == quizProbabilities) return "Quiz";
        if (selectedType == taskProbabilities) return "Task";
        if (selectedType == truthProbabilities) return "Truth";
        return "Extras";
    }

    public void handleSelectedCard(WildCardProperties selectedCard, String wildCardType, Player player) {
        if (selectedCard != null) {
            updateSelectedCard(selectedCard);
            setAnswersAndVisibility(selectedCard, player);
            logSelectedCardInfo(selectedCard, wildCardType);
            performWildCardAction(selectedCard.getText(), player);
        } else {
            btnAnswer.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSelectedCard(WildCardProperties selectedCard) {
        String selectedActivity = selectedCard.getText();
        wildActivityTextView.setText(selectedActivity);
        updateTextSize(selectedActivity);
        selectedWildCard = selectedCard;
    }

    private void updateTextSize(String selectedActivity) {
        int textSize = TextSizeCalculator.calculateTextSizeBasedOnCharacterCount(selectedActivity);
        wildActivityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private void setAnswersAndVisibility(WildCardProperties selectedCard, Player currentPlayer) {
        if (selectedCard.hasAnswer()) {
            if (currentPlayerHasQuizMagicianPassive(currentPlayer)) {
                setMultiChoiceRandomizedAnswersForQuizMagician(selectedCard);
                btnAnswer.setVisibility(View.INVISIBLE);
            } else if (GeneralSettingsLocalStore.fromContext(this).isMultiChoice()) {
                setMultiChoiceRandomizedAnswers(selectedCard);
                btnAnswer.setVisibility(View.INVISIBLE);
            } else {
                btnAnswer.setVisibility(View.VISIBLE);
            }
        } else {
            btnAnswer.setVisibility(View.INVISIBLE);
            btnWildContinue.setVisibility(View.VISIBLE);
        }
    }


    private void logSelectedCardInfo(WildCardProperties selectedCard, String wildCardType) {
        Log.d("WildCardInfo", "Type: " + wildCardType + ", " +
                "Question: " + selectedCard.getText() + ", " +
                "Answer: " + selectedCard.getAnswer() + ", " +
                "Wrong Answer 1: " + selectedCard.getWrongAnswer1() + ", " +
                "Wrong Answer 2: " + selectedCard.getWrongAnswer2() + ", " +
                "Wrong Answer 3: " + selectedCard.getWrongAnswer3() + ", " +
                "Category: " + selectedCard.getCategory());
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
                Game.getInstance().getCurrentPlayer().gainWildCards(3);
                break;
            case "Lose a couple wildcards :( oh also drink 3 lol!":
                player.loseWildCards(2);
                break;
            default:
                break;
        }
    }

    //-----------------------------------------------------Specific WildCard Functions---------------------------------------------------//

    private void doubleCurrentNumber() {
        Game game = Game.getInstance();
        int currentNumber = game.getCurrentNumber();
        int updatedNumber = Math.min(currentNumber * 2, 999999999);
        updateNumber(updatedNumber);
    }


    private void halveCurrentNumber() {
        Game game = Game.getInstance();
        int currentNumber = game.getCurrentNumber();
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
        numberCounterText.setText(String.valueOf(updatedNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(updatedNumber));
    }


    //-----------------------------------------------------Quiz Multi-Choice---------------------------------------------------//


    private void setMultiChoiceRandomizedAnswers(WildCardProperties selectedCard) {
        exposeQuizButtons();

        Log.d(TAG, "setMultiChoiceRandomizedAnswers: ");

        String[] answers = {
                selectedCard.getAnswer(),
                selectedCard.getWrongAnswer1(),
                selectedCard.getWrongAnswer2(),
                selectedCard.getWrongAnswer3()
        };

        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);
        answers = answerList.toArray(new String[0]);

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
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Game.getInstance().incrementPlayerQuizCorrectAnswers(currentPlayer);

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
        Game.getInstance().activateRepeatingTurn(currentPlayer, 1);
        Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);


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
            quizAnswerView(currentPlayer.getName() + " that's right! The answer was " + selectedWildCard.getAnswer() + "\n\n P.S. You get to give out a drink");
        } else {
            quizAnswerView(currentPlayer.getName() + " big ooooff! The answer actually was " + selectedWildCard.getAnswer() + "\n\n Take a drink and repeat your turn.");
        }

        hideQuizButtons();
        btnWildContinue.setVisibility(View.VISIBLE);
    }


    private void wildCardContinue() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if ("Quiz Magician".equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedClassAbility()) {
            wildCardActivate(currentPlayer);
            currentPlayer.gainWildCards(1);
            drinkNumberTextView.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberCounterText.setVisibility(View.INVISIBLE);
            btnWildContinue.setVisibility(View.INVISIBLE);
            currentPlayer.setUsedClassAbility(true);
            currentPlayer.setJustUsedClassAbility(false);
        } else {
            currentPlayer.useSkip();

            btnGenerate.setVisibility(View.VISIBLE);
            drinkNumberTextView.setVisibility(View.VISIBLE);
            numberCounterText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);

            wildText.setVisibility(View.INVISIBLE);
            btnWildContinue.setVisibility(View.INVISIBLE);
            btnAnswer.setVisibility(View.INVISIBLE);
            btnQuizAnswerBL.setVisibility(View.INVISIBLE);
            btnQuizAnswerBR.setVisibility(View.INVISIBLE);

        }
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
        btnWildContinue.setVisibility(View.VISIBLE);
        wildText.setVisibility(View.VISIBLE);
        btnWild.setVisibility(View.INVISIBLE);
        btnGenerate.setVisibility(View.INVISIBLE);
        nextPlayerText.setVisibility(View.INVISIBLE);
        numberCounterText.setVisibility(View.INVISIBLE);
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

        btnQuizAnswerBL.setVisibility(View.VISIBLE);
        btnQuizAnswerBR.setVisibility(View.VISIBLE);

        btnQuizAnswerBL.setText("Were you right?");
        btnQuizAnswerBR.setText("Were you wrong?");

        if (selectedWildCard != null) {
            if (selectedWildCard.hasAnswer()) {
                String answer = selectedWildCard.getAnswer();
                wildActivityTextView.setText(answer);
                Log.d("Answer", "Quiz WildCard: " + answer);

                btnWildContinue.setVisibility(View.INVISIBLE);

                btnUtils.setButton(btnQuizAnswerBL, () -> {
                    btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                    btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it right, give out a drink!");
                    Game.getInstance().incrementPlayerQuizCorrectAnswers(currentPlayer);
                });

                btnUtils.setButton(btnQuizAnswerBR, () -> {
                    btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                    btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it wrong, take a drink! \n\n P.S. Maybe read a book once in a while.");
                    Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);
                });

            } else {
                wildActivityTextView.setText("No answer available");
            }
        }
        btnAnswer.setVisibility(View.INVISIBLE);
    }

}
