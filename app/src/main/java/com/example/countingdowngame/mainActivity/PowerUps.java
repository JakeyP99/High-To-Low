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

    public static void getPowerUp(Runnable onDismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_wheel_of_fortune, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // Prevent closing by clicking outside or back button
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnDismissListener(d -> {
            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setVisibility(View.GONE); // Hide initially
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ArrayList<String> powerUpList = getPowerUps();
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);

        PowerUpAdapter adapter = new PowerUpAdapter(activity, powerUpList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        // Prevent user from clicking items manually
        listView.setOnTouchListener((v, event) -> true);

        final Handler handler = new Handler();
        final Random random = new Random();
        // Randomize the shuffle duration to ensure different results each time
        final int shuffleDuration = 3000 + random.nextInt(2000); 
        final int initialInterval = 50;

        final Runnable shuffleRunnable = new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            // Start at a random index to further increase randomness
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
                    Game.getInstance().getCurrentPlayer().gainPowerUp(selectedPowerUp);
                    updatePowerUpIcons(Game.getInstance().getCurrentPlayer());

                    // Show close button after 1 seconds of "viewing time"
                    handler.postDelayed(() -> {
                        closeButton.setVisibility(View.VISIBLE);
                    }, 1000);

                    // Auto-dismiss after 15 seconds total
                    handler.postDelayed(() -> {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }, 15000);
                }
            }
        };

        dialog.show();
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
            player.usePowerUp(powerUpName);
        } else if (powerUpName.contains("Shield")) {
            player.usePowerUp(powerUpName);
        } else if (powerUpName.contains("Double Trouble")) {
            player.usePowerUp(powerUpName);
        }
    }
}
