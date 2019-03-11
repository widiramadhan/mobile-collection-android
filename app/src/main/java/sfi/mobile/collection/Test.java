package sfi.mobile.collection;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.model.DetailTasklist;
import sfi.mobile.collection.util.HttpsTrustManager;

public class Test extends AppCompatActivity {

    ProgressDialog progressDialog;
    private static final String TAG = Test.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        getData();
    }

    private void getData() {
        HttpsTrustManager.allowAllSSL();
        String urlGetDKHC = ConnectionHelper.URL + "getTasklist.php";
        if (progressDialog == null) {
            // in standard case YourActivity.this
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        JsonArrayRequest jArr = new JsonArrayRequest(urlGetDKHC,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "Hasil response -> "+response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Log.e(TAG,"Masuk sini akhirnya Ya Allah");
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
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                progressDialog = null;            }
        });
        AppController.getInstance().addToRequestQueue(jArr);

    }
}
