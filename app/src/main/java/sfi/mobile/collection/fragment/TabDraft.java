package sfi.mobile.collection.fragment;


import android.database.Cursor;
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
import sfi.mobile.collection.adapter.StatusAdapter;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class TabDraft extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {


    public TabDraft() {
    }

    SwipeRefreshLayout swipe;
    ListView list;
    StatusAdapter statusAdapter;
    List<Status> itemList = new ArrayList<>();
    TextView txtcontractid, txtcusrtomername;

    protected Cursor cursor;
    DBHelper dbhelper;

    private static final String TAG = TabDraft.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_draft, container, false);

        list = (ListView) view.findViewById(R.id.listStatus);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        txtcontractid = (TextView) view.findViewById(R.id.contract_id);
        txtcusrtomername = (TextView) view.findViewById(R.id.customer_name);

        // mengisi data dari adapter ke listview
        statusAdapter = new StatusAdapter(getActivity(), itemList);
        list.setAdapter(statusAdapter);

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
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment).commit();
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
        ArrayList<HashMap<String, String>> row = dbhelper.getStatus();
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
        statusAdapter.notifyDataSetChanged();
    }
}
