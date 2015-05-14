package com.uta.shoeperstar.vibe.Adapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.material.widget.TabIndicator;
import com.uta.shoeperstar.vibe.Fragment.DashboardFragment;
import com.uta.shoeperstar.vibe.Fragment.DataVisualizationFragment;
import com.uta.shoeperstar.vibe.Fragment.MapViewFragment;
import com.uta.shoeperstar.vibe.Fragment.PaceFragment;
import com.uta.shoeperstar.vibe.Fragment.SettingsFragment;

/**
 * Created by zedd on 2/13/15.
 */



/**
 * Created by zedd on 2/7/15.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter implements TabIndicator.TabTextProvider{

    private static DashboardFragment dashboardFragment= null;
    private static MapViewFragment mapViewFragment = null;
    private static SettingsFragment settingsFragment = null;
    private static DataVisualizationFragment dataVisualizationFragment = null;
    private static PaceFragment paceFragment = null;

    private final String[] TITLES = { "Home", "Map", "Settings", "Stats", "Pace" };

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                if(dashboardFragment == null){
                    dashboardFragment = new DashboardFragment();
                }
                return dashboardFragment;
            case 1:
                if(mapViewFragment == null) {
                    mapViewFragment = new MapViewFragment();
                }
                return mapViewFragment;
            case 2:
                if(settingsFragment == null){
                    settingsFragment = new SettingsFragment();
                }
                return settingsFragment;
            case 3:
                if(dataVisualizationFragment == null){
                    dataVisualizationFragment = new DataVisualizationFragment();
                }
                return dataVisualizationFragment;
            case 4:
                if(paceFragment == null) {
                    paceFragment =  new PaceFragment();
                }
                return paceFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return TITLES.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public String getText(int position) {
        return TITLES[position].toUpperCase();
    }
}