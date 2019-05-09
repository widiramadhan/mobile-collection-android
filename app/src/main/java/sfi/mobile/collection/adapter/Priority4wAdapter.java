package sfi.mobile.collection.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;
import sfi.mobile.collection.model.Status;

public class Priority4wAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DKHC> itemList;
    private static final String TAG = Priority4wAdapter.class.getSimpleName();

    DBHelper dbhelper;
    Cursor cursor;

    public Priority4wAdapter(Activity activity, List<DKHC> itemList) {
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

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_task_priority4w, null);

        TextView txtcontract_id = (TextView) convertView.findViewById(R.id.contract_id);
        TextView txtcustomer_name = (TextView) convertView.findViewById(R.id.customer_name);
        TextView txttotal_tagihan = (TextView) convertView.findViewById(R.id.total_tagihan);
        //TextView txttanggal_jatuh_tempo = (TextView) convertView.findViewById(R.id.tanggal_jatuh_tempo);
        TextView txtTanggal = (TextView) convertView.findViewById(R.id.tgl);
        TextView txtBulan = (TextView) convertView.findViewById(R.id.bulan);
        TextView txtjarak = (TextView) convertView.findViewById(R.id.jarak);
        TextView txtod = (TextView) convertView.findViewById(R.id.od);

        DKHC j = itemList.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        DecimalFormat df = new DecimalFormat("#.##");

        String distance = "";
        if (j.getJarak() >= 1000) {
            distance = "- Km";
        } else {
            distance = String.valueOf(df.format(j.getJarak())) + " Km";
        }

        String daysOD = "";
        if (j.getOverDueDays() > 0) {
            daysOD = "Over due days : " + j.getOverDueDays() + " Days";
        } else if (j.getOverDueDays() < 0) {
            daysOD = "Over due days : " + j.getOverDueDays() + " Days";
        } else {
            daysOD = "Over due days : " + j.getOverDueDays() + " Days";
        }

        String bulan = j.getTanggalJatuhTempo().substring(5, 7);
        String tanggal = j.getTanggalJatuhTempo().substring(8, 10);
        String tahun = j.getTanggalJatuhTempo().substring(2, 4);
        String monthName = "";

        if (bulan.equals("01")) {
            monthName = "JAN";
        } else if (bulan.equals("02")) {
            monthName = "FEB";
        } else if (bulan.equals("03")) {
            monthName = "MAR";
        } else if (bulan.equals("04")) {
            monthName = "APR";
        } else if (bulan.equals("05")) {
            monthName = "MAY";
        } else if (bulan.equals("06")) {
            monthName = "JUN";
        } else if (bulan.equals("07")) {
            monthName = "JUL";
        } else if (bulan.equals("08")) {
            monthName = "AUG";
        } else if (bulan.equals("09")) {
            monthName = "SEP";
        } else if (bulan.equals("10")) {
            monthName = "OCT";
        } else if (bulan.equals("11")) {
            monthName = "NOV";
        } else if (bulan.equals("12")) {
            monthName = "DEC";
        }

        txtcontract_id.setText(j.getNomorKontrak());
        txtcustomer_name.setText(j.getNamaKostumer().toUpperCase());
        txttotal_tagihan.setText(String.valueOf(formatRupiah.format((double) j.getTotalTagihan())).replaceAll("Rp", ""));
        //txttanggal_jatuh_tempo.setText(j.getTanggalJatuhTempo());
        txtTanggal.setText(tanggal);
        txtBulan.setText(monthName + " " + tahun);
        txtjarak.setText(distance);
        txtod.setText(daysOD);

        return convertView;
    }
}
