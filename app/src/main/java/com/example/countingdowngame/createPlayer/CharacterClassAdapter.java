package com.example.countingdowngame.createPlayer;

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
    private OnItemSelectedListener onItemSelectedListener; // Interface instance

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition; // Method to retrieve the position of the selected item
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener; // Method to set the listener
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(holder.getAdapterPosition());

        // Set the data to the views in the ViewHolder
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.specialAbilityTextView.setText(characterClass.getCharacterClassDescriptions());

        // Check if the current item is selected based on the isSelected flag in CharacterClassStore
        if (characterClass.isSelected()) {
            // Highlight the selected item
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.buttonhighlight));
        } else {
            // Remove highlight from other items
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.color.transparent));
        }

        holder.itemView.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (characterClass.isSelected()) {
                    // Deselect the clicked item
                    characterClass.setSelected(false);
                    selectedItemPosition = RecyclerView.NO_POSITION;
                    notifyItemChanged(adapterPosition);
                } else {
                    // Deselect the previously selected item
                    if (selectedItemPosition != RecyclerView.NO_POSITION) {
                        characterClasses.get(selectedItemPosition).setSelected(false);
                        notifyItemChanged(selectedItemPosition);
                    }

                    // Select the clicked item
                    characterClass.setSelected(true);
                    selectedItemPosition = adapterPosition;
                    notifyItemChanged(adapterPosition);
                }

                // Notify listener about the item selection
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(characterClass.getId()); // Pass the selected ID
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
        return characterClasses.size();
    }
    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView;
        TextView specialAbilityTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            specialAbilityTextView = itemView.findViewById(R.id.specialAbilityTextView);
        }
    }
}
