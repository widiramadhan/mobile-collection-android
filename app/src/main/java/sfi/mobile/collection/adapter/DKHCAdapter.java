package sfi.mobile.collection.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.fragment.DashboardTabPriority;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;

public class DKHCAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DKHC> itemList;
    private static final String TAG = DKHCAdapter.class.getSimpleName();

    DBHelper dbhelper;
    Cursor cursor;

    public DKHCAdapter(Activity activity, List<DKHC> itemList) {
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_tasklist_layout, null);

        TextView txtcontract_id = (TextView) convertView.findViewById(R.id.contract_id);
        TextView txtcustomer_name = (TextView) convertView.findViewById(R.id.customer_name);
        TextView txttotal_tagihan = (TextView) convertView.findViewById(R.id.total_tagihan);
        TextView txttanggal_jatuh_tempo = (TextView) convertView.findViewById(R.id.tanggal_jatuh_tempo);
        TextView txtjarak = (TextView) convertView.findViewById(R.id.jarak);
        TextView txtod = (TextView) convertView.findViewById(R.id.od);
        ImageView Img = (ImageView) convertView.findViewById(R.id.imageView2);

        DKHC j = itemList.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        DecimalFormat df = new DecimalFormat("#.##");

        String distance = "";
        if(j.getJarak() >= 1000){
            distance = "- Km";
        }else{
            distance = String.valueOf(df.format(j.getJarak()))+" Km";
        }

        String daysOD = "";
        if(j.getOverDueDays() > 0){
            daysOD = "Over due days : "+j.getOverDueDays()+" Days";
        }else if(j.getOverDueDays() < 0){
            daysOD = "Over due days : "+j.getOverDueDays()+" Days";
        }else{
            daysOD = "Over due days : "+j.getOverDueDays()+" Days";
        }

        String tanggaljanjibayar="";
        if(j.getTanggalJanjiBayar() != null){
            Resources res = activity.getResources(); /** from an Activity */
            Img.setImageDrawable(res.getDrawable(R.drawable.circle_primary));
        }else{
            if(j.getOverDueDays() > 0 ){
                Resources res = activity.getResources(); /** from an Activity */
                Img.setImageDrawable(res.getDrawable(R.drawable.circle_danger));
            }
            else {
                Resources res = activity.getResources(); /** from an Activity */
                Img.setImageDrawable(res.getDrawable(R.drawable.circle_success));
            }
        }

        txtcontract_id.setText(j.getNomorKontrak());
        txtcustomer_name.setText(j.getNamaKostumer());
        txttotal_tagihan.setText(String.valueOf(formatRupiah.format((double)j.getTotalTagihan())).replaceAll( "Rp", "" ));
        txttanggal_jatuh_tempo.setText(j.getTanggalJatuhTempo());
        txtjarak.setText(distance);
        txtod.setText(daysOD);

        return convertView;
    }
}
