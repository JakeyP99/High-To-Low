package com.mydomain.countingdowngame.mainActivity.wildCards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.MainActivityGame;
import com.mydomain.countingdowngame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PowerUps {
    // Power-Up Type Constants
    public static final String SPLIT_THE_PAIN = "Split the Pain";
    public static final String DOUBLE_OR_NOTHING = "Double or Nothing";
    public static final String HIGH_STAKES = "High Stakes";
    public static final String TRADE_UP = "Trade Up";
    public static final String NOTHING = "Nothing";
    public static final String GET_OUT_OF_JAIL = "Get Out of Jail Free";
    private static final List<String> obtainedPowerUps = new ArrayList<>();
    private static MainActivityGame activity;

    public static void setActivity(MainActivityGame activityInstance) {
        activity = activityInstance;
    }

    public static void reset() {
        obtainedPowerUps.clear();
    }

    public static boolean isObtained(String powerUpName) {
        return obtainedPowerUps.contains(getPowerUpType(powerUpName));
    }

    public static String getPowerUpType(String powerUpName) {
        if (powerUpName == null) return "";
        if (powerUpName.contains(SPLIT_THE_PAIN)) return SPLIT_THE_PAIN;
        if (powerUpName.contains(DOUBLE_OR_NOTHING)) return DOUBLE_OR_NOTHING;
        if (powerUpName.contains(HIGH_STAKES)) return HIGH_STAKES;
        if (powerUpName.contains(TRADE_UP)) return TRADE_UP;
        if (powerUpName.contains(NOTHING)) return NOTHING;
        if (powerUpName.contains(GET_OUT_OF_JAIL)) return GET_OUT_OF_JAIL;
        return "";
    }

    private static boolean isPassive(String type) {
        return type.equals(SPLIT_THE_PAIN) || type.equals(DOUBLE_OR_NOTHING) || type.equals(GET_OUT_OF_JAIL);
    }

    public static ArrayList<String> getPowerUps() {
        ArrayList<String> powerUp = new ArrayList<>();
        powerUp.add(SPLIT_THE_PAIN + ": Divide your drinks with a random player if you lose!");
        powerUp.add(DOUBLE_OR_NOTHING + ": There is a 50/50 chance the final drinks will be doubled or turn to 0 if you lose!");
        powerUp.add(HIGH_STAKES + ": +3 drinks to the total, but gain 2 wildcards for your next turn!");
        powerUp.add(TRADE_UP + ": Lose 1 wildcard to reduce drinks by 3!");
        powerUp.add(GET_OUT_OF_JAIL + ": Automatically saves you from a 0 and reverts the number!");
        powerUp.add(NOTHING + ": Better luck next time!");
        return powerUp;
    }

    public static void gainPowerUp(Player player, String powerUpName) {
        if (player == null) return;
        if (getPowerUpType(powerUpName).equals(NOTHING)) return;

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
                    activity.renderPlayerUI(true); // Refresh UI to show updated wildcard count immediately
                } else {
                    activity.displayToastMessage("You need at least 1 wildcard to trade up!");
                    return;
                }
                break;
            case SPLIT_THE_PAIN:
            case DOUBLE_OR_NOTHING:
            case NOTHING:
            case GET_OUT_OF_JAIL:
                break;
        }
        updatePowerUpIcons(player);
    }

    public static int getPowerUpIcon(String powerUpName) {
        switch (getPowerUpType(powerUpName)) {
            case SPLIT_THE_PAIN:
                return R.drawable.division;
            case DOUBLE_OR_NOTHING:
                return R.drawable.dice;
            case HIGH_STAKES:
                return R.drawable.toast;
            case TRADE_UP:
                return R.drawable.trading;
            case NOTHING:
                return R.drawable.cross;
            case GET_OUT_OF_JAIL:
                return R.drawable.jail;
            default:
                return R.drawable.trading;
        }
    }

    // --- Losing Power-Up Handling ---

    public static void checkLosingPowerUps(Player player, Runnable onEndGame, TextView numberText) {
        List<String> playerPowerUps = new ArrayList<>(player.getPowerUps());

        // 1. Check for Get Out of Jail Free (Highest Priority, automatic)
        String jailFree = null;
        for (String p : playerPowerUps) {
            if (getPowerUpType(p).equals(GET_OUT_OF_JAIL)) {
                jailFree = p;
                break;
            }
        }

        if (jailFree != null) {
            player.usePowerUp(jailFree);
            activity.displayToastMessage("Saved by Get Out of Jail Free!");
            int prevNum = Game.getInstance().getPreviousNumber();

            // Revert number while view is hidden/alpha 0
            MainActivityGame.updateNumber(prevNum);

            // Animate it back "alive" as requested
            activity.animateTextViewBackAlive(numberText, () -> {
                Game.getInstance().nextPlayer(); // Go to next player after saving
                activity.enableButtons();
                activity.renderPlayerUI(true);
            });
            return; // Don't end game, player is saved
        }

        // 2. Check for Double or Nothing
        String allNothing = null;
        for (String p : playerPowerUps) {
            if (getPowerUpType(p).equals(DOUBLE_OR_NOTHING)) {
                allNothing = p;
                break;
            }
        }

        if (allNothing != null) {
            String finalAllNothing = allNothing;
            showAllOrNothingDialog(() -> {
                player.usePowerUp(finalAllNothing);
                checkSplitThePain(player, onEndGame);
            }, () -> {
                player.usePowerUp(finalAllNothing);
                onEndGame.run();
            });
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

    private static void showAllOrNothingDialog(Runnable onHandled, Runnable onSkip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_powerup_details, null);

        TextView title = dialogView.findViewById(R.id.powerup_title);
        TextView description = dialogView.findViewById(R.id.powerup_description);
        Button activateBtn = dialogView.findViewById(R.id.btn_activate_powerup);
        Button cancelBtn = dialogView.findViewById(R.id.btn_cancel_powerup);

        title.setText("Double or Nothing!");
        description.setText("Risk it all? 50/50 chance for 0 drinks or DOUBLE drinks!");
        activateBtn.setText("Risk It!");

        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setText("Don't Risk It");

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        activity.btnUtils.setButton(activateBtn, () -> {
            dialog.dismiss();
            showAllOrNothingGenerator(onHandled);
        });

        activity.btnUtils.setButton(cancelBtn, () -> {
            dialog.dismiss();
            onSkip.run();
        });

        dialog.show();
    }

    private static void showAllOrNothingGenerator(Runnable onHandled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_all_or_nothing_box, null);
        ImageView arrow = dialogView.findViewById(R.id.arrow_spinner);
        View frameZero = dialogView.findViewById(R.id.card_zero);
        View frameDouble = dialogView.findViewById(R.id.card_double);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Random random = new Random();
        boolean isDouble = random.nextBoolean();

        float currentRotation = 270f;
        float extraSpins = (6 + random.nextInt(4)) * 360f;
        float targetRotation = currentRotation + extraSpins + (isDouble ? 180 : 0);

        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(currentRotation, targetRotation);
        animator.setDuration(5000);
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            arrow.setRotation(value);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View winner = isDouble ? frameDouble : frameZero;

                // Scale up winner for emphasis
                winner.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .start();

                if (isDouble) {
                    MainActivityGame.drinkNumberCounterInt *= 2;
                } else {
                    MainActivityGame.drinkNumberCounterInt = 0;
                }

                // Flash winner highlight
                android.animation.ObjectAnimator flash = android.animation.ObjectAnimator.ofFloat(winner, "alpha", 1f, 0.4f);
                flash.setDuration(150);
                flash.setRepeatCount(6);
                flash.setRepeatMode(android.animation.ValueAnimator.REVERSE);

                flash.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        winner.setAlpha(1f);
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            onHandled.run();
                        }, 2000);
                    }
                });
                flash.start();
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
        View dialogView = activity.getLayoutInflater().inflate(R.layout.game_powerups, null);
        TextView title = dialogView.findViewById(R.id.powerup_dialogbox_textview);
        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);

        title.setText("Splitting the Pain...");

        // Use custom adapter for split the pain too
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.game_powerup_list_item, R.id.powerup_text, playerNames) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
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

                float progress = Math.min(1.0f, (float) elapsedTime / shuffleDuration);
                currentInterval = (int) (initialInterval + (progress * progress * 350));
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
                    }, 1500);
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

        if (availableTypes.isEmpty() || (availableTypes.size() == 1 && availableTypes.get(0).equals(NOTHING))) {
            if (onDismiss != null) onDismiss.run();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_powerups, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnDismissListener(d -> {
            if (onDismiss != null) onDismiss.run();
        });

        ListView listView = dialogView.findViewById(R.id.listViewPowerUps);
        // Pass true to bring back descriptions
        PowerUpAdapter adapter = new PowerUpAdapter(activity, powerUpList, true);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnTouchListener((v, event) -> true);

        final Handler handler = new Handler();

        // Pre-select the target for the roulette
        final String selectedPowerUpFinal = selectWeightedPowerUp(powerUpList);
        final int targetIndexFinal = powerUpList.indexOf(selectedPowerUpFinal);

        // If only one power-up remains, skip the shuffle animation.
        if (availableTypes.size() == 1) {
            listView.setItemChecked(targetIndexFinal, true);
            listView.setSelection(targetIndexFinal);

            handler.postDelayed(() -> {
                if (dialog.isShowing()) {
                    finalizePowerUpGain(selectedPowerUpFinal);
                    dialog.dismiss();
                }
            }, 1500);
            dialog.show();
            return;
        }

        final Random random = new Random();
        final int shuffleDuration = 1500 + random.nextInt(1000);
        final int initialInterval = 50;

        final Runnable shuffleRunnable = new Runnable() {
            int elapsedTime = 0;
            int currentInterval = initialInterval;

            // Start at a valid index
            int currentIndex = 0;

            {
                while (obtainedPowerUps.contains(getPowerUpType(powerUpList.get(currentIndex)))) {
                    currentIndex = (currentIndex + 1) % powerUpList.size();
                }
            }

            @Override
            public void run() {
                // Increment sequentially but skip obtained ones
                do {
                    currentIndex = (currentIndex + 1) % powerUpList.size();
                } while (obtainedPowerUps.contains(getPowerUpType(powerUpList.get(currentIndex))));

                listView.setItemChecked(currentIndex, true);
                adapter.notifyDataSetChanged(); // Ensure highlight updates in sequential order
                listView.smoothScrollToPosition(currentIndex);

                float progress = Math.min(1.0f, (float) elapsedTime / shuffleDuration);
                currentInterval = (int) (initialInterval + (progress * progress * 350));
                elapsedTime += currentInterval;

                // Continue if duration not reached OR we haven't hit the pre-selected target index yet
                if (elapsedTime < shuffleDuration || currentIndex != targetIndexFinal) {
                    handler.postDelayed(this, currentInterval);
                } else {
                    handler.postDelayed(() -> {
                        if (dialog.isShowing()) {
                            finalizePowerUpGain(selectedPowerUpFinal);
                            dialog.dismiss();
                        }
                    }, 1500);
                }
            }
        };

        dialog.show();
        handler.post(shuffleRunnable);
    }

    private static void finalizePowerUpGain(String powerUpName) {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        gainPowerUp(currentPlayer, powerUpName);
        if (!getPowerUpType(powerUpName).equals(NOTHING)) {
            obtainedPowerUps.add(getPowerUpType(powerUpName));
        }
    }

    private static String selectWeightedPowerUp(List<String> powerUpList) {
        List<String> available = new ArrayList<>();
        for (String s : powerUpList) {
            if (!obtainedPowerUps.contains(getPowerUpType(s))) {
                available.add(s);
            }
        }

        if (available.isEmpty()) return null;

        // Find if Get Out of Jail is available
        String jailFree = null;
        for (String s : available) {
            if (getPowerUpType(s).equals(GET_OUT_OF_JAIL)) {
                jailFree = s;
                break;
            }
        }

        Random random = new Random();
        if (jailFree != null) {
            // 5% chance for Get Out of Jail Free
            if (random.nextInt(100) < 5) {
                return jailFree;
            } else {
                // 95% chance for others
                List<String> others = new ArrayList<>(available);
                others.remove(jailFree);
                if (others.isEmpty()) return jailFree; // fallback if only jailFree was available
                return others.get(random.nextInt(others.size()));
            }
        }

        return available.get(random.nextInt(available.size()));
    }

    public static void updatePowerUpIcons(Player player) {
        if (player == null || activity == null) return;
        List<String> powerUps = player.getPowerUps();

        GifImageView powerUpLeft = activity.findViewById(R.id.powerup_left);
        GifImageView powerUpRight = activity.findViewById(R.id.powerup_right);

        if (!powerUps.isEmpty()) {
            String pName = powerUps.get(0);
            powerUpLeft.setVisibility(View.VISIBLE);
            powerUpLeft.setImageResource(getPowerUpIcon(pName));
            stopGifAnimation(powerUpLeft);
            // Click to show details first
            activity.btnUtils.setButton(powerUpLeft, () -> showPowerUpDetails(pName, player));
        } else {
            powerUpLeft.setVisibility(View.GONE);
        }

        if (powerUps.size() >= 2) {
            String pName = powerUps.get(1);
            powerUpRight.setVisibility(View.VISIBLE);
            powerUpRight.setImageResource(getPowerUpIcon(pName));
            stopGifAnimation(powerUpRight);
            // Click to show details first
            activity.btnUtils.setButton(powerUpRight, () -> showPowerUpDetails(pName, player));
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

        String type = getPowerUpType(powerUpName);
        title.setText(type);

        String cleanDescription = powerUpName;
        if (powerUpName.contains(": ")) {
            cleanDescription = powerUpName.substring(powerUpName.indexOf(": ") + 2);
        }
        description.setText(cleanDescription);

        if (isPassive(type) || type.equals(NOTHING)) {
            activateBtn.setVisibility(View.GONE);
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        activity.btnUtils.setButton(activateBtn, () -> {
            activatePowerUp(powerUpName, player);
            dialog.dismiss();
        });

        dialog.show();
    }
}
