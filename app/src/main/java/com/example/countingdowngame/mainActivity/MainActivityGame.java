package com.mydomain.countingdowngame.mainActivity;

import static android.content.ContentValues.TAG;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.mydomain.countingdowngame.mainActivity.MainActivityCatastrophes.decreaseNumberByRandom;
import static com.mydomain.countingdowngame.mainActivity.MainActivityCatastrophes.increaseNumberByRandom;
import static com.mydomain.countingdowngame.mainActivity.MainActivityCatastrophes.setCatastropheLimit;
import static com.mydomain.countingdowngame.mainActivity.MainActivityLogging.logPlayerInformation;
import static com.mydomain.countingdowngame.mainActivity.MainActivityLogging.logSelectedCardInfo;
import static com.mydomain.countingdowngame.mainActivity.classAbilities.ActiveAbilities.updateClassAbilityButton;
import static com.mydomain.countingdowngame.mainActivity.classAbilities.PassiveAbilities.characterPassiveClassAffects;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.game.GameEventType;
import com.mydomain.countingdowngame.mainActivity.classAbilities.ActiveAbilities;
import com.mydomain.countingdowngame.mainActivity.classAbilities.PassiveAbilities;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.settings.GeneralSettingsLocalStore;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;
import com.mydomain.countingdowngame.wildCards.wildCardTypes.WildCardRepository;

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
    public static int soldierActiveTurns = 0;
    public static boolean soldierRemoval;
    public static boolean catastrophesEnabled = true;
    public static boolean passivesEnabled = true;
    private static TextView numberCounterText;
    private static int turnCounter = 0;
    private static int catastropheTurnCounter = 0;

    //-----------------------------------------------------Maps and Sets---------------------------------------------------//
    public WildCardProperties selectedWildCard;
    Game game = Game.getInstance();

    //-----------------------------------------------------Views---------------------------------------------------//
    private Button btnWildContinue, btnGenerate;
    private View btnClassAbility, btnWild;
    private GifImageView infoGif, muteGif, soundGif;
    private ImageView playerImage;
    private TextView drinkNumberTextView, nextPlayerText, wildText, textWildCount, labelAbilityTitle, labelAbilityDesc;
    private ImageButton imageButtonExit;
    //-----------------------------------------------------Booleans---------------------------------------------------//
    private boolean doubleBackToExitPressedOnce = false;
    private boolean wasQuizCorrect = false;
    private WildCardDialogManager wildCardDialogManager;
    private int quizActiveQuestionsCount = 0;
    private int quizActiveCorrectCount = 0;
    private boolean isQuizActiveAbilitySession = false;
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

        boolean canSeeNumber = PassiveAbilities.canSeeNumber();

        if (canSeeNumber) {
            String textToDisplay = String.valueOf(currentNumber);
            numberCounterText.setText(textToDisplay);
            SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, textToDisplay);
            updateNumberColor();
        } else {
            String textToDisplay = "???";
            numberCounterText.setText(textToDisplay);
            SharedMainActivity.setTextViewSizeBasedOnInt(numberCounterText, textToDisplay);
            numberCounterText.setTextColor(ContextCompat.getColor(numberCounterText.getContext(), R.color.bluedark));
        }
    }

    public static String getDisplayNumber(int number) {
        boolean canSeeNumber = PassiveAbilities.canSeeNumber();
        return canSeeNumber ? String.valueOf(number) : "???";
    }

    public static void updateNumberColor() {
        if (numberCounterText == null) return;

        boolean canSeeNumber = PassiveAbilities.canSeeNumber();

        int blueDark = ContextCompat.getColor(numberCounterText.getContext(), R.color.bluedark);

        if (!canSeeNumber) {
            numberCounterText.setTextColor(blueDark);
            return;
        }
        numberCounterText.setTextColor(blueDark);
    }

    public static void resetStaticState() {
        drinkNumberCounterInt = 1;
        isFirstTurn = true;
        repeatedTurn = false;
        soldierActiveTurns = 0;
        soldierRemoval = false;
        turnCounter = 0;
        catastropheTurnCounter = 0;
        catastropheLimit = 0;
        catastrophesEnabled = true;
        passivesEnabled = true;
        PassiveAbilities.resetStaticState();
        ActiveAbilities.resetStaticState();
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

        numberGenerator = new MainActivityNumberGenerator(this, numberCounterText);
    }

    private void startGame() {
        resetStaticState();
        game.reset();

        catastrophesEnabled = GeneralSettingsLocalStore.fromContext(this).isCatastrophesActivated();

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
        btnUtils.setButton(btnClassAbility, ActiveAbilities::activateActiveAbility);

        btnUtils.setButton(btnWild, this::wildCardActivate);

        btnUtils.setButton(imageButtonExit, () -> {
            game.endGame(this);
            gotoHomeScreen();
        });

        btnUtils.setButton(infoGif, mainActivityDialog::showInstructionDialog);
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void characterClassDescriptions() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer != null) {
            mainActivityDialog.characterClassInformationDialog(currentPlayer);
        }
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
            activePlayer.decrementCooldowns();
            activePlayer.setUsedActiveAbility(false);
            characterPassiveClassAffects();
            PassiveAbilities.showCombinedPassives();
            PassiveAbilities.resetGoblinTrigger();
            isQuizActiveAbilitySession = false;
            quizActiveQuestionsCount = 0;
            quizActiveCorrectCount = 0;
            updateTurnCounter();
            updateCatastropheTurnCounter();
        }

        logPlayerInformation(activePlayer);

        btnUtils.setButton(btnGenerate, () -> {
            if (PassiveAbilities.canSeeNumber()) {
                handleGenerateClick();
            } else {
                PassiveAbilities.showRevealNumberDialog(this::handleGenerateClick);
            }
        });

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
            PassiveAbilities.clearTrollDebuffs(game.getCurrentPlayer());
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
            mainActivityDialog.showDialog(catastrophe.getMessage(), R.layout.game_catastrophe_dialog_box, R.id.dialogbox_textview);
            game.incrementCatastropheQuantity();
            catastropheTurnCounter = 0; // Reset the turn counter after reaching the limit

            // Generate a new random catastrophe limit
            setCatastropheLimit();
        }
    }

    private void updateWildCardVisibilityIfNeeded(Player currentPlayer) {
        if (!currentPlayer.getJustUsedWildCard()) {
            updateWildCardVisibility(currentPlayer);
        }

        TextView labelWild = findViewById(R.id.labelWild);
        TextView textWildCountView = findViewById(R.id.textWildCount);
        ImageView iconWild = findViewById(R.id.iconWild);

        if (currentPlayer.areWildcardsConsumed()) {
            if (currentPlayer.getWildCardAmount() > 0) {
                btnWild.setVisibility(View.VISIBLE);
                btnWild.setEnabled(false);
                btnWild.setAlpha(0.5f);
                labelWild.setText("Consumed");
                textWildCountView.setText("The Troll ate your cards!");
                textWildCountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                iconWild.setImageResource(R.drawable.eat);
            } else {
                btnWild.setVisibility(View.INVISIBLE);
            }
            return;
        }

        // Reset to normal state if not consumed
        btnWild.setEnabled(true);
        btnWild.setAlpha(1.0f);
        labelWild.setText("Wild Cards");
        textWildCountView.setText(String.valueOf(currentPlayer.getWildCardAmount()));
        textWildCountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        iconWild.setImageResource(R.drawable.playingcards);

        if (currentPlayer.getJustUsedWildCard()) {
            btnWild.setVisibility(View.INVISIBLE);
            currentPlayer.setJustUsedWildCard(false);
        }

        if (soldierActiveTurns > 0) {
            btnWild.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTextSize(String text, TextView textView) {
        int size = SharedMainActivity.TextSizeCalculatorPlayerName.calculateTextSizeBasedOnCharacterCount(text);

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
            playerImage.animate().rotationY(90f).setDuration(150).withEndAction(() -> {
                playerImage.setImageBitmap(decodedBitmap);
                playerImage.setRotationY(-90f);
                playerImage.animate().rotationY(0f).setDuration(150).start();
            }).start();

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
        playerImage.setEnabled(true);
        infoGif.setEnabled(true);
        imageButtonExit.setEnabled(true);
        findViewById(R.id.powerup_left).setEnabled(true);
        findViewById(R.id.powerup_right).setEnabled(true);

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer != null) {
            updateWildCardVisibilityIfNeeded(currentPlayer);
            updateClassAbilityButton(currentPlayer);
        }
    }

    //-----------------------------------------------------Active Effects---------------------------------------------------//

    public void renderPlayerUI(boolean isPowerUp) {
        renderPlayer(isPowerUp);
    }

    //-----------------------------------------------------Wild Card Functionality---------------------------------------------------//
    private void wildCardActivate() {

        wasQuizCorrect = false;

        Player currentPlayer = game.getCurrentPlayer();
        btnWild.setVisibility(View.INVISIBLE);
        currentPlayer.useWildCard();
        currentPlayer.incrementUsedWildcards();

        Runnable proceedToWildCard = () -> {

            WildCardRepository repository = new WildCardRepository(this);

            WildCardProperties[] quizWildCards = Arrays.stream(repository.loadQuizCards()).filter(WildCardProperties::isEnabled).toArray(WildCardProperties[]::new);
            WildCardProperties[] taskWildCards = Arrays.stream(repository.loadTaskCards()).filter(WildCardProperties::isEnabled).toArray(WildCardProperties[]::new);
            WildCardProperties[] truthWildCards = Arrays.stream(repository.loadTruthCards()).filter(WildCardProperties::isEnabled).toArray(WildCardProperties[]::new);

            WildCardProperties[] selectedType = selectWildCardType(currentPlayer, quizWildCards, taskWildCards, truthWildCards);

            if (selectedType == null) {
                currentPlayer.useSkip();
                return;
            }

            WildCardProperties selectedCard = WildCardRepository.getRandom(selectedType);

            handleSelectedCard(selectedCard, getWildCardType(selectedType, quizWildCards, taskWildCards));
        };

        PassiveAbilities.checkGoblinPassive(currentPlayer, proceedToWildCard);
    }

    private WildCardProperties[] selectWildCardType(Player currentPlayer, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards, WildCardProperties[] truthWildCards) {
        GeneralSettingsLocalStore settings = GeneralSettingsLocalStore.fromContext(this);

        if (QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) && isQuizActiveAbilitySession) {
            return quizWildCards.length > 0 ? quizWildCards : null;
        }

        List<WildCardProperties[]> enabledTypes = new ArrayList<>();
        if (settings.isQuizActivated() && quizWildCards.length > 0) enabledTypes.add(quizWildCards);
        if (settings.isTaskActivated() && taskWildCards.length > 0) enabledTypes.add(taskWildCards);
        if (settings.isTruthActivated() && truthWildCards.length > 0) enabledTypes.add(truthWildCards);

        if (enabledTypes.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return enabledTypes.get(random.nextInt(enabledTypes.size()));
    }


    private String getWildCardType(WildCardProperties[] selectedType, WildCardProperties[] quizWildCards, WildCardProperties[] taskWildCards) {
        if (selectedType == quizWildCards) return "Quiz";
        if (selectedType == taskWildCards) return "Task";
        return "Truth";
    }

    public void setWasQuizCorrect(boolean correct) {
        this.wasQuizCorrect = correct;
    }

    public boolean isQuizActiveAbilitySession() {
        return isQuizActiveAbilitySession;
    }

    public void startQuizMagicianActiveSession() {
        isQuizActiveAbilitySession = true;
        quizActiveQuestionsCount = 0;
        quizActiveCorrectCount = 0;
        wildCardActivate();
    }

    private void finalizeQuizMagicianActive() {
        isQuizActiveAbilitySession = false;
        Player currentPlayer = game.getCurrentPlayer();

        if (quizActiveCorrectCount > 0) {
            int drinksToHandOut = calculateQuizMagicianDrinks(quizActiveCorrectCount);
            String drinkText = (drinksToHandOut == 1) ? "drink" : "drinks";
            String message = "Streak Over! \n\n" + currentPlayer.getName() + " got " + quizActiveCorrectCount + " correct!\n\nHand out " + drinksToHandOut + " " + drinkText + " to everyone!";
            mainActivityDialog.showMainDialog(message, this::wildCardContinue);
            updateDrinkNumberCounter(drinksToHandOut, true);
        } else {
            mainActivityDialog.showMainDialog("Streak Over! \n\n" + currentPlayer.getName() + " got none correct. \n\nTake a drink!", this::wildCardContinue);
        }
    }

    private int calculateQuizMagicianDrinks(int correct) {
        return correct;
    }

    public void handleSelectedCard(WildCardProperties selectedCard, String wildCardType) {
        if (selectedCard == null) return;

        selectedWildCard = selectedCard;
        wildCardDialogManager.showWildCardDialog(selectedCard, wildCardType);
        logSelectedCardInfo(selectedCard, wildCardType);
    }


    //-----------------------------------------------------Specific WildCard Functions---------------------------------------------------//

    private void wildCardContinue() {
        Player currentPlayer = game.getCurrentPlayer();

        if (isQuizActiveAbilitySession) {
            quizActiveQuestionsCount++;
            if (wasQuizCorrect) {
                quizActiveCorrectCount++;
            }

            if (quizActiveQuestionsCount < 5 && wasQuizCorrect) {
                wildCardActivate();
                return;
            } else {
                finalizeQuizMagicianActive();
                return;
            }
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
