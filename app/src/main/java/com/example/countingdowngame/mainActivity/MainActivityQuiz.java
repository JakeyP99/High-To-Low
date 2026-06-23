package com.example.countingdowngame.mainActivity;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class MainActivityQuiz extends SharedMainActivity {

    private static final int BUTTON_COUNT = 4;
    private static final int BUTTON_COUNT_2 = 2;
    private static final int DELAY_MILLIS = 1500;

    private Button btnAnswer, btnWildContinue, btnQuizAnswerBL, btnQuizAnswerBR, btnQuizAnswerTL, btnQuizAnswerTR;
    private GifImageView confettiImageViewBL, confettiImageViewBR, confettiImageViewTL, confettiImageViewTR, muteGif, soundGif, infoGif;
    private TextView wildText;
    private ImageButton imageButtonExit;

    private Button[] answerButtons;
    private WildCardProperties selectedWildCard;
    private boolean wasQuizCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_quiz_activity);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);

        selectedWildCard = (WildCardProperties) getIntent().getSerializableExtra("selectedWildCard");
        if (selectedWildCard == null) {
            finish();
            return;
        }

        setupButtons();
        startQuiz();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        infoGif = findViewById(R.id.informationGif);
        wildText = findViewById(R.id.textView_WildText);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnWildContinue = findViewById(R.id.btnBackWildCard);
        btnQuizAnswerBL = findViewById(R.id.btnQuizAnswerBL);
        btnQuizAnswerBR = findViewById(R.id.btnQuizAnswerBR);
        btnQuizAnswerTL = findViewById(R.id.btnQuizAnswerTL);
        btnQuizAnswerTR = findViewById(R.id.btnQuizAnswerTR);
        imageButtonExit = findViewById(R.id.btnExitGame);

        confettiImageViewBL = findViewById(R.id.confettiImageViewBL);
        confettiImageViewTL = findViewById(R.id.confettiImageViewTL);
        confettiImageViewBR = findViewById(R.id.confettiImageViewBR);
        confettiImageViewTR = findViewById(R.id.confettiImageViewTR);

        answerButtons = new Button[]{btnQuizAnswerBL, btnQuizAnswerBR, btnQuizAnswerTL, btnQuizAnswerTR};
    }

    private void setupButtons() {
        btnUtils.setButton(btnAnswer, this::showAnswer);
        btnUtils.setButton(btnWildContinue, this::wildCardContinue);
        btnUtils.setButton(imageButtonExit, () -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
            overridePendingTransition(0, 0);
        });
        btnUtils.setButton(infoGif, this::showInstructionDialog);
    }

    private void startQuiz() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        String selectedActivity = selectedWildCard.getWildCard();
        wildText.setText(selectedActivity);
        updateTextSize(selectedActivity);

        if (selectedWildCard.hasAnswer()) {
            if (currentPlayerHasQuizMagicianPassive(currentPlayer)) {
                setMultiChoiceRandomizedAnswersForQuizMagician(selectedWildCard);
                btnAnswer.setVisibility(View.INVISIBLE);
            } else if (GeneralSettingsLocalStore.fromContext(this).isMultiChoice()) {
                setMultiChoiceRandomizedAnswers(selectedWildCard);
                btnAnswer.setVisibility(View.INVISIBLE);
            } else {
                btnAnswer.setVisibility(View.VISIBLE);
            }
        } else {
            btnAnswer.setVisibility(View.INVISIBLE);
            btnWildContinue.setVisibility(View.VISIBLE);
        }
    }

    private void updateTextSize(String selectedActivity) {
        int textSize = TextSizeCalculator.calculateTextSizeBasedOnCharacterCount(selectedActivity);
        wildText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private boolean currentPlayerHasQuizMagicianPassive(Player currentPlayer) {
        return QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) || (CharacterClassDescriptions.ANGRY_JIM.equals(currentPlayer.getClassChoice()) && Game.getInstance().getCurrentNumber() < 50);
    }

    private void setMultiChoiceRandomizedAnswers(WildCardProperties selectedCard) {
        exposeQuizButtons();
        String[] possibleAnswers = {selectedCard.getAnswer(), selectedCard.getWrongAnswer1(), selectedCard.getWrongAnswer2(), selectedCard.getWrongAnswer3()};
        List<String> answerList = Arrays.asList(possibleAnswers);
        Collections.shuffle(answerList);
        String[] randomizedAnswers = answerList.toArray(new String[0]);
        setAnswersToFourButtons(randomizedAnswers);
    }

    private void setMultiChoiceRandomizedAnswersForQuizMagician(WildCardProperties selectedCard) {
        exposeQuizButtons();
        Random random = new Random();
        String[] possibleAnswers = {selectedCard.getAnswer(), random.nextBoolean() ? selectedCard.getWrongAnswer1() : (random.nextBoolean() ? selectedCard.getWrongAnswer2() : selectedCard.getWrongAnswer3())};
        List<String> answerList = Arrays.asList(possibleAnswers);
        Collections.shuffle(answerList);
        String[] randomizedAnswers = answerList.toArray(new String[0]);
        setAnswersToTwoButtons(randomizedAnswers);
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
        wasQuizCorrect = isCorrect;
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
                quizAnswerView(currentPlayer.getName() + " that's right! The answer was " + selectedWildCard.getAnswer() + "\n\n P.S. You get to give out 2 drinks to everyone.");
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra("wasQuizCorrect", wasQuizCorrect);
        setResult(RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void displayConfetti(View confettiView) {
        AudioManager.getInstance().playConfettiSound(this);
        confettiView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> confettiView.setVisibility(View.INVISIBLE), 1500);
    }

    private View getConfettiView(int buttonId) {
        if (buttonId == R.id.btnQuizAnswerTL) return confettiImageViewTL;
        if (buttonId == R.id.btnQuizAnswerTR) return confettiImageViewTR;
        if (buttonId == R.id.btnQuizAnswerBL) return confettiImageViewBL;
        if (buttonId == R.id.btnQuizAnswerBR) return confettiImageViewBR;
        return null;
    }

    private void quizAnswerView(String string) {
        btnWildContinue.setVisibility(View.VISIBLE);
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
        btnQuizAnswerBL.setText(R.string.were_you_right);
        btnQuizAnswerBR.setText(R.string.were_you_wrong);
        if (selectedWildCard.hasAnswer()) {
            String answer = selectedWildCard.getAnswer();
            wildText.setText(answer);
            btnWildContinue.setVisibility(View.INVISIBLE);
            btnUtils.setButton(btnQuizAnswerBL, () -> {
                wasQuizCorrect = true;
                btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                quizAnswerView(currentPlayer.getName() + " since you got it right, give out a drink!");
                Game.getInstance().incrementPlayerQuizCorrectAnswers(currentPlayer);
            });
            btnUtils.setButton(btnQuizAnswerBR, () -> {
                wasQuizCorrect = false;
                btnQuizAnswerBL.setVisibility(View.INVISIBLE);
                btnQuizAnswerBR.setVisibility(View.INVISIBLE);
                quizAnswerView(currentPlayer.getName() + " since you got it wrong, take a drink!");
                Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);
            });
        } else {
            wildText.setText("No answer available");
        }
        btnAnswer.setVisibility(View.INVISIBLE);
    }
}
