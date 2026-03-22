package com.example.countingdowngame.mainActivity;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PowerUps {
    private static MainActivityGame activity;

    // Power-Up Type Constants
    private static final String SPLIT_THE_PAIN = "Split the Pain";
    private static final String ALL_OR_NOTHING = "All or Nothing";
    private static final String HIGH_STAKES = "High Stakes";
    private static final String TRADE_UP = "Trade Up";

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static String getPowerUpType(String powerUpName) {
        if (powerUpName == null) return "";
        if (powerUpName.contains(SPLIT_THE_PAIN)) return SPLIT_THE_PAIN;
        if (powerUpName.contains(ALL_OR_NOTHING)) return ALL_OR_NOTHING;
        if (powerUpName.contains(HIGH_STAKES)) return HIGH_STAKES;
        if (powerUpName.contains(TRADE_UP)) return TRADE_UP;
        return "";
    }

    private static boolean isPassive(String type) {
        return type.equals(SPLIT_THE_PAIN) || type.equals(ALL_OR_NOTHING);
    }

    public static ArrayList<String> getPowerUps() {
        ArrayList<String> powerUp = new ArrayList<>();
        powerUp.add(SPLIT_THE_PAIN + ": Divide your drinks with a random player if you lose!");
        powerUp.add(ALL_OR_NOTHING + ": 50/50 chance: 0 drinks or double drinks if you lose!");
        powerUp.add(HIGH_STAKES + ": +3 drinks to the total, but gain 2 wildcards!");
        powerUp.add(TRADE_UP + ": Lose 1 wildcard to reduce drinks by 3!");
        return powerUp;
    }

    public static void gainPowerUp(Player player, String powerUpName) {
        if (player == null) return;

        List<String> powerUps = player.getPowerUps();
        if (powerUps.size() < 2) {
            powerUps.add(powerUpName);
        }
        updatePowerUpIcons(player);
    }

    public static void activatePowerUp(String powerUpName, Player player) {
        if (player == null) return;
        String type = getPowerUpType(powerUpName);

        switch (type) {
            case HIGH_STAKES:
                activity.displayToastMessage("Activated: " + HIGH_STAKES);
                activity.updateDrinkNumberCounter(3, true);
                player.gainWildCards(2);
                player.usePowerUp(powerUpName);
                break;
            case TRADE_UP:
                if (player.getWildCardAmount() >= 1) {
                    activity.displayToastMessage("Activated: " + TRADE_UP);
                    player.loseWildCards(1);
                    activity.updateDrinkNumberCounter(-3, true);
                    player.usePowerUp(powerUpName);
                } else {
                    activity.displayToastMessage("You need at least 1 wildcard to trade up!");
                    return;
                }
                break;
            case SPLIT_THE_PAIN:
            case ALL_OR_NOTHING:
                // Passive power-ups are handled automatically via checkLosingPowerUps
                break;
        }
        updatePowerUpIcons(player);
    }

    public static int getPowerUpIcon(String powerUpName) {
        switch (getPowerUpType(powerUpName)) {
            case SPLIT_THE_PAIN:
                return R.drawable.shots;
            case ALL_OR_NOTHING:
                return R.drawable.scientist;
            case HIGH_STAKES:
                return R.drawable.bandaids;
            case TRADE_UP:
                return R.drawable.helmet;
            default:
                return R.drawable.helmet;
        }
    }

    // --- Losing Power-Up Handling ---

    public static void checkLosingPowerUps(Player player, Runnable onEndGame) {
        List<String> playerPowerUps = new ArrayList<>(player.getPowerUps());
        
        String allNothing = null;
        for (String p : playerPowerUps) {
            if (getPowerUpType(p).equals(ALL_OR_NOTHING)) {
                allNothing = p;
                break;
            }
        }

        if (allNothing != null) {
            String finalAllNothing = allNothing;
            showAllOrNothingDialog(player, () -> {
                player.usePowerUp(finalAllNothing);
                checkSplitThePain(player, onEndGame);
            }, () -> checkSplitThePain(player, onEndGame));
        } else {
            checkSplitThePain(player, onEndGame);
        }
    }

    private static void checkSplitThePain(Player player, Runnable onEndGame) {
        String splitPain = null;
        for (String p : player.getPowerUps()) {
            if (getPowerUpType(p).equals(SPLIT_THE_PAIN)) {
                splitPain = p;
                break;
            }
        }

        if (splitPain != null) {
            String finalSplitPain = splitPain;
            showSplitThePainRoulette(player, () -> {
                player.usePowerUp(finalSplitPain);
                onEndGame.run();
            });
        } else {
            onEndGame.run();
        }
    }

    private static void showAllOrNothingDialog(Player player, Runnable onHandled, Runnable onDeclined) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_powerup_details, null);

        TextView title = dialogView.findViewById(R.id.powerup_title);
        TextView description = dialogView.findViewById(R.id.powerup_description);
        Button activateBtn = dialogView.findViewById(R.id.btn_activate_powerup);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);

        title.setText("All or Nothing!");
        description.setText("Risk it all? 50/50 chance for 0 drinks or DOUBLE drinks!");
        activateBtn.setText("Risk It!");

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        activateBtn.setOnClickListener(v -> {
            dialog.dismiss();
            showAllOrNothingGenerator(onHandled);
        });

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
            onDeclined.run();
        });

        dialog.show();
    }

    private static void showAllOrNothingGenerator(Runnable onHandled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_wheel_of_fortune, null);
        TextView title = dialogView.findViewById(R.id.powerup_dialogbox_textview);
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);
        closeBtn.setVisibility(View.GONE);

        title.setText("Outcome...");
        
        ArrayList<String> outcomes = new ArrayList<>();
        outcomes.add("0 Drinks! (SAFE)");
        outcomes.add("DOUBLE DRINKS! (Ouch)");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_activated_1, outcomes);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        final Handler handler = new Handler();
        final Random random = new Random();
        final int shuffleDuration = 3000;
        final int initialInterval = 50;

        handler.post(new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            int currentIndex = 0;

            @Override
            public void run() {
                currentIndex = (currentIndex + 1) % outcomes.size();
                listView.setItemChecked(currentIndex, true);
                listView.smoothScrollToPosition(currentIndex);

                float progress = (float) elapsedTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 400));
                elapsedTime += currentInterval;

                if (elapsedTime < shuffleDuration) {
                    handler.postDelayed(this, currentInterval);
                } else {
                    String result = outcomes.get(currentIndex);
                    if (result.contains("0")) {
                        MainActivityGame.drinkNumberCounterInt = 0;
                        activity.displayToastMessage("PHEW! 0 Drinks!");
                    } else {
                        MainActivityGame.drinkNumberCounterInt *= 2;
                        activity.displayToastMessage("DOUBLE DRINKS!");
                    }
                    handler.postDelayed(() -> {
                        dialog.dismiss();
                        onHandled.run();
                    }, 2000);
                }
            }
        });
    }

    private static void showSplitThePainRoulette(Player player, Runnable onHandled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_wheel_of_fortune, null);
        TextView title = dialogView.findViewById(R.id.powerup_dialogbox_textview);
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);
        closeBtn.setVisibility(View.GONE);

        title.setText("Splitting the Pain...");

        List<Player> otherPlayers = new ArrayList<>(Game.getInstance().getPlayers());
        otherPlayers.remove(player);
        
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player p : otherPlayers) {
            playerNames.add(p.getName());
        }

        if (playerNames.isEmpty()) {
            onHandled.run();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_activated_1, playerNames);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        final Handler handler = new Handler();
        final int shuffleDuration = 4000;
        final int initialInterval = 50;

        handler.post(new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            int currentIndex = 0;

            @Override
            public void run() {
                currentIndex = (currentIndex + 1) % playerNames.size();
                listView.setItemChecked(currentIndex, true);
                listView.smoothScrollToPosition(currentIndex);

                float progress = (float) elapsedTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 500));
                elapsedTime += currentInterval;

                if (elapsedTime < shuffleDuration) {
                    handler.postDelayed(this, currentInterval);
                } else {
                    String targetName = playerNames.get(currentIndex);
                    int drinks = MainActivityGame.drinkNumberCounterInt;
                    int splitAmount = Math.max(drinks / 2, 1);
                    
                    activity.displayToastMessage("Split! " + targetName + " takes " + splitAmount + " drinks with you!");
                    
                    handler.postDelayed(() -> {
                        dialog.dismiss();
                        onHandled.run();
                    }, 2500);
                }
            }
        });
    }

    // --- Roulette Logic ---

    public static void getPowerUp(Runnable onDismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_wheel_of_fortune, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnDismissListener(d -> {
            if (onDismiss != null) onDismiss.run();
        });

        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setVisibility(View.GONE);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ArrayList<String> powerUpList = getPowerUps();
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        PowerUpAdapter adapter = new PowerUpAdapter(activity, powerUpList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        final Handler handler = new Handler();
        final Random random = new Random();
        final int shuffleDuration = 3000 + random.nextInt(2000);
        final int initialInterval = 50;

        final Runnable shuffleRunnable = new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            int currentIndex = random.nextInt(powerUpList.size());

            @Override
            public void run() {
                currentIndex = (currentIndex + 1) % powerUpList.size();
                listView.setItemChecked(currentIndex, true);
                listView.smoothScrollToPosition(currentIndex);

                float progress = (float) elapsedTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 500));
                elapsedTime += currentInterval;

                if (elapsedTime < shuffleDuration) {
                    handler.postDelayed(this, currentInterval);
                } else {
                    listView.setItemChecked(currentIndex, true);
                    String selectedPowerUp = powerUpList.get(currentIndex);
                    gainPowerUp(Game.getInstance().getCurrentPlayer(), selectedPowerUp);

                    handler.postDelayed(() -> closeButton.setVisibility(View.VISIBLE), 2000);
                    handler.postDelayed(() -> { if (dialog.isShowing()) dialog.dismiss(); }, 15000);
                }
            }
        };

        dialog.show();
        handler.post(shuffleRunnable);
    }

    public static void updatePowerUpIcons(Player player) {
        if (player == null || activity == null) return;
        List<String> powerUps = player.getPowerUps();

        ImageView powerUpLeft = activity.findViewById(R.id.powerup_left);
        ImageView powerUpRight = activity.findViewById(R.id.powerup_right);

        if (powerUps.size() >= 1) {
            powerUpLeft.setVisibility(View.VISIBLE);
            powerUpLeft.setImageResource(getPowerUpIcon(powerUps.get(0)));
            powerUpLeft.setOnClickListener(v -> showPowerUpDetails(powerUps.get(0), player));
        } else {
            powerUpLeft.setVisibility(View.GONE);
        }

        if (powerUps.size() >= 2) {
            powerUpRight.setVisibility(View.VISIBLE);
            powerUpRight.setImageResource(getPowerUpIcon(powerUps.get(1)));
            powerUpRight.setOnClickListener(v -> showPowerUpDetails(powerUps.get(1), player));
        } else {
            powerUpRight.setVisibility(View.GONE);
        }
    }

    public static void showPowerUpDetails(String powerUpName, Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_powerup_details, null);

        TextView title = dialogView.findViewById(R.id.powerup_title);
        TextView description = dialogView.findViewById(R.id.powerup_description);
        Button activateBtn = dialogView.findViewById(R.id.btn_activate_powerup);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);

        String type = getPowerUpType(powerUpName);
        title.setText(type);
        description.setText(powerUpName);

        if (isPassive(type)) {
            activateBtn.setVisibility(View.GONE);
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        activateBtn.setOnClickListener(v -> {
            activatePowerUp(powerUpName, player);
            dialog.dismiss();
        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
