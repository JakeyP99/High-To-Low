package com.example.countingdowngame.mainActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;

import java.util.List;

public class RouletteAdapter extends RecyclerView.Adapter<RouletteAdapter.VH> {
    private final List<Integer> numbers;
    private final List<Integer> classNumbers;

    public RouletteAdapter(List<Integer> numbers, List<Integer> classNumbers) {
        this.numbers = numbers;
        this.classNumbers = classNumbers;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roulette_number, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        int num = numbers.get(position);
        String numStr = String.valueOf(num);
        holder.tv.setText(numStr);

        // Adjust text size based on length of number to fit 9 digits
        if (numStr.length() > 6) {
            holder.tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 28);
        } else if (numStr.length() > 4) {
            holder.tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 35);
        } else {
            holder.tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 45);
        }

        if (classNumbers.contains(num)) {
            holder.tv.setTextColor(Color.YELLOW);
        } else {
            holder.tv.setTextColor(Color.parseColor("#021457")); // bluedark
        }
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) {
            super(v);
            tv = v.findViewById(R.id.text_number);
        }
    }
}
