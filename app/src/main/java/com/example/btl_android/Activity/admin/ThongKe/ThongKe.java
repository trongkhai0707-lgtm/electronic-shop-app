package com.example.btl_android.Activity.admin.ThongKe;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.btl_android.Adapter.admin.ThongKeViewAdapter;
import com.example.btl_android.R;
import com.google.android.material.tabs.TabLayout;

public class ThongKe extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thongke);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pagertk);
        btnBack = findViewById(R.id.btn_back);

        ThongKeViewAdapter adapter = new ThongKeViewAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        btnBack.setOnClickListener(v -> finish());

    }
}