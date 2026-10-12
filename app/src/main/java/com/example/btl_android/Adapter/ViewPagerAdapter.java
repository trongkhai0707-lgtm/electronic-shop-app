package com.example.btl_android.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.btl_android.Fragment.HomeFragment;
import com.example.btl_android.Fragment.LaptopFragment;
import com.example.btl_android.Fragment.PhoneFragment;
import com.example.btl_android.Fragment.ComputerFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new PhoneFragment();
            case 2:
                return new LaptopFragment();
            case 3:
                return new ComputerFragment();
            default:
                return new HomeFragment();
        }
    }
}
