package com.example.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.TROLL;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.decreaseNumberByRandom;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.increaseNumberByRandom;
import static com.example.countingdowngame.mainActivity.MainActivityCatastrophes.setCatastropheLimit;
import static com.example.countingdowngame.mainActivity.MainActivityLogging.logPlayerInformation;
import static com.example.countingdowngame.mainActivity.MainActivityLogging.logSelectedCardInfo;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleAngryJimPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleArcherPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleScientistPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleSoldierPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleTrollPassive;
import static com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities.handleWitchPassive;

import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.mainActivity.classAbilities.AbilityComplimentary;
import com.example.countingdowngame.mainActivity.classAbilities.ActiveAbilities;
import com.example.countingdowngame.mainActivity.classAbilities.PassiveAbilities;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityGame extends SharedMainActivity {

    //-----------------------------------------------------Constants---------------------------------------------------//
    static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    //-----------------------------------------------------Public ---------------------------------------------------//
    public static int drinkNumberCounterInt = 0;
    public static int catastropheLimit;
    public static boolean isFirstTurn;
    public static boolean repeatedTurn;
    public static boolean soldierRemoval;
    public static boolean catastrophesEnabled = true;
    public static boolean passivesEnabled = true;
    private static TextView numberCounterText;
    private static int turnCounter = 0;
    private static int catastropheTurnCounter = 0;
    private static Player hidingTroll = null;
    private static final List<Player> playersWhoPaidToll = new ArrayList<>();

    //-----------------------------------------------------Maps and Sets---------------------------------------------------//
    private final List<WildCardProperties> usedCards = new ArrayList<>();  // Class-level variable to track used cards
    public WildCardProperties selectedWildCard;
    Game game = Game.getInstance();

    //-----------------------------------------------------Views---------------------------------------------------//
    private Button btnWildContinue, btnGenerate;
    private View btnClassAbility, btnWild;
    private GifImageView infoGif, muteGif, soundGif;
    private ImageView playerImage, iconAbility;
    private TextView drinkNumberTextView, nextPlayerText, wildText, textWildCount, labelAbilityTitle, labelAbilityDesc;
    private ImageButton imageButtonExit;
    //-----------------------------------------------------Booleans---------------------------------------------------//
    private boolean doubleBackToExitPressedOnce = false;
    private boolean wasQuizCorrect = false;
    private WildCardDialogManager wildCardDialogManager;
    //-----------------------------------------------------Array---------------------------------------------------//
    private MainActivityCatastrophes catastrophesManager;
    private MainActivityNumberGenerator numberGenerator;

    public static void updateNumber(int updatedNumber) {
        Game.getInstance().setCurrentNumber(updatedNumber);
        updateNumberText();
    }

    public static void updateNumberText() {
        if (numberCounterText == null) return;
        int currentNumber = Game.getInstance().getCurrentNumber();
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        boolean canSeeNumber = hidingTroll == null ||
                (currentPlayer != null && currentPlayer.equals(hidingTroll)) ||
                playersWhoPaidToll.contains(currentPlayer);

        if (canSeeNumber) {
            String textToDisplay = String.valueOf(currentNumber);
            numberCounterText.setText(textToDisplay);
            SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, textToDisplay);
            updateNumberColor(currentNumber);
        } else {
            String textToDisplay = "???";
            numberCounterText.setText(textToDisplay);
            SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, textToDisplay);
            numberCounterText.setTextColor(Color.BLACK);
        }
    }

    public static String getDisplayNumber(int number) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        boolean canSeeNumber = hidingTroll == null ||
                (currentPlayer != null && currentPlayer.equals(hidingTroll)) ||
                playersWhoPaidToll.contains(currentPlayer);
        return canSeeNumber ? String.valueOf(number) : "???";
    }

    public void hideNumberForTroll(Player troll) {
        hidingTroll = troll;
        playersWhoPaidToll.clear();
        updateNumberText();
    }

    public static void updateNumberColor(int currentNumber) {
        if (numberCounterText == null) return;

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        boolean canSeeNumber = hidingTroll == null ||
                (currentPlayer != null && currentPlayer.equals(hidingTroll)) ||
                playersWhoPaidToll.contains(currentPlayer);

        if (!canSeeNumber) {
            numberCounterText.setTextColor(Color.BLACK);
            return;
        }

        if (currentNumber >= 1000) {
            numberCounterText.setTextColor(Color.BLACK);
        } else {
            ArgbEvaluator colorEvaluator = new ArgbEvaluator();
            // Scaling: 1000 is Black, 1 is Red
            float progress = 1.0f - ((float) (Math.max(1, currentNumber) - 1) / 999.0f);
            int currentColor = (int) colorEvaluator.evaluate(progress, Color.BLACK, Color.RED);
            numberCounterText.setTextColor(currentColor);
        }
    }

    public static void resetStaticState() {
        drinkNumberCounterInt = 1;
        isFirstTurn = true;
        repeatedTurn = false;
        soldierRemoval = false;
        turnCounter = 0;
        catastropheTurnCounter = 0;
        catastropheLimit = 0;
        catastrophesEnabled = true;
        passivesEnabled = true;
        hidingTroll = null;
        playersWhoPaidToll.clear();
        PowerUps.reset();
    }

    //-----------------------------------------------------Lifecycle Methods---------------------------------------------------//
    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    public void displayToastMessage(String message) {
        StyleableToast.makeText(this, message, R.style.newToast).show();
    }

    //-----------------------------------------------------Start Game Functions---------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main_activity);

        initializeViews();
        PassiveAbilities.setActivity(this);
        ActiveAbilities.setActivity(this);
        PowerUps.setActivity(this);
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setupButtons();
        startGame();
        initializeCatastrophe();
        wildCardDialogManager = new WildCardDialogManager(this, this::wildCardContinue);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    game.endGame(MainActivityGame.this);
                    gotoHomeScreen();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                displayToastMessage("Press back again to go to the home screen");
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
            }
        });
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
        btnClassAbility = findViewById(R.id.btnClassAbility);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnWildContinue = findViewById(R.id.btnBackWildCard);
        imageButtonExit = findViewById(R.id.btnExitGame);
        wildText = findViewById(R.id.textView_WildText);

        textWildCount = findViewById(R.id.textWildCount);
        labelAbilityTitle = findViewById(R.id.labelAbilityTitle);
        labelAbilityDesc = findViewById(R.id.labelAbilityDesc);
        labelAbilityDesc.setSelected(false);

        iconAbility = findViewById(R.id.iconAbility);

        numberGenerator = new MainActivityNumberGenerator(this, numberCounterText);
    }

    private void startGame() {
        resetStaticState();
        game.reset();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            game.setPlayers(this, playerList.size());
            game.setPlayerList(playerList);

            for (Player player : playerList) {
                player.resetWildCardAmount(this);
                player.setGame(game);
                player.setUsedActiveAbility(false);
            }

        }

        game.startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                labelAbilityDesc.setSelected(false);
                renderPlayer(false);
            }
        });
        renderPlayer(false);
        updateDrinkNumberCounterTextView();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    public void initializeCatastrophe() {
        if (!catastrophesEnabled) {
            Log.d(TAG, "Catastrophes are disabled.");
            return;
        }
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

        setupButtonActions(imageButtonExit);
    }

    private void setupButtonActions(ImageButton imageButtonExit) {
        btnUtils.setButton(btnGenerate, this::handleGenerateClick);

        playerImage.setOnClickListener(v -> characterClassDescriptions());
        btnUtils.setButton(btnWildContinue, this::wildCardContinue);
        btnUtils.setButton(btnClassAbility, this::activateActiveAbility);

        btnUtils.setButton(btnWild, () -> {
            wildCardActivate();
            btnWild.setVisibility(View.INVISIBLE);
        });

        btnUtils.setButton(imageButtonExit, () -> {
            game.endGame(this);
            gotoHomeScreen();
        });

        btnUtils.setButton(infoGif, this::showInstructionDialog);
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void characterClassDescriptions() {
        Player currentPlayer = game.getCurrentPlayer();
        String currentPlayerClassChoice = currentPlayer.getClassChoice();
        if (currentPlayerClassChoice != null) {
            characterClassInformationDialog(currentPlayerClassChoice, getClassActiveDescription(currentPlayerClassChoice), getClassPassiveDescription(currentPlayerClassChoice));
        }
    }


    private void showRevealNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_troll_reveal_dialog, null);
        Button payBtn = dialogView.findViewById(R.id.btn_pay_view);
        Button blindBtn = dialogView.findViewById(R.id.btn_generate_blind);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnUtils.setButton(payBtn, () -> {
            dialog.dismiss();
            Player currentPlayer = game.getCurrentPlayer();
            if (currentPlayer != null && !playersWhoPaidToll.contains(currentPlayer)) {
                playersWhoPaidToll.add(currentPlayer);
            }
            updateNumberText();
        });

        btnUtils.setButton(blindBtn, () -> {
            dialog.dismiss();
            handleGenerateClick();
        });

        dialog.show();
    }

    private void handleGenerateClick() {
        Player currentPlayer = game.getCurrentPlayer();
        int currentNum = game.getCurrentNumber();
        boolean isGambler = GAMBLER.equals(currentPlayer.getClassChoice());
        boolean isAngryJimUnder50 = ANGRY_JIM.equals(currentPlayer.getClassChoice()) && currentNum < 50;

        if ((isGambler || isAngryJimUnder50) && currentNum > 5) {
            PassiveAbilities.showGamblerBetDialog(() -> {
                disableButtons();
                numberGenerator.startNumberShuffleAnimation();
            });
        } else {
            disableButtons();
            numberGenerator.startNumberShuffleAnimation();
        }
    }

    private void renderPlayer(boolean isPowerUp) {

        Player activePlayer = game.getCurrentPlayer();

        if (activePlayer == null) return;

        Log.d("GAME", "RENDER PLAYER = " + activePlayer.getName());

        updatePlayerInfo(activePlayer);
        updateClassAbilityButton(activePlayer);
        updateWildCardVisibilityIfNeeded(activePlayer);
        updateNumberText();

        game.setLastTurnPlayer(activePlayer);


        if (!isPowerUp) {
            characterPassiveClassAffects();
            updateActiveAbilitiesAfterCooldown(activePlayer);
            updateTurnCounter();
            updateCatastropheTurnCounter();
        }

        logPlayerInformation(activePlayer);

        if (hidingTroll != null) {
            btnUtils.setButton(btnGenerate, () -> {
                if (activePlayer.equals(hidingTroll) || playersWhoPaidToll.contains(activePlayer)) {
                    handleGenerateClick();
                } else {
                    showRevealNumberDialog();
                }
            });
        } else {
            numberCounterText.setOnClickListener(null);
            btnUtils.setButton(btnGenerate, this::handleGenerateClick);
        }

    }

    //-----------------------------------------------------Update Player's Info---------------------------------------------------//

    public void renderCurrentNumber(int currentNumber, final Runnable onEnd, TextView generatedNumberTextView) {
        if (currentNumber == 0) {
            disableButtons();
            // Force reveal on lose
            generatedNumberTextView.setText(String.valueOf(currentNumber));

            animateTextView(generatedNumberTextView, () -> {
                btnUtils.playSoundEffects();
                Player loser = game.getLastTurnPlayer();
                PowerUps.checkLosingPowerUps(loser, () -> {
                    game.endGame(this);
                    onEnd.run();
                }, generatedNumberTextView);
            });
        } else {
            updateNumberText(); // Use logic-aware display
            game.nextPlayer();
        }
    }

    private void updateTurnCounter() {
        turnCounter++;
        Log.d(TAG, "updateTurnCounter: " + turnCounter);
        if (turnCounter == 4) {
            updateDrinkNumberCounter(1, false);
            turnCounter = 0;
        }
    }

    //-----------------------------------------------------Catastrophes---------------------------------------------------//

    private void updateCatastropheTurnCounter() {
        if (!catastrophesEnabled || catastrophesManager == null) return;
        MainActivityCatastrophes.Catastrophe catastrophe = catastrophesManager.deployCatastrophe();
        catastropheTurnCounter++;
        Log.d(TAG, "updateCatastropheTurnCounter: " + catastropheTurnCounter);
        Log.d(TAG, "catastropheLimit: " + catastropheLimit);
        if (catastropheTurnCounter == catastropheLimit) {
            Log.d(TAG, "catastropheTurnCounter = " + catastropheLimit);
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
                    for (Player player : game.getPlayers()) {
                        player.gainWildCards(2);
                    }
                    renderPlayer(true);
                    break;
                case 7:
                    for (Player player : game.getPlayers()) {
                        player.loseWildCards(2);
                    }
                    renderPlayer(true);
                    break;
                case 9:
                    game.activateRepeatingTurnForAllPlayers(2);
                    renderPlayer(true);
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
            Log.d(TAG, "Catastrophe message: " + catastrophe.getMessage());
            showDialog(catastrophe.getMessage(), R.layout.game_catastrophe_dialog_box, R.id.dialogbox_textview);
            game.incrementCatastropheQuantity();
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
            }
        }
        if (currentPlayer.getJustUsedWildCard()) {
            btnWild.setVisibility(View.INVISIBLE);
            currentPlayer.setJustUsedWildCard(false);
        }
    }

    private void updateClassIcon(String classChoice) {
        if (iconAbility == null || classChoice == null) return;

        int iconRes;
        switch (classChoice) {
            case ARCHER: iconRes = R.drawable.archer; break;
            case WITCH: iconRes = R.drawable.witch; break;
            case SCIENTIST: iconRes = R.drawable.scientist; break;
            case SOLDIER: iconRes = R.drawable.jail; break;
            case QUIZ_MAGICIAN: iconRes = R.drawable.books; break;
            case SURVIVOR: iconRes = R.drawable.bandaids; break;
            case ANGRY_JIM: iconRes = R.drawable.angry_jim; break;
            case GOBLIN: iconRes = R.drawable.goblin; break;
            case GAMBLER: iconRes = R.drawable.dice; break;
            case TROLL: iconRes = R.drawable.bridge; break;
            default: iconRes = R.drawable.wine; break;
        }
        iconAbility.setImageResource(iconRes);
    }

    private void updateClassAbilityButton(Player currentPlayer) {
        String classChoice = currentPlayer.getClassChoice();
        labelAbilityTitle.setText(getClassActiveButtonText(classChoice));
        labelAbilityDesc.setText(getClassActiveDescription(classChoice));

        updateClassIcon(classChoice);

        int wildCardCount = currentPlayer.getWildCardAmount();

        boolean canShowButton = (SCIENTIST.equals(classChoice) || ARCHER.equals(classChoice)
                || WITCH.equals(classChoice) || QUIZ_MAGICIAN.equals(classChoice)
                || SURVIVOR.equals(classChoice) || GOBLIN.equals(classChoice)
                || ANGRY_JIM.equals(classChoice) || SOLDIER.equals(classChoice)
                || GAMBLER.equals(classChoice) || TROLL.equals(classChoice))
                && !currentPlayer.getUsedActiveAbility();

        // Specific rules for dynamic hiding
        if (ARCHER.equals(classChoice) && drinkNumberCounterInt < 2) {
            canShowButton = false;
        }

        if (SOLDIER.equals(classChoice)) {
            if (isFirstTurn || game.getCurrentNumber() > 10) {
                canShowButton = false;
            }
        }

        if (QUIZ_MAGICIAN.equals(classChoice)) {
            if (wildCardCount < 1) {
                canShowButton = false;
            }
        }

        if (GOBLIN.equals(classChoice)) {
            boolean otherPlayersHaveWildcards = false;
            for (Player player : game.getPlayers()) {
                if (!player.equals(currentPlayer) && player.getWildCardAmount() > 0) {
                    otherPlayersHaveWildcards = true;
                    break;
                }
            }
            if (!otherPlayersHaveWildcards || wildCardCount < 1) {
                canShowButton = false;
            }
        }

        if (SURVIVOR.equals(classChoice)) {
            if (game.getCurrentNumber() == 1) {
                canShowButton = false;
            }
        }

        btnClassAbility.setVisibility(canShowButton ? View.VISIBLE : View.INVISIBLE);

        labelAbilityDesc.postDelayed(() -> {
            labelAbilityDesc.setSelected(true);
        }, 2000);
    }

    private String getClassActiveButtonText(String classChoice) {
        if (classChoice == null) return "";
        switch (classChoice) {
            case ARCHER:
                return CharacterClassDescriptions.archerActiveButtonText;
            case WITCH:
                return CharacterClassDescriptions.witchActiveButtonText;
            case SCIENTIST:
                return CharacterClassDescriptions.scientistActiveButtonText;
            case SOLDIER:
                return CharacterClassDescriptions.soldierActiveButtonText;
            case QUIZ_MAGICIAN:
                return CharacterClassDescriptions.quizMagicianActiveButtonText;
            case SURVIVOR:
                return CharacterClassDescriptions.survivorActiveButtonText;
            case ANGRY_JIM:
                return CharacterClassDescriptions.angryJimActiveButtonText;
            case GOBLIN:
                return CharacterClassDescriptions.goblinActiveButtonText;
            case GAMBLER:
                return CharacterClassDescriptions.gamblerActiveButtonText;
            case TROLL:
                return CharacterClassDescriptions.trollActiveButtonText;
            default:
                return "";
        }
    }
    private void updateTextSize(String text, TextView textView) {
        int size = SharedMainActivity.TextSizeCalculatorPlayerName
                .calculateTextSizeBasedOnCharacterCount(text);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }
    private void updatePlayerInfo(Player currentPlayer) {
        String playerName = currentPlayer.getName();
        String playerImageString = currentPlayer.getPhoto();
        game.addUpdatedName(playerName);

        // Ensure hidden state is reflected on UI
        updateNumber(game.getCurrentNumber());

        int turnCount = currentPlayer.getPlayerTurnCount();
        int wildCardCount = currentPlayer.getWildCardAmount();

        String turnText = turnCount == 1 ? "Turn" : "Turns";
        String fullTurnText = playerName + " has " + turnCount + " " + turnText;
        nextPlayerText.setText(fullTurnText);
        updateTextSize(nextPlayerText.getText().toString(), nextPlayerText);
        textWildCount.setText(String.valueOf(wildCardCount));

        if (playerImageString != null) {
            byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            //Add animation of the coin spinning and changing to the next players image
            playerImage.animate()
                    .rotationY(90f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        playerImage.setImageBitmap(decodedBitmap);
                        playerImage.setRotationY(-90f);
                        playerImage.animate()
                                .rotationY(0f)
                                .setDuration(150)
                                .start();
                    })
                    .start();

        }
        PowerUps.updatePowerUpIcons(currentPlayer);
    }

    //-----------------------------------------------------Update Drink Number Counter---------------------------------------------------//

    private void updateWildCardVisibility(Player currentPlayer) {
        btnWild.setVisibility(currentPlayer.getWildCardAmount() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateDrinkNumberCounter(int drinkNumberCounterInput, boolean activatedByAbility) {
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
        // Update ability button visibility when drink counter changes
        updateClassAbilityButton(game.getCurrentPlayer());
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

    public void disableButtons() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);
        btnClassAbility.setEnabled(false);
        playerImage.setEnabled(false);
        infoGif.setEnabled(false);
        imageButtonExit.setEnabled(false);
        findViewById(R.id.powerup_left).setEnabled(false);
        findViewById(R.id.powerup_right).setEnabled(false);
    }

    public void enableButtons() {
        btnGenerate.setEnabled(true);
        btnWild.setEnabled(true);
        btnClassAbility.setEnabled(true);
        playerImage.setEnabled(true);
        infoGif.setEnabled(true);
        imageButtonExit.setEnabled(true);
        findViewById(R.id.powerup_left).setEnabled(true);
        findViewById(R.id.powerup_right).setEnabled(true);
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
            case GAMBLER:
                return CharacterClassDescriptions.gamblerActiveDescription;
            case TROLL:
                return CharacterClassDescriptions.trollActiveDescription;
            default:
                return "I love you cutie pie hehe. You don't have a class to show any description for.";
        }
    }

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
            case GAMBLER:
                return CharacterClassDescriptions.gamblerPassiveDescription;
            case TROLL:
                return CharacterClassDescriptions.trollPassiveDescription;
            default:
                return "";
        }
    }

    private void characterPassiveClassAffects() {
        Player currentPlayer = game.getCurrentPlayer();
        String classChoice = currentPlayer.getClassChoice();

        Log.d(TAG, "Number was generated passive: " + game.getNumberWasGenerated());
        if (SOLDIER.equals(classChoice) && game.getNumberWasGenerated() == true) {
            handleSoldierPassive();
        } else if (WITCH.equals(classChoice) && game.getNumberWasGenerated() == true) {
            handleWitchPassive(currentPlayer);
        } else if (SCIENTIST.equals(classChoice)) {
            handleScientistPassive(currentPlayer);
        } else if (ANGRY_JIM.equals(classChoice)) {
            handleAngryJimPassive(currentPlayer);
        } else if (ARCHER.equals(classChoice)) {
            handleArcherPassive(currentPlayer);
        } else if (TROLL.equals(classChoice)) {
            handleTrollPassive(currentPlayer);
        }
    }

    private void updateActiveAbilitiesAfterCooldown(Player currentPlayer) {
        if (currentPlayer.getUsedActiveAbility() && (WITCH.equals(currentPlayer.getClassChoice()) ||
                SURVIVOR.equals(currentPlayer.getClassChoice()) ||
                ANGRY_JIM.equals(currentPlayer.getClassChoice()))) {

            currentPlayer.incrementActiveAbilityTurnCounter();

            if (currentPlayer.getActiveAbilityTurnCounter() >= currentPlayer.getActiveAbilityCooldown()) {
                currentPlayer.setUsedActiveAbility(false);
                currentPlayer.resetActiveAbilityTurnCounter();
            }
        }
    }

    public void activateActiveAbility() {
        Player currentPlayer = game.getCurrentPlayer();
        String classChoice = currentPlayer.getClassChoice();
        switch (classChoice) {
            case SCIENTIST:
                ActiveAbilities.handleScientistClass();
                break;
            case ARCHER:
                ActiveAbilities.handleArcherClass(currentPlayer);
                break;
            case WITCH:
                ActiveAbilities.handleWitchClass(currentPlayer);
                break;
            case SOLDIER:
                ActiveAbilities.handleSoldierClass(currentPlayer);
                break;
            case QUIZ_MAGICIAN:
                ActiveAbilities.handleQuizMagicianClass(currentPlayer);
                break;
            case SURVIVOR:
                ActiveAbilities.handleSurvivorClass(currentPlayer);
                break;
            case GOBLIN:
                ActiveAbilities.handleGoblinClass(currentPlayer);
                break;
            case ANGRY_JIM:
                ActiveAbilities.handleAngryJimClass(currentPlayer);
                break;
            case GAMBLER:
                ActiveAbilities.handleGamblerClass();
                break;
            case TROLL:
                ActiveAbilities.handleTrollClass(currentPlayer);
                break;
            default:
                break;
        }
    }

    private final List<AlertDialog> dialogQueue = new ArrayList<>();

    public void showDialog(String message, int layoutId, int textViewId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);
        TextView dialogBoxTextView = dialogView.findViewById(textViewId);

        if (dialogBoxTextView != null) {
            dialogBoxTextView.setText(message);
        }

        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(d -> {
            dialogQueue.remove(dialog);

            if (!dialogQueue.isEmpty()) {
                dialogQueue.get(0).show();
            }
        });

        dialogQueue.add(dialog);
        if (dialogQueue.size() == 1) {
            dialog.show();
        }
    }


    public void showClassDialog(String title, String description, int layoutId,
                                int classTextViewId, int descriptionTextViewId,
                                Runnable onDismiss) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(layoutId, null);

        TextView classTextView = dialogView.findViewById(classTextViewId);
        TextView descriptionTextView = dialogView.findViewById(descriptionTextViewId);

        if (classTextView != null) {
            classTextView.setText(title);
        }

        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }


        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(d -> {
            dialogQueue.remove(dialog);

            if (!dialogQueue.isEmpty()) {
                dialogQueue.get(0).show();
            }

            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        Log.d(TAG, "showDialog: " + title);
        Log.d(TAG, "dialogQueue: " + dialogQueue.size());

        dialogQueue.add(dialog);
        if (dialogQueue.size() == 1) {
            dialog.show();
        }
    }



    public void showClassAbilityDialog(String message) {
        showClassAbilityDialog(message, null);
    }

    public void showClassAbilityDialog(String message, Runnable onDismiss) {

        String title = "";
        String description = "";

        if (message.contains("\n\n")) {
            title = message.substring(0, message.indexOf("\n\n"));
            description = message.substring(message.indexOf("\n\n") + 2);
        } else {
            title = message;
        }


        showClassDialog(
                title,
                description,
                R.layout.game_use_class_ability_dialog_box,
                R.id.class_textview,
                R.id.description_textview,
                onDismiss
        );
    }



    public void showGameDialog(String message) {
        showDialog(message, R.layout.game_main_dialog_box, R.id.dialogbox_textview);
    }

    public void awardRandomClass(Player player, int number) {
        String[] classes = {
                ANGRY_JIM, ARCHER, GAMBLER, GOBLIN, QUIZ_MAGICIAN,
                SCIENTIST, SOLDIER, SURVIVOR, TROLL, WITCH
        };

        String currentClass = player.getClassChoice();
        List<String> availableClasses = new ArrayList<>();
        for (String c : classes) {
            if (!c.equals(currentClass)) {
                availableClasses.add(c);
            }
        }

        if (availableClasses.isEmpty()) return;

        String chosenClass = availableClasses.get(new Random().nextInt(availableClasses.size()));
        player.setClassChoice(chosenClass);
        player.setUsedActiveAbility(false);
        player.setJustUsedActiveAbility(false);
        AbilityComplimentary.assignActiveAbilityCooldown(player);

        showClassAbilityDialog("Class Obtained \n\n" + player.getName() + " landed on " + number + " and obtained the " + chosenClass + " Class!");
    }

    public void halveCurrentNumber() {
        int currentNumber = game.getCurrentNumber();
        int updatedNumber = Math.max(currentNumber / 2, 1);
        updateNumber(updatedNumber);
    }

    public void renderPlayerUI(boolean isPowerUp) {
        renderPlayer(isPowerUp);
    }

    //-----------------------------------------------------Wild Card Functionality---------------------------------------------------//
    private void wildCardActivate() {

        wasQuizCorrect = false;

        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.useWildCard();
        currentPlayer.incrementUsedWildcards();

        Runnable proceedToWildCard = () -> {

            WildCardRepository repository = new WildCardRepository(this);

            WildCardProperties[] quizWildCards = repository.loadQuizCards();
            WildCardProperties[] taskWildCards = repository.loadTaskCards();
            WildCardProperties[] truthWildCards = repository.loadTruthCards();

            WildCardProperties[] selectedType =
                    selectWildCardType(currentPlayer, quizWildCards, taskWildCards, truthWildCards);

            if (selectedType == null) {
                wildText.setText("No wild cards available, your turn is skipped!");
                btnWildContinue.setVisibility(View.VISIBLE);
                btnClassAbility.setVisibility(View.INVISIBLE);
                return;
            }

            WildCardProperties selectedCard =
                    WildCardRepository.getRandom(selectedType);

            handleSelectedCard(
                    selectedCard,
                    getWildCardType(selectedType, quizWildCards, taskWildCards)
            );
        };

        PassiveAbilities.checkGoblinPassive(currentPlayer, proceedToWildCard);
    }
    private WildCardProperties[] selectWildCardType(Player currentPlayer, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards, WildCardProperties[] truthWildCards) {
        if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedActiveAbility()) {
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


    private String getWildCardType(WildCardProperties[] selectedType, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards) {
        if (selectedType == quizWildCards) return "Quiz";
        if (selectedType == taskWildCards) return "Task";
        return "Truth";
    }

    public void setWasQuizCorrect(boolean correct) {
        this.wasQuizCorrect = correct;
    }

    public void handleSelectedCard(WildCardProperties selectedCard, String wildCardType) {
        if (selectedCard == null) return;

        selectedWildCard = selectedCard;
        wildCardDialogManager.showWildCardDialog(selectedCard, wildCardType);
        logSelectedCardInfo(selectedCard, wildCardType);
        Log.d(TAG, "handleSelectedCard: card type " + wildCardType);
    }




    //-----------------------------------------------------Specific WildCard Functions---------------------------------------------------//

    private void wildCardContinue() {
        Player currentPlayer = game.getCurrentPlayer();

        // Special case: Quiz Magician Active Ability allows for another activation
        if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) && currentPlayer.getJustUsedActiveAbility()) {
            wildCardActivate();
            currentPlayer.gainWildCards(1);
            currentPlayer.setUsedActiveAbility(true);
            currentPlayer.setJustUsedActiveAbility(false);
            return;
        }

        // Logic for ending the turn and potentially awarding a power-up
        boolean isQuiz = selectedWildCard != null && selectedWildCard.hasAnswer();
        boolean successfulTurn = !isQuiz || wasQuizCorrect;
        boolean powerupsEnabled = GeneralSettingsLocalStore.fromContext(this).arePowerupsActivated();
        boolean canReceivePowerUp = successfulTurn && currentPlayer.getPowerUps().size() < 2 && powerupsEnabled;

        Runnable finishWildCard = () -> {
            if (successfulTurn) {
                PowerUps.updatePowerUpIcons(currentPlayer);
            }
            currentPlayer.useSkip();
            resetUIAfterWildCard();
        };

        if (canReceivePowerUp) {
            PowerUps.getPowerUp(finishWildCard);
        } else {
            finishWildCard.run();
        }
    }

    private void resetUIAfterWildCard() {
        btnGenerate.setVisibility(View.VISIBLE);
        drinkNumberTextView.setVisibility(View.VISIBLE);
        numberCounterText.setVisibility(View.VISIBLE);
        nextPlayerText.setVisibility(View.VISIBLE);
        wildText.setVisibility(View.INVISIBLE);
        btnWildContinue.setVisibility(View.INVISIBLE);
    }
}
