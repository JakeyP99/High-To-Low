package com.example.countingdowngame;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuizWildCardsAdapter extends RecyclerView.Adapter<QuizWildCardsAdapter.QuizWildCardViewHolder> {
    private Settings_WildCard_Probabilities[] quizWildCards;
    private Context mContext;
    private Settings_WildCard_Mode mMode;

    public QuizWildCardsAdapter(Settings_WildCard_Probabilities[] quizWildCards, Context context, Settings_WildCard_Mode mode) {
        this.quizWildCards = quizWildCards;
        this.mContext = context;
        this.mMode = mode;
        loadWildCardProbabilitiesFromStorage(mode);
    }

    @NonNull
    @Override
    public QuizWildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new QuizWildCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizWildCardViewHolder holder, int position) {
        Settings_WildCard_Probabilities wildcard = quizWildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return quizWildCards.length;
    }

    public class QuizWildCardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewProbabilities;
        private Button editButton;
        private Switch switchEnabled;

        public QuizWildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
            switchEnabled = itemView.findViewById(R.id.switch_wildcard);

        }

        public void bind(Settings_WildCard_Probabilities wildcard) {
            textViewTitle.setText(wildcard.getText());
            textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));

            switchEnabled.setChecked(wildcard.isEnabled());

            switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildcard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(mMode, quizWildCards);
            });

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard Properties");
                int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText textInput = new EditText(mContext);
                textInput.setEnabled(false); // Disable editing text for quiz wildcards
                layout.addView(textInput);

                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildcard.getProbability()));
                layout.addView(probabilityInput);

                builder.setView(layout);
                saveWildCardProbabilitiesToStorage(mMode, quizWildCards);

                builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

                builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), (dialog, which) -> {
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

                    wildcard.setProbability(probability);

                    textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
                    setProbabilitySizeBasedOnString(textViewProbabilities, String.valueOf(wildcard.getProbability()));

                    saveWildCardProbabilitiesToStorage(mMode, quizWildCards);
                });

                builder.show();
            });
        }
    }

    private Settings_WildCard_Probabilities[] loadWildCardProbabilitiesFromStorage(Settings_WildCard_Mode mode) {
        SharedPreferences prefs;
        switch (mode) {
            case QUIZ: // Change to the appropriate storage name for quiz mode
                prefs = mContext.getSharedPreferences("QuizPrefs", MODE_PRIVATE);
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

            loadedWildCards[i] = new Settings_WildCard_Probabilities(activity, probability, enabled, false); // Set deletable to false for quiz mode
        }

        quizWildCards = loadedWildCards; // Update the quizWildCards array
        return loadedWildCards;
    }

    public void saveWildCardProbabilitiesToStorage(Settings_WildCard_Mode mode, Settings_WildCard_Probabilities[] probabilities) {
        SharedPreferences prefs;
        quizWildCards = probabilities;
        switch (mode) {
            case QUIZ: // Change to the appropriate storage name for quiz mode
                prefs = mContext.getSharedPreferences("QuizPrefs", MODE_PRIVATE);
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
