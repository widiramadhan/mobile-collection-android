package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;
import sfi.mobile.collection.util.DialogUtil;

public class CheckData extends AppCompatActivity {

    private static ProgressDialog progressDialog;
    DBHelper dbhelper;
    private static ProgressDialog myDialog;

    private static final String TAG = CheckData.class.getSimpleName();

    /*public static void main(String args[]){
        System.out.println("Hello World");
    }*/

    public void saveDataSQLite(final String employeeID, final String branchID, Context activityContext){
        String urlGetDKHC = ConnectionHelper.URL + "getTasklist.php";

        /*if (progressDialog == null) {
            progressDialog = new ProgressDialog(activityContext);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }*/

        myDialog= DialogUtil.showProgressDialog(activityContext,"some message");

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

                                dbhelper = new DBHelper(getApplicationContext());
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
                        //progressDialog.hide();
                        myDialog.dismiss();
                        //swipe.setRefreshing(false);
                        //getAllDKHC();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, mohon hubungi administrator", Toast.LENGTH_LONG).show();
                //progressDialog.hide();
                myDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jArr);
    }
}
