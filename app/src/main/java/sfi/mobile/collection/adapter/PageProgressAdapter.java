package sfi.mobile.collection.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sfi.mobile.collection.fragment.TabDone;
import sfi.mobile.collection.fragment.TabDraft;
import sfi.mobile.collection.fragment.TabDraftWithDelete;


public class PageProgressAdapter extends FragmentPagerAdapter{

    private static final String TAG = PageProgressAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public  PageProgressAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TabDraft();
            case 1:
                return new TabDone();
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
                return "Draft";
            case 1:
                return "Done";
        }
        return null;
    }
}
