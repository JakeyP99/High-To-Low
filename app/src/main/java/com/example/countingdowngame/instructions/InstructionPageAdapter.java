package com.example.countingdowngame.instructions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.countingdowngame.R;

import java.util.List;

public class InstructionPageAdapter extends PagerAdapter {
  private final List<Integer> instructions;

  public InstructionPageAdapter(List<Integer> instructions) {
    this.instructions = instructions;
  }

  @Override
  public int getCount() {
    return instructions.size();
  }

  @NonNull
  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    LayoutInflater inflater = LayoutInflater.from(container.getContext());
    View view = inflater.inflate(R.layout.c1_instructions_layout, container, false);
    TextView textView = view.findViewById(R.id.info_text);
    textView.setText(instructions.get(position));
    container.addView(view);
    return view;
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }
}