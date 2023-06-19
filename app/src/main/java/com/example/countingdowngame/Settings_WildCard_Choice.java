package com.example.countingdowngame;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Settings_WildCard_Choice extends ButtonUtilsActivity {
    private ViewPager viewPager;

    private Settings_WildCard_Adapter.WildCardsPagerAdapter pagerAdapter; // Declare the pagerAdapter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard_page1);
        // Initialize ViewPager and Adapter
        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new Settings_WildCard_Adapter.WildCardsPagerAdapter(getSupportFragmentManager());
        // Set Adapter to ViewPager
        viewPager.setAdapter(pagerAdapter);
        // Setup TabLayout with ViewPager
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }




}

