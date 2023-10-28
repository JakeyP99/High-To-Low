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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QuizWildCardsAdapter extends WildCardsAdapter {
    // Constants for view types
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_QUIZ_CARD = 2;
    private static final int ItemsPerCategory = 50;

    // Map to track visibility of quiz categories
    private final Map<Integer, CategoryVisibility> categoryVisibilityMap = new HashMap();
    private final Map<String, ArrayList<WildCardProperties>> groupedWildCardProps = new HashMap();

    // Array of category names
    private final String[] categoryNames = {"Science", "Geography", "History", "Art/Music", "Sport", "Movies", "Video Games"};


    // Array to store the visibility status of categories
    private final boolean[] categoryVisibility = new boolean[categoryNames.length];

    // Constructor
    public QuizWildCardsAdapter(WildCardProperties[] quizWildCards, Context context, WildCardType mode) {
        super("QuizPrefs", quizWildCards, context, mode);
        // Initialize the categoryVisibility array with 'true'
        Arrays.fill(categoryVisibility, true);
        for (WildCardProperties props : quizWildCards) {
            var existing = groupedWildCardProps.get(props.getCategory());
            ArrayList<WildCardProperties> result;
            if (existing != null) {
                result = existing;
            } else {
                result = new ArrayList();
            }
            result.add(props);
            groupedWildCardProps.put(props.getCategory(), result);
        }
    }

    // Create a ViewHolder for a specific view type
    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            // Inflate the layout for a category view
            View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_category, parent, false);
            int categoryIndex = getCategoryIndexForViewType(viewType);
            return new CategoryViewHolder(categoryView, categoryIndex);
        } else {
            // Inflate the layout for a quiz card view
            View quizCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    // Get the category index for a given view type
    private int getCategoryIndexForViewType(int viewType) {
        return viewType - VIEW_TYPE_CATEGORY;
    }

    // Bind data to a ViewHolder and handle user interactions
    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        if (holder instanceof CategoryViewHolder) {
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                // Bind category data to a CategoryViewHolder
                ((CategoryViewHolder) holder).bind(categoryNames[categoryIndex]);
            }
        } else {
            int quizCardIndex = getQuizCardIndexForPosition(position);
            if (quizCardIndex >= 0 && quizCardIndex < wildCards.length) {
                // Bind quiz card data to a QuizWildCardViewHolder and handle visibility
                WildCardProperties wildCard = wildCards[quizCardIndex];
                holder.bind(wildCard);
                int positionWithOneBasedIndex = quizCardIndex + 1;
                boolean isVisible = getCategoryVisibility(categoryIndex).isVisible();
                holder.itemView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = isVisible ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
                holder.itemView.setLayoutParams(params);
                holder.itemView.setOnClickListener(v -> {
                    Log.d("QuizWildCardsAdapter", "Tapped on wildcard: " + wildCard.getText() + " (Category: " + wildCard.getCategory() + "Position: " + positionWithOneBasedIndex + ")");
                });
            } else {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                holder.itemView.setLayoutParams(params);
            }
        }
    }

    // Get the category index for a given position
    private int getCategoryIndexForPosition(int position) {
        return position / (ItemsPerCategory + 1);
    }

    // Get the quiz card index for a given position
    private int getQuizCardIndexForPosition(int position) {
        int categoryIndex = getCategoryIndexForPosition(position);
        return position - categoryIndex - 1;
    }

    // Get the total item count (including categories)
    @Override
    public int getItemCount() {
        int visibleCount = 0;
        for (int i = 0; i < categoryNames.length; i++) {
            if (categoryVisibility[i]) {
                visibleCount += 1; // Add the category itself
                if (categoryVisibility[i]) {
                    visibleCount += ItemsPerCategory; // Add the visible items under the category
                }
            }
        }
        return visibleCount;
    }

    // Get the view type for a given position
    @Override
    public int getItemViewType(int position) {
        return isCategoryDividerPosition(position) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_QUIZ_CARD;
    }

    // Check if a position is a category divider
    private boolean isCategoryDividerPosition(int position) {
        return position % (ItemsPerCategory + 1) == 0;
    }

    // Get the visibility status for a category
    private CategoryVisibility getCategoryVisibility(int categoryIndex) {
        if (!categoryVisibilityMap.containsKey(categoryIndex)) {
            categoryVisibilityMap.put(categoryIndex, new CategoryVisibility());
        }
        return categoryVisibilityMap.get(categoryIndex);
    }

    // Class to represent the visibility status of a category
    public static class CategoryVisibility {
        private boolean isVisible;

        public CategoryVisibility() {
            this.isVisible = true;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void toggleVisibility() {
            isVisible = !isVisible;
        }
    }

    // ViewHolder for category views
    public class CategoryViewHolder extends WildCardViewHolder {
        private final TextView categoryTextView;
        private int categoryIndex;

        public CategoryViewHolder(View itemView, int _categoryIndex) {
            super(itemView);
            categoryIndex = _categoryIndex;
            categoryTextView = itemView.findViewById(R.id.text_category_title);
            View categoryBanner = itemView.findViewById(R.id.text_category_title);
            categoryBanner.setOnClickListener(v -> {
                toggleCategoryVisibility();
            });
        }

        // Toggle the visibility of a category and refresh the adapter
        private void toggleCategoryVisibility() {
            getCategoryVisibility(categoryIndex).toggleVisibility();
            notifyDataSetChanged();
        }

        // Bind category title to the view
        public void bind(String categoryTitle) {
            categoryTextView.setText(categoryTitle);
        }
    }

    // ViewHolder for quiz card views
    public class QuizWildCardViewHolder extends WildCardViewHolder {
        public QuizWildCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
