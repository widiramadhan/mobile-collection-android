package sfi.mobile.collection.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;

public class HomeFragment extends Fragment {

    private static final String TAG = ResultFragment.class.getSimpleName();

    public HomeFragment() {
    }

    SharedPreferences sharedpreferences;
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";

    LinearLayout ln_task_new, ln_task_draft, ln_task_done;
    TextView txtName, txtTotalTask, txtTotalDone;

    DBHelper dbhelper;
    protected Cursor cursor;
    protected Cursor cursor2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ln_task_new = (LinearLayout) view.findViewById(R.id.new_task);
        ln_task_draft = (LinearLayout) view.findViewById(R.id.task_draft);
        ln_task_done = (LinearLayout) view.findViewById(R.id.task_done);
        txtName = (TextView) view.findViewById(R.id.hello_name);
        txtTotalTask = (TextView) view.findViewById(R.id.total_task);
        txtTotalDone = (TextView) view.findViewById(R.id.total_done);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        txtName.setText("Hello,\n"+fullName);

        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM DKH WHERE IS_COLLECT = 0",null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        txtTotalTask.setText(String.valueOf(count));

        cursor2 = db.rawQuery("SELECT * FROM DKH WHERE IS_COLLECT = 1",null);
        cursor2.moveToFirst();
        int count2 = cursor2.getCount();
        txtTotalDone.setText(String.valueOf(count2));

        ln_task_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFragment fragment = new TaskFragment();
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        ln_task_draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressFragment fragment = new ProgressFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "flag" , "0");
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        ln_task_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressFragment fragment = new ProgressFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "flag" , "1");
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
    }
}
