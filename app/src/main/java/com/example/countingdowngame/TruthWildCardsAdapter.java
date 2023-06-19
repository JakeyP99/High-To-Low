package com.example.countingdowngame;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class TruthWildCardsAdapter extends RecyclerView.Adapter<TruthWildCardsAdapter.TruthWildCardViewHolder> {
    private Settings_WildCard_Probabilities[] truthWildCards;
    private Context mContext;
    private Settings_WildCard_Mode mMode;

    public TruthWildCardsAdapter(Settings_WildCard_Probabilities[] truthWildCards, Context context, Settings_WildCard_Mode mode) {
        this.truthWildCards = truthWildCards;
        this.mContext = context;
        this.mMode = mode;
        loadWildCardProbabilitiesFromStorage(mode);

    }

    @NonNull
    @Override
    public TruthWildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TruthWildCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruthWildCardViewHolder holder, int position) {
        Settings_WildCard_Probabilities wildcard = truthWildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return truthWildCards.length;
    }

    public class TruthWildCardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewProbabilities;
        private Button editButton;

        public TruthWildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
        }

        public void bind(Settings_WildCard_Probabilities wildcard) {
            textViewTitle.setText(wildcard.getText());
            textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard Properties");
                int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText textInput = new EditText(mContext);

                if (wildcard.isDeletable()) {
                    textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    textInput.setText(wildcard.getText());
                    layout.addView(textInput);
                } else {
                    textInput.setEnabled(false);
                }

                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildcard.getProbability()));
                layout.addView(probabilityInput);

                builder.setView(layout);
                saveWildCardProbabilitiesToStorage(mMode, truthWildCards);

                builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                ArrayList<Settings_WildCard_Probabilities> wildCardList = new ArrayList<>(Arrays.asList(truthWildCards));

                if (wildcard.isDeletable()) {
                    builder.setNeutralButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Delete</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wildCardList.remove(getAdapterPosition());
                            truthWildCards = wildCardList.toArray(new Settings_WildCard_Probabilities[0]);
                            notifyDataSetChanged();
                            saveWildCardProbabilitiesToStorage(mMode, truthWildCards);
                        }
                    });
                }

                builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = textInput.getText().toString().trim();
                        if (wildcard.isDeletable() && inputText.isEmpty()) {
                            Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int probability;
                        try {
                            probability = Integer.parseInt(probabilityInput.getText().toString());
                        } catch (NumberFormatException e) {
                            probability = 0; // Invalid input, set to 0
                        }

                        if (probabilityInput.getText().length() > 4) {
                            Toast.makeText(mContext, "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (textInput.length() > 100) {
                            Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 100 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        wildcard.setProbability(probability);

                        if (wildcard.isDeletable()) {
                            wildcard.setText(inputText);
                            textViewTitle.setText(wildcard.getText());
                        }

                        textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
                        setProbabilitySizeBasedOnString(textViewProbabilities, String.valueOf(wildcard.getProbability()));

                        saveWildCardProbabilitiesToStorage(mMode, truthWildCards);
                    }
                });

                builder.show();
            });
        }
    }
    private Settings_WildCard_Probabilities[] loadWildCardProbabilitiesFromStorage(Settings_WildCard_Mode mode) {
        SharedPreferences prefs;
        switch (mode) {
            case DELETABLE:
                prefs = mContext.getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
                break;
            default:
                // Handle other modes if necessary
                return new Settings_WildCard_Probabilities[0];
        }

        int wildCardCount = prefs.getInt("wild_card_count", 0);
        Settings_WildCard_Probabilities[] loadedWildCards = new Settings_WildCard_Probabilities[wildCardCount];

        for (int i = 0; i < wildCardCount; i++) {
            boolean enabled = prefs.getBoolean("wild_card_enabled_" + i, false);
            String activity = prefs.getString("wild_card_activity_" + i, "");
            int probability = prefs.getInt("wild_card_probability_" + i, 0);

            loadedWildCards[i] = new Settings_WildCard_Probabilities(activity, probability, enabled, true);
        }

        truthWildCards = loadedWildCards; // Update the truthWildCards array
        return loadedWildCards;
    }


    public void saveWildCardProbabilitiesToStorage(Settings_WildCard_Mode mode, Settings_WildCard_Probabilities[] probabilities) {
        SharedPreferences prefs;
        truthWildCards = probabilities;
        switch (mode) {
            case DELETABLE:
                prefs = mContext.getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
                break;
            default:
                // Handle other modes if necessary
                return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.putInt("wild_card_count", probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            Settings_WildCard_Probabilities probability = probabilities[i];
            editor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
            editor.putString("wild_card_activity_" + i, probability.getText());
            editor.putInt("wild_card_probability_" + i, probability.getProbability());
        }

        editor.apply();
    }


    private void setProbabilitySizeBasedOnString(TextView textView, String text) {
        int textSize = 18;
        if (text.length() > 2) {
            textSize = 12;
        } else if (text.length() > 0) {
            textSize = 18;
        }
        textView.setTextSize(textSize);
    }
}
