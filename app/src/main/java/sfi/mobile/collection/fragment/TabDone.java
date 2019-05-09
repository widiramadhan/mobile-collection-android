package sfi.mobile.collection.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.DraftAdapter;
import sfi.mobile.collection.adapter.TaskDoneAdapter;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class TabDone extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {


    public TabDone() {
    }

    SwipeRefreshLayout swipe;
    ListView list;
    TaskDoneAdapter taskDoneAdapter;
    List<Status> itemList = new ArrayList<>();
    TextView txtcontractid, txtcusrtomername;
    View view;

    SharedPreferences sharedpreferences;
    private static final String TAG = TabDone.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID = "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String branchID, employeeID, employeeJobID;

    protected Cursor cursor;
    DBHelper dbhelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_draft, container, false);

        list = (ListView) view.findViewById(R.id.listStatus);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        txtcontractid = (TextView) view.findViewById(R.id.contract_id);
        txtcusrtomername = (TextView) view.findViewById(R.id.customer_name);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        //txtName.setText("Hello,\n"+fullName);
        branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        employeeJobID = sharedpreferences.getString(TAG_EMP_JOB_ID, null);

        dbhelper = new DBHelper(getActivity());

        // mengisi data dari adapter ke listview
        taskDoneAdapter = new TaskDoneAdapter(getActivity(), itemList);
        list.setAdapter(taskDoneAdapter);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contract_id = ((TextView) view.findViewById(R.id.contract_id)).getText().toString();
                ProgressDetailFragment fragment = new ProgressDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putString("paramId", contract_id);
                Log.e(TAG, "Kontrak Id->" + contract_id);
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                fragmentTransaction.addToBackStack("A_B_TAG");
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void init(){
        getAllStatus();
    }

    @Override
    public void onRefresh() {
        init();
    }

    private void getAllStatus(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getStatus();
        swipe.setRefreshing(true);

        itemList.clear();
        for (int i = 0; i < row.size(); i++) {
            String strcontractid = row.get(i).get("CONTRACT_ID");
            String strtcustomername = row.get(i).get("NAMA_KOSTUMER");
            String strdate = row.get(i).get("DATE");

            Status data = new Status();

            data.setContractID(strcontractid);
            data.setCustomerName(strtcustomername);
            data.setDate(strdate);

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        taskDoneAdapter.notifyDataSetChanged();
    }

    //---------------HELPER--------------------------------------------
    public ArrayList<HashMap<String, String>> getStatus() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        //String selectQuery = "SELECT DISTINCT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.CREATE_DATE AS DATE FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID = B.NOMOR_KONTRAK WHERE B.IS_COLLECT=1 AND B.DailyCollectibility='Coll Harian' AND B.PIC='"+employeeID+"'";
        String selectQuery = "SELECT DISTINCT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.CREATE_DATE AS DATE FROM COLLECTED A LEFT JOIN DKH B ON A.CONTRACT_ID = B.NOMOR_KONTRAK WHERE A.IS_COLLECT=1 AND A.DailyCollectibility='Coll Harian' AND A.EMP_ID='"+employeeID+"'";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("CONTRACT_ID", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("DATE", String.valueOf(cursor.getString(2)));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        database.close();
        return wordList;
    }
}
