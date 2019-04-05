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

import sfi.mobile.collection.adapter.PageQuestionAdapter;
import sfi.mobile.collection.helper.DBHelper;

public class CollectionTaskFragment extends Fragment {

    private static final String TAG = CollectionTaskFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ProgressDialog pDialog;

    protected Cursor cursor;
    DBHelper dbhelper;

    TextView contract_id;

    public CollectionTaskFragment() {


    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_task, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs_2);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager_2);

        viewPager.setAdapter(new PageQuestionAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        dbhelper = new DBHelper(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        TextView contractID = (TextView) getActivity().findViewById(R.id.contract_id);
        contractID.setText(paramId);
        /*** end Get parameter dari halaman sebelumnya ***/
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
