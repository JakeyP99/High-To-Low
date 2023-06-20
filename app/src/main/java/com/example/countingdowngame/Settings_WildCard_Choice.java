package com.example.countingdowngame;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Settings_WildCard_Choice extends ButtonUtilsActivity {
    private ViewPager viewPager;

    private Settings_WildCard_Adapter.WildCardsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard_page1);
        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new Settings_WildCard_Adapter.WildCardsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}

