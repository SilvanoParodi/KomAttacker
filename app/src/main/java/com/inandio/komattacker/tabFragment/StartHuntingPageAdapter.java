package com.inandio.komattacker.tabFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by eme on 11/1/2015.
 */
public class StartHuntingPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    StartHunting_MainTab fragmentStartHunting;
    StartHunting_MapTab fragmentMap;
    StartHunting_DetailsTab fragmentDetails;

    public StartHuntingPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                fragmentStartHunting = new StartHunting_MainTab();
                return getFragmentStartHunting();
            case 1:
                fragmentMap = new StartHunting_MapTab();
                return fragmentMap;
            case 2:
                fragmentDetails = new StartHunting_DetailsTab();
                return fragmentDetails;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public StartHunting_MainTab getFragmentStartHunting() {
        return fragmentStartHunting;
    }

    public StartHunting_MapTab getFragmentMap() {
        return fragmentMap;
    }
}
