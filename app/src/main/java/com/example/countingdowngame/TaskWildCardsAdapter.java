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

import java.util.ArrayList;
import java.util.Arrays;

public class TaskWildCardsAdapter extends RecyclerView.Adapter<TaskWildCardsAdapter.TaskWildCardViewHolder> {
    private Settings_WildCard_Probabilities[] taskWildCards;
    private Context mContext;
    private Settings_WildCard_Mode mMode;

    public TaskWildCardsAdapter(Settings_WildCard_Probabilities[] taskWildCards, Context context, Settings_WildCard_Mode mode) {
        this.taskWildCards = taskWildCards;
        this.mContext = context;
        this.mMode = mode;
        loadWildCardProbabilitiesFromStorage(mode);
    }

    @NonNull
    @Override
    public TaskWildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new TaskWildCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskWildCardViewHolder holder, int position) {
        Settings_WildCard_Probabilities wildcard = taskWildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return taskWildCards.length;
    }

    public class TaskWildCardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewProbabilities;
        private Button editButton;
        private Switch switchEnabled;

        public TaskWildCardViewHolder(View itemView) {
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
                saveWildCardProbabilitiesToStorage(mMode, taskWildCards);
            });

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard Properties");
                int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText textInput = new EditText(mContext);
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setText(wildcard.getText());
                layout.addView(textInput);

                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildcard.getProbability()));
                layout.addView(probabilityInput);

                builder.setView(layout);
                saveWildCardProbabilitiesToStorage(mMode, taskWildCards);

                builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

                builder.setNeutralButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Delete</font>"), (dialog, which) -> {
                    ArrayList<Settings_WildCard_Probabilities> wildCardList = new ArrayList<>(Arrays.asList(taskWildCards));
                    wildCardList.remove(getAdapterPosition());
                    taskWildCards = wildCardList.toArray(new Settings_WildCard_Probabilities[0]);
                    notifyDataSetChanged();
                    saveWildCardProbabilitiesToStorage(mMode, taskWildCards);
                });

                builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), (dialog, which) -> {
                    String inputText = textInput.getText().toString().trim();
                    if (inputText.isEmpty()) {
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

                    wildcard.setText(inputText);
                    wildcard.setProbability(probability);

                    textViewTitle.setText(wildcard.getText());
                    textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
                    setProbabilitySizeBasedOnString(textViewProbabilities, String.valueOf(wildcard.getProbability()));

                    saveWildCardProbabilitiesToStorage(mMode, taskWildCards);
                });

                builder.show();
            });
        }
    }

    private Settings_WildCard_Probabilities[] loadWildCardProbabilitiesFromStorage(Settings_WildCard_Mode mode) {
        SharedPreferences prefs;
        switch (mode) {
            case TASK: // Change to the appropriate storage name for task mode
                prefs = mContext.getSharedPreferences("TaskPrefs", MODE_PRIVATE);
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

        taskWildCards = loadedWildCards; // Update the taskWildCards array
        return loadedWildCards;
    }

    public void saveWildCardProbabilitiesToStorage(Settings_WildCard_Mode mode, Settings_WildCard_Probabilities[] probabilities) {
        SharedPreferences prefs;
        taskWildCards = probabilities;
        switch (mode) {
            case TASK: // Change to the appropriate storage name for task mode
                prefs = mContext.getSharedPreferences("TaskPrefs", MODE_PRIVATE);
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
