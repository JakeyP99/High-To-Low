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
    private final List<CharacterClassStore> characterClasses;
    private int selectedItem = -1;

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(position);
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.specialAbilityTextView.setText(characterClass.getSpecialAbility());

        // Check if this item is the selected one and update the background
        if (position == selectedItem) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.buttonhighlight));
        }  // Set a default background when the item is not selected
        else {
            // Set a default background when the item is not selected
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.color.transparent));
        }
        // Set an onClickListener for item clicks
        holder.itemView.setOnClickListener(view -> {
            int previousSelected = selectedItem;
            selectedItem = holder.getAdapterPosition(); // Get the current position dynamically
            notifyItemChanged(previousSelected); // Unhighlight previous
            notifyItemChanged(selectedItem); // Highlight the selected one
        });
    }
    @Override
    public int getItemCount() {
        return characterClasses.size();
    }

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
