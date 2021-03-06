package sfi.mobile.collection.fragment;


import android.Manifest;
import android.app.AlertDialog;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.DKHCAdapter;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;

public class TabNormal extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, LocationListener {

    public TabNormal() {
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
    private static final String TAG = TabPriority.class.getSimpleName();
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
        View view = inflater.inflate(R.layout.tab_task_priority, container, false);

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
                        if (strSort == "Jarak Terdekat") {
                            Log.e(TAG, "Anda memilih Jarak Terdekat");
                            dialog.dismiss();
                        } else if (strSort == "Jarak Terjauh") {
                            Log.e(TAG, "Anda memilih Jarak Terjauh");
                            dialog.dismiss();
                        } else if (strSort == "Tagihan Terbesar") {
                            Log.e(TAG, "Anda memilih Tagihan Terbesar");
                            dialog.dismiss();
                            getDKHTagihanTerbesar();
                        } else if (strSort == "Tagihan Terendah") {
                            Log.e(TAG, "Anda memilih Tagihan Terendah");
                            dialog.dismiss();
                            getDKHTagihanTerendah();
                        } else if (strSort == "Overdue Tertinggi") {
                            Log.e(TAG, "Anda memilih Overdue Tertinggi");
                            dialog.dismiss();
                            getDKHODtertinggi();
                        } else if (strSort == "Overdue Terendah") {
                            Log.e(TAG, "Anda memilih Overdue Terendah");
                            dialog.dismiss();
                            getDKHODterendah();
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

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if(info == null) {
                    TaskDetailFragment fragment = new TaskDetailFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString("paramId", contract_id);
                    fragment.setArguments(arguments);
                    FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                    fragmentTransaction.addToBackStack("A_B_TAG");
                    fragmentTransaction.commit();
                }else{
                    checkCollectibility(contract_id);
                }
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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pemberitahuan");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Gps pada handphone harus diaktifkan?")
                .setCancelable(false)
                .setPositiveButton("Aktifkan", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void init() {
        getLocation();
        getAllDKHC();
    }

    public void getLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5,this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            if(!location.equals("")) {
                try {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    txtLatitude.setText(String.valueOf(lat));
                    txtLongitude.setText(String.valueOf(lng));
                    Log.d(TAG, "Lat -> " + lat);
                    Log.d(TAG, "Lng -> " + lng);
                } catch (Exception e) {
                    Log.d(TAG, String.valueOf(e));
                }
            }else{
                txtLatitude.setText("0.000000");
                txtLongitude.setText("0.000000");
            }
        }else{
            txtLatitude.setText("0.000000");
            txtLongitude.setText("0.000000");
        }
    }

    private void getAllDKHC(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getData30Days();
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

    private void getDKHTagihanTerbesar(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getDKHtagihanTerbesar();
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
            String strjanjibayar = row.get(i).get("TANGGAL_JANJI_BAYAR");

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
            //data.setTanggalJanjiBayar(strjanjibayar);

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        dkhcAdapter.notifyDataSetChanged();
    }

    private void getDKHTagihanTerendah(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getDKHtagihanTerendah();
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
            String strjanjibayar = row.get(i).get("TANGGAL_JANJI_BAYAR");

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
            //data.setTanggalJanjiBayar(strjanjibayar);

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        dkhcAdapter.notifyDataSetChanged();
    }

    private void getDKHODtertinggi(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getDKHODTertinggi();
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
            String strjanjibayar = row.get(i).get("TANGGAL_JANJI_BAYAR");

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
            //data.setTanggalJanjiBayar(strjanjibayar);

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        dkhcAdapter.notifyDataSetChanged();
    }

    private void getDKHODterendah(){
        dbhelper = new DBHelper(getActivity());
        ArrayList<HashMap<String, String>> row = getDKHODTerendah();
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
            String strjanjibayar = row.get(i).get("TANGGAL_JANJI_BAYAR");

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
            //data.setTanggalJanjiBayar(strjanjibayar);

            itemList.add(data);
        }

        swipe.setRefreshing(false);
        dkhcAdapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------

    //-----------------------------------------HELPER------------------------------------------
    public ArrayList<HashMap<String, String>> getData30Days() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS FROM DKH WHERE IS_COLLECT=0 AND DailyCollectibility='Coll Harian' AND PIC='"+employeeID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"'" ;
        String selectQuery = "SELECT A.NOMOR_KONTRAK,A.NAMA_KOSTUMER, A.TOTAL_TAGIHAN, A.TANGGAL_JATUH_TEMPO, A.LATITUDE, A.LONGITUDE, A.OVERDUE_DAYS,A.TANGGAL_JANJI_BAYAR from DKH A LEFT JOIN COLLECTED B ON A.NOMOR_KONTRAK=B.CONTRACT_ID AND A.PERIOD=B.PERIOD where B.CONTRACT_ID IS NULL AND A.PIC = '"+employeeID+"' AND A.DailyCollectibility = 'Coll Harian' AND A.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"'";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHtagihanTerbesar() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 AND PIC='"+employeeID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by TOTAL_TAGIHAN desc limit 5";
        String selectQuery = "SELECT A.NOMOR_KONTRAK,A.NAMA_KOSTUMER, A.TOTAL_TAGIHAN, A.TANGGAL_JATUH_TEMPO, A.LATITUDE, A.LONGITUDE, A.OVERDUE_DAYS,A.TANGGAL_JANJI_BAYAR from DKH A LEFT JOIN COLLECTED B ON A.NOMOR_KONTRAK=B.CONTRACT_ID AND A.PERIOD=B.PERIOD where B.CONTRACT_ID IS NULL AND A.PIC = '"+employeeID+"' AND A.DailyCollectibility = 'Coll Harian' AND A.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by A.TOTAL_TAGIHAN desc limit 5";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHtagihanTerendah() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH LEFT JOIN  WHERE IS_COLLECT = 0 AND PIC='"+employeeID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by TOTAL_TAGIHAN asc limit 5";
        String selectQuery = "SELECT A.NOMOR_KONTRAK,A.NAMA_KOSTUMER, A.TOTAL_TAGIHAN, A.TANGGAL_JATUH_TEMPO, A.LATITUDE, A.LONGITUDE, A.OVERDUE_DAYS,A.TANGGAL_JANJI_BAYAR from DKH A LEFT JOIN COLLECTED B ON A.NOMOR_KONTRAK=B.CONTRACT_ID AND A.PERIOD=B.PERIOD where B.CONTRACT_ID IS NULL AND A.PIC = '"+employeeID+"' AND A.DailyCollectibility = 'Coll Harian' AND A.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by A.TOTAL_TAGIHAN asc limit 5";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHODTerendah() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 AND PIC='"+employeeID+"' order by OVERDUE_DAYS asc limit 5";
        String selectQuery = "SELECT A.NOMOR_KONTRAK,A.NAMA_KOSTUMER, A.TOTAL_TAGIHAN, A.TANGGAL_JATUH_TEMPO, A.LATITUDE, A.LONGITUDE, A.OVERDUE_DAYS,A.TANGGAL_JANJI_BAYAR from DKH A LEFT JOIN COLLECTED B ON A.NOMOR_KONTRAK=B.CONTRACT_ID AND A.PERIOD=B.PERIOD where B.CONTRACT_ID IS NULL AND A.PIC = '"+employeeID+"' AND A.DailyCollectibility = 'Coll Harian' AND A.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by A.OVERDUE_DAYS asc limit 5";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHODTertinggi() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 AND PIC='"+employeeID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by OVERDUE_DAYS desc limit 5";
        String selectQuery = "SELECT A.NOMOR_KONTRAK,A.NAMA_KOSTUMER, A.TOTAL_TAGIHAN, A.TANGGAL_JATUH_TEMPO, A.LATITUDE, A.LONGITUDE, A.OVERDUE_DAYS,A.TANGGAL_JANJI_BAYAR from DKH A LEFT JOIN COLLECTED B ON A.NOMOR_KONTRAK=B.CONTRACT_ID AND A.PERIOD=B.PERIOD where B.CONTRACT_ID IS NULL AND A.PIC = '"+employeeID+"' AND A.DailyCollectibility = 'Coll Harian' AND A.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"' order by A.OVERDUE_DAYS desc limit 5";
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    //----------------------------------------------------
    //-----------------------------------------------------------------

    private void checkCollectibility(final String strContractID){
        String urlCheck = ConnectionHelper.URL + "checkCollectibility.php";
        String tag_json = "tag_json";

        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading..");
            progressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCheck, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) { //Coll Harian
                        TaskDetailFragment fragment = new TaskDetailFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString("paramId", strContractID);
                        fragment.setArguments(arguments);
                        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                        fragmentTransaction.addToBackStack("A_B_TAG");
                        fragmentTransaction.commit();
                    } else { //Non Coll Harian
                        Toast.makeText(getActivity(), "Customer ini sudah membayar dengan metode pembayaran lain", Toast.LENGTH_LONG).show();

                        dbhelper = new DBHelper(getActivity());
                        SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
                        String updateCollectibility = "update DKH set IS_COLLECT=0 and DailyCollectibility='Non Coll Harian' where NOMOR_KONTRAK = "+strContractID;
                        dbInsert.execSQL(updateCollectibility);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROR", error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("contractID", strContractID);
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }

    @Override
    public void onRefresh() {
        init();
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
