package com.example.countingdowngame.createPlayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;

import java.util.List;

public class CharacterClassAdapter extends RecyclerView.Adapter<CharacterClassAdapter.ViewHolder> {
    private final List<CharacterClassStore> characterClasses; // List to hold character class data
    private int selectedItemPosition = -1; // Variable to keep track of the selected item position

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition; // Method to retrieve the position of the selected item
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the character class at the current position
        CharacterClassStore characterClass = characterClasses.get(position);

        // Set the data to the views in the ViewHolder
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.specialAbilityTextView.setText(characterClass.getCharacterClassDescriptions());

        // Check if the current position is the selected item
        if (position == selectedItemPosition) {
            // Highlight the selected item
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.buttonhighlight));
        } else {
            // Remove highlight from other items
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.color.transparent));
        }

        holder.itemView.setOnClickListener(view -> {
            if (position == selectedItemPosition) {
                // If the tapped item is already selected, deselect it
                int previousSelected = selectedItemPosition;
                selectedItemPosition = -1; // Deselect the item
                notifyItemChanged(previousSelected); // Notify the adapter that the item has changed
                Log.d("CharacterClassAdapter", "Item at position " + position + " is DESELECTED: " + characterClasses.get(position).getClassName());
            } else {
                // If a different item is tapped, select it
                int previousSelected = selectedItemPosition;
                selectedItemPosition = position; // Update the selected item position
                notifyItemChanged(previousSelected); // Notify the adapter that the previous item has changed
                notifyItemChanged(selectedItemPosition); // Notify the adapter that the selected item has changed

                // Logging selected and unselected items
                for (int i = 0; i < getItemCount(); i++) {
                    if (i == selectedItemPosition) {
                        Log.d("CharacterClassAdapter", "Item at position " + i + " is SELECTED: " + characterClasses.get(i).getClassName());
                    } else {
                        Log.d("CharacterClassAdapter", "Item at position " + i + " is NOT SELECTED: " + characterClasses.get(i).getClassName());
                    }
                }
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_class_item, parent, false);
        return new ViewHolder(view); // Return a ViewHolder instance for each item
    }

    @Override
    public int getItemCount() {
        return characterClasses.size(); // Return the total number of items in the list
    }

    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView;
        TextView specialAbilityTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize TextViews from the layout for each item
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            specialAbilityTextView = itemView.findViewById(R.id.specialAbilityTextView);
        }
    }
}
