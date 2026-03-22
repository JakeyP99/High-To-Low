package com.example.countingdowngame.mainActivity;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
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
    private static boolean doubleTroubleActive = false;

    // Power-Up Type Constants
    private static final String SHIELD = "Shield";
    private static final String DOUBLE_TROUBLE = "Double Trouble";
    private static final String WILDCARD_BONUS = "Wildcard Bonus";

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    private static String getPowerUpType(String powerUpName) {
        if (powerUpName == null) return "";
        if (powerUpName.contains(SHIELD)) return SHIELD;
        if (powerUpName.contains(DOUBLE_TROUBLE)) return DOUBLE_TROUBLE;
        if (powerUpName.contains(WILDCARD_BONUS)) return WILDCARD_BONUS;
        return "";
    }

    public static ArrayList<String> getPowerUps() {
        ArrayList<String> powerUp = new ArrayList<>();
        powerUp.add(SHIELD + ": Immune to your next drink!");
        powerUp.add(DOUBLE_TROUBLE + ": The next person to drink takes double!");
        powerUp.add(WILDCARD_BONUS + ": Gain 1 extra Wildcard!");
        return powerUp;
    }

    public static void gainPowerUp(Player player, String powerUpName) {
        if (player == null) return;

        List<String> powerUps = player.getPowerUps();
        if (powerUps.size() < 2) {
            powerUps.add(powerUpName);
        }

        switch (getPowerUpType(powerUpName)) {
            case WILDCARD_BONUS:
                player.gainWildCards(1);
                break;
            case SHIELD:
            case DOUBLE_TROUBLE:
                // No immediate effect on gain
                break;
        }

        updatePowerUpIcons(player);
    }

    public static void activatePowerUp(String powerUpName, Player player) {
        if (player == null) return;
        activity.displayToastMessage("Activated: " + powerUpName);

        switch (getPowerUpType(powerUpName)) {
            case SHIELD:
                // Shield is passive once in inventory, but we'll remove it when "activated"
                // or handle it purely in handleDrinkingEvent. 
                // Currently, activate button removes it from icons.
                player.usePowerUp(powerUpName);
                break;
            case DOUBLE_TROUBLE:
                doubleTroubleActive = true;
                player.usePowerUp(powerUpName);
                break;
            case WILDCARD_BONUS:
                // Already awarded on gain, just clear the slot
                player.usePowerUp(powerUpName);
                break;
        }
        updatePowerUpIcons(player);
    }

    public static int getPowerUpIcon(String powerUpName) {
        switch (getPowerUpType(powerUpName)) {
            case SHIELD:
                return R.drawable.helmet;
            case DOUBLE_TROUBLE:
                return R.drawable.shots;
            case WILDCARD_BONUS:
                return R.drawable.bandaids;
            default:
                return R.drawable.helmet;
        }
    }

    public static void handleDrinkingEvent(Player playerWhoShouldDrink, int drinksToTake) {
        if (playerWhoShouldDrink == null) return;

        int finalDrinks = drinksToTake;

        // 1. Check Double Trouble (Global effect)
        if (doubleTroubleActive) {
            finalDrinks *= 2;
            doubleTroubleActive = false;
            activity.displayToastMessage("Double Trouble! Take " + finalDrinks + " drinks!");
        }

        // 2. Check Shield (Individual effect)
        String shieldItem = null;
        for (String p : playerWhoShouldDrink.getPowerUps()) {
            if (getPowerUpType(p).equals(SHIELD)) {
                shieldItem = p;
                break;
            }
        }

        if (shieldItem != null) {
            activity.displayToastMessage("Shielded! No drinks for " + playerWhoShouldDrink.getName());
            playerWhoShouldDrink.usePowerUp(shieldItem);
            updatePowerUpIcons(playerWhoShouldDrink);
        }
    }

    // --- UI and Dialog Logic ---

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

        title.setText(getPowerUpType(powerUpName));
        description.setText(powerUpName);

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
