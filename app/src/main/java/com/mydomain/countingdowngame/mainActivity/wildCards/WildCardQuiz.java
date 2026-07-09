package com.mydomain.countingdowngame.mainActivity.wildCards;

import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.MainActivityGame;
import com.mydomain.countingdowngame.mainActivity.SharedMainActivity;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/**
 * Handles the logic and UI for the Quiz WildCard.
 */
public class WildCardQuiz {

    private final MainActivityGame activity;
    private final WildCardDialogManager.DialogUI ui;
    private final WildCardProperties selectedCard;
    private final Player player;
    private final boolean isQuizMagician;
    private final QuizCallback callback;

    public interface QuizCallback {
        void onQuizResult(boolean correct);
        void onDismissRequested();
    }

    public WildCardQuiz(MainActivityGame activity, WildCardDialogManager.DialogUI ui, WildCardProperties selectedCard, Player player, boolean isQuizMagician, QuizCallback callback) {
        this.activity = activity;
        this.ui = ui;
        this.selectedCard = selectedCard;
        this.player = player;
        this.isQuizMagician = isQuizMagician;
        this.callback = callback;
    }

    // ==========================================
    // 1. INITIAL SETUP
    // ==========================================

    public void setupMultipleChoice() {
        if (ui.btnAnswer != null) ui.btnAnswer.setVisibility(View.GONE);

        String[] answers = buildAnswers();
        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);

        resetAnswerButtons();

        int visibleCount = isQuizMagician ? 2 : 4;
        for (int i = 0; i < visibleCount; i++) {
            final int index = i;
            Button btn = ui.answerButtons[index];
            if (btn == null) continue;

            String answer = answerList.get(index);
            btn.setVisibility(View.VISIBLE);
            btn.setText(answer);

            int size = SharedMainActivity.TextSizeCalculatorQuizAnswers.calculateTextSizeBasedOnCharacterCount(answer);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

            activity.btnUtils.setButton(btn, () -> processMCQSelection(answerList, answer, index));
        }
    }

    public void setupTrueFalse() {
        hideAllChoices();
        if (ui.btnAnswer != null) {
            ui.btnAnswer.setVisibility(View.VISIBLE);
            activity.btnUtils.setButton(ui.btnAnswer, this::revealTrueFalseOptions);
        }
    }

    // ==========================================
    // 2. INTERACTION LOGIC
    // ==========================================

    private void processMCQSelection(List<String> answers, String selected, int index) {
        disableButtons(ui.answerButtons);

        boolean correct = Objects.equals(selected, selectedCard.getAnswer());
        callback.onQuizResult(correct);

        if (correct) {
            applySuccessFeedback(index);
            Game.getInstance().incrementPlayerQuizCorrectAnswers(player);
        } else {
            applyFailureFeedback(answers, index);
            Game.getInstance().incrementPlayerQuizIncorrectAnswers(player);
        }

        new Handler().postDelayed(() -> {
            hideConfetti();
            finalizeQuiz(correct);
        }, 1500);
    }

    private void revealTrueFalseOptions() {
        if (ui.btnAnswer != null) ui.btnAnswer.setVisibility(View.GONE);
        ui.text.setText(selectedCard.getAnswer());

        if (ui.answerButtons[0] != null) {
            ui.answerButtons[0].setVisibility(View.VISIBLE);
            ui.answerButtons[0].setText(R.string.were_you_right);
            activity.btnUtils.setButton(ui.answerButtons[0], () -> processTFSelection(true));
        }
        if (ui.answerButtons[1] != null) {
            ui.answerButtons[1].setVisibility(View.VISIBLE);
            ui.answerButtons[1].setText(R.string.were_you_wrong);
            activity.btnUtils.setButton(ui.answerButtons[1], () -> processTFSelection(false));
        }
    }

    private void processTFSelection(boolean correct) {
        callback.onQuizResult(correct);

        if (correct) {
            Game.getInstance().incrementPlayerQuizCorrectAnswers(player);
        } else {
            Game.getInstance().incrementPlayerQuizIncorrectAnswers(player);
        }

        finalizeQuiz(correct);
    }

    // ==========================================
    // 3. FINALIZATION
    // ==========================================

    private void finalizeQuiz(boolean correct) {
        if (isQuizMagicianActive()) {
            if (correct) {
                showMagicianStreakChoice();
            } else {
                activity.setWasQuizCorrect(false);
                callback.onDismissRequested();
                activity.stopQuizMagicianStreak();
            }
            return;
        }

        displayFinalMessage(correct);
        hideAllChoices();
        if (ui.btnContinue != null) ui.btnContinue.setVisibility(View.VISIBLE);
    }

    /**
     * Swaps the quiz content for the magician streak choice within the same dialog.
     */
    private void showMagicianStreakChoice() {
        hideAllChoices();
        if (ui.text != null) ui.text.setVisibility(View.GONE);
        if (ui.magicianStreakGroup != null) ui.magicianStreakGroup.setVisibility(View.VISIBLE);

        int currentStreak = activity.getQuizActiveCorrectCount() + 1;
        ui.title.setText("Streak: " + currentStreak);

        activity.btnUtils.setButton(ui.btnMagicianContinue, () -> {
            activity.setWasQuizCorrect(true);
            activity.wildCardContinue();
        });

        activity.btnUtils.setButton(ui.btnMagicianStop, () -> {
            callback.onDismissRequested();
            activity.setWasQuizCorrect(true);
            activity.stopQuizMagicianStreak();
        });
    }

    // ==========================================
    // 4. HELPERS
    // ==========================================

    private void applySuccessFeedback(int index) {
        if (ui.answerButtons[index] != null) ui.answerButtons[index].setBackgroundResource(R.drawable.buttonhighlightgreen);
        if (ui.confetti[index] != null) ui.confetti[index].setVisibility(View.VISIBLE);
        AudioManager.getInstance().playConfettiSound(activity);
    }

    private void applyFailureFeedback(List<String> answers, int selectedIndex) {
        if (ui.answerButtons[selectedIndex] != null) ui.answerButtons[selectedIndex].setBackgroundResource(R.drawable.buttonhighlightred);
        for (int j = 0; j < answers.size(); j++) {
            if (Objects.equals(answers.get(j), selectedCard.getAnswer())) {
                if (ui.answerButtons[j] != null) ui.answerButtons[j].setBackgroundResource(R.drawable.buttonhighlightgreen);
                break;
            }
        }
    }

    private void displayFinalMessage(boolean correct) {
        boolean isMagician = QUIZ_MAGICIAN.equals(player.getClassChoice());
        String msg;
        if (correct) {
            msg = player.getName() + " that's right! " + (isMagician ? "\n\n  Quiz Magician's Passive: Give out 2 drinks to everyone." : "\n\n Give out a drink.");
        } else {
            msg = player.getName() + " big ooooff! Take a drink.";
        }
        ui.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        ui.text.setText(msg);
    }

    private String[] buildAnswers() {
        if (isQuizMagician) {
            Random random = new Random();
            String wrong = random.nextBoolean() ? selectedCard.getWrongAnswer1() : (random.nextBoolean() ? selectedCard.getWrongAnswer2() : selectedCard.getWrongAnswer3());
            return new String[]{selectedCard.getAnswer(), wrong};
        }
        return new String[]{selectedCard.getAnswer(), selectedCard.getWrongAnswer1(), selectedCard.getWrongAnswer2(), selectedCard.getWrongAnswer3()};
    }

    private void resetAnswerButtons() {
        for (Button b : ui.answerButtons) {
            if (b == null) continue;
            b.setVisibility(View.GONE);
            b.setEnabled(true);
        }
    }

    private void disableButtons(Button[] buttons) {
        for (Button b : buttons) if (b != null) b.setEnabled(false);
    }

    private void hideAllChoices() {
        if (ui.btnAnswer != null) ui.btnAnswer.setVisibility(View.GONE);
        for (Button b : ui.answerButtons) if (b != null) b.setVisibility(View.GONE);
    }

    private void hideConfetti() {
        for (GifImageView gif : ui.confetti) if (gif != null) gif.setVisibility(View.GONE);
    }

    private boolean isQuizMagicianActive() {
        boolean isMagician = QUIZ_MAGICIAN.equals(player.getClassChoice());
        return isMagician && activity.isQuizActiveAbilitySession();
    }
}
