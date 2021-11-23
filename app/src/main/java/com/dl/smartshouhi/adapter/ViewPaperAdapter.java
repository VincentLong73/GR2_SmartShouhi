package com.dl.smartshouhi.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dl.smartshouhi.fragment.HomeFragment1;
import com.dl.smartshouhi.fragment.TestHomeFragment;
import com.dl.smartshouhi.fragment.TestInfoFragment;
import com.dl.smartshouhi.fragment.TestPersonFragment;

public class ViewPaperAdapter extends FragmentStateAdapter {


    public ViewPaperAdapter( FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment1();
            case 1:
                return new TestPersonFragment();
            case 2:
                return new TestInfoFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
