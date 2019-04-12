package sfi.mobile.collection.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sfi.mobile.collection.R;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    public ProfileFragment() {
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

    TextView txtnameHeader, txtjobHeader, txtnamaLengkap, txtusername, txtnik, txtjabatan;
    Button btnChangePassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtnameHeader = (TextView) view.findViewById(R.id.name_header);
        txtjobHeader = (TextView) view.findViewById(R.id.job_header);
        txtnamaLengkap = (TextView) view.findViewById(R.id.nama_lengkap);
        txtusername = (TextView) view.findViewById(R.id.username);
        txtnik = (TextView) view.findViewById(R.id.nik);
        txtjabatan = (TextView) view.findViewById(R.id.jabatan);
        btnChangePassword = (Button) view.findViewById(R.id.btnChangePassword);

        /*** set session to variable ***/
        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        String employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        String branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);
        String username = sharedpreferences.getString(TAG_USERNAME, null);
        String fullname = sharedpreferences.getString(TAG_FULL_NAME, null);
        String job = sharedpreferences.getString(TAG_EMP_JOB_ID, null);
        /*** end set session to variable ***/

        Log.e(TAG, "Fullname -> "+fullname);
        Log.e(TAG, "Job -> "+job);
        Log.e(TAG, "Nik -> "+employeeID);

        txtnameHeader.setText(fullname);
        txtjobHeader.setText(job);
        txtnamaLengkap.setText(fullname);
        txtusername.setText(username);
        txtnik.setText(employeeID);
        txtjabatan .setText(job);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment fragment = new ChangePasswordFragment();
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
    }
}
