package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;


//TODO fix this adapter so it displays the correct wildcards in the correct category

public class QuizWildCardsAdapter extends WildCardsAdapter {
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_QUIZ_CARD = 2;

    private final String[] categoryNames = {
            "Science Questions",
            "Geography Questions",
            "History Questions",
            "Art/Music Questions"
    };

    public QuizWildCardsAdapter(WildCardHeadings[] quizWildCards, Context context, WildCardType mode) {
        super("QuizPrefs", quizWildCards, context, mode);
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
        if (holder instanceof CategoryViewHolder) {
            int categoryIndex = getCategoryIndexForPosition(position);
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                ((CategoryViewHolder) holder).bind(categoryNames[categoryIndex]);
            }
        } else {
            int quizCardIndex = getQuizCardIndexForPosition(position);
            if (quizCardIndex >= 0 && quizCardIndex < wildCards.length) {
                super.onBindViewHolder(holder, quizCardIndex);
            }
        }
    }

    private int getCategoryIndexForPosition(int position) {
        return position / (getItemsPerCategory() + 1);
    }

    private int getQuizCardIndexForPosition(int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        int itemsPerCategory = getItemsPerCategory();
        int categoryOffset = categoryIndex * (itemsPerCategory + 1);
        int quizCardIndex = position - categoryIndex - 1; // Subtract category index instead of offset
        return quizCardIndex;
    }

    @Override
    public int getItemCount() {
        return wildCards.length + getCategoryDividerCount();
    }
    private int getCategoryDividerCount() {
        int itemsPerCategory = getItemsPerCategory();
        return Math.max(categoryNames.length - 1, 0) * (itemsPerCategory > 0 ? itemsPerCategory : 1);
    }
    @Override
    public int getItemViewType(int position) {
        return isCategoryDividerPosition(position) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_QUIZ_CARD;
    }

    private boolean isCategoryDividerPosition(int position) {
        return position % (getItemsPerCategory() + 1) == 0;
    }

    private int getItemsPerCategory() {
        return wildCards.length / categoryNames.length;
    }

    public class CategoryViewHolder extends WildCardViewHolder {
        private final TextView categoryTextView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.text_category_title);
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