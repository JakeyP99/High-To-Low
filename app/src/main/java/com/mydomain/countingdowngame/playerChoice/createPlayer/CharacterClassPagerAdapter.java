package com.mydomain.countingdowngame.playerChoice.createPlayer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CharacterClassPagerAdapter extends PagerAdapter {

    private final List<List<CharacterClassStore>> characterClassesPages;
    private boolean isInfinite = false;

    public CharacterClassPagerAdapter(List<List<CharacterClassStore>> characterClassesPages) {
        this.characterClassesPages = characterClassesPages;
    }

    public void setInfinite(boolean infinite) {
        this.isInfinite = infinite;
    }

    @Override
    public int getCount() {
        if (characterClassesPages.isEmpty()) return 0;
        return isInfinite ? Integer.MAX_VALUE : characterClassesPages.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position % characterClassesPages.size();
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        CharacterClassAdapter adapter = new CharacterClassAdapter(characterClassesPages.get(realPosition));
        recyclerView.setAdapter(adapter);

        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
