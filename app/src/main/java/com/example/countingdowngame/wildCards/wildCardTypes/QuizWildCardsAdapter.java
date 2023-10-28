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

    // Map to track visibility of quiz categories
    private final Map<String, CategoryVisibility> categoryVisibilityMap = new HashMap();
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
            return new CategoryViewHolder(categoryView, categoryIndex);
        } else {
            // Inflate the layout for a quiz card view
            View quizCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    // Bind data to a ViewHolder and handle user interactions
    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {

        var categoryOffset = getPositionCategoryOffset(position);
        int categoryIndex = categoryOffset.getIndex();
        String category = categoryNames[categoryIndex];
        if (holder instanceof CategoryViewHolder) {
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                // Bind category data to a CategoryViewHolder
                ((CategoryViewHolder) holder).bind(categoryNames[categoryIndex]);
            }
        } else {
            int quizCardIndex = position - categoryOffset.getOffset();
            if (quizCardIndex >= 0 && quizCardIndex < wildCards.length) {
                // Bind quiz card data to a QuizWildCardViewHolder and handle visibility
                WildCardProperties wildCard = wildCards[quizCardIndex];
                holder.bind(wildCard);
                int positionWithOneBasedIndex = quizCardIndex + 1;
                boolean isVisible = getCategoryVisibility(category).isVisible();
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

    // Get the total item count (including categories)
    @Override
    public int getItemCount() {
        int visibleCount = 0;
        for (String category : categoryNames) {
            if (getCategoryVisibility(category).isVisible()) {
                visibleCount += getCategoryProps(category).size();
            }
        }
        return visibleCount;
    }

    private CategoryOffset getPositionCategoryOffset(int position) {
        int offset = 0;
        int index = 0;
        for (String cursor : categoryNames) {
            var count = getCategoryProps(cursor).size();
            if (position > count + offset) {
                index += 1;
                offset += count;
            } else {
                break;
            }
        }
        return new CategoryOffset(index, offset);
    }

    private class CategoryOffset {
        private int offset;
        private int index;

        public CategoryOffset(int index, int offset) {
            this.offset = offset;
            this.index = index;
        }

        public int getOffset() {
            return offset;
        }

        public int getIndex() {
            return index;
        }
    }

    // Get the view type for a given position
    @Override
    public int getItemViewType(int position) {
        return position - getPositionCategoryOffset(position).getOffset() == 0 ? VIEW_TYPE_CATEGORY : VIEW_TYPE_QUIZ_CARD;
    }

    // Get the visibility status for a category
    private CategoryVisibility getCategoryVisibility(String category) {
        if (!categoryVisibilityMap.containsKey(category)) {
            categoryVisibilityMap.put(category, new CategoryVisibility());
        }
        return categoryVisibilityMap.get(category);
    }

    private ArrayList<WildCardProperties> getCategoryProps(String category) {
        if (!groupedWildCardProps.containsKey(category)) {
            groupedWildCardProps.put(category, new ArrayList<>());
        }
        return groupedWildCardProps.get(category);
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
        private String category;

        public CategoryViewHolder(View itemView, String _category) {
            super(itemView);
            category = _category;
            categoryTextView = itemView.findViewById(R.id.text_category_title);
            View categoryBanner = itemView.findViewById(R.id.text_category_title);
            categoryBanner.setOnClickListener(v -> {
                toggleCategoryVisibility();
            });
        }

        // Toggle the visibility of a category and refresh the adapter
        private void toggleCategoryVisibility() {
            getCategoryVisibility(category).toggleVisibility();
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
