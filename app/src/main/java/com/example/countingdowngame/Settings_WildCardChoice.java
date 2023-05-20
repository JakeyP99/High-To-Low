package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings_WildCardChoice extends AppCompatActivity {
    private ListView listViewWildCard;
    private ListView listViewWildCardGameMode;
    private WildCardProbabilities[] deletableWildCards;
    private WildCardProbabilities[] nonDeletableWildCards;
    private WildCardAdapter deletableAdapter;
    private WildCardAdapter nonDeletableAdapter;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingClass.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wildcard_edit);

        listViewWildCard = findViewById(R.id.listView_WildCard);
        listViewWildCardGameMode = findViewById(R.id.listView_GameModeWildCards);

        WildCardProbabilities[][] wildCardArrays = loadWildCardProbabilitiesFromStorage(getApplicationContext());
        deletableWildCards = wildCardArrays[0];
        nonDeletableWildCards = wildCardArrays[1];

        deletableAdapter = new WildCardAdapter(this, deletableWildCards);
        nonDeletableAdapter = new WildCardAdapter(this, nonDeletableWildCards);

        listViewWildCard.setAdapter(deletableAdapter);
        listViewWildCardGameMode.setAdapter(nonDeletableAdapter);

        Button btnAddWildCard = findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

    }

    private void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Wildcard");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText probabilityInput = new EditText(this);
        probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        probabilityInput.setHint("Probability (1-100)");
        layout.addView(probabilityInput);

        final EditText textInput = new EditText(this);
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Text");
        layout.addView(textInput);

        builder.setView(layout);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("OK", (dialog, which) -> {
            int probability = Integer.parseInt(probabilityInput.getText().toString());
            String text = textInput.getText().toString();

            WildCardProbabilities newWildCard = new WildCardProbabilities(text, probability, true, true);

            WildCardProbabilities[][] probabilitiesArray = loadWildCardProbabilitiesFromStorage(getApplicationContext());
            WildCardProbabilities[] deletableProbabilities = probabilitiesArray[0];

            ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(deletableProbabilities));
            wildCardList.add(newWildCard);

            deletableProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);

            deletableAdapter = new WildCardAdapter(this, deletableProbabilities);
            listViewWildCard.setAdapter(deletableAdapter);

            saveWildCardProbabilitiesToStorage(deletableProbabilities);

            // Update the probabilities array in settings
            probabilitiesArray[0] = deletableProbabilities;

            // Refresh the adapter if needed
            deletableAdapter.notifyDataSetChanged();
        });

        builder.show();
    }
    WildCardProbabilities[][] loadWildCardProbabilitiesFromStorage(Context context) {
        SharedPreferences deletablePrefs = context.getSharedPreferences("DeletablePrefs", MODE_PRIVATE);

        WildCardProbabilities[] deletableProbabilities = new WildCardProbabilities[]{
                new WildCardProbabilities("Take 1 drink.", 10, true, true),
                new WildCardProbabilities("Take 2 drinks.", 8, true, true),
                new WildCardProbabilities("Take 3 drinks.", 5, true, true),
                new WildCardProbabilities("Finish your drink.", 3, true, true),
                new WildCardProbabilities("Give 1 drink.", 10, true, true),
                new WildCardProbabilities("Give 2 drinks.", 8, true, true),
                new WildCardProbabilities("Give 3 drinks.", 5, true, true),
                new WildCardProbabilities("Choose a player to finish their drink.", 3, true, true),
                new WildCardProbabilities("The player to the left takes a drink.", 10, true, true),
                new WildCardProbabilities("The player to the right takes a drink.", 10, true, true),
                new WildCardProbabilities("The oldest player takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("The youngest player takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("The player who last peed takes 3 drinks.", 10, true, true),
                new WildCardProbabilities("The player with the oldest car takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Whoever last rode on a train takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.", 10,true, true),
                new WildCardProbabilities("Anyone who is sitting takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Whoever has the longest hair takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Whoever is wearing a watch takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Whoever has a necklace on takes 2 drinks.", 10, true, true),
                new WildCardProbabilities("Double the ending drink (whoever loses must now do double the consequence).", 5, true, true),
                new WildCardProbabilities("Drink for courage then deliver a line from your favourite film making it as dramatic as possible!", 2, true, true),
                new WildCardProbabilities("Give 1 drink for every cheese you can name in 10 seconds.", 2, true, true),
                new WildCardProbabilities("The shortest person at the table must take 4 drinks then give 4 drinks.", 2, true, true),
                new WildCardProbabilities("Bare your biceps and flex for everyone. The players next to you each drink 2 for the view.", 2, true, true),
                new WildCardProbabilities("All females drink 3, and all males drink 3. Equality.", 5, true, true)
        };


        int deletableCount = deletablePrefs.getInt("wild_card_count", deletableProbabilities.length);
        if (deletableCount > deletableProbabilities.length) {
            deletableProbabilities = Arrays.copyOf(deletableProbabilities, deletableCount);
        }

        for (int i = 0; i < deletableCount; i++) {
            boolean enabled = deletablePrefs.getBoolean("wild_card_enabled_" + i, false);
            String activity = deletablePrefs.getString("wild_card_activity_" + i, "");
            int probability = deletablePrefs.getInt("wild_card_probability_" + i, 0);

            deletableProbabilities[i] = new WildCardProbabilities(activity, probability, enabled, enabled);
        }

        WildCardProbabilities[] nonDeletableProbabilities = new WildCardProbabilities[]{
                new WildCardProbabilities("Get a skip button to use on any one of your turns!", 3, true, false)
        };

        return new WildCardProbabilities[][] { deletableProbabilities, nonDeletableProbabilities };
    }

    private void saveWildCardProbabilitiesToStorage(WildCardProbabilities[] probabilities) {
        SharedPreferences deletablePrefs = getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
        SharedPreferences.Editor deletableEditor = deletablePrefs.edit();

        deletableEditor.clear(); // Clear previous data

        for (int i = 0; i < probabilities.length; i++) {
            WildCardProbabilities probability = probabilities[i];
            deletableEditor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
            deletableEditor.putString("wild_card_activity_" + i, probability.getText());
            deletableEditor.putInt("wild_card_probability_" + i, probability.getProbability());
        }

        deletableEditor.putInt("wild_card_count", probabilities.length);
        deletableEditor.apply();
    }


    private class WildCardAdapter extends ArrayAdapter<WildCardProbabilities> {
        private Context mContext;
        private WildCardProbabilities[] mProbabilities;

        public WildCardAdapter(Context context, WildCardProbabilities[] probabilities) {
            super(context, R.layout.list_item_wildcard, probabilities);
            mContext = context;
            mProbabilities = probabilities;
        }
        @Override
        public int getCount() {
            return mProbabilities.length;
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_wildcard, null);

            Button editButton = view.findViewById(R.id.button_edit_probability);
            TextView textViewWildCard = view.findViewById(R.id.textview_wildcard);
            TextView textViewProbability = view.findViewById(R.id.textview_probability);
            Switch switchWildCard = view.findViewById(R.id.switch_wildcard);

            WildCardProbabilities wildCard = mProbabilities[position];

            textViewWildCard.setText(wildCard.getText());
            textViewProbability.setText(String.valueOf(wildCard.getProbability()));
            switchWildCard.setChecked(wildCard.isEnabled());

            String wildCardText = wildCard.getText();
            setTextViewSizeBasedOnString(textViewWildCard, wildCardText);

            String probabilityText = String.valueOf(wildCard.getProbability());
            setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText textInput = new EditText(mContext);
                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildCard.getProbability()));
                layout.addView(probabilityInput);

                if (wildCard.isDeletable()) {
                    textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    textInput.setText(wildCard.getText());
                    layout.addView(textInput);
                } else {
                    textInput.setEnabled(false);
                }

                builder.setView(layout);

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
                mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);
                mProbabilities = wildCardList.toArray(new WildCardProbabilities[1]);

                if (wildCard.isDeletable()) {
                    builder.setNeutralButton("Delete", (dialog, which) -> {
                        wildCardList.remove(position);
                        notifyDataSetChanged();
                        saveWildCardProbabilitiesToStorage(mProbabilities);
                    });
                }

                builder.setPositiveButton("OK", (dialog, which) -> {
                    int probability = Integer.parseInt(probabilityInput.getText().toString());
                    wildCard.setProbability(probability);

                    if (wildCard.isDeletable()) {
                        String text = textInput.getText().toString();
                        wildCard.setText(text);
                        textViewWildCard.setText(wildCard.getText());
                    }

                    textViewProbability.setText(String.valueOf(wildCard.getProbability()));
                    setProbabilitySizeBasedOnString(textViewProbability, String.valueOf(wildCard.getProbability()));

                    saveWildCardProbabilitiesToStorage(mProbabilities);
                });

                builder.show();
            });

            switchWildCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildCard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(mProbabilities);
            });

            return view;
        }


        private void setTextViewSizeBasedOnString(TextView textView, String text) {
            int textSize = 20;
            if (text.length() > 20) {
                textSize = 14;
            } else if (text.length() > 10) {
                textSize = 16;
            }
            textView.setTextSize(textSize);
        }

        private void setProbabilitySizeBasedOnString(TextView textView, String text) {
            int textSize = 18;
            if (text.length() > 3) {
                textSize = 12;
            } else if (text.length() > 0) {
                textSize = 18;
            }
            textView.setTextSize(textSize);
        }
    }
}
