package com.example.countingdowngame.mainActivity;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_quiz_dialog, null);

        TextView wildTextDialog = dialogView.findViewById(R.id.textView_WildText);
        Button btnAnswerDialog = dialogView.findViewById(R.id.btnAnswer);
        Button btnWildContinueDialog = dialogView.findViewById(R.id.btnBackWildCard);
        Button btnQuizAnswerBL = dialogView.findViewById(R.id.btnQuizAnswerBL);
        Button btnQuizAnswerBR = dialogView.findViewById(R.id.btnQuizAnswerBR);
        Button btnQuizAnswerTL = dialogView.findViewById(R.id.btnQuizAnswerTL);
        Button btnQuizAnswerTR = dialogView.findViewById(R.id.btnQuizAnswerTR);

        GifImageView confettiTL = dialogView.findViewById(R.id.confettiImageViewTL);
        GifImageView confettiTR = dialogView.findViewById(R.id.confettiImageViewTR);
        GifImageView confettiBL = dialogView.findViewById(R.id.confettiImageViewBL);
        GifImageView confettiBR = dialogView.findViewById(R.id.confettiImageViewBR);

        Button[] answerButtons = new Button[]{btnQuizAnswerTL, btnQuizAnswerTR, btnQuizAnswerBL, btnQuizAnswerBR};
        GifImageView[] confettiViews = new GifImageView[]{confettiTL, confettiTR, confettiBL, confettiBR};

        wildTextDialog.setText(selectedCard.getWildCard());
        updateTextSize(selectedCard.getWildCard(), wildTextDialog);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        boolean isQuizMagician = QUIZ_MAGICIAN.equals(currentPlayer.getClassChoice()) || 
                (CharacterClassDescriptions.ANGRY_JIM.equals(currentPlayer.getClassChoice()) && Game.getInstance().getCurrentNumber() < 50);

        if (isQuizMagician || GeneralSettingsLocalStore.fromContext(activity).isMultiChoice()) {
            btnAnswerDialog.setVisibility(View.GONE);
            String[] answers;
            if (isQuizMagician) {
                Random random = new Random();
                String wrong = random.nextBoolean() ? selectedCard.getWrongAnswer1() : (random.nextBoolean() ? selectedCard.getWrongAnswer2() : selectedCard.getWrongAnswer3());
                answers = new String[]{selectedCard.getAnswer(), wrong};
                btnQuizAnswerBL.setVisibility(View.GONE);
                btnQuizAnswerBR.setVisibility(View.GONE);
            } else {
                answers = new String[]{selectedCard.getAnswer(), selectedCard.getWrongAnswer1(), selectedCard.getWrongAnswer2(), selectedCard.getWrongAnswer3()};
            }

            List<String> answerList = Arrays.asList(answers);
            Collections.shuffle(answerList);

            for (int i = 0; i < answerList.size(); i++) {
                Button btn = answerButtons[i];
                btn.setVisibility(View.VISIBLE);
                btn.setText(answerList.get(i));
                String selectedAnswer = answerList.get(i);
                int finalI = i;
                btn.setOnClickListener(v -> {
                    for (Button b : answerButtons) b.setEnabled(false);
                    boolean isCorrect = selectedAnswer.equals(selectedCard.getAnswer());
                    wasQuizCorrect = isCorrect;
                    if (isCorrect) {
                        btn.setBackgroundResource(R.drawable.buttonhighlightgreen);
                        confettiViews[finalI].setVisibility(View.VISIBLE);
                        AudioManager.getInstance().playConfettiSound(activity);
                        Game.getInstance().incrementPlayerQuizCorrectAnswers(currentPlayer);
                    } else {
                        btn.setBackgroundResource(R.drawable.buttonhighlightred);
                        for (int j = 0; j < answerList.size(); j++) {
                            if (answerList.get(j).equals(selectedCard.getAnswer())) {
                                answerButtons[j].setBackgroundResource(R.drawable.buttonhighlightgreen);
                                break;
                            }
                        }
                        Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);
                    }

                    new Handler().postDelayed(() -> {
                        confettiViews[finalI].setVisibility(View.INVISIBLE);
                        btnQuizAnswerTL.setVisibility(View.GONE);
                        btnQuizAnswerTR.setVisibility(View.GONE);
                        btnQuizAnswerBL.setVisibility(View.GONE);
                        btnQuizAnswerBR.setVisibility(View.GONE);
                        btnWildContinueDialog.setVisibility(View.VISIBLE);

                        if (isCorrect) {
                            String msg = isQuizMagician ? currentPlayer.getName() + " that's right! The answer was " + selectedCard.getAnswer() + "\n\n P.S. You get to give out 2 drinks to everyone."
                                    : currentPlayer.getName() + " that's right! The answer was " + selectedCard.getAnswer() + "\n\n P.S. You get to give out a drink.";
                            wildTextDialog.setText(msg);
                        } else {
                            wildTextDialog.setText(currentPlayer.getName() + " big ooooff! The answer actually was " + selectedCard.getAnswer() + "\n\n Take a drink.");
                        }
                    }, 1500);
                });
            }
        } else {
            btnAnswerDialog.setVisibility(View.VISIBLE);
            btnAnswerDialog.setOnClickListener(v -> {
                btnAnswerDialog.setVisibility(View.GONE);
                wildTextDialog.setText(selectedCard.getAnswer());
                btnQuizAnswerTL.setVisibility(View.VISIBLE);
                btnQuizAnswerTR.setVisibility(View.VISIBLE);
                btnQuizAnswerTL.setText(R.string.were_you_right);
                btnQuizAnswerTR.setText(R.string.were_you_wrong);

                btnQuizAnswerTL.setOnClickListener(v1 -> {
                    wasQuizCorrect = true;
                    Game.getInstance().incrementPlayerQuizCorrectAnswers(currentPlayer);
                    btnQuizAnswerTL.setVisibility(View.GONE);
                    btnQuizAnswerTR.setVisibility(View.GONE);
                    wildTextDialog.setText(currentPlayer.getName() + " since you got it right, give out a drink!");
                    btnWildContinueDialog.setVisibility(View.VISIBLE);
                });

                btnQuizAnswerTR.setOnClickListener(v1 -> {
                    wasQuizCorrect = false;
                    Game.getInstance().incrementPlayerQuizIncorrectAnswers(currentPlayer);
                    btnQuizAnswerTL.setVisibility(View.GONE);
                    btnQuizAnswerTR.setVisibility(View.GONE);
                    wildTextDialog.setText(currentPlayer.getName() + " since you got it wrong, take a drink!");
                    btnWildContinueDialog.setVisibility(View.VISIBLE);
                });
            });
        }

        btnWildContinueDialog.setOnClickListener(v -> {
            dialog.dismiss();
            activity.setWasQuizCorrect(wasQuizCorrect);
            onContinue.run();
        });

        dialog.show();
    }

    private void updateTextSize(String selectedActivity, TextView textView) {
        int textSize = SharedMainActivity.TextSizeCalculator.calculateTextSizeBasedOnCharacterCount(selectedActivity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}
