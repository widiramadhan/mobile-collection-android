package sfi.mobile.collection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import sfi.mobile.collection.R;

import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.fragment.TaskListDetailFragment;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.model.DKHC;
import sfi.mobile.collection.model.TaskList;
import sfi.mobile.collection.util.HttpsTrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    ProgressDialog pDialog;
    EditText txtusername, txtpassword;
    Button btnLogin;
    TextView btnRegister;

    int success;
    ConnectivityManager conMgr;

    private String url = ConnectionHelper.URL + "checkLogin.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID = "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    String tag_json_obj = "json_obj_req";
    SharedPreferences sharedpreferences;
    Boolean session = false;
    String userId, username, fullName, branchId, empId, empJobId, branchName;

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    final String MESSAGE_NO_INTERNET_ACCESS = "No Internet Connection";
    final String MESSAGE_CANNOT_BE_EMPTY = "Kolom Tidak Boleh Kosong";
    final String MESSAGE_LOGIN = "Logging in ...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), MESSAGE_NO_INTERNET_ACCESS,
                        Toast.LENGTH_LONG).show();
            }
        }

        btnLogin = (Button) findViewById(R.id.btn_login);
        txtusername = (EditText) findViewById(R.id.txt_username);
        txtpassword = (EditText) findViewById(R.id.txt_password);

        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        userId = sharedpreferences.getString(TAG_USER_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        branchId = sharedpreferences.getString(TAG_BRANCH_ID, null);
        empId = sharedpreferences.getString(TAG_EMP_ID, null);
        empJobId = sharedpreferences.getString(TAG_EMP_JOB_ID, null);
        branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);

        if (session) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra(TAG_USER_ID, userId);
            intent.putExtra(TAG_USERNAME, username);
            intent.putExtra(TAG_FULL_NAME, fullName);
            intent.putExtra(TAG_BRANCH_ID, branchId);
            intent.putExtra(TAG_EMP_ID, empId);
            intent.putExtra(TAG_EMP_JOB_ID, empJobId);
            intent.putExtra(TAG_BRANCH_NAME, branchName);
            finish();
            startActivity(intent);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username = txtusername.getText().toString();
                String password = txtpassword.getText().toString();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext(), MESSAGE_NO_INTERNET_ACCESS, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), MESSAGE_CANNOT_BE_EMPTY, Toast.LENGTH_LONG).show();
                    /*Intent intent = new Intent(Login.this, MainActivity.class);
                    finish();
                    startActivity(intent);*/
                }
            }
        });
    }

    private void checkLogin(final String username, final String password) {
        HttpsTrustManager.allowAllSSL();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(MESSAGE_LOGIN);
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String user_id = jObj.getString(TAG_USER_ID);
                        String user_name = jObj.getString(TAG_USERNAME);
                        String full_name = jObj.getString(TAG_FULL_NAME);
                        String branch_id = jObj.getString(TAG_BRANCH_ID);
                        String emp_id = jObj.getString(TAG_EMP_ID);
                        String emp_job_id = jObj.getString(TAG_EMP_JOB_ID);
                        String branch_name = jObj.getString(TAG_BRANCH_NAME);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_USER_ID, user_id);
                        editor.putString(TAG_USERNAME, user_name);
                        editor.putString(TAG_FULL_NAME, full_name);
                        editor.putString(TAG_BRANCH_ID, branch_id);
                        editor.putString(TAG_EMP_ID, emp_id);
                        editor.putString(TAG_EMP_JOB_ID, emp_job_id);
                        editor.putString(TAG_BRANCH_NAME, branch_name);
                        editor.commit();

                        // Memanggil halaman setelah login
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra(TAG_USER_ID, user_id);
                        intent.putExtra(TAG_USERNAME, full_name);
                        intent.putExtra(TAG_FULL_NAME, fullName);
                        intent.putExtra(TAG_BRANCH_ID, branch_id);
                        intent.putExtra(TAG_EMP_ID, emp_id);
                        intent.putExtra(TAG_EMP_JOB_ID, emp_job_id);
                        intent.putExtra(TAG_BRANCH_NAME, branch_name);
                        finish();
                        startActivity(intent);

                        //saveDataSQLite();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        Log.e("username : ", username);
                        Log.e("password : ",password);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                /*Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();*/
                Toast.makeText(getApplicationContext(), "Login Error, Silahkan hubungi administrator", Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /*private void saveDataSQLite(){
        String urlGetDKHC = "";
        HttpsTrustManager.allowAllSSL();
        JsonArrayRequest jArr = new JsonArrayRequest(urlGetDKHC,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                DKHC j = new DKHC();
                                j.setNomorKontrak(obj.getString("CONTRACT_ID"));
                                j.setNamaKustomer(obj.getString("NAMA_KOSTUMER"));
                                j.setTanggalJatuhTempo(obj.getString("TANGGAL_JATUH_TEMPO"));
                                j.setOverDueDays(obj.getString("OVERDUE_DAYS"));
                                j.setAngsuranKe(obj.getInt("ANGSURAN_KE"));
                                j.setJumlahAngsuranOverdue(obj.getInt("JUMLAH_ANGSURAN_OVERDUE"));
                                j.setTenor(obj.getInt("TENOR"));
                                j.setAngsuranBerjalan(obj.getInt("ANGSURAN_BERJALAN"));
                                j.setAngsuranTertunggak(obj.getInt("ANGSURAN_TERTUNGGAK"));
                                j.setDenda(obj.getInt("DENDA"));
                                j.setTitipan(obj.getInt("TITIPAN"));
                                j.setTotalTagihan(obj.getInt("TOTAL_TAGIHAN"));
                                j.setOutstandingAR(obj.getInt("OUTSTANDING_AR"));
                                j.setAlamatKTP(obj.getString("ALAMAT_KTP"));
                                j.setNomorTelpRumah(obj.getString("NOMOR_TELEPON_RUMAH"));
                                j.setNomorHandphone(obj.getString("NOMOR_HANDPHONE"));
                                j.setAlamatKantor(obj.getString("ALAMAT_KANTOR"));
                                j.setNomorTelpKantor(obj.getString("NOMOR_TELEPON_KANTOR"));
                                j.setAlamatSurat(obj.getString("ALAMAT_SURAT"));
                                j.setNomorTelpSurat(obj.getString("NOMOR_TELEPON_SURAT"));
                                j.setPicTerakhir(obj.getString("PIC_TERAKHIR"));
                                j.setPenangananTerakhir(obj.getString("PENANGANAN_TERAKHIR"));
                                j.setTanggalJanjiBayar(obj.getString("TANGGAL_JANJI_BAYAR"));
                                j.setLat(obj.getString("LAT"));
                                j.setLng(obj.getString("LNG"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, mohon hubungi administrator", Toast.LENGTH_LONG).show();
            }
        });
        // menambah permintaan ke queue
        AppController.getInstance().addToRequestQueue(jArr);
    }*/
}
