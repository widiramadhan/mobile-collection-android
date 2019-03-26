package sfi.mobile.collection.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.Status;

public class DraftAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Status> itemList;
    private static final String TAG = DKHCAdapter.class.getSimpleName();

    DBHelper dbhelper;
    Cursor cursor;

    public DraftAdapter(Activity activity, List<Status> itemList) {
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
        TextView txtTanggal = (TextView) convertView.findViewById(R.id.tgl_draft);
        TextView txtBulan= (TextView) convertView.findViewById(R.id.bulan_draft);

        Status j = itemList.get(position);

        Log.e(TAG, "Date -> "+j.getDate());

        String bulan = j.getDate().substring(5,7);
        String tanggal = j.getDate().substring(8,10);
        String tahun = j.getDate().substring(2,4);
        String monthName = "";

        if(bulan.equals("01")){
            monthName = "JAN";
        }else if(bulan.equals("02")){
            monthName = "FEB";
        }else if(bulan.equals("03")){
            monthName = "MAR";
        }else if(bulan.equals("04")){
            monthName = "APR";
        }else if(bulan.equals("05")){
            monthName = "MAY";
        }else if(bulan.equals("06")){
            monthName = "JUN";
        }else if(bulan.equals("07")){
            monthName = "JUL";
        }else if(bulan.equals("08")){
            monthName = "AUG";
        }else if(bulan.equals("09")){
            monthName = "SEP";
        }else if(bulan.equals("10")){
            monthName = "OCT";
        }else if(bulan.equals("11")){
            monthName = "NOV";
        }else if(bulan.equals("12")){
            monthName = "DEC";
        }

        txtcontract_id.setText(j.getContractID());
        txtcustomer_name.setText(j.getCustomerName());
        txtTanggal.setText(tanggal);
        txtBulan.setText(monthName+" "+tahun);

        return convertView;
    }
}
