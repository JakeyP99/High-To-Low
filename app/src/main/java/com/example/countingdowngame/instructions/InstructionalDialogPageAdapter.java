package com.example.countingdowngame.instructions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class InstructionalDialogPageAdapter extends PagerAdapter {
    private final List<Integer> layoutResIds;

    public InstructionalDialogPageAdapter(List<Integer> layoutResIds) {
        this.layoutResIds = layoutResIds;
    }

    @Override
    public int getCount() {
        return layoutResIds.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(layoutResIds.get(position), container, false);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
