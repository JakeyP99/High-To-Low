package com.example.countingdowngame;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class QuizWildCardsAdapter extends WildCardsAdapter {
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_QUIZ_CARD = 2;
    private final String[] categoryNames = {"Science", "Geography", "History", "Art/Music"};
    private final HashMap<String, Integer> wildcardsPerCategory = new HashMap<>();

    public QuizWildCardsAdapter(WildCardHeadings[] quizWildCards, Context context, WildCardType mode) {
        super("QuizPrefs", quizWildCards, context, mode);
        calculateWildcardsPerCategory();
    }

    private void calculateWildcardsPerCategory() {
        for (String categoryName : categoryNames) {
            int count = 0;
            for (WildCardHeadings wildCard : wildCards) {
                if (wildCard.getCategory().equals(categoryName)) {
                    count++;
                }
            }
            wildcardsPerCategory.put(categoryName, count);
        }
    }

    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_CATEGORY) {
            View categoryView = inflater.inflate(R.layout.item_quiz_category, parent, false);
            return new CategoryViewHolder(categoryView);
        } else {
            View quizCardView = inflater.inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            int categoryIndex = getCategoryIndexForPosition(position);
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                ((CategoryViewHolder) holder).bind(categoryNames[categoryIndex]);

                int itemsPerCategory = getItemsPerCategory(categoryNames[categoryIndex]);
                int categoryStartIndex = categoryIndex * (itemsPerCategory + 1);
                int categoryEndIndex = categoryStartIndex + itemsPerCategory;

                int scienceQuestionsCount = getQuestionsCountForCategory("Science");
                Log.d("QuizWildCardsAdapter", "Number of Science Questions: " + scienceQuestionsCount);

                int geographyQuestionsCount = getQuestionsCountForCategory("Geography");
                Log.d("QuizWildCardsAdapter", "Number of Geography Questions: " + geographyQuestionsCount);

                int historyQuestionsCount = getQuestionsCountForCategory("History");
                Log.d("QuizWildCardsAdapter", "Number of History Questions: " + historyQuestionsCount);

                int artMusicQuestionsCount = getQuestionsCountForCategory("Art/Music");
                Log.d("QuizWildCardsAdapter", "Number of Art/Music Questions: " + artMusicQuestionsCount);

                int actualCardsInCategory = Math.min(itemsPerCategory, wildCards.length - categoryStartIndex);
                Log.d("QuizWildCardsAdapter", "Category: " + categoryNames[categoryIndex] +
                        ", Cards in Category: " + actualCardsInCategory +
                        ", Displayed under banner: " + (categoryEndIndex - categoryStartIndex));
            }
        } else {
            int quizCardIndex = getQuizCardIndexForPosition(position);
            if (quizCardIndex >= 0 && quizCardIndex < wildCards.length) {
                super.onBindViewHolder(holder, quizCardIndex);
            }
        }
    }

    private int getCategoryIndexForPosition(int position) {
        int itemsPerCategory = getItemsPerCategory(categoryNames[0]); // Just pass any valid category name, e.g., the first one
        return position / (itemsPerCategory + 1);
    }



    private int getQuizCardIndexForPosition(int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        int itemsPerCategory = getItemsPerCategory(categoryNames[categoryIndex]);
        int categoryStartIndex = categoryIndex * (itemsPerCategory + 1);
        return position - categoryStartIndex - 1;
    }


    @Override
    public int getItemCount() {
        return wildCards.length + getCategoryDividerCount();
    }


    private int getCategoryDividerCount() {
        int totalCategoryDividers = 0;
        for (String categoryName : categoryNames) {
            int itemsPerCategory = getItemsPerCategory(categoryName);
            totalCategoryDividers += Math.max(itemsPerCategory, 0);
        }
        return totalCategoryDividers;
    }


    @Override
    public int getItemViewType(int position) {
        return isCategoryDividerPosition(position) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_QUIZ_CARD;
    }

    private boolean isCategoryDividerPosition(int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        String categoryName = categoryNames[categoryIndex];
        int itemsPerCategory = getItemsPerCategory(categoryName);
        int totalCategoryDividers = getCategoryDividerCount();
        int itemCount = wildCards.length + totalCategoryDividers;
        return position < itemCount && position % (itemsPerCategory + 1) == 0;
    }



    private int getItemsPerCategory(String categoryName) {
        Integer count = wildcardsPerCategory.get(categoryName);
        return count != null ? count : 0;
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

    private int getQuestionsCountForCategory(String categoryName) {
        int count = 0;
        for (WildCardHeadings wildCard : wildCards) {
            if (wildCard.getCategory().equals(categoryName)) {
                count++;
            }
        }
        return count;
    }



}
