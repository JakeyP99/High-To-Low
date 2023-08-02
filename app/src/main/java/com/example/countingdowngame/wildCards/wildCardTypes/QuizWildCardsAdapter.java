package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;

import java.util.Arrays;


//TODO fix this adapter so it displays the correct wildcards in the correct category

public class QuizWildCardsAdapter extends WildCardsAdapter {
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_QUIZ_CARD = 2;

    private static final int ItemsPerCategory = 50;
    private static final int CategoryDividerCount = 5;

    private final String[] categoryNames = {
            "Science",
            "Geography",
            "History",
            "Art/Music",
            "Sport"
    };
    private boolean[] categoryVisibility = new boolean[categoryNames.length];

    public QuizWildCardsAdapter(WildCardProperties[] quizWildCards, Context context, WildCardType mode) {
        super("QuizPrefs", quizWildCards, context, mode);
        Arrays.fill(categoryVisibility, true);
    }


    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_category, parent, false);
            return new CategoryViewHolder(categoryView);
        } else {
            View quizCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        if (holder instanceof CategoryViewHolder) {
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                ((CategoryViewHolder) holder).bind(categoryNames[categoryIndex]);
            }
        } else {
            int quizCardIndex = getQuizCardIndexForPosition(position);
            if (quizCardIndex >= 0 && quizCardIndex < wildCards.length) {
                WildCardProperties wildCard = wildCards[quizCardIndex];
                ((QuizWildCardViewHolder) holder).bind(wildCard);
                int positionWithOneBasedIndex = quizCardIndex + 1;

                boolean isVisible = categoryVisibility[categoryIndex];
                holder.itemView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = isVisible ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
                holder.itemView.setLayoutParams(params);

                holder.itemView.setOnClickListener(v -> {
                    Log.d("QuizWildCardsAdapter", "Tapped on wildcard: " + wildCard.getText() + " (Category: " + wildCard.getCategory() + "Position: " + positionWithOneBasedIndex + ")");
                });
            } else {
                // Hide the views for invisible items
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                holder.itemView.setLayoutParams(params);
            }
        }
    }



    private int getCategoryIndexForPosition(int position) {
        return position / (ItemsPerCategory + 1);
    }

    private int getQuizCardIndexForPosition(int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        return position - categoryIndex - 1;
    }

    @Override
    public int getItemCount() {
        int visibleCount = 0;
        for (int i = 0; i < categoryNames.length; i++) {
            if (categoryVisibility[i]) {
                visibleCount += ItemsPerCategory + 1; // Add the category and its visible items
            }
        }
        return visibleCount;
    }



    @Override
    public int getItemViewType(int position) {
        return isCategoryDividerPosition(position) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_QUIZ_CARD;
    }

    private boolean isCategoryDividerPosition(int position) {
        return position % (ItemsPerCategory + 1) == 0;
    }

    public class CategoryViewHolder extends WildCardViewHolder {
        private final TextView categoryTextView;
        private final View categoryBanner;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.text_category_title);
            categoryBanner = itemView.findViewById(R.id.text_category_title); // Assuming you have a view with this ID in your category layout
            categoryBanner.setOnClickListener(v -> {
                int categoryIndex = getAdapterPosition() / (ItemsPerCategory + 1);
                toggleCategoryVisibility(categoryIndex);
            });
        }
        private void toggleCategoryVisibility(int categoryIndex) {
            categoryVisibility[categoryIndex] = !categoryVisibility[categoryIndex];
            notifyDataSetChanged();
        }

        public void bind(String categoryTitle) {
            categoryTextView.setText(categoryTitle);
        }
    }


    public class QuizWildCardViewHolder extends WildCardViewHolder {
        public QuizWildCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}