package com.example.countingdowngame.createPlayer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CharacterClassPagerAdapter extends PagerAdapter {
    private int pageNumber;

    private final List<List<CharacterClassStore>> characterClassesPages;

    public CharacterClassPagerAdapter(List<List<CharacterClassStore>> characterClassesPages) {
        this.characterClassesPages = characterClassesPages;
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

        // Update selected item in the adapter when clicked
        adapter.setOnItemSelectedListener(selectedPosition -> {
            pageNumber = position + 1; // Increment by 1 to display the correct page number
//            logSelectedItems(adapter, pageNumber);
            notifyDataSetChanged();
        });

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

    public int getPageNumber() {
        return pageNumber;
    }

//    private void logSelectedItems(CharacterClassAdapter adapter, int pageNumber) {
//        int selectedPosition = adapter.getSelectedItemPosition();
//        List<CharacterClassStore> characterClasses = adapter.getCharacterClasses();
//
//        for (int i = 0; i < characterClasses.size(); i++) {
//            if (i == selectedPosition) {
//                CharacterClassStore selectedCharacter = characterClasses.get(i);
//                if (selectedCharacter != null) {
//                    Log.d("CharacterClassPagerAdapter", "Page " + pageNumber +
//                            " - Item at position " + i + " is SELECTED: " + selectedCharacter.getClassName());
//                }
//            } else {
//                Log.d("CharacterClassPagerAdapter", "Page " + pageNumber +
//                        " - Item at position " + i + " is NOT SELECTED: " + characterClasses.get(i).getClassName());
//            }
//        }
//    }
}
