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
    private boolean isExpanded = false;
    private OnExpandListener expandListener;
    public static final String PAYLOAD_EXPANSION = "PAYLOAD_EXPANSION";

    public interface OnExpandListener {
        void onExpandToggled(boolean expanded);
    }

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public void setOnExpandListener(OnExpandListener listener) {
        this.expandListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                if (payload.equals(PAYLOAD_EXPANSION)) {
                    updateExpansionViews(holder, characterClasses.get(position));
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private void updateExpansionViews(ViewHolder holder, CharacterClassStore characterClass) {
        if (characterClass.getClassName().equals("No Class")) {
            return;
        }
        if (isExpanded) {
            holder.activeAbilityTextView.setText(characterClass.getCharacterActiveDescriptions());
            holder.passiveAbilityTextView.setText(characterClass.getCharacterPassiveDescriptions());
            holder.btnActiveInfo.setImageResource(R.drawable.ic_arrow_up);
            holder.btnPassiveInfo.setImageResource(R.drawable.ic_arrow_up);
        } else {
            holder.activeAbilityTextView.setText(characterClass.getShortActive());
            holder.passiveAbilityTextView.setText(characterClass.getShortPassive());
            holder.btnActiveInfo.setImageResource(R.drawable.ic_arrow_down);
            holder.btnPassiveInfo.setImageResource(R.drawable.ic_arrow_down);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(position);
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.classQuoteTextView.setText(characterClass.getQuote());

        updateExpansionViews(holder, characterClass);

        if (characterClass.getCooldown() > 0) {
            holder.activeCooldownTextView.setVisibility(View.VISIBLE);
            holder.cooldownDivider.setVisibility(View.VISIBLE);
            holder.activeCooldownTextView.setText("Cooldown: " + characterClass.getCooldown() + " Turns");
        } else {
            holder.activeCooldownTextView.setVisibility(View.GONE);
            holder.cooldownDivider.setVisibility(View.GONE);
        }

        View.OnClickListener toggleClick = v -> {
            if (expandListener != null) {
                expandListener.onExpandToggled(!isExpanded);
            }
        };

        holder.btnActiveInfo.setOnClickListener(toggleClick);
        holder.btnPassiveInfo.setOnClickListener(toggleClick);
        holder.activeAbilityBox.setOnClickListener(toggleClick);
        holder.passiveAbilityBox.setOnClickListener(toggleClick);

        holder.itemView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollListener != null) {
                scrollListener.onScrolled(scrollY - oldScrollY);
            }
        });
        // Set the visibility of Active Ability and Passive Ability TextViews based on the class
        if (characterClass.getClassName().equals("No Class")) {
            holder.activeAbilityBox.setVisibility(View.GONE);
            holder.passiveAbilityBox.setVisibility(View.GONE);
            holder.classQuoteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.activeAbilityBox.setVisibility(View.VISIBLE);
            holder.passiveAbilityBox.setVisibility(View.VISIBLE);
            holder.classQuoteTextView.setVisibility(View.VISIBLE);
            holder.activeAbilityText.setText("ACTIVE");
            holder.btnActiveInfo.setVisibility(View.VISIBLE);
        }

        // Only set the image if it has changed to prevent GIF reset
        Object currentId = holder.classImageView.getTag();
        int newId = characterClass.getImageResource();
        if (currentId == null || (int) currentId != newId) {
            holder.classImageView.setImageResource(newId);
            holder.classImageView.setTag(newId);
        }
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
        TextView activeAbilityText;
        View activeAbilityBox;
        View passiveAbilityBox;
        ImageView btnActiveInfo;
        ImageView btnPassiveInfo;
        View cooldownDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            classImageView = itemView.findViewById(R.id.classImageView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            activeAbilityTextView = itemView.findViewById(R.id.activeAbilityTextView);
            passiveAbilityTextView = itemView.findViewById(R.id.passiveAbilityTextView);
            classQuoteTextView = itemView.findViewById(R.id.classQuoteTextView);
            activeCooldownTextView = itemView.findViewById(R.id.activeCooldownTextView);
            activeAbilityText = itemView.findViewById(R.id.activeAbilityText);
            activeAbilityBox = itemView.findViewById(R.id.activeAbilityBox);
            passiveAbilityBox = itemView.findViewById(R.id.passiveAbilityBox);
            btnActiveInfo = itemView.findViewById(R.id.btnActiveInfo);
            btnPassiveInfo = itemView.findViewById(R.id.btnPassiveInfo);
            cooldownDivider = itemView.findViewById(R.id.cooldownDivider);
        }
    }

}
