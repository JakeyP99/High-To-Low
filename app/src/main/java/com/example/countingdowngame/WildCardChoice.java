package com.example.countingdowngame;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class WildCardChoice extends ButtonUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard_tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        Settings_WildCard_Adapter.WildCardsPagerAdapter pagerAdapter = new Settings_WildCard_Adapter.WildCardsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}

