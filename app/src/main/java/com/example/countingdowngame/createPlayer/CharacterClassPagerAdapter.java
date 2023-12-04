package com.example.countingdowngame.createPlayer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CharacterClassPagerAdapter extends PagerAdapter {
    private final List<List<CharacterClassStore>> characterClassesPages;
    private CharacterClassAdapter.OnArrowClickListener arrowClickListener;

    public CharacterClassPagerAdapter(List<List<CharacterClassStore>> characterClassesPages) {
        this.characterClassesPages = characterClassesPages;
    }

    public void setArrowClickListener(CharacterClassAdapter.OnArrowClickListener listener) {
        this.arrowClickListener = listener;
    }

    @Override
    public int getCount() {
        return characterClassesPages.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        CharacterClassAdapter adapter = new CharacterClassAdapter(characterClassesPages.get(position));
        recyclerView.setAdapter(adapter);


        adapter.setOnArrowClickListener(new CharacterClassAdapter.OnArrowClickListener() {
            @Override
            public void onLeftArrowClick() {
                if (arrowClickListener != null) {
                    arrowClickListener.onLeftArrowClick();
                }
            }


            @Override
            public void onRightArrowClick() {
                if (arrowClickListener != null) {
                    arrowClickListener.onRightArrowClick();
                }
            }
        });

        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
