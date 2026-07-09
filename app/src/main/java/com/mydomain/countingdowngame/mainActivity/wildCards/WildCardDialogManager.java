package com.mydomain.countingdowngame.mainActivity.wildCards;

import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;

import android.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.MainActivityGame;
import com.mydomain.countingdowngame.mainActivity.SharedMainActivity;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.settings.GeneralSettingsLocalStore;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;

import pl.droidsonroids.gif.GifImageView;

public class WildCardDialogManager {

    private final MainActivityGame activity;
    private final Runnable onContinue;
    private boolean wasQuizCorrect = false;
    private AlertDialog activeDialog;

    public WildCardDialogManager(MainActivityGame activity, Runnable onContinue) {
        this.activity = activity;
        this.onContinue = onContinue;
    }

    public static class DialogUI {
        public final TextView text;
        public final TextView title;
        public final Button btnAnswer;
        public final Button btnContinue;
        public final Button[] answerButtons;
        public final GifImageView[] confetti;

        public final View magicianStreakGroup;
        public final Button btnMagicianContinue;
        public final Button btnMagicianStop;

        public DialogUI(TextView text, Button btnAnswer, Button btnContinue, Button[] answerButtons, GifImageView[] confetti, TextView title, View magicianStreakGroup, Button btnMagicianContinue, Button btnMagicianStop) {
            this.text = text;
            this.btnAnswer = btnAnswer;
            this.btnContinue = btnContinue;
            this.answerButtons = answerButtons;
            this.confetti = confetti;
            this.title = title;
            this.magicianStreakGroup = magicianStreakGroup;
            this.btnMagicianContinue = btnMagicianContinue;
            this.btnMagicianStop = btnMagicianStop;
        }
    }

    public void showWildCardDialog(WildCardProperties selectedCard, String type) {
        View dialogView = inflateDialog(type);

        if (activeDialog != null) activeDialog.dismiss();
        activeDialog = createDialog(dialogView);

        DialogUI ui = bindViews(dialogView);

        Player player = Game.getInstance().getCurrentPlayer();
        boolean isQuizMagician = QUIZ_MAGICIAN.equals(player.getClassChoice()) || (CharacterClassDescriptions.ANGRY_JIM.equals(player.getClassChoice()) && Game.getInstance().getCurrentNumber() < 50);

        setupBaseUI(ui, selectedCard, type, isQuizMagician);

        if (selectedCard.hasAnswer()) {
            WildCardQuiz quiz = new WildCardQuiz(activity, ui, selectedCard, player, isQuizMagician, new WildCardQuiz.QuizCallback() {
                @Override
                public void onQuizResult(boolean correct) {
                    wasQuizCorrect = correct;
                }

                @Override
                public void onDismissRequested() {
                    if (activeDialog != null) activeDialog.dismiss();
                }
            });

            boolean isMultiChoice = GeneralSettingsLocalStore.fromContext(activity).isMultiChoice();
            boolean isQuizMode = isQuizMagician || isMultiChoice;

            if (isQuizMode) {
                quiz.setupMultipleChoice();
            } else {
                quiz.setupTrueFalse();
            }
        } else {
            setupWildCardOnly(ui);
        }

        setupContinue(ui.btnContinue, activeDialog);
        activeDialog.show();
    }

    private void setupWildCardOnly(DialogUI ui) {
        if (ui.btnAnswer != null) ui.btnAnswer.setVisibility(View.GONE);
        if (ui.answerButtons != null) {
            for (Button b : ui.answerButtons) {
                if (b != null) b.setVisibility(View.GONE);
            }
        }
        ui.btnContinue.setVisibility(View.VISIBLE);
    }

    private View inflateDialog(String type) {
        int layoutId;
        switch (type) {
            case "Task":
                layoutId = R.layout.game_wildcard_task;
                break;
            case "Truth":
                layoutId = R.layout.game_wildcard_truth;
                break;
            default:
                layoutId = R.layout.game_wildcard_quiz;
                break;
        }
        return activity.getLayoutInflater().inflate(layoutId, null);
    }

    private AlertDialog createDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    private DialogUI bindViews(View view) {
        return new DialogUI(
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
                },
                view.findViewById(R.id.textView),
                view.findViewById(R.id.magician_streak_group),
                view.findViewById(R.id.btn_continue_streak),
                view.findViewById(R.id.btn_stop_streak)
        );
    }

    private void setupBaseUI(DialogUI ui, WildCardProperties card, String type, boolean isQuizMagician) {
        ui.text.setText(card.getWildCard());
        ui.title.setText(type + "!");

        if (!card.hasAnswer()) { // Task or Truth
            ui.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        } else if (isQuizMagician) {
            ui.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        } else {
            int size = SharedMainActivity.TextSizeCalculatorQuizQuestion.calculateTextSizeBasedOnCharacterCount(ui.text.getText().toString());
            ui.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }
    }

    private void setupContinue(Button btn, AlertDialog dialog) {
        activity.btnUtils.setButton(btn, () -> {
            dialog.dismiss();
            activity.setWasQuizCorrect(wasQuizCorrect);
            onContinue.run();
        });
    }
}
