package sfi.mobile.collection.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sfi.mobile.collection.fragment.DashboardTabPriority;
import sfi.mobile.collection.fragment.DashboardTabStatus;
import sfi.mobile.collection.fragment.DashboardTabTasklist;


public class CustomFragmentPageAdapter extends FragmentPagerAdapter{

    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 3;

    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new DashboardTabPriority();
            case 1:
                return new DashboardTabTasklist();
            case 2:
                return new DashboardTabStatus();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Priority";
            case 1:
                return "Tasklist";
            case 2:
                return "Status";
        }
        return null;
    }
}
