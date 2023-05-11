package com.example.countingdowngame;

import android.content.Context;
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
    private WildCardProbabilities[] mProbabilities;
    private WildCardAdapter mAdapter;
    private Context mContext;

    public Settings_WildCardChoice() {
        mContext = this;
    }

    @Override
    public void onBackPressed(){
        saveWildCardProbabilitiesToStorage(mProbabilities);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wildcard_edit);

        listViewWildCard = findViewById(R.id.listView_WildCard);

        mProbabilities = loadWildCardProbabilitiesFromStorage(getApplicationContext());

        mAdapter = new WildCardAdapter(this, mProbabilities);
        listViewWildCard.setAdapter(mAdapter);
        ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
        mAdapter.notifyDataSetChanged();

        Button btnAddWildCard = findViewById(R.id.btnAddWildCard);
        ButtonUtils.setButton(btnAddWildCard, null, this, () -> {
            addNewWildCard();
        });

    }


    private void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add New Wildcard");

        // Create a layout to hold the two EditText views
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create the EditText for the probability
        final EditText probabilityInput = new EditText(mContext);
        probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        probabilityInput.setHint("Probability (1-100)");
        layout.addView(probabilityInput);

        // Create the EditText for the text
        final EditText textInput = new EditText(mContext);
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Text");
        layout.addView(textInput);

        builder.setView(layout);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("OK", (dialog, which) -> {
            int probability = Integer.parseInt(probabilityInput.getText().toString());
            String text = textInput.getText().toString();

            WildCardProbabilities newWildCard = new WildCardProbabilities(text, probability, true);

            ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>();
            for (WildCardProbabilities probabilityItem : mProbabilities) {
                wildCardList.add(probabilityItem);
            }
            wildCardList.add(newWildCard);
            mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);

            mAdapter.notifyDataSetChanged();

            saveWildCardProbabilitiesToStorage(mProbabilities);
        });

        builder.show();
    }


    WildCardProbabilities[] loadWildCardProbabilitiesFromStorage(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();


        WildCardProbabilities[] allProbabilities = new WildCardProbabilities[]{
                new WildCardProbabilities("Take 1 drink.", 10, true),
                new WildCardProbabilities("Take 2 drinks.", 8, true),
                new WildCardProbabilities("Take 3 drinks.", 5, true),
                new WildCardProbabilities("Finish your drink.", 3, true),
                new WildCardProbabilities("Give 1 drink.", 10, true),
                new WildCardProbabilities("Give 2 drinks.", 8, true),
                new WildCardProbabilities("Give 3 drinks.", 5, true),
                new WildCardProbabilities("Choose a player to finish their drink.", 3, true),
                new WildCardProbabilities("The player to the left takes a drink.", 10, true),
                new WildCardProbabilities("The player to the right takes a drink.", 10, true),
                new WildCardProbabilities("The oldest player takes 2 drinks.", 10, true),
                new WildCardProbabilities("The youngest player takes 2 drinks.", 10, true),
                new WildCardProbabilities("The player who last peed takes 3 drinks.", 10, true),
                new WildCardProbabilities("The player with the oldest car takes 2 drinks.", 10, true),
                new WildCardProbabilities("Whoever last rode on a train takes 2 drinks.", 10, true),
                new WildCardProbabilities("Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.", 10, true),
                new WildCardProbabilities("Anyone who is sitting takes 2 drinks.", 10, true),
                new WildCardProbabilities("Whoever has the longest hair takes 2 drinks.", 10, true),
                new WildCardProbabilities("Whoever is wearing a watch takes 2 drinks.", 10, true),
                new WildCardProbabilities("Whoever has a necklace on takes 2 drinks.", 10, true),
                new WildCardProbabilities("Double the ending drink (whoever loses must now do double the consequence).", 5, true),
                new WildCardProbabilities("Get a skip button to use on any one of your turns!", 3, true),
                new WildCardProbabilities("Drink for courage then deliver a line from your favourite film making it as dramatic as possible!", 2, true),
                new WildCardProbabilities("Give 1 drink for every cheese you can name in 10 seconds.", 2, true),
                new WildCardProbabilities("The shortest person at the table must take 4 drinks then give 4 drinks.", 2, true),
                new WildCardProbabilities("Bare your biceps and flex for everyone. The players next to you each drink 2 for the view.", 2, true),
                new WildCardProbabilities("All females drink 3, and all males drink 3. Equality.", 5, true)
        };

        for (int i = 0; i < allProbabilities.length; i++) {
            boolean enabled = prefs.getBoolean("wild_card_enabled_" + i, allProbabilities[i].isEnabled());
            String activity = prefs.getString("wild_card_activity_" + i, allProbabilities[i].getText());
            int probability = prefs.getInt("wild_card_probability_" + i, allProbabilities[i].getProbability());

            allProbabilities[i].setEnabled(enabled);
            allProbabilities[i].setText(activity);
            allProbabilities[i].setProbability(probability);

        }

        return allProbabilities;
    }


    private void saveWildCardProbabilitiesToStorage(WildCardProbabilities[] probabilities) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < probabilities.length; i++) {
            editor.putBoolean("wild_card_enabled_" + i, probabilities[i].isEnabled());
            editor.putString("wild_card_activity_" + i, probabilities[i].getText());
            editor.putInt("wild_card_probability_" + i, probabilities[i].getProbability());

        }



        editor.apply();



    }

    private class WildCardAdapter extends ArrayAdapter<WildCardProbabilities> {
        private Context mContext;
        private WildCardProbabilities[] mProbabilities;

        public WildCardAdapter(Context context, WildCardProbabilities[] probabilities) {
            super(context, R.layout.list_item_wildcard, probabilities);
            mContext = context;
            mProbabilities = probabilities;
        }

        private void setTextViewSizeBasedOnString(TextView textView, String text) {
            int textSize = 20; // set default text size
            if (text.length() > 20) {
                textSize = 14; // set smaller text size for longer strings
            } else if (text.length() > 10) {
                textSize = 16; // set slightly smaller text size for medium length strings
            }
            textView.setTextSize(textSize);
        }


        @NonNull
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
            textViewWildCard.setText(wildCardText);
            setTextViewSizeBasedOnString(textViewWildCard, wildCardText);

            String probabilityText = String.valueOf(wildCard.getProbability());
            textViewProbability.setText(probabilityText);
            setTextViewSizeBasedOnString(textViewProbability, probabilityText);


            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard");

                // Create a layout to hold the two EditText views
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Create the EditText for the probability
                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildCard.getProbability()));
                layout.addView(probabilityInput);

                // Create the EditText for the text
                final EditText textInput = new EditText(mContext);
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setText(wildCard.getText()); // updated line
                layout.addView(textInput);

                builder.setView(layout);

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.setNeutralButton("Delete", (dialog, which) -> {
                    ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
                    wildCardList.remove(position);
                    mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);
                    mAdapter.notifyDataSetChanged();
                    saveWildCardProbabilitiesToStorage(mProbabilities);
                });


                builder.setPositiveButton("OK", (dialog, which) -> {
                    int probability = Integer.parseInt(probabilityInput.getText().toString());
                    String text = textInput.getText().toString();
                    if (!text.equals(wildCard.getText()) || probability != wildCard.getProbability()) {
                        mProbabilities[position].setProbability(probability);
                        mProbabilities[position].setText(text);
                        textViewWildCard.setText(mProbabilities[position].getText());
                        textViewProbability.setText(String.valueOf(mProbabilities[position].getProbability()));
                        saveWildCardProbabilitiesToStorage(mProbabilities);
                    }
                });

                builder.show();

            });


            switchWildCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildCard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(mProbabilities);
            });

            return view;
        }
    }
}
