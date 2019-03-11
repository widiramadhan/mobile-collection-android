package sfi.mobile.collection.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sfi.mobile.collection.MainActivity;
import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.DKHCAdapter;
import sfi.mobile.collection.adapter.TaskListAdapter;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;
import sfi.mobile.collection.model.TaskList;
import sfi.mobile.collection.util.HttpsTrustManager;

public class DashboardTabPriority extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, LocationListener {

    public DashboardTabPriority() {
    }

    SwipeRefreshLayout swipe;
    ListView list;
    DKHCAdapter dkhcAdapter;
    List<DKHC> itemList = new ArrayList<>();
    TextView txtcontractid, txtcusrtomername, txtLatitude, txtLongitude;

    protected Cursor cursor;
    DBHelper dbhelper;
    ProgressDialog progressDialog;
    LocationManager locationManager;

    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    private static final String TAG = DashboardTabPriority.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID = "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String branchID, employeeID, employeeJobID;

    /*** end memanggil session yang terdaftar ***/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_priority_dashboard, container, false);

        list = (ListView) view.findViewById(R.id.listTaskList);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        txtcontractid = (TextView) view.findViewById(R.id.contract_id);
        txtcusrtomername = (TextView) view.findViewById(R.id.customer_name);

        // mengisi data dari adapter ke listview
        dkhcAdapter = new DKHCAdapter(getActivity(), itemList);
        list.setAdapter(dkhcAdapter);

        /*** set session to variable ***/
        sharedpreferences = this.getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        employeeJobID = sharedpreferences.getString(TAG_EMP_JOB_ID, null);
        /*** end set session to variable ***/

        txtLatitude = (TextView) view.findViewById(R.id.latitude);
        txtLongitude = (TextView) view.findViewById(R.id.longitude);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fabFilter);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle("Sort berdasarkan");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Jarak Terdekat");
                arrayAdapter.add("Jarak Terjauh");
                arrayAdapter.add("Tagihan Terbesar");
                arrayAdapter.add("Tagihan Terendah");
                arrayAdapter.add("Overdue Tertinggi");
                arrayAdapter.add("Overdue Terendah");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strSort = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                        /*builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();*/
                        if (strSort == "Jarak Terdekat") {
                            Log.e(TAG, "Anda memilih Jarak Terdekat");
                            dialog.dismiss();
                        } else {
                            Log.e(TAG, "Anda memilih selain Jarak Terdekat");
                            dialog.dismiss();

                        }
                    }
                });
                builderSingle.show();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contract_id = ((TextView) view.findViewById(R.id.contract_id)).getText().toString();
                TaskListDetailFragment fragment = new TaskListDetailFragment();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
        //init();
    }

    private void init() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            txtLatitude.setText(String.valueOf(lat));
            txtLongitude.setText(String.valueOf(lng));
        }
        //getLocation();
        getDKHCPriority();
    }

    private void getDKHCPriority() {
        //cek koneksi
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info == null){ //jika tidak ada koneksi
            Log.e(TAG, "Tidak ada koneksi");
            dbhelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM DKH WHERE PIC = '"+employeeID+"' AND BRANCH_ID = '"+branchID+"' AND PERIOD = case strftime('%m','now') when '01' then 'January' when '02' then 'Febuary' when '03' then 'March' when '04' then 'April' when '05' then 'May' when '06' then 'June' when '07' then 'July' when '08' then 'August' when '09' then 'September' when '10' then 'October' when '11' then 'November' when '12' then 'December' else '' end  || ' ' || strftime('%Y','now')",null);
            cursor.moveToFirst();
            if(cursor.getCount() == 0){
                Toast.makeText(getActivity(), "Membutuhkan koneksi internet untuk synchonize data", Toast.LENGTH_LONG).show();
            }else {
                getAllDKHC();
            }
        }else { //jika ada koneksi
            Log.e(TAG, "Ada koneksi");
            //cek data ada atau tidak
            dbhelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM DKH WHERE PIC = '"+employeeID+"' AND BRANCH_ID = '"+branchID+"' AND PERIOD = case strftime('%m','now') when '01' then 'January' when '02' then 'Febuary' when '03' then 'March' when '04' then 'April' when '05' then 'May' when '06' then 'June' when '07' then 'July' when '08' then 'August' when '09' then 'September' when '10' then 'October' when '11' then 'November' when '12' then 'December' else '' end  || ' ' || strftime('%Y','now')",null);
            cursor.moveToFirst();
            if(cursor.getCount() == 0){ //jika data tidak ada
                Log.e(TAG, "Data tidak ada, insert ke sqlite");
                saveDataSQLite();
            }else {
                Log.e(TAG, "Data ada di sqlite, load dari sqlite");
                getAllDKHC();
            }
        }
    }

    private void getAllDKHC(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = dbhelper.getData30Days();
        swipe.setRefreshing(true);

        itemList.clear();
        for (int i = 0; i < row.size(); i++) {
            String strcontractid = row.get(i).get("NOMOR_KONTRAK");
            String strtcustomername = row.get(i).get("NAMA_KOSTUMER");
            String strtotaltagihan = row.get(i).get("TOTAL_TAGIHAN");
            String strtgljatuhtempo = row.get(i).get("TANGGAL_JATUH_TEMPO");
            String strlat = row.get(i).get("LAT");
            String strlng = row.get(i).get("LNG");
            String stroverduedays = row.get(i).get("OVERDUE_DAYS");

            double currentLat = Double.parseDouble(txtLatitude.getText().toString());
            double currentLng = Double.parseDouble(txtLongitude.getText().toString());
            double latFromDB = Double.parseDouble(strlat);
            double lngFromDB = Double.parseDouble(strlng);

            double earthRadius = 6371;

            double distance = (earthRadius * Math.acos(Math.sin(Math.toRadians(latFromDB)) * Math.sin(Math.toRadians(currentLat)) + Math.cos(Math.toRadians(lngFromDB - currentLng)) * Math.cos(Math.toRadians(latFromDB)) * Math.cos(Math.toRadians(currentLat))));
            DecimalFormat df = new DecimalFormat("#.##");
            //Log.e(TAG,"distance -> "+df.format(distance));

            DKHC data = new DKHC();

            data.setNomorKontrak(strcontractid);
            data.setNamaKostumer(strtcustomername);
            data.setTotalTagihan(Integer.valueOf(strtotaltagihan));
            data.setTanggalJatuhTempo(strtgljatuhtempo);
            data.setJarak(distance);
            data.setOverDueDays(Integer.valueOf(stroverduedays));

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        dkhcAdapter.notifyDataSetChanged();
    }

    private void saveDataSQLite(){
        //HttpsTrustManager.allowAllSSL();
        String urlGetDKHC = ConnectionHelper.URL + "getTasklist.php";
        Log.e(TAG, "link -> "+urlGetDKHC+"?employeeID="+employeeID+"&branchID="+branchID);

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        JsonArrayRequest jArr = new JsonArrayRequest(urlGetDKHC+"?employeeID="+employeeID+"&branchID="+branchID,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                DKHC j = new DKHC();
                                j.setBranchID(obj.getString("BRANCH_ID"));
                                j.setBranchName(obj.getString("BRANCH_NAME"));
                                j.setPic(obj.getString("EMP_ID"));
                                j.setNomorKontrak(obj.getString("NOMOR_KONTRAK"));
                                j.setNamaKostumer(obj.getString("NAMA_KOSTUMER"));
                                j.setTanggalJatuhTempo(obj.getString("TANGGAL_JATUH_TEMPO"));
                                j.setOverDueDays(obj.getInt("OVERDUE_DAYS"));
                                j.setAngsuranKe(obj.getInt("ANGSURAN_KE"));
                                j.setJumlahAngsuranOverDue(obj.getInt("JUMLAH_ANGSURAN_OVERDUE"));
                                j.setTenor(obj.getInt("TENOR"));
                                j.setAngsuranBerjalan(obj.getInt("ANGSURAN_BERJALAN"));
                                j.setAngsuranTertunggak(obj.getInt("ANGSURAN_TERTUNGGAK"));
                                j.setDenda(obj.getInt("DENDA"));
                                j.setTitipan(obj.getInt("TITIPAN"));
                                j.setTotalTagihan(obj.getInt("TOTAL_TAGIHAN"));
                                j.setOutstandingAR(obj.getInt("OUTSTANDING_AR"));
                                j.setAlamatKTP(obj.getString("ALAMAT_KTP"));
                                j.setNomorTlpRumah(obj.getString("NOMOR_TELEPON_RUMAH"));
                                j.setNomorHanphone(obj.getString("NOMOR_HANDPHONE"));
                                j.setAlamatKantor(obj.getString("ALAMAT_KANTOR"));
                                j.setNomorTlpKantor(obj.getString("NOMOR_TELEPON_KANTOR"));
                                j.setAlamatSurat(obj.getString("ALAMAT_SURAT"));
                                j.setNomorTlpSurat(obj.getString("NOMOR_TELEPON_SURAT"));
                                j.setPicTerakhir(obj.getString("PIC_TERAKHIR"));
                                j.setPenangananTerakhir(obj.getString("PENANGANAN_TERAKHIR"));
                                j.setTanggalJanjiBayar(obj.getString("TANGGAL_JANJI_BAYAR"));
                                j.setDailyCollectibility(obj.getString("DailyCollectibility"));
                                j.setOdHarian(obj.getInt("OvdDaysHarian"));
                                j.setTanggalJatuhTempoHarian(obj.getString("TglJatuhTempoHarian"));
                                j.setARin(obj.getInt("ARIN"));
                                j.setFlowUp(obj.getInt("FlowUp"));
                                j.setTanggalTarikHarian(obj.getString("TglTarikHarian"));
                                j.setTanggalTerimaKlaim(obj.getString("TglTerimaKlaim"));
                                j.setLat(obj.getString("LATITUDE"));
                                j.setLng(obj.getString("LONGITUDE"));
                                j.setApproval(obj.getInt("APPROVAL"));
                                j.setIsCollect(obj.getInt("IS_COLLECT"));
                                j.setPeriod(obj.getString("PERIOD"));
                                //insert to database sqlite
                                dbhelper = new DBHelper(getActivity());
                                SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
                                String Sql = "insert into DKH (BRANCH_ID, BRANCH_NAME, PIC, NOMOR_KONTRAK, NAMA_KOSTUMER, TANGGAL_JATUH_TEMPO,OVERDUE_DAYS,ANGSURAN_KE,JUMLAH_ANGSURAN_OVERDUE,TENOR,ANGSURAN_BERJALAN,ANGSURAN_TERTUNGGAK,DENDA,TITIPAN,TOTAL_TAGIHAN,OUTSTANDING_AR,ALAMAT_KTP,NOMOR_TELEPON_RUMAH,NOMOR_HANDPHONE,ALAMAT_KANTOR,NOMOR_TELEPON_RUMAH,ALAMAT_SURAT,NOMOR_TELEPON_SURAT,PIC_TERAKHIR,PENANGANAN_TERAKHIR,TANGGAL_JANJI_BAYAR,DailyCollectibility,OvdDaysHarian,TglJatuhTempoHarian,ARIN,FlowUp,TglTarikHarian,TglTerimaKlaim,LATITUDE,LONGITUDE,APPROVAL,IS_COLLECT,PERIOD)" +
                                        " values ('" + j.getBranchID().toString() + "','"+ j.getBranchName().toString() +"','"+ j.getPic().toString() +"','"+ j.getNomorKontrak().toString() +"','"+ j.getNamaKostumer().toString() +"','"+ j.getTanggalJatuhTempo().toString()+"','"+ Integer.valueOf(j.getOverDueDays()) +"','"+ Integer.valueOf(j.getAngsuranKe()) +"','"+ Integer.valueOf(j.getJumlahAngsuranOverDue()).toString() +"','"+ Integer.valueOf(j.getTenor()).toString() +"','"+ Integer.valueOf(j.getAngsuranBerjalan()).toString() +"','"+ Integer.valueOf(j.getAngsuranTertunggak()).toString() +"','"+ Integer.valueOf(j.getDenda()) +"','"+ Integer.valueOf(j.getTitipan()) +"','"+ j.getTotalTagihan() +"','"+ j.getOutstandingAR() +"','"+ j.getAlamatKTP() +"','"+ j.getNomorTlpRumah() +"','"+ j.getNomorHanphone() +"','"+ j.getAlamatKantor() +"','"+ j.getNomorTlpKantor() +"','"+ j.getAlamatSurat() +"','"+ j.getNomorTlpSurat() +"','"+ j.getPicTerakhir() +"','"+ j .getPenangananTerakhir()+"','"+ j.getTanggalJanjiBayar() +"','"+ j.getDailyCollectibility() +"','"+ j.getOdHarian() +"','"+ j.getTanggalJatuhTempoHarian() +"','"+ j.getARin() +"','"+ j.getFlowUp() +"','"+ j.getTanggalTarikHarian() +"','"+ j.getTanggalTerimaKlaim() +"','"+ j.getLat() +"','"+ j.getLng() +"','"+ j.getApproval() +"','"+ j.getIsCollect() +"','"+ j.getPeriod() +"')";
                                dbInsert.execSQL(Sql);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Masuk catch");
                            }
                        }
                        progressDialog.hide();
                        swipe.setRefreshing(false);
                        getAllDKHC();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan, mohon hubungi administrator", Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });
        AppController.getInstance().addToRequestQueue(jArr);
    }

    @Override
    public void onRefresh() {
        init();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            txtLatitude.setText("-");
            txtLongitude.setText("-");
        }else {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            txtLatitude.setText(String.valueOf(lat));
            txtLongitude.setText(String.valueOf(lng));
        }
        /*Log.e(TAG, "Lat : " + txtLatitude.getText() + ", Lng : " + txtLongitude.getText());*/
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
