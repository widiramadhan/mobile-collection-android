package sfi.mobile.collection.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.DraftAdapter;
import sfi.mobile.collection.adapter.HistoryAdapter;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class TabHistory extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipe;
    ListView list;
    HistoryAdapter historytAdapter;
    List<Status> itemList = new ArrayList<>();
    TextView txtcontractid, txtcusrtomername;

    protected Cursor cursor;
    DBHelper dbhelper;

    public TabHistory(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_history, container, false);

        list = (ListView) view.findViewById(R.id.listStatus);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        // mengisi data dari adapter ke listview
        historytAdapter = new HistoryAdapter(getActivity(), itemList);
        list.setAdapter(historytAdapter);

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
                ProgressDetailFragmentHistory fragment = new ProgressDetailFragmentHistory();
                Bundle arguments = new Bundle();
                arguments.putString("paramId", contract_id);
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

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    public void init(){
        getAllhistory();
    }

    private void getAllhistory(){
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
        historytAdapter.notifyDataSetChanged();
    }

    public void onRefresh() {
        init();
    }
}
