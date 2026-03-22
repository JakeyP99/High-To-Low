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

public class PowerUps {
    static Game game = Game.getInstance();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static ArrayList<String> getPowerUps() {
        ArrayList<String> powerUp = new ArrayList<>();

        powerUp.add("Shield: Immune to your next drink!");
        powerUp.add("Double Trouble: The next person to drink takes double!");
        powerUp.add("Wildcard Bonus: Gain 1 extra Wildcard!");

        return powerUp;
    }

    public static void getPowerUp() {
        // Show dialog
        View dialogView = activity.showDialog(
                "Power Up!",
                R.layout.game_wheel_of_fortune,
                R.id.powerup_dialogbox_textview,
                R.id.close_button
        );

        ArrayList<String> powerUpList = getPowerUps();
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);

        // ✅ Set full list ONCE with custom adapter
        PowerUpAdapter adapter = new PowerUpAdapter(activity, powerUpList);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final Handler handler = new Handler();
        final int shuffleDuration = 3000; // total spin time (ms)
        final int initialInterval = 50;

        final Runnable shuffleRunnable = new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            int currentIndex = 0;

            @Override
            public void run() {

                // 👉 Move sequentially (better roulette feel)
                currentIndex = (currentIndex + 1) % powerUpList.size();

                // Highlight + scroll
                listView.setItemChecked(currentIndex, true);
                listView.smoothScrollToPosition(currentIndex);

                // Slow down over time (ease-out effect)
                float progress = (float) elapsedTime / shuffleDuration;
                currentInterval = (int) (initialInterval + (progress * progress * 400));

                elapsedTime += currentInterval;

                if (elapsedTime < shuffleDuration) {
                    handler.postDelayed(this, currentInterval);
                } else {
                    // ✅ Final landing
                    listView.setItemChecked(currentIndex, true);

                    // Optional: do something with selected power-up
                    String selectedPowerUp = powerUpList.get(currentIndex);

                    Game.getInstance().getCurrentPlayer().gainPowerUp(selectedPowerUp);
                    updatePowerUpIcons(Game.getInstance().getCurrentPlayer());
                }
            }
        };

        // Start spinning
        handler.post(shuffleRunnable);
    }

    public static void updatePowerUpIcons(Player player) {
        if (player == null || activity == null) return;
        List<String> powerUps = player.getPowerUps();
        if (powerUps == null) return;

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

    public static int getPowerUpIcon(String powerUpName) {
        if (powerUpName.contains("Shield")) return R.drawable.helmet;
        if (powerUpName.contains("Double Trouble")) return R.drawable.shots;
        if (powerUpName.contains("Wildcard Bonus")) return R.drawable.bandaids;
        return R.drawable.helmet; // Default
    }

    public static void showPowerUpDetails(String powerUpName, Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_powerup_details, null);

        TextView title = dialogView.findViewById(R.id.powerup_title);
        TextView description = dialogView.findViewById(R.id.powerup_description);
        Button activateBtn = dialogView.findViewById(R.id.btn_activate_powerup);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);

        title.setText(powerUpName.split(":")[0]);
        description.setText(powerUpName);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        activateBtn.setOnClickListener(v -> {
            activatePowerUp(powerUpName, player);
            dialog.dismiss();
            updatePowerUpIcons(player);
        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void activatePowerUp(String powerUpName, Player player) {
        activity.displayToastMessage("Activated: " + powerUpName);
        if (powerUpName.contains("Wildcard Bonus")) {
            // Already applied when gained, but we can use it to "consume" the slot
            player.usePowerUp(powerUpName);
        } else if (powerUpName.contains("Shield")) {
            // Handle shield logic later
            player.usePowerUp(powerUpName);
        } else if (powerUpName.contains("Double Trouble")) {
            // Handle double trouble logic later
            player.usePowerUp(powerUpName);
        }
    }
}
