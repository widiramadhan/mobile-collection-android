package sfi.mobile.collection.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class StatusAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Status> itemList;
    private static final String TAG = DKHCAdapter.class.getSimpleName();

    DBHelper dbhelper;
    Cursor cursor;

    public StatusAdapter(Activity activity, List<Status> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_progress_layout, null);

        TextView txtcontract_id = (TextView) convertView.findViewById(R.id.contract_id);
        TextView txtcustomer_name = (TextView) convertView.findViewById(R.id.customer_name);
        //TextView txttanggal = (TextView) convertView.findViewById(R.id.date);

        Status j = itemList.get(position);

        txtcontract_id.setText(j.getContractID());
        txtcustomer_name.setText(j.getCustomerName());
        //txttanggal.setText("Saved at "+j.getDate());

        return convertView;
    }
}
