package com.example.hciproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class pagerAdapter extends FragmentStateAdapter {

    public pagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new assignmentsFragment();
            case 1:
                return new homeFragment();
            case 2:
                return new timetableFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
