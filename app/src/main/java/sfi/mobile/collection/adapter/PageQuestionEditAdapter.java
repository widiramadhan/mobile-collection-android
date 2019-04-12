package sfi.mobile.collection.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sfi.mobile.collection.fragment.TabCustomerInfo;
import sfi.mobile.collection.fragment.TabQuestionEdit;

public class PageQuestionEditAdapter extends FragmentPagerAdapter {

    private static final String TAG = PageQuestionEditAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 2;
    public PageQuestionEditAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TabQuestionEdit();
            /*case 1:
                return new QuestionCustResult();*/
            case 1:
                return new TabCustomerInfo();
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
                return "Collection Form";
            /*case 1:
                return "Visit Result";*/
            case 1:
                return "Customer Info";
        }
        return null;
    }
}
