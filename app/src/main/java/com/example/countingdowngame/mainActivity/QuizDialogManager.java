package com.example.countingdowngame.mainActivity;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class QuizDialogManager {

    private final MainActivityGame activity;
    private final Runnable onContinue;
    private boolean wasQuizCorrect = false;

    public QuizDialogManager(MainActivityGame activity, Runnable onContinue) {
        this.activity = activity;
        this.onContinue = onContinue;
    }

    public void showQuizDialog(WildCardProperties selectedCard) {
        View dialogView = inflateDialog();
        AlertDialog dialog = createDialog(dialogView);

        UIRefs ui = bindViews(dialogView);
        setupBaseUI(ui, selectedCard);

        Player player = Game.getInstance().getCurrentPlayer();

        boolean isQuizMagician =
                QUIZ_MAGICIAN.equals(player.getClassChoice()) ||
                        (CharacterClassDescriptions.ANGRY_JIM.equals(player.getClassChoice())
                                && Game.getInstance().getCurrentNumber() < 50);

        boolean isMultiChoice =
                GeneralSettingsLocalStore.fromContext(activity).isMultiChoice();

        boolean isQuizMode = isQuizMagician || isMultiChoice;

        if (isQuizMode) {
            setupMultipleChoice(ui, selectedCard, player, isQuizMagician);
        } else {
            setupTrueFalse(ui, selectedCard, player);
        }

        setupContinue(ui.btnContinue, dialog);
        dialog.show();
    }

    // ---------------- SETUP ----------------

    private View inflateDialog() {
        return activity.getLayoutInflater()
                .inflate(R.layout.game_quiz_dialog, null);
    }

    private AlertDialog createDialog(View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    private UIRefs bindViews(View view) {
        return new UIRefs(
                view.findViewById(R.id.textView_WildText),
                view.findViewById(R.id.btnAnswer),
                view.findViewById(R.id.btnBackWildCard),
                new Button[]{
                        view.findViewById(R.id.btnQuizAnswerTL),
                        view.findViewById(R.id.btnQuizAnswerTR),
                        view.findViewById(R.id.btnQuizAnswerBL),
                        view.findViewById(R.id.btnQuizAnswerBR)
                },
                new GifImageView[]{
                        view.findViewById(R.id.confettiImageViewTL),
                        view.findViewById(R.id.confettiImageViewTR),
                        view.findViewById(R.id.confettiImageViewBL),
                        view.findViewById(R.id.confettiImageViewBR)
                }
        );
    }

    private void setupBaseUI(UIRefs ui, WildCardProperties card) {
        ui.text.setText(card.getWildCard());
        updateTextSize(card.getWildCard(), ui.text);
    }

    // ---------------- MULTIPLE CHOICE ----------------

    private void setupMultipleChoice(UIRefs ui,
                                     WildCardProperties card,
                                     Player player,
                                     boolean isQuizMagician) {

        ui.btnAnswer.setVisibility(View.GONE);

        String[] answers = buildAnswers(card, player, isQuizMagician);
        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);

        for (Button b : ui.answerButtons) {
            b.setVisibility(View.GONE);
            b.setEnabled(true);
        }

        int visibleCount = isQuizMagician ? 2 : 4;

        for (int i = 0; i < visibleCount; i++) {

            Button btn = ui.answerButtons[i];
            String answer = answerList.get(i);

            btn.setVisibility(View.VISIBLE);
            btn.setText(answer);

            int index = i;

            btn.setOnClickListener(v ->
                    handleMCQSelection(ui, card, player, answerList, answer, index)
            );
        }
    }

    private String[] buildAnswers(WildCardProperties card,
                                  Player player,
                                  boolean isQuizMagician) {

        if (isQuizMagician) {
            Random random = new Random();

            String wrong = random.nextBoolean()
                    ? card.getWrongAnswer1()
                    : (random.nextBoolean()
                       ? card.getWrongAnswer2()
                       : card.getWrongAnswer3());

            return new String[]{card.getAnswer(), wrong};
        }

        return new String[]{
                card.getAnswer(),
                card.getWrongAnswer1(),
                card.getWrongAnswer2(),
                card.getWrongAnswer3()
        };
    }

    private void handleMCQSelection(UIRefs ui,
                                    WildCardProperties card,
                                    Player player,
                                    List<String> answers,
                                    String selected,
                                    int index) {

        disableButtons(ui.answerButtons);

        boolean correct = selected.equals(card.getAnswer());
        wasQuizCorrect = correct;

        if (correct) {
            ui.answerButtons[index].setBackgroundResource(R.drawable.buttonhighlightgreen);
            ui.confetti[index].setVisibility(View.VISIBLE);
            AudioManager.getInstance().playConfettiSound(activity);
            Game.getInstance().incrementPlayerQuizCorrectAnswers(player);
        } else {
            ui.answerButtons[index].setBackgroundResource(R.drawable.buttonhighlightred);
            highlightCorrect(ui, answers, card);
            Game.getInstance().incrementPlayerQuizIncorrectAnswers(player);
        }

        new Handler().postDelayed(() -> {
            hideConfetti(ui);   // ✅ ADD THIS FIRST
            showMCQResult(ui, player, card, correct);
        }, 1500);
    }

    private void highlightCorrect(UIRefs ui,
                                  List<String> answers,
                                  WildCardProperties card) {

        for (int j = 0; j < answers.size(); j++) {
            if (answers.get(j).equals(card.getAnswer())) {
                ui.answerButtons[j].setBackgroundResource(R.drawable.buttonhighlightgreen);
                break;
            }
        }
    }

    private void showMCQResult(UIRefs ui,
                               Player player,
                               WildCardProperties card,
                               boolean correct) {

        hideAllChoices(ui);
        ui.btnContinue.setVisibility(View.VISIBLE);

        String msg;

        if (correct) {
            boolean isMagician = QUIZ_MAGICIAN.equals(player.getClassChoice());

            msg = player.getName() + " that's right! The answer was "
                    + card.getAnswer()
                    + (isMagician
                    ? "\n\n P.S. You get to give out 2 drinks to everyone."
                    : "\n\n P.S. You get to give out a drink.");
        } else {
            msg = player.getName()
                    + " big ooooff! The answer actually was "
                    + card.getAnswer()
                    + "\n\n Take a drink.";
        }

        ui.text.setText(msg);
    }

    // ---------------- TRUE / FALSE ----------------

    private void setupTrueFalse(UIRefs ui,
                                WildCardProperties card,
                                Player player) {

        ui.btnAnswer.setVisibility(View.VISIBLE);

        ui.btnAnswer.setOnClickListener(v -> {
            ui.btnAnswer.setVisibility(View.GONE);

            ui.text.setText(card.getAnswer());

            ui.answerButtons[0].setVisibility(View.VISIBLE);
            ui.answerButtons[1].setVisibility(View.VISIBLE);

            ui.answerButtons[0].setText(R.string.were_you_right);
            ui.answerButtons[1].setText(R.string.were_you_wrong);

            ui.answerButtons[0].setOnClickListener(v1 ->
                    handleTF(ui, player, true));

            ui.answerButtons[1].setOnClickListener(v1 ->
                    handleTF(ui, player, false));
        });
    }

    private void handleTF(UIRefs ui, Player player, boolean correct) {
        wasQuizCorrect = correct;

        if (correct) {
            Game.getInstance().incrementPlayerQuizCorrectAnswers(player);
            ui.text.setText(player.getName()
                    + " since you got it right, give out a drink!");
        } else {
            Game.getInstance().incrementPlayerQuizIncorrectAnswers(player);
            ui.text.setText(player.getName()
                    + " since you got it wrong, take a drink!");
        }

        hideAllChoices(ui);
        ui.btnContinue.setVisibility(View.VISIBLE);
    }

    // ---------------- CONTINUE ----------------

    private void setupContinue(Button btn, AlertDialog dialog) {
        btn.setOnClickListener(v -> {
            dialog.dismiss();
            activity.setWasQuizCorrect(wasQuizCorrect);
            onContinue.run();
        });
    }

    // ---------------- HELPERS ----------------

    private void disableButtons(Button[] buttons) {
        for (Button b : buttons) b.setEnabled(false);
    }

    private void hideAllChoices(UIRefs ui) {
        for (Button b : ui.answerButtons) b.setVisibility(View.GONE);
    }

    private void updateTextSize(String text, TextView textView) {
        int size = SharedMainActivity.TextSizeCalculator
                .calculateTextSizeBasedOnCharacterCount(text);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }
    private void hideConfetti(UIRefs ui) {
        for (GifImageView gif : ui.confetti) {
            gif.setVisibility(View.GONE);
        }
    }
    // ---------------- HOLDER ----------------

    private static class UIRefs {
        TextView text;
        Button btnAnswer;
        Button btnContinue;
        Button[] answerButtons;
        GifImageView[] confetti;

        UIRefs(TextView text,
               Button btnAnswer,
               Button btnContinue,
               Button[] answerButtons,
               GifImageView[] confetti) {
            this.text = text;
            this.btnAnswer = btnAnswer;
            this.btnContinue = btnContinue;
            this.answerButtons = answerButtons;
            this.confetti = confetti;
        }
    }
}