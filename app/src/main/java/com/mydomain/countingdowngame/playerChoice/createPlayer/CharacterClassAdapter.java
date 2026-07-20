package com.mydomain.countingdowngame.playerChoice.createPlayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mydomain.countingdowngame.R;

import java.util.List;

public class CharacterClassAdapter extends RecyclerView.Adapter<CharacterClassAdapter.ViewHolder> {
    private final List<CharacterClassStore> characterClasses; // List to hold character class data
    private OnRecyclerViewScrollListener scrollListener;

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(position);
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.activeAbilityTextView.setText(characterClass.getCharacterActiveDescriptions());
        holder.passiveAbilityTextView.setText(characterClass.getCharacterPassiveDescriptions());
        holder.classQuoteTextView.setText(characterClass.getQuote());

        if (characterClass.getCooldown() > 0) {
            holder.activeCooldownTextView.setVisibility(View.VISIBLE);
            holder.activeCooldownTextView.setText("🕒 Cooldown: " + characterClass.getCooldown() + " Turns");
        } else {
            holder.activeCooldownTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollListener != null) {
                scrollListener.onScrolled(scrollY - oldScrollY);
            }
        });
        // Set the visibility of Active Ability and Passive Ability TextViews based on the class
        if (characterClass.getClassName().equals("No Class")) {
            holder.activeAbilityBox.setVisibility(View.GONE);
            holder.passiveAbilityBox.setVisibility(View.GONE);
            holder.classQuoteTextView.setVisibility(View.GONE);
        } else {
            holder.activeAbilityBox.setVisibility(View.VISIBLE);
            holder.passiveAbilityBox.setVisibility(View.VISIBLE);
            holder.classQuoteTextView.setVisibility(View.VISIBLE);
        }

        holder.classImageView.setImageResource(characterClass.getImageResource());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_choice_character_class_item, parent, false);
        return new ViewHolder(view); // Return a ViewHolder instance for each item
    }

    @Override
    public int getItemCount() {
        return characterClasses.size();
    }

    public interface OnRecyclerViewScrollListener {
        void onScrolled(int dy);
    }

    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView classImageView;
        TextView classNameTextView;
        TextView activeAbilityTextView;
        TextView passiveAbilityTextView;
        TextView classQuoteTextView;
        TextView activeCooldownTextView;
        View activeAbilityBox;
        View passiveAbilityBox;

        public ViewHolder(View itemView) {
            super(itemView);
            classImageView = itemView.findViewById(R.id.classImageView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            activeAbilityTextView = itemView.findViewById(R.id.activeAbilityTextView);
            passiveAbilityTextView = itemView.findViewById(R.id.passiveAbilityTextView);
            classQuoteTextView = itemView.findViewById(R.id.classQuoteTextView);
            activeCooldownTextView = itemView.findViewById(R.id.activeCooldownTextView);
            activeAbilityBox = itemView.findViewById(R.id.activeAbilityBox);
            passiveAbilityBox = itemView.findViewById(R.id.passiveAbilityBox);
        }
    }

}
