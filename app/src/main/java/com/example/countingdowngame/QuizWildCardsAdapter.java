package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

   public class QuizWildCardsAdapter extends WildCardsAdapter {
       public QuizWildCardsAdapter(WildCardHeadings[] quizWildCards, Context context, WildCardType mode) {
            super("QuizPrefs", quizWildCards, context, mode);
       }
        @NonNull
        @Override
        public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
            return new com.example.countingdowngame.QuizWildCardsAdapter.QuizWildCardViewHolder(view);
        }
       public class QuizWildCardViewHolder extends WildCardViewHolder {
           public QuizWildCardViewHolder(View itemView) {
               super(itemView);
           }

        }
    }