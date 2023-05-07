package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class Settings_WildCardChoice extends AppCompatActivity {
    private ListView listViewWildCard;
    private WildCardProbabilities[] mProbabilities;
    private WildCardAdapter mAdapter;

    @Override
    public void onBackPressed() {
        saveWildCardProbabilitiesToStorage(mProbabilities);
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wildcard_edit);

        listViewWildCard = findViewById(R.id.listView_WildCard);

        mProbabilities = loadWildCardProbabilitiesFromStorage();

        mAdapter = new WildCardAdapter(this, mProbabilities);
        listViewWildCard.setAdapter(mAdapter);
    }

    private WildCardProbabilities[] loadWildCardProbabilitiesFromStorage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean wildCardsEnabled = prefs.getBoolean("wild_cards_toggle", false);

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

        if (!wildCardsEnabled) {
            return new WildCardProbabilities[0];
        }
        return Arrays.stream(allProbabilities)
                .filter(c -> c.isEnabled())
                .toArray(WildCardProbabilities[]::new);
    }

    private void saveWildCardProbabilitiesToStorage(WildCardProbabilities[] probabilities) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < probabilities.length; i++) {
            editor.putBoolean("wild_card_" + i, probabilities[i].isEnabled());
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

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_wildcard, null);

            TextView textViewWildCard = view.findViewById(R.id.textview_wildcard);
            Switch switchWildCard = view.findViewById(R.id.switch_wildcard);

            WildCardProbabilities wildCard = mProbabilities[position];
            textViewWildCard.setText(wildCard.getText());
            switchWildCard.setChecked(wildCard.isEnabled());

            switchWildCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildCard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(mProbabilities);
            });

            return view;
        }
    }
}
