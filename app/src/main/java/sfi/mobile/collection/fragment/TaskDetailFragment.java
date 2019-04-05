package sfi.mobile.collection.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;


public class TaskDetailFragment extends Fragment {

    private static final String TAG = TaskDetailFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    TextView txtCustomerName, txtApplicationID, txtContractID, txtMobilePhone, txtAdress, txtTotal, txtTanggalJatuhTempo, lat, lng;
    Button btnStart;
    ImageView btn_direction;
    FloatingActionButton btnPhone;
    ProgressDialog progressDialog;
    DBHelper dbhelper;
    protected Cursor cursor;

    public TaskDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        return view;
    }

    private void getData(String ContractID){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, ALAMAT_KTP, NOMOR_HANDPHONE, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE  FROM DKH WHERE NOMOR_KONTRAK='" + ContractID +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
        {
            cursor.moveToPosition(0);
            txtApplicationID.setText(cursor.getString(0));
            txtCustomerName.setText(cursor.getString(1));
            txtAdress.setText(cursor.getString(2));
            txtMobilePhone.setText(cursor.getString(3));
            txtTotal.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(4)))).replaceAll( "Rp", "" ));
            txtTanggalJatuhTempo.setText(cursor.getString(5));
            lat.setText(cursor.getString(6));
            lng.setText(cursor.getString(7));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        /*** end Get parameter dari halaman sebelumnya ***/


        txtCustomerName = (TextView) getActivity().findViewById(R.id.customer_name);
        txtApplicationID = (TextView) getActivity().findViewById(R.id.app_id);
        //txtContractID = (TextView) getActivity().findViewById(R.id.contract_id);
        txtMobilePhone = (TextView) getActivity().findViewById(R.id.mobile_phone);
        txtAdress = (TextView) getActivity().findViewById(R.id.address);
        btnPhone = (FloatingActionButton) getActivity().findViewById(R.id.btn_phone);
        btnStart = (Button) getActivity().findViewById(R.id.btn_start);
        txtTotal = (TextView) getActivity().findViewById(R.id.total);
        txtTanggalJatuhTempo = (TextView) getActivity().findViewById(R.id.duedate);
        btn_direction = (ImageView) getActivity().findViewById(R.id.btn_direction);
        lat = (TextView) getActivity().findViewById(R.id.lat);
        lng = (TextView) getActivity().findViewById(R.id.lng);

        getData(paramId);

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toDial="tel:"+txtMobilePhone.getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse(toDial)));
            }
        });

        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paramLat = ((TextView) getActivity().findViewById(R.id.lat)).getText().toString();
                String paramLng = ((TextView) getActivity().findViewById(R.id.lng)).getText().toString();
                Log.e(TAG, "Lat -> "+paramLat+", Lng -> "+paramLng);
                if(paramLat.equals("0.000000") && paramLng.equals("0.000000")){
                    Toast.makeText(getActivity(), "Koordinat customer ini belum diketahui", Toast.LENGTH_LONG).show();
                }else {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + paramLat + "," + paramLng + "");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

        btnStart = (Button) getActivity().findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract_id = ((TextView) getActivity().findViewById(R.id.app_id)).getText().toString();
                CollectionTaskFragment fragment = new CollectionTaskFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "paramId" , contract_id);
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
