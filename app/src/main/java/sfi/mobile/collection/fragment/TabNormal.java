package sfi.mobile.collection.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.adapter.DKHCAdapter;
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
                TaskDetailFragment fragment = new TaskDetailFragment();
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
        getAllDKHC();
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
