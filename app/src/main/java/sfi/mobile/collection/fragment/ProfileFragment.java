package sfi.mobile.collection.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sfi.mobile.collection.R;

public class ProfileFragment extends Fragment {

    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    private static final String TAG = DashboardTabTasklist.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String branchID, employeeID, branchName, fullname, username, job;
    /*** end memanggil session yang terdaftar ***/

    TextView txtnameHeader, txtjobHeader, txtnamaLengkap, txtusername, txtnik, txtjabatan;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** set session to variable ***/
        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        fullname = sharedpreferences.getString(TAG_FULL_NAME, null);
        job = sharedpreferences.getString(TAG_EMP_JOB_ID, null);
        /*** end set session to variable ***/

        txtnameHeader = (TextView) getActivity().findViewById(R.id.name_header);
        txtjobHeader = (TextView) getActivity().findViewById(R.id.job_header);
        txtnamaLengkap = (TextView) getActivity().findViewById(R.id.nama_lengkap);
        txtusername = (TextView) getActivity().findViewById(R.id.username);
        txtnik = (TextView) getActivity().findViewById(R.id.nik);
        txtjabatan = (TextView) getActivity().findViewById(R.id.jabatan);

        txtnameHeader.setText(fullname);
        txtjobHeader.setText(job);
        txtnamaLengkap.setText(fullname);
        txtusername.setText(username);
        txtnik.setText(employeeID);
        txtjabatan .setText(job);
    }
}
