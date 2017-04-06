package com.example.android.motiondetect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alex on 4/5/2017.
 */

public class Pager extends FragmentStatePagerAdapter {

    private int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                CameraListActivityFragment tab1 = new CameraListActivityFragment();
                return tab1;
            case 1:
                SavedVideosFragment tab2 = new SavedVideosFragment();
                return tab2;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
