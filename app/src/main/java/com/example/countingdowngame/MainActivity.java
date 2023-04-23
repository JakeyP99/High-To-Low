package com.example.countingdowngame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    // This means you can't go back
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    // This sets the new game.
    static Game gameInstance = new Game();
    private TextView numberText;
    private TextView nextPlayerText;
    private Button btnSkip;
    private Button btnWild;

    private Map<Player, Set<WildCardProbabilities>> usedWildCards = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View wildText = findViewById(R.id.wild_textview);

        numberText = findViewById(R.id.numberText);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnSkip = findViewById(R.id.btnSkip);
        Button btnWild = findViewById(R.id.btnWild);
        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);



        //These are the button controls
        ButtonUtils.setButton(btnGenerate,null, null, this, () -> {
            gameInstance.nextNumber();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
            numberText.setVisibility(View.VISIBLE);
        });

        ButtonUtils.setButton(btnSkip, null,null, this, () -> {
            gameInstance.getCurrentPlayer().useSkip();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
            numberText.setVisibility(View.VISIBLE);

        });


        ButtonUtils.setImageButton( imageButtonExit, HomeScreen.class,this, () -> {
            gameInstance.endGame();
        });

        ButtonUtils.setButton(btnWild, null,null, this, () -> {
            btnWild.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            btnSkip.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);

            gameInstance.getCurrentPlayer().useWildCard();
            Player currentPlayer = gameInstance.getCurrentPlayer();
            wildCardActivate(currentPlayer);
        });


        // This sets a new playerEventListener, which is linked to the skip button. So the app knows when that button is clicked, it provides a functionality to go to the next player (we made the functionality below)
        gameInstance.setPlayerEventListener(e -> {
            if (e.type == PlayerEventType.SKIP) {
                gameInstance.nextPlayer();
            }
        });

        //This is the functionality mentioned above. Note getCurrentPlayer is found in Game class. Note gameInstance means that it is set for a brand new game.
        gameInstance.setGameEventListener(e -> {
            switch (e.type) {
                case NEXT_PLAYER: {
                    renderPlayer();
                    break;
                }

                case GAME_END: {
                    gameInstance.endGame();
                    startActivity(new Intent(MainActivity.this, EndActivity.class));
                    break;
                }

                case NEXT_NUMBER: {
                    numberText.setText(String.valueOf(gameInstance.currentNumber));
                    break;
                }
                case GAME_START: {
                    break;
                }
            }
        });

        gameInstance.startGame(NumberChoice.startingNumber);
        numberText.setText(Integer.toString(gameInstance.currentNumber));
        renderPlayer();
    }

    private void renderPlayer() {
        nextPlayerText.setText("Player " + (gameInstance.getCurrentPlayer().getName()) + "'s" + " Turn");

        if (gameInstance.getCurrentPlayer().getSkipAmount() > 0) {
                btnSkip.setVisibility(View.VISIBLE);
        } else {
                btnSkip.setVisibility(View.INVISIBLE);
            }

        if (gameInstance.getCurrentPlayer().getWildCardAmount() > 0) {
            if (btnWild == null) {
                btnWild = findViewById(R.id.btnWild);
            }
            btnWild.setVisibility(View.VISIBLE);
        } else {
            if (btnWild != null) {
                btnWild.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void wildCardActivate(Player player) {
        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        player.useWildCard();

        // Define the wild card probabilities
        WildCardProbabilities[] activityProbabilities = {
                new WildCardProbabilities("Take 1 drink.", 10),
                new WildCardProbabilities("Take 2 drinks.", 8),
                new WildCardProbabilities("Take 3 drinks.", 5),
                new WildCardProbabilities("Finish your drink.", 3),
                new WildCardProbabilities("Give 1 drink.", 10),
                new WildCardProbabilities("Give 2 drinks.", 8),
                new WildCardProbabilities("Give 3 drinks.", 5),
                new WildCardProbabilities("Choose a player to finish their drink.", 3),
                new WildCardProbabilities("The player to the left takes a drink.", 10),
                new WildCardProbabilities("The player to the right takes a drink.", 10),
                new WildCardProbabilities("The oldest player takes 2 drinks.", 10),
                new WildCardProbabilities("The youngest player takes 2 drinks.", 10),
                new WildCardProbabilities("The player who last peed takes 3 drinks.", 10),
                new WildCardProbabilities("The player with the oldest car takes 2 drinks.", 10),
                new WildCardProbabilities("Whoever last rode on a train takes 2 drinks.", 10),
                new WildCardProbabilities("Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.", 10),
                new WildCardProbabilities("Anyone who is sitting takes 2 drinks.", 10),
                new WildCardProbabilities("Whoever has the longest hair takes 2 drinks.", 10),
                new WildCardProbabilities("Whoever is wearing a watch takes 2 drinks.", 10),
                new WildCardProbabilities("Whoever has a necklace on takes 2 drinks.", 10),
                new WildCardProbabilities("Double the ending drink, whoever loses must now do double the consequence.", 5),
                new WildCardProbabilities("Get a skip button to use on any one of your turns!", 3)
        };


        // Get the set of used wild cards for the current player

        Set<WildCardProbabilities> usedCards = usedWildCards.getOrDefault(player, new HashSet<>());

        // Filter out the used wild cards from the list of probabilities
        List<WildCardProbabilities> unusedCards = Arrays.stream(activityProbabilities)
                .filter(c -> !usedCards.contains(c))
                .collect(Collectors.toList());

        if (unusedCards.isEmpty()) {
            // If all wild cards have been used, reset the usedCards set for the current player
            usedCards.clear();
        }

        int totalWeight = unusedCards.stream()
                .mapToInt(WildCardProbabilities::getProbability)
                .sum();

        if (totalWeight <= 0) {
            wildActivityTextView.setText("No wild cards available");
            return;
        }

        Random random = new Random();
        String selectedActivity = "";
        boolean foundUnusedCard = false;
        while (!foundUnusedCard) {
            int randomWeight = random.nextInt(totalWeight);
            int weightSoFar = 0;
            for (WildCardProbabilities activityProbability : unusedCards) {
                weightSoFar += activityProbability.getProbability();
                if (randomWeight < weightSoFar) {
                    selectedActivity = activityProbability.getActivity();
                    foundUnusedCard = true;
                    usedCards.add(activityProbability); // Mark the card as used for the current player
                    break;
                }
            }
        }

        if (selectedActivity.equals("Get a skip button to use on any one of your turns!")) {
            // add skip to player's integer if they have zero skips left
            if (player.getSkipAmount() == 0) {
                player.skips++;
                btnSkip.setVisibility(View.VISIBLE);
            }

            // check if the selected activity is associated with a specific card that has already been used
            if (usedCards.contains(activityProbabilities[0])) {
                // make skip button visible if player has at least one skip left
                if (player.getSkipAmount() > 0) {
                    btnSkip.setVisibility(View.VISIBLE);
                } else {
                    btnSkip.setVisibility(View.GONE);
                }
            } else {
                // hide skip button if the selected activity is not associated with a specific card that has already been used
                btnSkip.setVisibility(View.GONE);
            }
        } else {
            // hide skip button for all other activities
            btnSkip.setVisibility(View.GONE);
        }

        if (player.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }


        wildActivityTextView.setText(selectedActivity);

    }}
