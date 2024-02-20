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
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_ARCHER;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_JIM;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_QUIZ_MAGICIAN;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_SCIENTIST;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_SOLDIER;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_SURVIVOR;
import static com.example.countingdowngame.mainActivity.PlayerChoice.CLASS_WITCH;

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

    //-----------------------------------------------------Constants---------------------------------------------------//
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private static final int BUTTON_COUNT = 4;
    private static final int BUTTON_COUNT_2 = 2;
    private static final int DELAY_MILLIS = 1500;


    //-----------------------------------------------------Public ---------------------------------------------------//
    public static int drinkNumberCounterInt = 0;
    //-----------------------------------------------------Maps and Sets---------------------------------------------------//
    private final Map<Player, Set<WildCardProperties>> usedWildCard = new HashMap<>();
    private final Set<WildCardProperties> usedWildCards = new HashSet<>();
    public WildCardProperties selectedWildCard;
    private int turnCounter = 0;
    //-----------------------------------------------------Views---------------------------------------------------//
    private Button btnAnswer;
    private Button btnBackWild;
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
    private GifImageView muteGif;
    private GifImageView soundGif;
    private ImageView playerImage;
    private TextView drinkNumberCounterTextView;
    private TextView nextPlayerText;
    private TextView numberText;
    private TextView wildText;

    //-----------------------------------------------------Booleans---------------------------------------------------//
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isFirstTurn = true;
    private boolean soldierRemoval = false;
    private boolean repeatedTurn = false;


    //-----------------------------------------------------Array---------------------------------------------------//
    private Button[] answerButtons; // Array to hold the answer buttons
    private Handler shuffleHandler;

    //-----------------------------------------------------Lifecycle Methods---------------------------------------------------//
    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteSoundButtonsForBackgroundMusic(isMuted, muteGif, soundGif);
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
        startGame();
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);


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
                player.setClassAbility(false); // Initialize class ability to false
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
                String classDescription;
                if (currentPlayerClassChoice.equalsIgnoreCase("Jim")) {
                    classDescription = currentPlayerClassChoice + "'s Abilities" + "\n\n" + "Passive: " + getClassPassiveDescription(currentPlayerClassChoice);
                } else {
                    classDescription = currentPlayerClassChoice + "'s Abilities" + "\n\n" + "Active: " + getClassActiveDescription(currentPlayerClassChoice)
                            + "\n\n" + "Passive: " + getClassPassiveDescription(currentPlayerClassChoice);
                }
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
        updateClassAbilityButton(currentPlayer);
        updateTurnCounter();
        updateSurvivorAbilities(currentPlayer);
        updatePlayerInfo(currentPlayer);
        updateNumberText();
        logPlayerInformation(currentPlayer);
        updateWildCardVisibilityIfNeeded(currentPlayer);
    }

    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView generatedNumberTextView) {
        if (currentNumber == 0) {
            btnGenerate.setEnabled(false);
            btnWild.setEnabled(false);
            btnClassAbility.setEnabled(false);

            generatedNumberTextView.setText(String.valueOf(currentNumber));
            applyPulsingEffect(generatedNumberTextView);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Game.getInstance().endGame(this);
                onEnd.run();
            }, 2000);
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
                "Soldier".equals(currentPlayer.getClassChoice())) &&
                !currentPlayer.usedClassAbility();

        btnClassAbility.setVisibility(showClassAbilityButton ? View.VISIBLE : View.INVISIBLE);

        if ("Jim".equals(currentPlayer.getClassChoice()) || "No Class".equals(currentPlayer.getClassChoice())) {
            btnClassAbility.setVisibility(View.INVISIBLE);
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

        drinkNumberCounterTextView.setText(drinkNumberText);
    }


    private void updateNumberText() {
        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
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
                numberText.setText(String.valueOf(randomDigit));

                shuffleTime += shuffleInterval;

                if (shuffleTime < shuffleDuration) {
                    shuffleHandler.postDelayed(this, shuffleInterval);
                } else {
                    numberText.setText(String.valueOf(randomDigit));
                    int currentNumber = Game.getInstance().nextNumber();
                    Log.d(TAG, "NextNumber = " + currentNumber);

                    if ("Survivor".equals(currentPlayer.getClassChoice()) && originalNumber == 1 && currentNumber == 1) {
                        handleSurvivorPassive(currentPlayer);
                    }

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
            case CLASS_JIM:
                return CharacterClassDescriptions.jimActiveDescription;
            default:
                return "I love you cutie pie hehe. You don't have a class to show any description for.";
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
            case "Survivor":
                handleSurvivorClass(currentPlayer);
                break;
            default:
                break;
        }
    }

    private void handleScientistClass(Player currentPlayer) {
        changeCurrentNumber();
        Game.getInstance().activateRepeatingTurn(currentPlayer, 1);
    }

    private void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (Game.getInstance().getCurrentNumber() <= 10) {
                currentPlayer.setClassAbility(true);
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

    private void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            halveCurrentNumber();
            currentPlayer.setClassAbility(true);
            btnClassAbility.setVisibility(View.INVISIBLE);
        } else {
            displayToastMessage("You can only halve the number when it is greater than 1.");
        }
    }

    private void updateSurvivorAbilities(Player currentPlayer) {
        if ("Survivor".equals(currentPlayer.getClassChoice()) && currentPlayer.usedClassAbility()) {
            currentPlayer.incrementSpecificTurnCounter();
            if (currentPlayer.getSpecificActiveTurnCounter() == 3) {
                currentPlayer.setClassAbility(false);
                currentPlayer.resetSpecificTurnCounter();
            }
        }
    }

    private void handleArcherClass(Player currentPlayer) {
        Log.d("ArcherClass", "handleArcherClass called");

        if (drinkNumberCounterInt >= 2) {
            showDialog("Archer's Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setClassAbility(true);
            updateDrinkNumberCounter(-2, true);
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
            case CLASS_JIM:
                return CharacterClassDescriptions.jimPassiveDescription;
            default:
                return "";
        }
    }

    private void characterPassiveClassAffects() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }
        handleSoldierPassive(currentPlayer);
        handleWitchPassive(currentPlayer);
        handleScientistPassive(currentPlayer);
        handleJimPassive(currentPlayer);
        handleArcherPassive(currentPlayer);
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
                showDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " take two drinks.");
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
                    showDialog("Scientist's Passive: \n\n" + currentPlayer.getName() + " is a scientist and their turn was skipped. ");
                    currentPlayer.useSkip();
                }
            }, delayMillis);
        }
    }

    private void handleJimPassive(Player currentPlayer) {
        if ("Jim".equals(currentPlayer.getClassChoice())) {
            currentPlayer.incrementSpecificTurnCounter();
            if (currentPlayer.getSpecificActiveTurnCounter() == 3) {
                currentPlayer.gainWildCards(1);
            }
        }
    }

    private void handleArcherPassive(Player currentPlayer) {
        if ("Archer".equals(currentPlayer.getClassChoice())) {
            currentPlayer.incrementSpecificTurnCounter();

            if (currentPlayer.getSpecificActiveTurnCounter() == 4) {
                currentPlayer.resetSpecificTurnCounter();
                Log.d("ArcherClass", "Passive ability triggered");

                int chance = new Random().nextInt(100);
                if (chance < 60) {
                    updateDrinkNumberCounter(2, true);
                    updateDrinkNumberCounterTextView();
                    showDialog("Archer's Passive: \n\nDrinking number increased by 2!");
                } else {
                    updateDrinkNumberCounter(-2, true);
                    if (drinkNumberCounterInt < 0) {
                        drinkNumberCounterInt = 0;
                    }
                    updateDrinkNumberCounterTextView();
                    showDialog("Archer's Passive: \n\nDrinking number decreased by 2!");
                }
            }
        }
    }


    private void handleSurvivorPassive(Player currentPlayer) {
        int currentNumber = Game.getInstance().getCurrentNumber();
        Log.d(TAG, "handleSurvivorPassive: current number = " + currentNumber);

        String drinksText = (drinkNumberCounterInt == 1) ? "drink" : "drinks";

        if ("Survivor".equals(currentPlayer.getClassChoice()) && currentNumber == 1) {
            showDialog("Survivor's Passive: \n\n" + currentPlayer.getName() + " survived a 1, hand out " + drinkNumberCounterInt + " " + drinksText);
        }
    }


    //-----------------------------------------------------External Class Effects---------------------------------------------------//

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
        closeButton.setOnClickListener(v -> dialog.dismiss());
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
                        foundWildCardType = false;
                        btnClassAbility.setVisibility(View.INVISIBLE);
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
            assert selectedCard != null;
            Log.d("WildCardInfo", "Type: " + wildCardType + ", " +
                    "Question: " + selectedCard.getText() + ", " +
                    "Answer: " + selectedCard.getAnswer() + ", " +
                    "Wrong Answer 1: " + selectedCard.getWrongAnswer1() + ", " +
                    "Wrong Answer 2: " + selectedCard.getWrongAnswer2() + ", " +
                    "Wrong Answer 3: " + selectedCard.getWrongAnswer3() + ", " +
                    "Category: " + selectedCard.getCategory());
        }

        assert selectedActivity != null;
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
            default:
                break;
        }
    }

    //-----------------------------------------------------Specific WildCard Functions---------------------------------------------------//

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
                    btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                    btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it right, give out a drink!");
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
