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
import sfi.mobile.collection.adapter.PageTaskAdapter;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.util.HttpsTrustManager;


public class TaskFragment extends Fragment {

    private static final String TAG = TaskFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView txtLatitude, txtLongitude;

    protected Cursor cursor;
    DBHelper dbhelper;

    ProgressDialog pDialog;

    public TaskFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new PageTaskAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        dbhelper = new DBHelper(getActivity());

        /*//cek koneksi
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info == null){ //jika tidak ada koneksi

        }else { //jika ada koneksi
            //cek data ada atau tidak
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            //cursor = db.rawQuery("SELECT * FROM dkhc WHERE nama='" + getIntent().getStringExtra("nama") +"'",null);
            cursor = db.rawQuery("SELECT * FROM dkhc WHERE DATE_NOW = 'February 2019'",null);
            cursor.moveToFirst();
            if(cursor.getCount()>0){ //jika data ada
                //cek status perubahan

            }else{ //jika data tidak ada
                Log.e(TAG, "data tidak ada, lakukan save");
            }
        }*/

        return view;
    }

    private void checkData(final String contractID){
        HttpsTrustManager.allowAllSSL();
        pDialog.setCancelable(false);
        pDialog.setMessage("Sedang sinkronisasi...");
        showDialog();

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
