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
import android.widget.Toast;

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


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingClass.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wildcard_edit);

        listViewWildCard = findViewById(R.id.listView_WildCard);

        mProbabilities = loadWildCardProbabilitiesFromStorage(getApplicationContext());

        mAdapter = new WildCardAdapter(this, mProbabilities);
        listViewWildCard.setAdapter(mAdapter);

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

            WildCardProbabilities newWildCard = new WildCardProbabilities(text, probability, true);

            ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
            wildCardList.add(newWildCard);

            mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);

            mAdapter = new WildCardAdapter(this, mProbabilities);
            listViewWildCard.setAdapter(mAdapter);

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
        int count = prefs.getInt("wild_card_count", allProbabilities.length);
        if (count > allProbabilities.length) {
            allProbabilities = new WildCardProbabilities[count];
            for (int i = 0; i < count; i++) {

                WildCardProbabilities p = allProbabilities[i];
                if (p != null) {
                    allProbabilities[i] = new WildCardProbabilities(p.getText(), p.getProbability(), p.isEnabled());
                } else {
                    allProbabilities[i] = new WildCardProbabilities("", 0, false);
                }
            }
        }
        for (int i = 0; i < count; i++) {
            boolean enabled = prefs.getBoolean("wild_card_enabled_" + i, false);
            String activity = prefs.getString("wild_card_activity_" + i, "");
            int probability = prefs.getInt("wild_card_probability_" + i, 0);

            allProbabilities[i] = new WildCardProbabilities(activity, probability, enabled);
        }

        return allProbabilities;
    }

    private void saveWildCardProbabilitiesToStorage(WildCardProbabilities[] probabilities) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < probabilities.length; i++) {
            WildCardProbabilities probability = probabilities[i];
            editor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
            editor.putString("wild_card_activity_" + i, probability.getText());
            editor.putInt("wild_card_probability_" + i, probability.getProbability());
        }

        editor.putInt("wild_card_count", probabilities.length);
        editor.apply();
    }

    private class WildCardAdapter extends ArrayAdapter<WildCardProbabilities> {
        private Context mContext;

        public WildCardAdapter(Context context, WildCardProbabilities[] probabilities) {
            super(context, R.layout.list_item_wildcard, probabilities);
            mContext = context;
        }

        @Override
        public int getCount() {
            return mProbabilities.length;
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
            setTextViewSizeBasedOnString(textViewWildCard, wildCardText);

            String probabilityText = String.valueOf(wildCard.getProbability());
            setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildCard.getProbability()));
                layout.addView(probabilityInput);

                final EditText textInput = new EditText(mContext);
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setText(wildCard.getText());
                layout.addView(textInput);

                builder.setView(layout);

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.setNeutralButton("Delete", (dialog, which) -> {
                    ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
                    wildCardList.remove(position);
                    mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);
                    notifyDataSetChanged();
                    saveWildCardProbabilitiesToStorage(mProbabilities);
                });


                builder.setPositiveButton("OK", (dialog, which) -> {
                    int probability = Integer.parseInt(probabilityInput.getText().toString());
                    String text = textInput.getText().toString();
                    String probabilityInputText = probabilityInput.getText().toString();

                    if (probabilityInputText.length() > 4) {
                        Toast.makeText(Settings_WildCardChoice.this, "Mate, that probability number is a bit high don't you think?", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!text.equals(wildCard.getText()) || probability != wildCard.getProbability()) {
                        wildCard.setProbability(probability);
                        wildCard.setText(text);
                        textViewWildCard.setText(wildCard.getText());
                        textViewProbability.setText(String.valueOf(wildCard.getProbability()));
                        setProbabilitySizeBasedOnString(textViewProbability, String.valueOf(wildCard.getProbability())); // Updated this line
                        saveWildCardProbabilitiesToStorage(mProbabilities);
                    }
                });

                builder.show();
            });;

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
