package com.example.countingdowngame.endGame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;

import java.util.List;

public class EndGameListAdapter extends RecyclerView.Adapter<EndGameListAdapter.PreviousNumbersViewHolder> {

    private List<String> items;

    public EndGameListAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PreviousNumbersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card, parent, false);
        return new PreviousNumbersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousNumbersViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class PreviousNumbersViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItem;

        PreviousNumbersViewHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.statsItemView);
        }

        void bind(String item) {
            tvItem.setText(item);
        }
    }
}
