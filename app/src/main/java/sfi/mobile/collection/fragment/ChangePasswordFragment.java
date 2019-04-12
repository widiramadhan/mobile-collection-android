package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import sfi.mobile.collection.MainActivity;
import sfi.mobile.collection.R;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;

public class ChangePasswordFragment extends Fragment {

    public ChangePasswordFragment() {
    }

    TextView txtPasswordLama, txtPasswordBaru, txtKonfirmasiPasswordBaru;
    Button btnUpdate;

    private static final String TAG = ChangePasswordFragment.class.getSimpleName();
    ProgressDialog pd;

    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String userName;
    /*** end memanggil session yang terdaftar ***/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        txtPasswordLama = (TextView) view.findViewById(R.id.txt_password_lama);
        txtPasswordBaru = (TextView) view.findViewById(R.id.txt_password_baru);
        txtKonfirmasiPasswordBaru = (TextView) view.findViewById(R.id.txt_password_konfirmasi);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

        /*** set session to variable ***/
        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        userName = sharedpreferences.getString(TAG_USERNAME, null);
        Log.e(TAG,"username -> "+userName);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(getActivity());

                String passwordLama = txtPasswordLama.getText().toString().trim();
                String passwordBaru = txtPasswordBaru.getText().toString().trim();
                String konfirmasiPassword = txtKonfirmasiPasswordBaru.getText().toString().trim();

                if(passwordLama.isEmpty() || passwordBaru.isEmpty() || konfirmasiPassword.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(), "Harap Lengkapi Data", Toast.LENGTH_LONG).show();
                }else{
                    updatePassword(userName, passwordLama, passwordBaru, konfirmasiPassword);
                }
            }
        });

        return view;
    }

    private void updatePassword(final String strUsername, final String strPasswordLama, final String strPasswordBaru, final String strKonfirmasiPassword){
        String url_simpan = ConnectionHelper.URL + "updatePassword.php";
        String tag_json = "tag_json";

        if(pd == null){
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Loading..");
            showDialog();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_simpan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response.toString());
                hideDialog();

                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) {
                        Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();

                        HomeFragment fragment = new HomeFragment();
                        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
                    } else {
                        Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
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
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("userName", strUsername);
                param.put("passwordLama", strPasswordLama);
                param.put("passwordBaru", strPasswordBaru);
                param.put("konfirmasiPassword", strKonfirmasiPassword);
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }

    private void showDialog() {
        if (!pd.isShowing())
            pd.show();
    }

    private void hideDialog() {
        if (pd.isShowing())
            pd.dismiss();
    }
}
