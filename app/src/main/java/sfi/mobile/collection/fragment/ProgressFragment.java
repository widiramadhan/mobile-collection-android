package sfi.mobile.collection.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.PageProgressAdapter;
import sfi.mobile.collection.adapter.PageTaskAdapter;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.util.HttpsTrustManager;


public class ProgressFragment extends Fragment {

    private static final String TAG = ProgressFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    protected Cursor cursor;
    DBHelper dbhelper;

    ProgressDialog pDialog;

    public ProgressFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new PageProgressAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        dbhelper = new DBHelper(getActivity());

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String flag = arguments.getString("flag");
        /*** end Get parameter dari halaman sebelumnya ***/

        if(flag.equals("0")) {
            viewPager.setCurrentItem(0);
        }else{
            viewPager.setCurrentItem(1);
        }

        return view;
    }
}
