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
import java.util.HashMap;
import java.util.Map;

//TODO I still need to figure out why my quiz categories are not being hidden properly


public class QuizWildCardsAdapter extends WildCardsAdapter {
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_QUIZ_CARD = 2;
    private static final int ItemsPerCategory = 50;
    private final Map<Integer, CategoryVisibility> categoryVisibilityMap = new HashMap<>();
    private final String[] categoryNames = {"Science", "Geography", "History", "Art/Music", "Sport"};
    private final int[] bannerIdentifiers = {1, // Science
            2, // Geography
            3, // History
            4, // Art/Music
            5  // Sport
    };
    private final boolean[] categoryVisibility = new boolean[categoryNames.length];

    public QuizWildCardsAdapter(WildCardProperties[] quizWildCards, Context context, WildCardType mode) {
        super("QuizPrefs", quizWildCards, context, mode);
        Arrays.fill(categoryVisibility, true);
    }

    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_category, parent, false);
            int categoryIndex = getCategoryIndexForViewType(viewType);
            int bannerIdentifier = bannerIdentifiers[categoryIndex];
            return new CategoryViewHolder(categoryView, bannerIdentifier);
        } else {
            View quizCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    private int getCategoryIndexForViewType(int viewType) {
        return viewType - VIEW_TYPE_CATEGORY;
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
                visibleCount += 1; // Add the category itself
                if (categoryVisibility[i]) {
                    visibleCount += ItemsPerCategory; // Add the visible items under the category
                }
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

    private CategoryVisibility getCategoryVisibility(int categoryIndex) {
        if (!categoryVisibilityMap.containsKey(categoryIndex)) {
            categoryVisibilityMap.put(categoryIndex, new CategoryVisibility());
        }
        return categoryVisibilityMap.get(categoryIndex);
    }

    private int getCategoryIndexForBannerIdentifier(int bannerIdentifier) {
        for (int i = 0; i < bannerIdentifiers.length; i++) {
            if (bannerIdentifiers[i] == bannerIdentifier) {
                return i;
            }
        }
        return -1;
    }

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

    public class CategoryViewHolder extends WildCardViewHolder {
        private final TextView categoryTextView;

        public CategoryViewHolder(View itemView, int bannerIdentifier) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.text_category_title);
            View categoryBanner = itemView.findViewById(R.id.text_category_title);
            categoryBanner.setOnClickListener(v -> {
                toggleCategoryVisibility(bannerIdentifier);
            });
        }

        private void toggleCategoryVisibility(int bannerIdentifier) {
            int categoryIndex = getCategoryIndexForBannerIdentifier(bannerIdentifier);
            getCategoryVisibility(categoryIndex).toggleVisibility();
            notifyDataSetChanged();
            String bannerStatus = getCategoryVisibility(categoryIndex).isVisible() ? "Visible" : "Hidden";
            Log.d("QuizWildCardsAdapter", "Clicked on banner: " + bannerIdentifier + ", Status: " + bannerStatus);
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
