package sfi.mobile.collection.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sfi.mobile.collection.fragment.TabPriority;
import sfi.mobile.collection.fragment.TabNormal;


public class PageTaskAdapter extends FragmentPagerAdapter{

    private static final String TAG = PageTaskAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public PageTaskAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TabPriority();
            case 1:
                return new TabNormal();
            /*case 2:
                return new DashboardTabStatus();*/
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
                return "All Data";
            /*case 2:
                return "Status";*/
        }
        return null;
    }
}
