package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.R.id.close_button;
import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.decreaseNumberByRandom;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.increaseNumberByRandom;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.setCatastropheLimit;
import static com.example.countingdowngame.mainActivity.MainActivityLogging.logPlayerInformation;
import static com.example.countingdowngame.mainActivity.MainActivityLogging.logSelectedCardInfo;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleSoldierPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleWitchPassive;

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
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;
import com.example.countingdowngame.wildCards.wildCardTypes.QuizWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TaskWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TruthWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityGame extends SharedMainActivity {

    //-----------------------------------------------------Constants---------------------------------------------------//
    static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private static final int BUTTON_COUNT = 4;
    private static final int BUTTON_COUNT_2 = 2;
    private static final int DELAY_MILLIS = 1500;
    //-----------------------------------------------------Public ---------------------------------------------------//
    public static int drinkNumberCounterInt = 0;
    public static int catastropheLimit;
    public static boolean isFirstTurn = true;
    public static boolean soldierRemoval = false;
    private static TextView numberCounterText;
    //-----------------------------------------------------Maps and Sets---------------------------------------------------//
    private final List<WildCardProperties> usedCards = new ArrayList<>();  // Class-level variable to track used cards
    public WildCardProperties selectedWildCard;
    Game game = Game.getInstance();
    private int turnCounter = 0;
    private int catastropheTurnCounter = 0;
    //-----------------------------------------------------Views---------------------------------------------------//
    private Button btnAnswer, btnWildContinue, btnClassAbility, btnGenerate, btnQuizAnswerBL, btnQuizAnswerBR, btnQuizAnswerTL, btnQuizAnswerTR, btnWild;
    private GifImageView confettiImageViewBL, confettiImageViewBR, confettiImageViewTL, confettiImageViewTR, infoGif, muteGif, soundGif;
    private ImageView playerImage;
    private TextView drinkNumberTextView, nextPlayerText, wildActivityTextView, wildText;
    private ImageButton imageButtonExit;
    //-----------------------------------------------------Booleans---------------------------------------------------//
    private boolean doubleBackToExitPressedOnce = false;
    private boolean repeatedTurn = false;
    //-----------------------------------------------------Array---------------------------------------------------//
    private Button[] answerButtons; // Array to hold the answer buttons
    private Handler shuffleHandler;
    private MainActivityCatastrophes catastrophesManager;

    static void updateNumber(int updatedNumber) {
        Game.getInstance().setCurrentNumber(updatedNumber);
        numberCounterText.setText(String.valueOf(updatedNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(updatedNumber));
    }

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
        PassiveAbilities.setActivity(this);
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
        numberCounterText = findViewById(R.id.textView_NumberText);
        drinkNumberTextView = findViewById(R.id.textView_numberCounter);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        confettiImageViewBL = findViewById(R.id.confettiImageViewBL);
        confettiImageViewTL = findViewById(R.id.confettiImageViewTL);
        confettiImageViewBR = findViewById(R.id.confettiImageViewBR);
        confettiImageViewTR = findViewById(R.id.confettiImageViewTR);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnClassAbility = findViewById(R.id.btnClassAbility);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnWildContinue = findViewById(R.id.btnBackWildCard);
        btnQuizAnswerBL = findViewById(R.id.btnQuizAnswerBL);
        btnQuizAnswerBR = findViewById(R.id.btnQuizAnswerBR);
        btnQuizAnswerTL = findViewById(R.id.btnQuizAnswerTL);
        btnQuizAnswerTR = findViewById(R.id.btnQuizAnswerTR);
        wildActivityTextView = findViewById(R.id.textView_WildText);
        imageButtonExit = findViewById(R.id.btnExitGame);
        shuffleHandler = new Handler();
        wildText = findViewById(R.id.textView_WildText);

        answerButtons = new Button[]{btnQuizAnswerBL, btnQuizAnswerBR, btnQuizAnswerTL, btnQuizAnswerTR};
    }

    private void startGame() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            Game.getInstance().setPlayers(this, playerList.size());
            Game.getInstance().setPlayerList(playerList);

            for (Player player : playerList) {
                player.resetWildCardAmount(this);
                player.setGame(Game.getInstance());
                player.setUsedClassAbility(false);
            }

        }

        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });
        isFirstTurn = true;
        renderPlayer();
        drinkNumberCounterInt = 1;
        updateDrinkNumberCounterTextView();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    public void initializeCatastrophe() {
        catastrophesManager = new MainActivityCatastrophes();
        List<Player> playerCharacterList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        boolean allNoClass = true;
        for (Player player : playerCharacterList) {
            if (!player.getClassChoice().equals(NO_CLASS)) {
                allNoClass = false;
                break;
            }
        }
        if (!allNoClass) {
            setCatastropheLimit();
            Log.d(TAG, "Catastrophe limit set for selected players.");
        } else {
            Log.d(TAG, "All players are NO_CLASS. Catastrophes will not be initialized.");
        }
    }


    private void setupButtons() {
        btnWildContinue.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnQuizAnswerBL.setVisibility(View.INVISIBLE);
        btnQuizAnswerBR.setVisibility(View.INVISIBLE);

        setupButtonActions(imageButtonExit);
    }

    private void setupButtonActions(ImageButton imageButtonExit) {
        btnUtils.setButton(btnGenerate, () -> {
            disableButtons();
            startNumberShuffleAnimation();
        });

        playerImage.setOnClickListener(v -> characterClassDescriptions());
        btnUtils.setButton(btnAnswer, this::showAnswer);
        btnUtils.setButton(btnWildContinue, this::wildCardContinue);
        btnUtils.setButton(btnClassAbility, this::activateActiveAbility);

        // call the method
        btnUtils.setButton(btnWild, this::activateActiveAbility);

        btnUtils.setButton(btnWild, () -> {
            wildCardActivate();
            drinkNumberTextView.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberCounterText.setVisibility(View.INVISIBLE);
        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });

        infoGif.setOnClickListener(view -> showInstructionDialog());
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void characterClassDescriptions() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        String currentPlayerClassChoice = currentPlayer.getClassChoice();
        if (currentPlayerClassChoice != null) {
            characterClassInformationDialog(currentPlayerClassChoice, getClassActiveDescription(currentPlayerClassChoice), getClassPassiveDescription(currentPlayerClassChoice));
        }
    }

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        updateAbilitiesAfterCooldown(currentPlayer);
        updateClassAbilityButton(currentPlayer);
        updatePlayerInfo(currentPlayer);
        updateNumberText();
        updateTurnCounter();
        logPlayerInformation(currentPlayer);
        updateWildCardVisibilityIfNeeded(currentPlayer);
        updateCatastropheTurnCounter();
        Game.getInstance().setLastTurnPlayer(currentPlayer);
    }

    //-----------------------------------------------------Update Player's Info---------------------------------------------------//

    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView generatedNumberTextView) {
        if (currentNumber == 0) {
            disableButtons();
            Game.getInstance().setLastTurnPlayer(null);
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

    private void updateTurnCounter() {
        turnCounter++;
        if (turnCounter == 4) {
            updateDrinkNumberCounter(1, false);
            turnCounter = 0;
        }
    }

    //-----------------------------------------------------Catastrophes---------------------------------------------------//

    private void updateCatastropheTurnCounter() {
        MainActivityCatastrophes.Catastrophe catastrophe = catastrophesManager.deployCatastrophe();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        catastropheTurnCounter++;
        if (catastropheTurnCounter == catastropheLimit) {
            switch (catastrophe.getEffect()) {
                case 1:
                    drinkNumberCounterInt += 2;
                    updateDrinkNumberCounterTextView();
                    break;
                case 2:
                    drinkNumberCounterInt -= 2;
                    if (drinkNumberCounterInt < 0) {
                        drinkNumberCounterInt = 0; // Ensure the counter doesn't go below 0
                    }
                    updateDrinkNumberCounterTextView();
                    break;
                case 3:
                    increaseNumberByRandom();
                    break;
                case 4:
                    decreaseNumberByRandom();
                    break;
                case 5:
                    reverseTurnOrder();
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
                    renderPlayer();
                    // Apply the specified logic to drinkNumberCounterInt
                    if (drinkNumberCounterInt <= 1) {
                        updateDrinkNumberCounter(2, false);
                    } else if (drinkNumberCounterInt <= 3) {
                        updateDrinkNumberCounter(1, false);
                    } else if (drinkNumberCounterInt <= 5) {
                        updateDrinkNumberCounter(-1, false);
                    } else {
                        updateDrinkNumberCounter(-2, false);
                    }
                    break;
                default:
                    break;
            }
            showDialog(catastrophe.getMessage(), R.layout.catastrophe_dialog_box, R.id.dialogbox_textview, R.id.close_button);
            Game.getInstance().incrementCatastropheQuantity();
            catastropheTurnCounter = 0; // Reset the turn counter after reaching the limit

            // Generate a new random catastrophe limit
            setCatastropheLimit();
        }
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

        boolean showClassAbilityButton = (SCIENTIST.equals(currentPlayer.getClassChoice()) || ARCHER.equals(currentPlayer.getClassChoice()) || WITCH.equals(currentPlayer.getClassChoice()) || QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) || SURVIVOR.equals(currentPlayer.getClassChoice()) || GOBLIN.equals(currentPlayer.getClassChoice()) || ANGRY_JIM.equals(currentPlayer.getClassChoice()) || SOLDIER.equals(currentPlayer.getClassChoice())) && !currentPlayer.getUsedClassAbility();

        Log.d(TAG, "updateClassAbilityButton: " + currentPlayer.getUsedClassAbility());

        btnClassAbility.setVisibility(showClassAbilityButton ? View.VISIBLE : View.INVISIBLE);
        if (NO_CLASS.equals(currentPlayer.getClassChoice())) {
            btnClassAbility.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePlayerInfo(Player currentPlayer) {
        String playerName = currentPlayer.getName();
        String playerImageString = currentPlayer.getPhoto();
        Game.getInstance().addUpdatedName(playerName);

        int turnCount = currentPlayer.getPlayerTurnCount();
        int wildCardCount = currentPlayer.getWildCardAmount();

        String turnText = turnCount == 1 ? "Turn" : "Turns";
        String wildCardText = wildCardCount == 1 ? "Wild Card" : "Wild Cards";

        nextPlayerText.setText(playerName + " has " + turnCount + " " + turnText);
        btnWild.setText(wildCardCount + "\n" + wildCardText);

        if (playerImageString != null) {
            byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            playerImage.setImageBitmap(decodedBitmap);
        }
    }

    //-----------------------------------------------------Update Drink Number Counter---------------------------------------------------//

    private void updateWildCardVisibility(Player currentPlayer) {
        btnWild.setVisibility(currentPlayer.getWildCardAmount() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

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
                drinkNumberCounterInt = Math.max(potentialNewValue, 1);
            } else {
                drinkNumberCounterInt = Math.max(drinkNumberCounterInt + drinkNumberCounterInput, 1);
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


    //-----------------------------------------------------Shuffler---------------------------------------------------//

    private void updateNumberText() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        numberCounterText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(currentNumber));
        SharedMainActivity.setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());
    }

    private void startNumberShuffleAnimation() {
        int originalNumber = Game.getInstance().getCurrentNumber(); // Store the original number
        final int shuffleDuration = 1500;
        int shuffleInterval = originalNumber >= 1000 ? 50 : 100;
        final Random random = new Random();

        shuffleHandler.postDelayed(new ShuffleRunnable(random, originalNumber, shuffleDuration, shuffleInterval), shuffleInterval);
    }

    private void disableButtons() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);
        btnClassAbility.setEnabled(false);
        playerImage.setEnabled(false);
        infoGif.setEnabled(false);
        imageButtonExit.setEnabled(false);
        disableAnswerButtons(answerButtons);
    }

    private void enableButtons() {
        btnGenerate.setEnabled(true);
        btnWild.setEnabled(true);
        btnClassAbility.setEnabled(true);
        playerImage.setEnabled(true);
        infoGif.setEnabled(true);
        imageButtonExit.setEnabled(true);
        enableAnswerButtons(answerButtons);
    }

    //-----------------------------------------------------Active Effects---------------------------------------------------//
    private String getClassActiveDescription(String classChoice) {
        switch (classChoice) {
            case ARCHER:
                return CharacterClassDescriptions.archerActiveDescription;
            case WITCH:
                return CharacterClassDescriptions.witchActiveDescription;
            case SCIENTIST:
                return CharacterClassDescriptions.scientistActiveDescription;
            case SOLDIER:
                return CharacterClassDescriptions.soldierActiveDescription;
            case QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianActiveDescription;
            case SURVIVOR:
                return CharacterClassDescriptions.survivorActiveDescription;
            case ANGRY_JIM:
                return CharacterClassDescriptions.angryJimActiveDescription;
            case GOBLIN:
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
            case SCIENTIST:
                handleScientistClass();
                break;
            case ARCHER:
                handleArcherClass(currentPlayer);
                break;
            case WITCH:
                handleWitchClass(currentPlayer);
                break;
            case SOLDIER:
                handleSoldierClass(currentPlayer);
                break;
            case QUIZ_MAGICIAN:
                handleQuizMagicianClass(currentPlayer);
                break;
            case SURVIVOR:
                handleSurvivorClass(currentPlayer);
                break;
            case GOBLIN:
                handleGoblinClass(currentPlayer);
                break;
            case ANGRY_JIM:
                handleAngryJimClass(currentPlayer);
                break;
            default:
                break;
        }
    }

    private void handleScientistClass() {
        scientistChangeCurrentNumber();
    }

    private void handleSoldierClass(Player currentPlayer) {

        if (!isFirstTurn) {
            if (game.getCurrentNumber() <= 10) {
                currentPlayer.setUsedClassAbility(true);
                game.updateRepeatingTurns(currentPlayer, 1);
                renderPlayer();
                repeatedTurn = true;
                updateDrinkNumberCounter(4, true);
                updateDrinkNumberCounterTextView();
                AudioManager.getInstance().playSoundEffects(this, SOLDIER);
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
        AudioManager.getInstance().playSoundEffects(this, QUIZ_MAGICIAN);
    }

    private void handleGoblinClass(Player currentPlayer) {

        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        randomPlayer.removeWildCard(randomPlayer, 2);
        showGameDialog(GOBLIN + "'s Active: \n\n" + randomPlayer.getName() + " lost two wildcards!");

        currentPlayer.setUsedClassAbility(true);
        btnClassAbility.setVisibility(View.INVISIBLE);
        AudioManager.getInstance().playSoundEffects(this, GOBLIN);
    }

    private void handleAngryJimClass(Player currentPlayer) {

        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        game.updateRepeatingTurns(randomPlayer, 1);
        showGameDialog(ANGRY_JIM + "'s Active: \n\n" + randomPlayer.getName() + " must repeat their turn.");
        btnClassAbility.setVisibility(View.INVISIBLE);
        currentPlayer.setUsedClassAbility(true);
        AudioManager.getInstance().playSoundEffects(this, ANGRY_JIM);
    }


    private void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            halveCurrentNumber();
            currentPlayer.setUsedClassAbility(true);
            btnClassAbility.setVisibility(View.INVISIBLE);
            AudioManager.getInstance().playSoundEffects(this, SURVIVOR);
        } else {
            displayToastMessage("You can only halve the number when it is greater than 1.");
        }
    }

    private void handleArcherClass(Player currentPlayer) {
        if (drinkNumberCounterInt >= 2) {
            showGameDialog(ARCHER + "'s Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setUsedClassAbility(true);
            updateDrinkNumberCounter(-2, true);
            updateDrinkNumberCounterTextView();
            btnClassAbility.setVisibility(View.INVISIBLE);
            AudioManager.getInstance().playSoundEffects(this, ARCHER);
        } else {
            displayToastMessage("There must be more than two total drinks.");
        }
    }

    private void handleWitchClass(Player currentPlayer) {
        currentPlayer.setUsedClassAbility(true);
        currentPlayer.useSkip();
        AudioManager.getInstance().playSoundEffects(this, WITCH);
    }

    private void updateAbilitiesAfterCooldown(Player currentPlayer) {
        if (currentPlayer.getUsedClassAbility()) {
            currentPlayer.incrementAbilityTurnCounter();
            Log.d(TAG, "Incremented counter for " + currentPlayer.getName() + ": " + currentPlayer.getAbilityTurnCounter());

            if (currentPlayer.getAbilityTurnCounter() >= currentPlayer.getClassAbilityCooldown()) {
                Log.d(TAG, "Cooldown complete for: " + currentPlayer.getName());
                currentPlayer.setUsedClassAbility(false);
                currentPlayer.resetSpecificTurnCounter();
            }
        }
    }


    //-----------------------------------------------------Passive Effects---------------------------------------------------//
    private String getClassPassiveDescription(String classChoice) {
        switch (classChoice) {
            case ARCHER:
                return CharacterClassDescriptions.archerPassiveDescription;
            case WITCH:
                return CharacterClassDescriptions.witchPassiveDescription;
            case SCIENTIST:
                return CharacterClassDescriptions.scientistPassiveDescription;
            case SOLDIER:
                return CharacterClassDescriptions.soldierPassiveDescription;
            case QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianPassiveDescription;
            case SURVIVOR:
                return CharacterClassDescriptions.survivorPassiveDescription;
            case ANGRY_JIM:
                return CharacterClassDescriptions.angryJimPassiveDescription;
            case GOBLIN:
                return CharacterClassDescriptions.goblinPassiveDescription;
            default:
                return "";
        }
    }

    private void characterPassiveClassAffects() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        // Check the class choice of the current player
        String classChoice = currentPlayer.getClassChoice();

        // Handle passive effects based on the player's class choice
        if (SOLDIER.equals(classChoice)) {
            handleSoldierPassive();
        } else if (WITCH.equals(classChoice)) {
            handleWitchPassive(currentPlayer);
        } else if (SCIENTIST.equals(classChoice)) {
            handleScientistPassive(currentPlayer);
        } else if (ANGRY_JIM.equals(classChoice)) {
            handleAngryJimPassive(currentPlayer);
        } else if (ARCHER.equals(classChoice)) {
            handleArcherPassive(currentPlayer);
        } else if (GOBLIN.equals(classChoice)) {
            handleGoblinPassive(currentPlayer);
        }
    }


    private void handleScientistPassive(Player currentPlayer) {
        if (!repeatedTurn && !isFirstTurn) {
            Handler handler = new Handler();
            int delayMillis = 1;
            int chance = new Random().nextInt(100);

            handler.postDelayed(() -> {
                if (chance < 10) {
                    showGameDialog(SCIENTIST + "'s Passive: \n\n" + currentPlayer.getName() + " is a scientist and their turn was skipped. ");
                    currentPlayer.useSkip();
                }
            }, delayMillis);
        }
    }

    private void handleAngryJimPassive(Player currentPlayer) {

        boolean numberBelow50 = game.getCurrentNumber() < 50;
        Player lastPlayer = game.getLastTurnPlayer();
        boolean isFirstAngryJimTurn = lastPlayer == null || !lastPlayer.equals(currentPlayer);

        if (numberBelow50 && isFirstAngryJimTurn) {
            game.updateRepeatingTurns(currentPlayer, 1);
            updatePlayerInfo(currentPlayer);
            currentPlayer.incrementAbilityTurnCounter();
        }
        if (numberBelow50) {
            handleSoldierPassive();
            handleArcherPassive(currentPlayer);
            handleGoblinPassive(currentPlayer);
            handleWitchPassive(currentPlayer);
            handleScientistPassive(currentPlayer);
        }
    }


    private void handleGoblinPassive(Player currentPlayer) {
        if (!ANGRY_JIM.equals(currentPlayer.getClassChoice())) {
            currentPlayer.incrementAbilityTurnCounter();
        }
        if (currentPlayer.getAbilityTurnCounter() == 3) {
            currentPlayer.resetSpecificTurnCounter();
            Log.d(TAG, "handleGoblinPassive: reset turn");
            currentPlayer.gainWildCards(1);
        }
    }

    private void handleArcherPassive(Player currentPlayer) {
        // Check if the current player is Angry Jim
        if (!ANGRY_JIM.equals(currentPlayer.getClassChoice())) {
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
                showGameDialog(ARCHER + "'s Passive: \n\nDrinking number increased by 2!");
            } else {
                updateDrinkNumberCounter(-2, true);
                if (drinkNumberCounterInt < 0) {
                    drinkNumberCounterInt = 0;
                }
                updateDrinkNumberCounterTextView();
                showGameDialog(ARCHER + "'s Passive: \n\nDrinking number decreased by 2!");
            }
        }
    }

    private boolean currentPlayerHasQuizMagicianPassive(Player currentPlayer) {
        return QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) || (ANGRY_JIM.equals(currentPlayer.getClassChoice()) && Game.getInstance().getCurrentNumber() < 50);
    }

    private void handleSurvivorPassive(Player currentPlayer) {
        int currentNumber = Game.getInstance().getCurrentNumber();
        String classChoice = currentPlayer.getClassChoice();

        if ((SURVIVOR.equals(classChoice) || ANGRY_JIM.equals(classChoice)) && currentNumber == 1) {
            Log.d(TAG, "handleSurvivorPassive: current number = " + currentNumber);
            String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";
            showGameDialog(SURVIVOR + "'s Passive: \n\n" + currentPlayer.getName()
                    + " survived a 1, hand out " + drinkNumberCounterInt + " " + drinksText);
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

    public void showGameDialog(String message) {
        showDialog(message, R.layout.game_dialog_box, R.id.dialogbox_textview, R.id.close_button);
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

                    Player currentPlayer = game.getCurrentPlayer();

                    if (game.getRepeatingTurnsForPlayer(currentPlayer) == 0) {
                        game.activateRepeatingTurn(currentPlayer, 1);
                    } else {
                        game.updateRepeatingTurns(currentPlayer, 1);
                    }

                    Game.getInstance().setCurrentNumber(newNumber);
                    SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, String.valueOf(newNumber));
                    numberCounterText.setText(String.valueOf(newNumber));
                    renderCurrentNumber(newNumber, this::gotoGameEnd, numberCounterText);
                    currentPlayer.setUsedClassAbility(true);
                    updateNumber(newNumber);
                    AudioManager.getInstance().playSoundEffects(this, SCIENTIST);
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
    private void wildCardActivate() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Game.getInstance().getCurrentPlayer().useWildCard();
        currentPlayer.incrementUsedWildcards();

        WildCardProperties[] wildCardArray = new WildCardProperties[0];
        QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(wildCardArray, this, WildCardType.QUIZ);
        TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(wildCardArray, this, WildCardType.TASK);
        TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(wildCardArray, this, WildCardType.TRUTH);

        WildCardProperties[] quizWildCards = quizAdapter.loadWildCardsFromAdapter(WildCardData.QUIZ_WILD_CARDS);
        WildCardProperties[] taskWildCards = taskAdapter.loadWildCardsFromAdapter(WildCardData.TASK_WILD_CARDS);
        WildCardProperties[] truthWildCards = truthAdapter.loadWildCardsFromAdapter(WildCardData.TRUTH_WILD_CARDS);

        WildCardProperties[] selectedType = selectWildCardType(currentPlayer, quizWildCards, taskWildCards, truthWildCards);
        if (selectedType == null) {
            wildActivityTextView.setText("No wild cards available, your turn is skipped!");
            btnWildContinue.setVisibility(View.VISIBLE);
            btnClassAbility.setVisibility(View.INVISIBLE);
            return;
        }

        WildCardProperties selectedCard = selectRandomCard(selectedType);
        handleSelectedCard(selectedCard, getWildCardType(selectedType, quizWildCards, taskWildCards), currentPlayer);
        btnClassAbility.setVisibility(View.INVISIBLE);

    }

    private WildCardProperties[] selectWildCardType(Player currentPlayer, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards, WildCardProperties[] truthWildCards) {
        if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedClassAbility()) {
            btnClassAbility.setVisibility(View.INVISIBLE);
            return quizWildCards;
        }

        List<WildCardProperties[]> enabledTypes = new ArrayList<>();
        addIfEnabled(enabledTypes, quizWildCards);
        addIfEnabled(enabledTypes, taskWildCards);
        addIfEnabled(enabledTypes, truthWildCards);

        if (enabledTypes.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return enabledTypes.get(random.nextInt(enabledTypes.size()));
    }

    private void addIfEnabled(List<WildCardProperties[]> enabledTypes, WildCardProperties[] enabled) {
        if (Arrays.stream(enabled).anyMatch(WildCardProperties::isEnabled)) {
            enabledTypes.add(enabled);
        }
    }


    private WildCardProperties selectRandomCard(WildCardProperties[] WildCards) {
        // Filter out cards already used
        List<WildCardProperties> unusedCards = Arrays.stream(WildCards).filter(wildcard -> !usedCards.contains(wildcard)).collect(Collectors.toList());

        // Reset logic: If no unused cards remain, reset usedCards
        if (unusedCards.isEmpty()) {
            System.out.println("All cards have been used. Resetting used cards.");
            usedCards.clear();
            unusedCards = new ArrayList<>(Arrays.asList(WildCards)); // Recreate unusedCards with all wildcards
        }

        // Select a random card from unused cards
        Random rand = new Random();
        WildCardProperties selectedCard = unusedCards.get(rand.nextInt(unusedCards.size()));

        // Update the lists
        usedCards.add(selectedCard);
        unusedCards.remove(selectedCard);

        // Log the count of cards in each list
        System.out.println("Number of Used Cards: " + usedCards.size());
        System.out.println("Number of Unused Cards: " + unusedCards.size());

        return selectedCard;
    }


    private String getWildCardType(WildCardProperties[] selectedType, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards) {
        if (selectedType == quizWildCards) return "Quiz";
        if (selectedType == taskWildCards) return "Task";
        return "Truth";
    }

    public void handleSelectedCard(WildCardProperties selectedCard, String wildCardType, Player player) {
        if (selectedCard != null) {
            updateSelectedCard(selectedCard);
            setAnswersAndVisibility(selectedCard, player);
            logSelectedCardInfo(selectedCard, wildCardType);
        } else {
            btnAnswer.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSelectedCard(WildCardProperties selectedCard) {
        String selectedActivity = selectedCard.getWildCard();
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


    //-----------------------------------------------------Specific WildCard Functions---------------------------------------------------//
    private void halveCurrentNumber() {
        int currentNumber = game.getCurrentNumber();
        int updatedNumber = Math.max(currentNumber / 2, 1);
        updateNumber(updatedNumber);
    }

    private void setMultiChoiceRandomizedAnswers(WildCardProperties selectedCard) {
        exposeQuizButtons();

        Log.d(TAG, "setMultiChoiceRandomizedAnswers: ");

        String[] answers = {selectedCard.getAnswer(), selectedCard.getWrongAnswer1(), selectedCard.getWrongAnswer2(), selectedCard.getWrongAnswer3()};

        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);
        answers = answerList.toArray(new String[0]);

        setAnswersToFourButtons(answers);
    }


    //-----------------------------------------------------Quiz Multi-Choice---------------------------------------------------//

    private void setMultiChoiceRandomizedAnswersForQuizMagician(WildCardProperties selectedCard) {
        exposeQuizButtons();

        // Assign two random answers
        Random random = new Random();
        String[] answers = {selectedCard.getAnswer(), random.nextBoolean() ? selectedCard.getWrongAnswer1() : (random.nextBoolean() ? selectedCard.getWrongAnswer2() : selectedCard.getWrongAnswer3())};

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
        disableAnswerButtons(answerButtons);

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
            enableAnswerButtons(answerButtons);
        }, DELAY_MILLIS);
    }

    private void handleIncorrectAnswer(Button selectedButton, String correctAnswer) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);


        selectedButton.setBackgroundResource(R.drawable.buttonhighlightred);
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
            enableAnswerButtons(answerButtons);
        }, DELAY_MILLIS);
    }

    private void handleAnswerOutcome(boolean isCorrect) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (isCorrect) {
            if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice())) {
                quizAnswerView(currentPlayer.getName() + " that's right! The answer was " + selectedWildCard.getAnswer() + "\n\n P.S. You get to give out two drinks to everyone.");
            } else {
                quizAnswerView(currentPlayer.getName() + " that's right! The answer was " + selectedWildCard.getAnswer() + "\n\n P.S. You get to give out a drink.");
            }
        } else {
            quizAnswerView(currentPlayer.getName() + " big ooooff! The answer actually was " + selectedWildCard.getAnswer() + "\n\n Take a drink.");
        }

        hideQuizButtons();
        btnWildContinue.setVisibility(View.VISIBLE);
    }

    private void wildCardContinue() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedClassAbility()) {
            wildCardActivate();
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
        AudioManager.getInstance().playConfettiSound(this);
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

    private void quizAnswerView(String string) {
        btnWildContinue.setVisibility(View.VISIBLE);
        wildText.setVisibility(View.VISIBLE);
        btnWild.setVisibility(View.INVISIBLE);
        btnGenerate.setVisibility(View.INVISIBLE);
        nextPlayerText.setVisibility(View.INVISIBLE);
        numberCounterText.setVisibility(View.INVISIBLE);
        wildText.setText(string);
    }

    //-----------------------------------------------------Quiz---------------------------------------------------//

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

    private class ShuffleRunnable implements Runnable {
        private final Random random;
        private final int originalNumber;
        private final int shuffleDuration;
        private final int shuffleInterval;
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        private int shuffleTime = 0;

        ShuffleRunnable(Random random, int originalNumber, int shuffleDuration, int shuffleInterval) {
            this.random = random;
            this.originalNumber = originalNumber;
            this.shuffleDuration = shuffleDuration;
            this.shuffleInterval = shuffleInterval;
        }

        @Override
        public void run() {
            int randomDigit = random.nextInt(originalNumber + 1);
            numberCounterText.setText(String.valueOf(randomDigit));

            shuffleTime += shuffleInterval;

            if (shuffleTime < shuffleDuration) {
                shuffleHandler.postDelayed(this, shuffleInterval);
            } else {

                int currentNumber = Game.getInstance().nextNumber();

                Game.getInstance().recordTurn(currentPlayer, currentNumber);
                Log.d(TAG, "NextNumber = " + currentNumber);

                handleSurvivorPassive(currentPlayer);


                renderCurrentNumber(currentNumber, MainActivityGame.this::gotoGameEnd, numberCounterText);

                if (currentNumber != 0) {
                    enableButtons();
                }
                Log.d("startNumberShuffleAnimation", "Next player's turn");
            }
        }
    }

}
