package sfi.mobile.collection.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.model.DetailTasklist;
import sfi.mobile.collection.model.Question;
import sfi.mobile.collection.util.HttpsTrustManager;

public class QuestionFragment2 extends Fragment {
    private static final String TAG = QuestionFragment2.class.getSimpleName();

    EditText ContractId,Customer_name,telepon,overduedays,jumlah_angsuran,duedate,alamat;

    ProgressDialog progressDialog;
    String url = ConnectionHelper.URL + "getTasklistByID.php";

    public QuestionFragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question2, container, false);

        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        String paramId = arguments.getString("paramId");
        /*** end Get parameter dari halaman sebelumnya ***/

        getData(paramId);
        //Toast.makeText(getContext(),paramId,Toast.LENGTH_LONG).show();
    }

    private void getData(String contractID) {
        HttpsTrustManager.allowAllSSL();
        if (progressDialog == null) {
            // in standard case YourActivity.this
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        JsonArrayRequest jArr = new JsonArrayRequest(url + "?contractID=" + contractID,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Question j = new Question();
                                j.setContractID(obj.getString("ContractID"));
                                j.setCustomerName(obj.getString("CustomerName"));
                                j.setMobilePhone(obj.getString("MobilePhone"));
                                j.setDaysOD(obj.getString("DaysOD"));
                                j.setAngsuran(obj.getString("Jumlah_AngsuranOD"));
                                j.setDuedate(obj.getString("Duedate"));
                                j.setAlamat(obj.getString("Alamat"));


                                /*ContractId = (EditText) getActivity().findViewById(R.id.contract_id);
                                Customer_name = (EditText) getActivity().findViewById(R.id.customer_name);
                                telepon = (EditText) getActivity().findViewById(R.id.telepon);
                                overduedays = (EditText) getActivity().findViewById(R.id.oveduedays);
                                jumlah_angsuran = (EditText) getActivity().findViewById(R.id.jumlah_angsuran);
                                //duedate = (EditText) getActivity().findViewById(R.id.duedate);
                                alamat = (EditText) getActivity().findViewById(R.id.alamatktp);

                                Customer_name.setText(j.getCustomerName());
                                ContractId.setText(j.getContractID());
                                telepon.setText(j.getMobilePhone());
                                overduedays.setText(j.getDaysOD());
                                jumlah_angsuran.setText(j.getAngsuran());
                                duedate.setText(j.getDuedate());
                                alamat.setText(j.getAlamat());*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jArr);
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
