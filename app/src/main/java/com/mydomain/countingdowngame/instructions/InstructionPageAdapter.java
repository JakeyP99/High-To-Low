package com.mydomain.countingdowngame.instructions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mydomain.countingdowngame.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class InstructionPageAdapter extends RecyclerView.Adapter<InstructionPageAdapter.ViewHolder> {
    private final List<InstructionStep> steps;

    public InstructionPageAdapter(List<InstructionStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instruction_page_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InstructionStep step = steps.get(position);
        holder.titleView.setText(step.getTitle());
        holder.descriptionView.setText(step.getDescriptionResId());
        holder.visualView.setImageResource(step.getImageResId());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final GifImageView visualView;
        final TextView titleView;
        final TextView descriptionView;

        ViewHolder(View view) {
            super(view);
            visualView = view.findViewById(R.id.instructionVisual);
            titleView = view.findViewById(R.id.instructionTitle);
            descriptionView = view.findViewById(R.id.instructionDescription);
        }
    }
}
