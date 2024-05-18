package com.example.countingdowngame.createPlayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;

import java.util.List;

public class CharacterClassAdapter extends RecyclerView.Adapter<CharacterClassAdapter.ViewHolder> {
    private final List<CharacterClassStore> characterClasses; // List to hold character class data
    private OnRecyclerViewScrollListener scrollListener;

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }
    public interface OnRecyclerViewScrollListener {
        void onScrolled(int dy);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(position);
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.activeAbilityTextView.setText(characterClass.getCharacterActiveDescriptions());
        holder.passiveAbilityTextView.setText(characterClass.getCharacterPassiveDescriptions());

        holder.itemView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollListener != null) {
                scrollListener.onScrolled(scrollY - oldScrollY);
            }
        });
        // Set the visibility of Active Ability and Passive Ability TextViews based on the class
        if (characterClass.getClassName().equals("No Class")) {
            holder.activeAbilityText.setVisibility(View.GONE);
            holder.passiveAbilityText.setVisibility(View.GONE);
        } else {
            holder.activeAbilityTextView.setVisibility(View.VISIBLE);
            holder.passiveAbilityTextView.setVisibility(View.VISIBLE);
        }

        holder.classImageView.setImageResource(characterClass.getImageResource());
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
        ImageView classImageView;
        TextView classNameTextView;
        TextView activeAbilityTextView;
        TextView passiveAbilityTextView;
        TextView activeAbilityText;
        TextView passiveAbilityText;

        public ViewHolder(View itemView) {
            super(itemView);
            classImageView = itemView.findViewById(R.id.classImageView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            activeAbilityTextView = itemView.findViewById(R.id.activeAbilityTextView);
            passiveAbilityTextView = itemView.findViewById(R.id.passiveAbilityTextView);
            activeAbilityText = itemView.findViewById(R.id.activeAbilityText); // Initialization of activeAbilityTextView
            passiveAbilityText = itemView.findViewById(R.id.passiveAbilityText);

        }
    }

}
