package com.example.countingdowngame.createPlayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;

import java.util.List;

public class CharacterClassAdapter extends RecyclerView.Adapter<CharacterClassAdapter.ViewHolder> {
    private final List<CharacterClassStore> characterClasses;
    private static int selectedItem = -1;
    private OnArrowClickListener arrowClickListener;

    public CharacterClassAdapter(List<CharacterClassStore> characterClasses) {
        this.characterClasses = characterClasses;
        selectedItem = -1;
    }

    public int getSelectedItemPosition() {
        return selectedItem;
    }

    public void setOnArrowClickListener(OnArrowClickListener listener) {
        this.arrowClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterClassStore characterClass = characterClasses.get(position);
        holder.classNameTextView.setText(characterClass.getClassName());
        holder.specialAbilityTextView.setText(characterClass.getCharacterClassDescriptions());

        if (position == selectedItem) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.buttonhighlight));
            holder.dottedLineBelow.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.color.transparent));
            holder.dottedLineBelow.setVisibility(View.VISIBLE);
        }

        holder.leftArrow.setOnClickListener(v -> {
            if (arrowClickListener != null) {
                arrowClickListener.onLeftArrowClick();
            }
        });

        holder.rightArrow.setOnClickListener(v -> {
            if (arrowClickListener != null) {
                arrowClickListener.onRightArrowClick();
            }
        });


        // Set an onClickListener for item clicks
        holder.itemView.setOnClickListener(view -> {
            int previousSelected = selectedItem;
            selectedItem = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedItem);
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_class_item, parent, false);
        return new ViewHolder(view);
    }

    public interface OnArrowClickListener {
        void onLeftArrowClick();

        void onRightArrowClick();
    }

    @Override
    public int getItemCount() {
        return characterClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView;
        TextView specialAbilityTextView;
        View dottedLineBelow;
        ImageButton leftArrow;
        ImageButton rightArrow;

        public ViewHolder(View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            specialAbilityTextView = itemView.findViewById(R.id.specialAbilityTextView);
            dottedLineBelow = itemView.findViewById(R.id.dotted_line_below); // Initialize the reference
            leftArrow = itemView.findViewById(R.id.leftArrow);
            rightArrow = itemView.findViewById(R.id.rightArrow);
        }
    }
}
