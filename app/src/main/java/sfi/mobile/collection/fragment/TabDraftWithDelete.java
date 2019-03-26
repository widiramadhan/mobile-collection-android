package sfi.mobile.collection.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class TabDraftWithDelete extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {


    public TabDraftWithDelete() {
    }

    SwipeRefreshLayout swipe;
    ListView list;
    DraftAdapter draftAdapter;
    List<Status> itemList = new ArrayList<>();
    TextView txtcontractid, txtcusrtomername;

    protected Cursor cursor;
    DBHelper dbhelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_draft, container, false);

        list = (ListView) view.findViewById(R.id.listStatus);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        /*txtcontractid = (TextView) view.findViewById(R.id.contract_id);
        txtcusrtomername = (TextView) view.findViewById(R.id.customer_name);*/

        // mengisi data dari adapter ke listview
        draftAdapter = new DraftAdapter(getActivity(), itemList);
        list.setAdapter(draftAdapter);

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
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                //adb.setView(alertDialogView);
                adb.setTitle("Hapus Data ?");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String contract_id = ((TextView) getActivity().findViewById(R.id.contract_id)).getText().toString();
                        dbhelper = new DBHelper(getActivity());
                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                        db.execSQL("delete from RESULT where CONTRACT_ID = '" + contract_id + "'");
                        getAllStatus();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();


            }
        });

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

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
        draftAdapter.notifyDataSetChanged();
    }
}
