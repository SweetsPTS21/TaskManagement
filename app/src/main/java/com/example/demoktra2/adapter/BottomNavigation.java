package com.example.demoktra2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.demoktra2.fragment.FragmentInfo;
import com.example.demoktra2.fragment.FragmentList;
import com.example.demoktra2.fragment.FragmentSearch;

public class BottomNavigation extends FragmentPagerAdapter {

    public BottomNavigation(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new FragmentList();
            case 1:return new FragmentSearch();
            case 2:return new FragmentInfo();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
