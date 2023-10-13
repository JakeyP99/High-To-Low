package com.example.countingdowngame.mainActivity;

import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_ARCHER;
import static com.example.countingdowngame.createPlayer.PlayerChoice.CLASS_JIM;
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
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.stores.PlayerModelLocalStore;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

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
    private Button btnAnswerRight;
    private Button btnAnswerWrong;

    private ImageView playerImage;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private Handler shuffleHandler;

    private final Map<Player, Set<WildCardProperties>> usedWildCard = new HashMap<>();
    private final Set<WildCardProperties> usedWildCards = new HashSet<>();
    private Map<Player, Integer> playerTurnCountMap = new HashMap<>();

    public static int drinkNumberCounterInt = 0;
    private WildCardProperties selectedWildCard; // Declare selectedWildCard at a higher level
    private Player firstPlayer;
    private boolean isFirstTurn = true;
    private boolean soldierRemoval = false;

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
    }

    private void initializeViews() {
        playerImage = findViewById(R.id.playerImage);
        numberText = findViewById(R.id.textView_NumberText);
        drinkNumberCounterTextView = findViewById(R.id.textView_numberCounter);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnClassAbility = findViewById(R.id.btnClassAbility);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackWild = findViewById(R.id.btnBackWildCard);
        btnAnswerRight = findViewById(R.id.btnGotQuizRight);
        btnAnswerWrong = findViewById(R.id.btnGotQuizWrong);
        shuffleHandler = new Handler();
        wildText = findViewById(R.id.textView_WildText);
    }


    private void startGame() {
        drinkNumberCounterInt = 0;
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

    private String getClassDescription(String classChoice) {
        switch (classChoice) {
            case CLASS_ARCHER:
                return CharacterClassDescriptions.CLASS_ARCHER_DESCRIPTION;
            case CLASS_WITCH:
                return CharacterClassDescriptions.CLASS_WITCH_DESCRIPTION;
            case CLASS_SCIENTIST:
                return CharacterClassDescriptions.CLASS_SCIENTIST_DESCRIPTION;
            case CLASS_SOLDIER:
                return CharacterClassDescriptions.CLASS_SOLDIER_DESCRIPTION;
            case CLASS_JIM:
                return CharacterClassDescriptions.CLASS_JIM_DESCRIPTION;
            default:
                return "I love you cutie pie hehe. You don't have a class to show any description for.";
        }
    }



    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupButtons() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("setupButtons", " " + currentPlayer.getClassChoice());

        ImageButton imageButtonExit = findViewById(R.id.btnExitGame);

        btnBackWild.setVisibility(View.INVISIBLE);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnAnswerRight.setVisibility(View.INVISIBLE);
        btnAnswerWrong.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, () -> {
            startNumberShuffleAnimation();
            isFirstTurn = false;
        }, this);

        playerImage.setOnClickListener(v -> {
            setupPlayerImageClickListener(); // Corrected this line
        });


        btnUtils.setButton(btnAnswer, this::showAnswer, this);

        btnUtils.setButton(btnBackWild, this::wildCardContinue, this);

        btnUtils.setButton(btnClassAbility, this::characterActiveAbilities, this);

        btnUtils.setButton(btnWild, () -> {
            wildCardActivate(Game.getInstance().getCurrentPlayer());
            drinkNumberCounterTextView.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            btnWild.setVisibility(View.INVISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
            isFirstTurn = false;
        }, this);

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
                String classDescription = currentPlayer.getClassChoice() + "\n\n" + getClassDescription(currentPlayerClassChoice);
                showDialogWithFixedTextSize(classDescription, 20);
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
        btnClassAbility.setText(String.format("%s's Ability", currentPlayer.getClassChoice()));


        if (("Scientist".equals(currentPlayer.getClassChoice()) ||
                "Archer".equals(currentPlayer.getClassChoice()) ||
                "Witch".equals(currentPlayer.getClassChoice()) ||
                "Soldier".equals(currentPlayer.getClassChoice())
        ) &&
                !currentPlayer.usedClassAbility()) {
            btnClassAbility.setVisibility(View.VISIBLE);
        } else {
            btnClassAbility.setVisibility(View.INVISIBLE);
        }
        if ("Jim".equals(currentPlayer.getClassChoice())) {
            btnClassAbility.setVisibility(View.INVISIBLE);
        }

        if (currentPlayer.equals(firstPlayer)) {
            drinkNumberCounterInt++;
            updateDrinkNumberCounterTextView();
        }

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

        btnWild.setVisibility(currentPlayer.getWildCardAmount() > 0 ? View.VISIBLE : View.INVISIBLE);

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        SharedMainActivity.setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());

        Log.d("renderPlayer", "Current number is " + Game.getInstance().getCurrentNumber() + " - Player was rendered " +
                currentPlayer.getName() + " is a " + currentPlayer.getClassChoice() + " with " + currentPlayer.getWildCardAmount() +
                " Wildcards " + "and " + currentPlayer.usedClassAbility() + " is the class abilitiy and are they removed ?" + currentPlayer.isRemoved());
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
        if ("Soldier".equals(currentPlayer.getClassChoice())) {
            new Handler().postDelayed(() -> {
                if (!currentPlayer.isRemoved()) {
                    removeCharacterFromGame();
                } else {
                    currentPlayer.useSkip();
                }
            }, 1);
        }
        if ("Witch".equals(currentPlayer.getClassChoice())) {
            if (!isFirstTurn) {
                if (Game.getInstance().getCurrentNumber() % 2 == 0) {
                    showDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " hand out two drinks.");
                } else {
                    showDialog("Witch's Passive: \n\n" + currentPlayer.getName() + " take a drink.");
                }
            }
        }
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
        if ("Jim".equals(currentPlayer.getClassChoice())) {
            int turnCounter = currentPlayer.getTurnCounter();
            if (turnCounter > 0 && turnCounter % 3 == 0) {
                currentPlayer.gainWildCards(1);
            }
        }
        if ("Archer".equals(currentPlayer.getClassChoice())) {
            int currentPlayerTurnCount = playerTurnCountMap.getOrDefault(currentPlayer, 0);
            currentPlayerTurnCount++;

            Log.d("ArcherClass", "Turn count: " + currentPlayerTurnCount);
            playerTurnCountMap.put(currentPlayer, currentPlayerTurnCount);

            if (currentPlayerTurnCount % 3 == 0) {
                Log.d("ArcherClass", "Passive ability triggered");

                if (new Random().nextInt(100) < 60) {
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

    public void characterActiveAbilities() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        Log.d("characterActiveAbilities", "Class Activated" + currentPlayer.getClassChoice());
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
        TextView dialogboxtextview = dialogView.findViewById(R.id.dialogbox_textview);
        dialogboxtextview.setText(string);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
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

        EditText editCurrentNumberText = dialogView.findViewById(R.id.editCurrentNumberTextView);
        Button okButton = dialogView.findViewById(R.id.close_button);

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

        final TextView wildActivityTextView = findViewById(R.id.textView_WildText);

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
                btnAnswer.setVisibility(View.VISIBLE);
                btnBackWild.setVisibility(View.INVISIBLE);
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
                    btnAnswer.setVisibility(View.VISIBLE);
                } else {
                    btnAnswer.setVisibility(View.INVISIBLE);
                }
            } else {
                btnAnswer.setVisibility(View.INVISIBLE);
            }
            Log.d("WildCardType", "Type: " + wildCardType); // Log the type of wildcard
        }


        switch (Objects.requireNonNull(selectedActivity)) {
            case "Double the current number!": {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.min(currentNumber * 2, 999999999);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
                break;
            }
            case "Half the current number!": {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.max(currentNumber / 2, 1);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
                break;
            }
            case "Reset the number!":
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    throw new RuntimeException("Missing extras");
                }
                int startingNumber = extras.getInt("startingNumber");
                Game.getInstance().setCurrentNumber(startingNumber);
                Game.getInstance().addUpdatedNumber(startingNumber);
                numberText.setText(String.valueOf(startingNumber));
                SharedMainActivity.setTextViewSizeBasedOnInt(numberText, String.valueOf(startingNumber));
                break;
            case "Reverse the turn order!":
                reverseTurnOrder(player);
                break;
            case "Gain a couple more wildcards to use, I gotchya back!":
                Game.getInstance().getCurrentPlayer().gainWildCards(3);
                break;
            case "Lose a couple wildcards :( oh also drink 3 lol!":
                Game.getInstance().getCurrentPlayer().loseWildCards();
                break;
        }
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
        btnAnswerRight.setVisibility(View.INVISIBLE);
        btnAnswerWrong.setVisibility(View.INVISIBLE);
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

    private void showAnswer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();

        TextView wildActivityTextView = findViewById(R.id.textView_WildText);
        btnAnswerRight.setVisibility(View.VISIBLE);
        btnAnswerWrong.setVisibility(View.VISIBLE);

        if (selectedWildCard != null) {
            if (selectedWildCard.hasAnswer()) {
                String answer = selectedWildCard.getAnswer();
                wildActivityTextView.setText(answer);
                Log.d("Answer", "Quiz WildCard: " + answer);

                btnBackWild.setVisibility(View.INVISIBLE);

                btnUtils.setButton(btnAnswerRight, () -> {
                    Game.getInstance().getCurrentPlayer().gainWildCards(1);
                    btnAnswerRight.setVisibility(View.INVISIBLE);
                    btnAnswerWrong.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it right, give out a drink! \n\n P.S. You get to keep your wildcard too.");
                }, this);

                btnUtils.setButton(btnAnswerWrong, () -> {
                    btnAnswerRight.setVisibility(View.INVISIBLE);
                    btnAnswerWrong.setVisibility(View.INVISIBLE);
                    quizAnswerView(currentPlayer.getName() + " since you got it wrong, take a drink! \n\n P.S. Maybe read a book once in a while.");
                }, this);

            } else {
                wildActivityTextView.setText("No answer available");
            }
        }
        btnAnswer.setVisibility(View.INVISIBLE);
    }
}
