package com.example.countingdowngame;

import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings_WildCard_Adapter extends ArrayAdapter<Settings_WildCard_Probabilities> {
    private final Settings_WildCard_Choice mContext;
    private final Settings_WildCard_Mode mMode;
    private Settings_WildCard_Probabilities[] mProbabilities;

    public Settings_WildCard_Adapter(Settings_WildCard_Mode mode, Settings_WildCard_Choice context, Settings_WildCard_Probabilities[] probabilities) {
        super(context, R.layout.list_view_wild_cards, probabilities);
        mContext = context;
        mProbabilities = probabilities;
        mMode = mode;
    }
    @Override
    public int getCount() {
        return mProbabilities.length;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_view_wild_cards, null);

        Button editButton = view.findViewById(R.id.button_edit_probability);
        TextView textViewWildCard = view.findViewById(R.id.textview_wildcard);
        TextView textViewProbability = view.findViewById(R.id.textview_probability);
        Switch switchWildCard = view.findViewById(R.id.switch_wildcard);
        Settings_WildCard_Probabilities wildCard = mProbabilities[position];
        textViewWildCard.setText(wildCard.getText());
        textViewProbability.setText(String.valueOf(wildCard.getProbability()));
        switchWildCard.setChecked(wildCard.isEnabled());
        String wildCardText = wildCard.getText();
        setTextViewSizeBasedOnString(textViewWildCard, wildCardText);
        String probabilityText = String.valueOf(wildCard.getProbability());
        setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

        editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Edit Wildcard Properties");
            int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText textInput = new EditText(mContext);

            if (wildCard.isDeletable()) {
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setText(wildCard.getText());
                layout.addView(textInput);
            } else {
                textInput.setEnabled(false);
            }

            final EditText probabilityInput = new EditText(mContext);
            probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            probabilityInput.setText(String.valueOf(wildCard.getProbability()));
            layout.addView(probabilityInput);



            builder.setView(layout);

            builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

            ArrayList<Settings_WildCard_Probabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
            mProbabilities = wildCardList.toArray(new Settings_WildCard_Probabilities[0]);
            mProbabilities = wildCardList.toArray(new Settings_WildCard_Probabilities[1]);

            if (wildCard.isDeletable()) {
                builder.setNeutralButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Delete</font>"), (dialog, which) -> {
                    wildCardList.remove(position);
                    mProbabilities = wildCardList.toArray(new Settings_WildCard_Probabilities[0]);
                    notifyDataSetChanged();
                    mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);
                });
            }
            builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), (dialog, which) -> {

                int probability;

                try {
                    probability = Integer.parseInt(probabilityInput.getText().toString());
                } catch (NumberFormatException e) {
                    probability = 0; // Invalid input, set to a negative value
                }

                wildCard.setProbability(probability);

                String inputText = probabilityInput.getText().toString().trim();

                if (inputText.length() > 4) {
                    Toast.makeText(Settings_WildCard_Adapter.this.getContext(), "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                    return;
                }
                  if (wildCard.isDeletable()) {
                    String text = textInput.getText().toString();
                    wildCard.setText(text);
                    textViewWildCard.setText(wildCard.getText());
                }

                textViewProbability.setText(String.valueOf(wildCard.getProbability()));
                setProbabilitySizeBasedOnString(textViewProbability, String.valueOf(wildCard.getProbability()));

                mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);

            });

            builder.show();
        });

        switchWildCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wildCard.setEnabled(isChecked);
            mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);
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
        if (text.length() > 2) {
            textSize = 12;
        } else if (text.length() > 0) {
            textSize = 18;
        }
        textView.setTextSize(textSize);
    }
}