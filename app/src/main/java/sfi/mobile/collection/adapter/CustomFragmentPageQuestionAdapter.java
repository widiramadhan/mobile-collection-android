package sfi.mobile.collection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sfi.mobile.collection.fragment.QuestionCustInfo;
import sfi.mobile.collection.fragment.QuestionCustResult;
import sfi.mobile.collection.fragment.QuestionCustUpdate;

public class CustomFragmentPageQuestionAdapter extends FragmentPagerAdapter {

    private static final String TAG = CustomFragmentPageQuestionAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public CustomFragmentPageQuestionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new QuestionCustUpdate();
            /*case 1:
                return new QuestionCustResult();*/
            case 1:
                return new QuestionCustInfo();
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
