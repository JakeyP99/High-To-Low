package com.example.countingdowngame.mainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PowerUps {
    private static MainActivityGame activity;
    private static final List<String> obtainedPowerUps = new ArrayList<>();

    // Power-Up Type Constants
    private static final String SPLIT_THE_PAIN = "Split the Pain";
    private static final String ALL_OR_NOTHING = "All or Nothing";
    private static final String HIGH_STAKES = "High Stakes";
    private static final String TRADE_UP = "Trade Up";

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static void reset() {
        obtainedPowerUps.clear();
    }

    public static boolean isObtained(String powerUpName) {
        return obtainedPowerUps.contains(getPowerUpType(powerUpName));
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
        powerUp.add(HIGH_STAKES + ": +3 drinks to the total, but gain 2 wildcards for your next turn!");
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
                    activity.renderPlayerUI(); // Refresh UI to show updated wildcard count immediately
                } else {
                    activity.displayToastMessage("You need at least 1 wildcard to trade up!");
                    return;
                }
                break;
            case SPLIT_THE_PAIN:
            case ALL_OR_NOTHING:
                break;
        }
        updatePowerUpIcons(player);
    }

    public static int getPowerUpIcon(String powerUpName) {
        switch (getPowerUpType(powerUpName)) {
            case SPLIT_THE_PAIN:
                return R.drawable.division;
            case ALL_OR_NOTHING:
                return R.drawable.dice;
            case HIGH_STAKES:
                return R.drawable.toast;
            case TRADE_UP:
                return R.drawable.trading;
            default:
                return R.drawable.trading;
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
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_all_or_nothing_box, null);
        GifImageView arrow = dialogView.findViewById(R.id.arrow_spinner);
        View frameZero = dialogView.findViewById(R.id.card_zero);
        View frameDouble = dialogView.findViewById(R.id.card_double);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Random random = new Random();
        boolean isDouble = random.nextBoolean();
        
        // Initial rotation is 270 (pointing up at zero)
        // Pointing Down (Double) is 90 degrees (or 270 + 180 = 450)
        float currentRotation = 270f;
        float extraSpins = (4 + random.nextInt(3)) * 360f;
        float targetRotation = currentRotation + extraSpins + (isDouble ? 180 : 0);

        ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "rotation", currentRotation, targetRotation);
        animator.setDuration(4000); 
        animator.setInterpolator(new DecelerateInterpolator());
        
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isDouble) {
                    MainActivityGame.drinkNumberCounterInt *= 2;
                    frameDouble.setActivated(true);
                    frameDouble.setAlpha(0.5f);
                } else {
                    MainActivityGame.drinkNumberCounterInt = 0;
                    frameZero.setActivated(true);
                    frameZero.setAlpha(0.5f);
                }

                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    onHandled.run();
                }, 3000);
            }
        });

        animator.start();
    }

    private static void showSplitThePainRoulette(Player player, Runnable onHandled) {
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

        // If only 1 other player, skip roulette and auto-assign split target
        if (playerNames.size() == 1) {
            Game.getInstance().setSplitTarget(playerNames.get(0));
            onHandled.run();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_wheel_of_fortune, null);
        TextView title = dialogView.findViewById(R.id.powerup_dialogbox_textview);
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        ImageButton closeBtn = dialogView.findViewById(R.id.close_button);
        closeBtn.setVisibility(View.GONE);

        title.setText("Splitting the Pain...");

        // Use custom adapter for split the pain too
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.game_powerup_list_item, R.id.powerup_text, playerNames) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                GifImageView icon = view.findViewById(R.id.powerup_icon);
                icon.setImageResource(R.drawable.shots);
                
                // Stop the gif from animating
                try {
                    GifDrawable gifDrawable = (GifDrawable) icon.getDrawable();
                    if (gifDrawable != null) {
                        gifDrawable.stop();
                        gifDrawable.seekTo(0);
                    }
                } catch (ClassCastException e) {
                    // If it's not a gif, ignore
                }

                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        final Handler handler = new Handler();
        final int shuffleDuration = 2500;
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
                    Game.getInstance().setSplitTarget(targetName);
                    
                    title.setText("Split with " + targetName + "!");

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
        ArrayList<String> powerUpList = getPowerUps();
        
        // Filter out obtained ones to check if we should even run
        List<String> availableTypes = new ArrayList<>();
        for (String p : powerUpList) {
            if (!obtainedPowerUps.contains(getPowerUpType(p))) {
                availableTypes.add(getPowerUpType(p));
            }
        }

        if (availableTypes.isEmpty()) {
            if (onDismiss != null) onDismiss.run();
            return;
        }

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

        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        PowerUpAdapter adapter = new PowerUpAdapter(activity, powerUpList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        final Handler handler = new Handler();

        // If only one power-up remains, skip the shuffle animation.
        if (availableTypes.size() == 1) {
            int selectedIndex = -1;
            for (int i = 0; i < powerUpList.size(); i++) {
                if (!obtainedPowerUps.contains(getPowerUpType(powerUpList.get(i)))) {
                    selectedIndex = i;
                    break;
                }
            }
            listView.setItemChecked(selectedIndex, true);
            listView.setSelection(selectedIndex);
            String selectedPowerUp = powerUpList.get(selectedIndex);
            gainPowerUp(Game.getInstance().getCurrentPlayer(), selectedPowerUp);

            handler.postDelayed(() -> {
                if (dialog.isShowing()) {
                    obtainedPowerUps.add(getPowerUpType(selectedPowerUp));
                    dialog.dismiss();
                }
            }, 2500);
            dialog.show();
            return;
        }

        final Random random = new Random();
        final int shuffleDuration = 1500 + random.nextInt(1000);
        final int initialInterval = 50;

        final Runnable shuffleRunnable = new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;
            
            // Start at a random index that IS available
            int currentIndex = powerUpList.indexOf(findRandomAvailable(powerUpList));

            @Override
            public void run() {
                // Find next available sequential index
                do {
                    currentIndex = (currentIndex + 1) % powerUpList.size();
                } while (obtainedPowerUps.contains(getPowerUpType(powerUpList.get(currentIndex))));

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

                    handler.postDelayed(() -> {
                        if (dialog.isShowing()) {
                            obtainedPowerUps.add(getPowerUpType(selectedPowerUp));
                            dialog.dismiss();
                        }
                    }, 2500);
                }
            }
        };

        dialog.show();
        handler.post(shuffleRunnable);
    }

    private static String findRandomAvailable(List<String> list) {
        List<String> available = new ArrayList<>();
        for (String s : list) {
            if (!obtainedPowerUps.contains(getPowerUpType(s))) {
                available.add(s);
            }
        }
        if (available.isEmpty()) return null;
        return available.get(new Random().nextInt(available.size()));
    }

    public static void updatePowerUpIcons(Player player) {
        if (player == null || activity == null) return;
        List<String> powerUps = player.getPowerUps();

        GifImageView powerUpLeft = activity.findViewById(R.id.powerup_left);
        GifImageView powerUpRight = activity.findViewById(R.id.powerup_right);

        if (powerUps.size() >= 1) {
            powerUpLeft.setVisibility(View.VISIBLE);
            powerUpLeft.setImageResource(getPowerUpIcon(powerUps.get(0)));
            stopGifAnimation(powerUpLeft);
            powerUpLeft.setOnClickListener(v -> showPowerUpDetails(powerUps.get(0), player));
        } else {
            powerUpLeft.setVisibility(View.GONE);
        }

        if (powerUps.size() >= 2) {
            powerUpRight.setVisibility(View.VISIBLE);
            powerUpRight.setImageResource(getPowerUpIcon(powerUps.get(1)));
            stopGifAnimation(powerUpRight);
            powerUpRight.setOnClickListener(v -> showPowerUpDetails(powerUps.get(1), player));
        } else {
            powerUpRight.setVisibility(View.GONE);
        }
    }

    private static void stopGifAnimation(GifImageView gifImageView) {
        try {
            GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
            if (gifDrawable != null) {
                gifDrawable.stop();
                gifDrawable.seekTo(0);
            }
        } catch (ClassCastException e) {
            // Not a gif
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
