package com.example.countingdowngame.wildCards;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.ButtonUtilsActivity;
import com.example.countingdowngame.R;
import com.google.android.material.tabs.TabLayout;

public class WildCardChoice extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard_tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        WildCardView.WildCardsPagerAdapter pagerAdapter = new WildCardView.WildCardsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}

