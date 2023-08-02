package com.example.countingdowngame.wildCards.wildCardTypes.quizWildCardCategories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.WildCardProperties;

import java.util.List;

public class WildCardGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BANNER = 1;
    private static final int VIEW_TYPE_WILDCARD = 2;

    private Context context;
    private List<WildCardGroup> wildCardGroups;

    public WildCardGroupsAdapter(Context context, List<WildCardGroup> wildCardGroups) {
        this.context = context;
        this.wildCardGroups = wildCardGroups;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BANNER) {
            View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_category, parent, false);
            return new BannerViewHolder(bannerView);
        } else {
            View quizCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new QuizWildCardViewHolder(quizCardView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BannerViewHolder) {
            BannerViewHolder bannerHolder = (BannerViewHolder) holder;
            WildCardGroup wildCardGroup = wildCardGroups.get(position);

            bannerHolder.categoryTextView.setText(wildCardGroup.getCategory());

            // Handle click events for the banners
            bannerHolder.itemView.setOnClickListener(v -> {
                wildCardGroup.setExpanded(!wildCardGroup.isExpanded());
                notifyItemRangeChanged(position + 1, wildCardGroup.getWildCards().size());
            });

        } else if (holder instanceof QuizWildCardViewHolder) {
            QuizWildCardViewHolder quizCardHolder = (QuizWildCardViewHolder) holder;
            WildCardGroup wildCardGroup = wildCardGroups.get(getBannerPosition(position));
            WildCardProperties wildCard = wildCardGroup.getWildCards().get(getCardPosition(position));

            quizCardHolder.bind(wildCard);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (WildCardGroup group : wildCardGroups) {
            count += group.isExpanded() ? group.getWildCards().size() + 1 : 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return isBannerPosition(position) ? VIEW_TYPE_BANNER : VIEW_TYPE_WILDCARD;
    }

    private boolean isBannerPosition(int position) {
        int bannerPosition = 0;
        for (WildCardGroup group : wildCardGroups) {
            if (position == bannerPosition) {
                return true;
            }
            bannerPosition += group.isExpanded() ? group.getWildCards().size() + 1 : 1;
        }
        return false;
    }

    private int getBannerPosition(int position) {
        int bannerPosition = 0;
        for (WildCardGroup group : wildCardGroups) {
            if (position == bannerPosition) {
                return bannerPosition;
            }
            bannerPosition += group.isExpanded() ? group.getWildCards().size() + 1 : 1;
        }
        return -1; // Not found
    }

    private int getCardPosition(int position) {
        int bannerPosition = getBannerPosition(position);
        if (bannerPosition != -1) {
            WildCardGroup wildCardGroup = wildCardGroups.get(bannerPosition);
            return position - bannerPosition - 1;
        }
        return -1; // Not found
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTextView;

        public BannerViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.text_category_title);
        }
    }

    public class QuizWildCardViewHolder extends RecyclerView.ViewHolder {
        public QuizWildCardViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(WildCardProperties wildCard) {
            // Bind the wildCard data to the views in the QuizWildCardViewHolder
        }
    }
}
