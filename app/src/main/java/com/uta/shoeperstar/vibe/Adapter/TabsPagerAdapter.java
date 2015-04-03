package com.uta.shoeperstar.vibe.Adapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.uta.shoeperstar.vibe.Fragment.DashboardFragment;
import com.uta.shoeperstar.vibe.Fragment.DataVisualizationFragment;
import com.uta.shoeperstar.vibe.Fragment.MapViewFragment;
import com.uta.shoeperstar.vibe.Fragment.SettingsFragment;

/**
 * Created by zedd on 2/13/15.
 */



/**
 * Created by zedd on 2/7/15.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES = { "Dashboard", "Map", "Settings", "Data Visualization" };

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new MapViewFragment();
            case 2:
                return new SettingsFragment();
            case 3:
                return new DataVisualizationFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

}