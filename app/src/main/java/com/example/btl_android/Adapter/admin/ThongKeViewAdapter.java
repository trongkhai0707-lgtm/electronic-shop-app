package com.example.btl_android.Adapter.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.btl_android.Fragment.admin.DanhThuFragment;
import com.example.btl_android.Fragment.admin.DonHangFragment;
import com.example.btl_android.Fragment.admin.TonKhoFragment;

public class ThongKeViewAdapter extends FragmentStatePagerAdapter {
    public ThongKeViewAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DanhThuFragment();
            case 1:
                return new TonKhoFragment();
            case 2:
                return new DonHangFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Báo cáo danh thu";
                break;
            case 1:
                title = "Tồn kho";
                break;
            case 2:
                title = "Đơn hàng";
                break;
        }
        return title;
    }
}
