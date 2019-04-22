package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;

public class HomeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ResultFragment.class.getSimpleName();

    public HomeFragment() {
    }

    SharedPreferences sharedpreferences;
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";

    SwipeRefreshLayout swipe;
    LinearLayout ln_task_new, ln_task_draft, ln_task_done;
    TextView txtName, txtTotalTask, txtTotalDone, txtTotalDraft;

    DBHelper dbhelper;
    protected Cursor cursor, cursor2, cursor3;

    ProgressDialog progressDialog;

    String branchID, employeeID, employeeJobID,branchName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ln_task_new = (LinearLayout) view.findViewById(R.id.new_task);
        ln_task_draft = (LinearLayout) view.findViewById(R.id.task_draft);
        ln_task_done = (LinearLayout) view.findViewById(R.id.task_done);
        txtName = (TextView) view.findViewById(R.id.hello_name);
        txtTotalTask = (TextView) view.findViewById(R.id.total_task);
        txtTotalDone = (TextView) view.findViewById(R.id.total_done);
        txtTotalDraft = (TextView) view.findViewById(R.id.total_draft);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        txtName.setText("Hello,\n"+fullName);
        branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        employeeJobID = sharedpreferences.getString(TAG_EMP_JOB_ID, null);
        //branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);
        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {
                checkData();
            }
        });

        ln_task_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFragment fragment = new TaskFragment();
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        ln_task_draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressFragment fragment = new ProgressFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "flag" , "0");
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        ln_task_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressFragment fragment = new ProgressFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "flag" , "1");
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
    }

    private void checkData() {
        //cek koneksi
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info == null){ //jika tidak ada koneksi
            Log.e(TAG, "Tidak ada koneksi");
            dbhelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM DKH WHERE PIC = '"+employeeID+"' AND BRANCH_ID = '"+branchID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount() == 0){
                Toast.makeText(getActivity(), "Membutuhkan koneksi internet untuk synchonize data", Toast.LENGTH_LONG).show();
            }
        }else { //jika ada koneksi
            Log.e(TAG, "Ada koneksi");
            //cek data ada atau tidak
            dbhelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM DKH WHERE PIC = '"+employeeID+"' AND BRANCH_ID = '"+branchID+"' AND PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"'",null);
            cursor.moveToFirst();
            if(cursor.getCount() == 0){ //jika data tidak ada
                Log.e(TAG, "Data di sqlite belum ada, insert data....");
                saveDataSQLite(employeeID, branchID);
            }else {
                Log.e(TAG, "Data di sqlite sudah ada, load data....");
                swipe.setRefreshing(false);
                CountData();
            }
        }
    }

    public void saveDataSQLite(final String employeeID, final String branchID){
        String urlGetDKHC = ConnectionHelper.URL + "getTasklist.php"+"?employeeID="+employeeID+"&branchID="+branchID;

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
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
                                j.setColAreaID(obj.getDouble("M_AREA_COLL_ID"));
                                j.setCreateUser(obj.getString("CREATE_USER"));
                                j.setCreateDate(obj.getString("CREATE_DATE"));
                                j.setStatusVoid(obj.getInt("VOID"));

                                dbhelper = new DBHelper(getActivity().getApplicationContext());
                                dbhelper.insertDataDKH(j.getBranchID(), j.getBranchName(), j.getPic(), j.getNomorKontrak(), j.getNamaKostumer(), j.getTanggalJatuhTempo(),
                                        j.getOverDueDays(), j.getAngsuranKe(), j.getJumlahAngsuranOverDue(), j.getTenor(), j.getAngsuranBerjalan(), j.getAngsuranTertunggak(),
                                        j.getDenda(), j.getTitipan(), j.getTotalTagihan(), j.getOutstandingAR(), j.getAlamatKTP(), j.getNomorTlpRumah(), j.getNomorHanphone(),
                                        j.getAlamatKantor(), j.getNomorTlpKantor(), j.getAlamatSurat(), j.getNomorTlpSurat(), j.getPicTerakhir(), j.getPenangananTerakhir(),
                                        j.getTanggalJanjiBayar(), j.getDailyCollectibility(), j.getOdHarian(), j.getTanggalJatuhTempoHarian(), j.getARin(), j.getFlowUp(),
                                        j.getTanggalTarikHarian(), j.getTanggalTerimaKlaim(), j.getLat(), j.getLng(), j.getApproval(), j.getIsCollect(), j.getPeriod(),  j.getColAreaID(),
                                        j.getCreateUser(), j.getCreateDate(), j.getStatusVoid() );
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Masuk catch");
                            }
                        }
                        progressDialog.hide();
                        swipe.setRefreshing(false);
                        CountData();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan, mohon hubungi administrator", Toast.LENGTH_LONG).show();
                progressDialog.hide();
                swipe.setRefreshing(false);
            }
        });
        AppController.getInstance().addToRequestQueue(jArr);
    }

    @Override
    public void onRefresh() {
        checkData();
    }

    private void CountData(){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        //cek jumlah task yang ada
        cursor = db.rawQuery("SELECT * FROM DKH WHERE IS_COLLECT = 0 AND DailyCollectibility = 'Coll Harian' AND PIC = '"+employeeID+"'" ,null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        txtTotalTask.setText(String.valueOf(count));

        //cek jumlah task yang sudah di upload ke server
        cursor2 = db.rawQuery("SELECT * FROM DKH WHERE IS_COLLECT = 1 AND DailyCollectibility = 'Coll Harian' AND PIC = '"+employeeID+"'",null);
        cursor2.moveToFirst();
        int count2 = cursor2.getCount();
        txtTotalDone.setText(String.valueOf(count2));

        //cek jumlah task yang ada di draft
        cursor3 = db.rawQuery("SELECT * FROM DKH WHERE IS_COLLECT = 2 AND DailyCollectibility = 'Coll Harian' AND PIC = '"+employeeID+"'",null);
        cursor3.moveToFirst();
        int count3 = cursor3.getCount();
        txtTotalDraft.setText(String.valueOf(count3));
    }
}
