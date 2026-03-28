package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.R.id.editCurrentNumberTextView;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.example.countingdowngame.mainActivity.MainActivityGame.drinkNumberCounterInt;
import static com.example.countingdowngame.mainActivity.MainActivityGame.isFirstTurn;
import static com.example.countingdowngame.mainActivity.MainActivityGame.repeatedTurn;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifImageView;

public class ActiveAbilities {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static void hideAbilityButton() {
        if (activity != null) {
            Button btnClassAbility = activity.findViewById(R.id.btnClassAbility);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }


    private static void hideWildButton() {
        if (activity != null) {
            Button btnClassAbility = activity.findViewById(R.id.btnWild);
            if (btnClassAbility != null) {
                btnClassAbility.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void handleScientistClass() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_scientist_change_number, null);

        EditText editCurrentNumberText = dialogView.findViewById(editCurrentNumberTextView);
        Button okButton = dialogView.findViewById(R.id.close_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(view -> {
            try {
                String userInput = editCurrentNumberText.getText().toString();
                int newNumber = Integer.parseInt(userInput);
                if (newNumber > 999999999) {
                    activity.displayToastMessage("That number was too high!");
                    Button btnClassAbility = activity.findViewById(R.id.btnClassAbility);
                    if (btnClassAbility != null) btnClassAbility.setVisibility(View.VISIBLE);
                } else if (newNumber == 0) {
                    activity.displayToastMessage("You cannot choose 0 as your number.");
                    Button btnClassAbility = activity.findViewById(R.id.btnClassAbility);
                    if (btnClassAbility != null) btnClassAbility.setVisibility(View.VISIBLE);
                } else {

                    Player currentPlayer = game.getCurrentPlayer();

                    Game.getInstance().setCurrentNumber(newNumber);
                    currentPlayer.setUsedActiveAbility(true);
                    MainActivityGame.updateNumber(newNumber);
                    AudioManager.getInstance().playSoundEffects(activity, SCIENTIST);
                    hideAbilityButton();
                    dialog.dismiss(); // Close the dialog on success
                }
            } catch (NumberFormatException e) {
                activity.displayToastMessage("Invalid number input");
            }
        });

        dialog.show();
    }

    public static void handleSoldierClass(Player currentPlayer) {
        if (!isFirstTurn) {
            if (game.getCurrentNumber() <= 10) {
                currentPlayer.setUsedActiveAbility(true);
                game.updateRepeatingTurns(currentPlayer, 1);
                activity.renderPlayerUI(false);
                repeatedTurn = true;
                activity.updateDrinkNumberCounter(4, true);
                hideAbilityButton();
                hideWildButton();
                AudioManager.getInstance().playSoundEffects(activity, SOLDIER);
            } else {
                activity.displayToastMessage("The +4 ability can only be activated when the number is below 10.");
            }
        } else {
            activity.displayToastMessage("Cannot activate on the first turn.");
        }
    }

    public static void handleQuizMagicianClass(Player currentPlayer) {
        currentPlayer.setUsedActiveAbility(true);
        currentPlayer.setJustUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, QUIZ_MAGICIAN);
    }

    public static void handleGoblinClass(Player currentPlayer) {
        if (currentPlayer.getWildCardAmount() <= 0) {
            Toast.makeText(activity, "You need at least one wildcard to sacrifice!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Player> eligiblePlayers = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            if (!player.equals(currentPlayer) && player.getWildCardAmount() > 0) {
                eligiblePlayers.add(player);
            }
        }

        if (eligiblePlayers.isEmpty()) {
            Toast.makeText(activity, "All other players have no wildcards", Toast.LENGTH_SHORT).show();
            return;
        }

        Player randomPlayer = eligiblePlayers.get(new Random().nextInt(eligiblePlayers.size()));
        randomPlayer.removeWildCard(randomPlayer, 2);

        int wildcardsLeft = randomPlayer.getWildCardAmount();
        String wildcardText = wildcardsLeft == 0 ? "no more wildcards" : (wildcardsLeft == 1 ? "1 wildcard left" : wildcardsLeft + " wildcards left");

        activity.showGameDialog(GOBLIN + "'s Active: \n\n" +
                randomPlayer.getName() + " lost two wildcards.\n\n" +
                randomPlayer.getName() + " now has " + wildcardText + ".");

        currentPlayer.loseWildCards(1);
        currentPlayer.setUsedActiveAbility(true);
        activity.renderPlayerUI(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, GOBLIN);
    }

    public static void handleAngryJimClass(Player currentPlayer) {
        Player randomPlayer = game.getRandomPlayerExcludingCurrent();
        game.updateRepeatingTurns(randomPlayer, 1);
        activity.showGameDialog(ANGRY_JIM + "'s Active: \n\n" + randomPlayer.getName() + " must repeat their turn.");
        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, ANGRY_JIM);
    }

    public static void handleSurvivorClass(Player currentPlayer) {
        if (Game.getInstance().getCurrentNumber() > 1) {
            activity.halveCurrentNumber();
            currentPlayer.setUsedActiveAbility(true);
            hideAbilityButton();
            AudioManager.getInstance().playSoundEffects(activity, SURVIVOR);
        }
    }

    public static void handleArcherClass(Player currentPlayer) {
        if (drinkNumberCounterInt >= 2) {
            activity.showGameDialog(ARCHER + "'s Active: \n\n" + currentPlayer.getName() + " hand out two drinks!");
            currentPlayer.setUsedActiveAbility(true);
            activity.updateDrinkNumberCounter(-2, true);
            hideAbilityButton();
            AudioManager.getInstance().playSoundEffects(activity, ARCHER);
        }
    }

    public static void handleWitchClass(Player currentPlayer) {
        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        currentPlayer.useSkip();
        AudioManager.getInstance().playSoundEffects(activity, WITCH);
    }

    public static void handleGamblerClass() {
        List<Player> opponents = game.getPlayers().stream()
                .filter(p -> !p.equals(game.getCurrentPlayer()))
                .collect(Collectors.toList());

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_select_opponent, null);

        ListView listView = dialogView.findViewById(R.id.listViewOpponents);
        ImageButton closeButton = dialogView.findViewById(R.id.close_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(activity, R.layout.game_powerup_list_item, R.id.powerup_text, opponents) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Player opponent = opponents.get(position);
                TextView textView = view.findViewById(R.id.powerup_text);
                textView.setText(opponent.getName());
                GifImageView icon = view.findViewById(R.id.powerup_icon);
                icon.setImageResource(R.drawable.dice); // Using dice icon for gambler duel
                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Player selectedOpponent = opponents.get(position);
            dialog.dismiss();
            showBetDialog(selectedOpponent);
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static void showBetDialog(Player opponent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_gambler_duel_bet, null);

        EditText editBetAmount = dialogView.findViewById(R.id.editBetAmount);
        
        TextView subtitle = dialogView.findViewById(R.id.bet_subtitle);
        if (subtitle != null) {
            subtitle.setText("Duel against " + opponent.getName());
        }

        Button okButton = dialogView.findViewById(R.id.btn_confirm_bet);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> {
            try {
                int bet = Integer.parseInt(editBetAmount.getText().toString());
                if (bet < 1 || bet > 5) {
                    activity.displayToastMessage("Bet must be between 1 and 5!");
                } else {
                    dialog.dismiss();
                    startHighCardDuel(opponent, bet);
                }
            } catch (NumberFormatException e) {
                activity.displayToastMessage("Invalid bet!");
            }
        });
        dialog.show();
    }

    private static void startHighCardDuel(Player opponent, int bet) {
        Random r = new Random();
        int gamblerCard = r.nextInt(13) + 2; // 2-14 (Ace high)
        int opponentCard = r.nextInt(13) + 2;

        // Handle ties
        while (opponentCard == gamblerCard) {
            opponentCard = r.nextInt(13) + 2;
        }

        String gamblerCardName = getCardName(gamblerCard);
        String opponentCardName = getCardName(opponentCard);

        Player currentPlayer = game.getCurrentPlayer();
        String resultMsg;
        if (gamblerCard > opponentCard) {
            resultMsg = currentPlayer.getName() + " drew " + gamblerCardName + "!\n" +
                    opponent.getName() + " drew " + opponentCardName + ".\n\n" +
                    currentPlayer.getName() + " wins! " + opponent.getName() + " takes " + bet + " drinks.";
        } else {
            resultMsg = opponent.getName() + " drew " + opponentCardName + "!\n" +
                    currentPlayer.getName() + " drew " + gamblerCardName + ".\n\n" +
                    opponent.getName() + " wins! " + currentPlayer.getName() + " takes " + bet + " drinks.";
        }

        activity.showGameDialog(resultMsg);
        currentPlayer.setUsedActiveAbility(true);
        hideAbilityButton();
        AudioManager.getInstance().playSoundEffects(activity, GAMBLER);
    }

    private static String getCardName(int value) {
        if (value <= 10) return String.valueOf(value);
        if (value == 11) return "Jack";
        if (value == 12) return "Queen";
        if (value == 13) return "King";
        return "Ace";
    }
}
