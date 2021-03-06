package sfi.mobile.collection.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.helper.ConnectionHelper;
import sfi.mobile.collection.helper.DBHelper;

public class ProgressDetailFragment extends Fragment {

    private static Bitmap bitMapImage;

    public ProgressDetailFragment() {
    }
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
    /*** end memanggil session yang terdaftar ***/

    ProgressDialog progressDialog;

    TextView txtContractID, txtCustomerName, txtResult, txtMeetup, txtContactName, txtHubungan, txtAddress, txtNewAddress, txtUnit, txtAmount, txtSisa, txtLokasiPembayaran, txtLokasiPertemuan, txtJanjiBayar, txtHasilKunjungan,txtTotalTagihanStatus,txtpembayaranStatus,txt_pic,txt_branch,txt_period,txt_status,txt_blob;
    double biaya_admin = 10000;
    double intSisa,intTotal;
    ImageView imgPembayaran, imgPertemuan;
    Button btnUpload;
    ImageButton btnEdit,btnPrint;

    String strMeetup, strContactName, strHubungan, strAddress, strQAddress, strNewAddress, strUnit, strQbayar, strAmount, strSisa, strLatPembayaran, strLngPembayaran, strLatPertemuan, strLngPertemuan, strJanjiBayar, strHasilKunjungan, strCreateDate;

    LinearLayout ln_ketemudengankosumen,ln_contactpersonname,ln_hubungancostumer,ln_alamatkunjungan,ln_alamatbaru,ln_apakah_unitada,ln_pembayaranditerima,ln_sisa_tagihan,ln_lokasiPembayaran,ln_lokasipertemuan,ln_tgljanjibayar,ln_hasilKunjungan,ln_bertemukonsumen,ln_editData,ln_printStruk,ln_sendEmail,ln_print_disable,ln_saveStruk,ln_saveStrukDisable;

    protected Cursor cursor, cursor2;
    DBHelper dbhelper;
    private static final String TAG = ProgressDetailFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_detail, container, false);

        progressDialog = new ProgressDialog(getActivity());

        txtContractID = (TextView) view.findViewById(R.id.contract_id_detail_status);
        txtCustomerName = (TextView) view.findViewById(R.id.customer_name_detail_status);
        txtResult = (TextView) view.findViewById(R.id.hasil_kunjungan);
        txtMeetup = (TextView) view.findViewById(R.id.answer_meetup);
        txtContactName = (TextView) view.findViewById(R.id.answer_contact_name);
        txtHubungan = (TextView) view.findViewById(R.id.answer_hubungan);
        txtAddress = (TextView) view.findViewById(R.id.answer_address);
        txtNewAddress = (TextView) view.findViewById(R.id.answer_new_address);
        txtUnit = (TextView) view.findViewById(R.id.answer_unit);
        txtAmount = (TextView) view.findViewById(R.id.answer_pembayaran);
        txtSisa = (TextView) view.findViewById(R.id.answer_sisa);
        txtLokasiPembayaran = (TextView) view.findViewById(R.id.answer_lokasi_pembayaran);
        txtLokasiPertemuan = (TextView) view.findViewById(R.id.answer_lokasi_pertemuan);
        txtJanjiBayar = (TextView) view.findViewById(R.id.answer_tgl_janji_bayar);
        txtHasilKunjungan = (TextView) view.findViewById(R.id.answer_hasil_kunjungan);
        txt_pic = (TextView) view.findViewById(R.id.txt_PIC);
        txt_branch = (TextView) view.findViewById(R.id.txt_branch);
        txt_period = (TextView) view.findViewById(R.id.txt_period);
        txt_status = (TextView) view.findViewById(R.id.txt_status);

        txtpembayaranStatus = (TextView) view.findViewById(R.id.pembayaran_status);
        txtTotalTagihanStatus = (TextView) view.findViewById(R.id.totaltagihan_status);

        imgPembayaran = (ImageView) view.findViewById(R.id.answer_img_pembayaran);
        imgPertemuan = (ImageView) view.findViewById(R.id.answer_img_pertemuan);

        btnUpload = (Button) view.findViewById(R.id.btnUpload);

        //-------------------------------------------------//
        ln_ketemudengankosumen = (LinearLayout) view.findViewById(R.id.ln_ketemudengankosumen);
        ln_contactpersonname = (LinearLayout) view.findViewById(R.id.ln_contactpersonname);
        ln_alamatkunjungan = (LinearLayout) view.findViewById(R.id.ln_alamatkunjungan);
        ln_hubungancostumer = (LinearLayout) view.findViewById(R.id.ln_hubungancostumer);
        ln_alamatbaru = (LinearLayout) view.findViewById(R.id.ln_alamatbaru);
        ln_apakah_unitada = (LinearLayout) view.findViewById(R.id.ln_apakah_unitada);
        ln_pembayaranditerima = (LinearLayout) view.findViewById(R.id.ln_pembayaranditerima);
        ln_sisa_tagihan = (LinearLayout) view.findViewById(R.id.ln_sisa_tagihan);
        ln_lokasiPembayaran = (LinearLayout) view.findViewById(R.id.ln_lokasiPembayaran);
        ln_lokasipertemuan = (LinearLayout) view.findViewById(R.id.ln_lokasipertemuan);
        ln_tgljanjibayar = (LinearLayout) view.findViewById(R.id.ln_tgljanjibayar);
        ln_hasilKunjungan = (LinearLayout) view.findViewById(R.id.ln_hasilKunjungan);
        ln_bertemukonsumen = (LinearLayout) view.findViewById(R.id.ln_bertemukonsumen);
        ln_editData = (LinearLayout) view.findViewById(R.id.ln_editdata);
        ln_printStruk = (LinearLayout) view.findViewById(R.id.ln_printStruk);
        ln_sendEmail = (LinearLayout) view.findViewById(R.id.ln_sendemail);
        ln_print_disable = (LinearLayout) view.findViewById(R.id.ln_printStruk_disable);
        ln_saveStruk = (LinearLayout) view.findViewById(R.id.ln_saveStruk);
        ln_saveStrukDisable = (LinearLayout) view.findViewById(R.id.ln_saveStrukDisable);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        /*** end Get parameter dari halaman sebelumnya ***/

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String strPic = sharedpreferences.getString(TAG_EMP_ID, null);
        String strBranchID = sharedpreferences.getString(TAG_BRANCH_ID, null);

        txt_pic.setText(strPic);
        txt_branch.setText(strBranchID);

        dbhelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.QUESTION, A.ANSWER, A.CREATE_DATE,B.TOTAL_TAGIHAN,B.PERIOD FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID=B.NOMOR_KONTRAK WHERE A.CONTRACT_ID ='" + paramId +"' AND B.PERIOD = '"+new SimpleDateFormat("yyyyMM").format(new Date())+"01"+"'" ,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            cursor.moveToPosition(0);
            txtContractID.setText(cursor.getString(0));
            txtCustomerName.setText(cursor.getString(1));
            strCreateDate = cursor.getString(4);
            txtTotalTagihanStatus.setText(String.valueOf(Double.parseDouble(cursor.getString(5))));
            txt_period.setText(cursor.getString(6)) ;
            do {
                String questionID = cursor.getString(2);

                //Apakah bertemu dengan customer ?
                if(questionID.equals("MS_Q20190226172031880")){
                    strMeetup = cursor.getString(3);
                }
                //Nama kontak person
                if(questionID.equals("MS_Q20190226172302530")){
                    strContactName = cursor.getString(3);
                }
                //Hubungan Kontak Person dengan Kostumer
                if(questionID.equals("MS_Q20190226172325360")){
                    strHubungan = cursor.getString(3);
                }
                //Alamat yang dikunjungi
                if(questionID.equals("MS_Q20190226172343540")){
                    strAddress = cursor.getString(3);
                }
                //Apakah alamat berubah
                if(questionID.equals("MS_Q20190226172405297")){
                    strQAddress = cursor.getString(3);
                }
                //Alamat Baru
                if(questionID.equals("MS_Q20190226172432320")){
                    strNewAddress = cursor.getString(3);
                }
                //Apakah unit ada
                if(questionID.equals("MS_Q20190226172447930")){
                    strUnit = cursor.getString(3);
                }
                //Apakah customer akan membayar
                if(questionID.equals("MS_Q20190226172517357")){
                    strQbayar = cursor.getString(3);
                }
                //Latitude Lokasi Pembayaran
                if(questionID.equals("MS_Q20190226172558067")){
                    strLatPembayaran = cursor.getString(3);
                }
                //Longitude Lokasi Pembayaran
                if(questionID.equals("MS_Q20190226172603397")){
                    strLngPembayaran = cursor.getString(3);
                }
                //Latitude Lokasi Pertemuan
                if(questionID.equals("MS_Q20190226172624710")){
                    strLatPertemuan = cursor.getString(3);
                }
                //Longitude Lokasi Pertemuan
                if(questionID.equals("MS_Q20190226172628683")){
                    strLngPertemuan = cursor.getString(3);
                }
                //Pembayaran yang diterima
                if(questionID.equals("MS_Q20190226172644783")){
                    strAmount = cursor.getString(3);
                }
                //Foto Lokasi Pembayaran
                if(questionID.equals("MS_Q20190226172753329")){
                    //imgPembayaran = cursor.getString(3);
                }
                //Foto Lokasi Pertemuan
                if(questionID.equals("MS_Q20190226172753330")){
                    //imgPertemuan = cursor.getString(3);
                }
                //Janji Bayar
                if(questionID.equals("MS_Q20190226172810420")){
                    strJanjiBayar = cursor.getString(3);
                }
                //Hasil Kunjungan
                if(questionID.equals("MS_Q20190226172818070")){
                    strHasilKunjungan = cursor.getString(3);
                }
            } while (cursor.moveToNext());

            txtMeetup.setText(strMeetup);
            txtContactName.setText(strContactName);
            txtHubungan.setText(strHubungan);
            txtAddress.setText(strAddress);
            txtNewAddress.setText(strNewAddress);
            txtUnit.setText(strUnit);
            txtAmount.setText(strAmount);
            txtLokasiPembayaran.setText(strLatPembayaran+"\n"+strLngPembayaran);
            txtLokasiPertemuan.setText(strLatPertemuan+"\n"+strLngPertemuan);
            txtJanjiBayar.setText(strJanjiBayar);
            txtHasilKunjungan.setText(strHasilKunjungan);
            txtpembayaranStatus.setText(strAmount);
            //---------------------------------//

            if (strAmount.equals("")){
                txtAmount.setText("Rp. 0");
            }else{
                txtAmount.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(strAmount.toString())).replaceAll("Rp","")));
            }

            /*Log.d(TAG,"Amount->"+txtAmount.getText().toString());
            Log.d(TAG,"Amount2 ->"+txtpembayaranStatus.getText().toString());*/
            /*intTotal = ((Double.parseDouble(txtTotalTagihanStatus.getText().toString()) + biaya_admin)) - Double.parseDouble(txtpembayaranStatus.getText().toString());
            txtSisa.setText("Rp ."+formatRupiah.format((double)intTotal).replaceAll("Rp",""));
*/
            Log.d(TAG,"Amount ->" + txtpembayaranStatus.getText().toString());
            //---------------------------------//
            if(txtpembayaranStatus.getText().equals("0")|| txtpembayaranStatus.getText().equals("")) {
                if(txtJanjiBayar.getText().equals("") || txtJanjiBayar.getText().equals(null) || txtMeetup.getText().equals("Tidak bertemu siapapun") ){
                    txtResult.setText("Tidak bertemu");
                }else {
                    txtResult.setText("Janji Bayar");
                }
            }else{
                txtResult.setText("Customer Membayar");
            }

            if(strQAddress.equals("Ya")){
                ln_alamatbaru.setVisibility(View.VISIBLE);
            }else{
                ln_alamatbaru.setVisibility(View.GONE);
            }
        }

        if(txtResult.getText().equals("Janji Bayar")){
            ln_pembayaranditerima.setVisibility(View.GONE);
            ln_sisa_tagihan.setVisibility(View.GONE);
            ln_lokasiPembayaran.setVisibility(View.GONE);
            ln_print_disable.setVisibility(View.VISIBLE);
            ln_printStruk.setVisibility(View.GONE);
            ln_saveStruk.setVisibility(View.GONE);
            ln_saveStrukDisable.setVisibility(View.VISIBLE);
            txt_status.setText("2");
        }else if (txtResult.getText().equals("Customer Membayar")){
            ln_sisa_tagihan.setVisibility(View.GONE);
            ln_lokasipertemuan.setVisibility(View.GONE);
            ln_tgljanjibayar.setVisibility(View.GONE);
            ln_printStruk.setVisibility(View.VISIBLE);
            ln_print_disable.setVisibility(View.GONE);
            ln_saveStruk.setVisibility(View.VISIBLE);
            ln_saveStrukDisable.setVisibility(View.GONE);
            txt_status.setText("1");
        }else if (txtResult.getText().equals("Tidak bertemu")){
            /*
            ln_hasilKunjungan.setVisibility(View.VISIBLE);
            ln_ketemudengankosumen.setVisibility(View.VISIBLE);
            */
            /*ln_hubungancostumer.setVisibility(View.GONE);
            ln_contactpersonname.setVisibility(View.GONE);
            ln_pembayaranditerima.setVisibility(View.GONE);
            ln_sisa_tagihan.setVisibility(View.GONE);
            ln_pembayaranditerima.setVisibility(View.GONE);
            ln_lokasiPembayaran.setVisibility(View.GONE);
            ln_apakah_unitada.setVisibility(View.GONE);
            ln_lokasipertemuan.setVisibility(View.VISIBLE);*/

            txt_status.setText("3");
            ln_print_disable.setVisibility(View.VISIBLE);
            ln_printStruk.setVisibility(View.GONE);
        }

        if(txtMeetup.getText().equals("Ya, bertemu dengan customer")){
            ln_contactpersonname.setVisibility(View.GONE);
            ln_hubungancostumer.setVisibility(View.GONE);
        }else if(txtMeetup.getText().equals("Tidak, bertemu dengan orang lain")){
            ln_contactpersonname.setVisibility(View.VISIBLE);
            ln_hubungancostumer.setVisibility(View.VISIBLE);
            ln_lokasipertemuan.setVisibility(View.VISIBLE);
        }else if(txtMeetup.getText().equals("Tidak bertemu siapapun")){
            ln_hubungancostumer.setVisibility(View.GONE);
            ln_contactpersonname.setVisibility(View.GONE);
            ln_pembayaranditerima.setVisibility(View.GONE);
            ln_sisa_tagihan.setVisibility(View.GONE);
            ln_pembayaranditerima.setVisibility(View.GONE);
            ln_lokasiPembayaran.setVisibility(View.GONE);
            ln_apakah_unitada.setVisibility(View.GONE);
            ln_lokasipertemuan.setVisibility(View.VISIBLE);
            ln_tgljanjibayar.setVisibility(View.GONE);
            ln_alamatkunjungan.setVisibility(View.GONE);

        }
        if(strLatPembayaran.equals("Lat  : ") && strLngPembayaran.equals("Long : ")){
            Log.d(TAG, "lokasi pembayaran Kosong");
        }else if (!strLatPembayaran.equals("Lat  : ") && !strLngPembayaran.equals("Long : ")){
            //uploadRoute(strLatPembayaran,strLngPembayaran);
            Log.d(TAG, "lokasi pembayaran Ada");
        }

        if(strLatPertemuan.equals("Lat  : ") && strLngPertemuan.equals("Long : ")){
            Log.d(TAG, "lokasi Pertemuan Kosong");
        }else if(!strLatPertemuan.equals("Lat  : ") && !strLngPertemuan.equals("Long : ")){
            //uploadRoute(strLatPertemuan,strLngPertemuan);
            Log.d(TAG, "lokasi pertemuan Ada");
        }
        Log.d(TAG,"lokasi Pembayaran ->" + strLatPembayaran.replaceAll("Lat : ",""));
        Log.d(TAG,"lokasi Pembayaran ->" + strLngPembayaran.replaceAll("Long : ",""));
        Log.d(TAG,"lokasi Pertemuan ->" + strLatPertemuan.replaceAll("Lat : ",""));
        Log.d(TAG,"lokasi Pertemuan ->" + strLngPertemuan.replaceAll("Long : ",""));

        cursor2 = db.rawQuery("SELECT * FROM TBimage WHERE CONTRACT_ID ='" + txtContractID.getText().toString() +"'",null);
        cursor2.moveToFirst();
        if(cursor2.getCount()>0) {
            cursor2.moveToPosition(0);
            if(txtResult.getText().toString().equals("Janji Bayar") ||txtResult.getText().toString().equals("Tidak bertemu")) {
                byte[] IMAGE = cursor2.getBlob(2);
                Bitmap bmp= BitmapFactory.decodeByteArray(IMAGE, 0 , IMAGE.length);
                imgPertemuan.setImageBitmap(bmp);
                /*String imageString = Base64.encodeToString(IMAGE, Base64.DEFAULT);
                Log.d(TAG,"IMG BLOB -> " + imageString);*/
                //return BitmapFactory.decodeByteArray(IMAGE, 0, IMAGE.length);
                //imgPembayaran.setImageResource(convertByteArrayToBitmap(IMAGE));
                //imgPembayaran.setImageBitmap(BitmapFactory.decodeByteArray(IMAGE,0,IMAGE.length));
                //Log.d(TAG,"IMAGE -> "+imgPertemuan);
            }else if (txtResult.getText().toString().equals("Customer Membayar")){
                byte[] IMAGE = cursor2.getBlob(2);
                Bitmap bmp= BitmapFactory.decodeByteArray(IMAGE, 0 , IMAGE.length);
                imgPembayaran.setImageBitmap(bmp);
                //return BitmapFactory.decodeByteArray(IMAGE, 0, IMAGE.length);
                //imgPembayaran.setImageResource(convertByteArrayToBitmap(IMAGE));
                //imgPembayaran.setImageBitmap(BitmapFactory.decodeByteArray(IMAGE,0,IMAGE.length));
                //Log.d(TAG,"IMAGE -> "+imgPembayaran);
            }
        }
    /*  //--------------------------------------------------------//
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract_id = ((TextView) getActivity().findViewById(R.id.contract_id_detail_status)).getText().toString();
                PrintFragment fragment = new PrintFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "paramId" , contract_id);
                Log.d(TAG,"Contract ID -> " + contract_id);
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper,fragment).commit();
            }
        });*/

        /*ln_editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Edit Data", Toast.LENGTH_SHORT).show();
            }
        });

        ln_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Send Email", Toast.LENGTH_SHORT).show();
            }
        });*/

        ln_printStruk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract_id = ((TextView) getActivity().findViewById(R.id.contract_id_detail_status)).getText().toString();
                PrintFragment fragment = new PrintFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "paramId" , contract_id);
                Log.d(TAG,"Contract ID -> " + contract_id);
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                fragmentTransaction.addToBackStack("A_B_TAG");
                fragmentTransaction.commit();
            }
        });

        ln_saveStruk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintImage fragment = new PrintImage();
                Bundle arguments = new Bundle();
                arguments.putString( "paramId" , txtContractID.getText().toString());
                fragment.setArguments(arguments);
                Log.d(TAG,"ContractID -> " + txtContractID.getText().toString());
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                fragmentTransaction.addToBackStack("A_B_TAG");
                fragmentTransaction.commit();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                //adb.setView(alertDialogView);
                adb.setTitle("Konfirmasi");
                adb.setMessage("Apakah anda yakin akan mengupload data ini ?");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uploadResultHeader();
                        String questionID = "";
                        String answer = "";
                        int loop = 0;
                        for (int i = 1; i <= 17; i++) {
                            if (i == 1) {
                                //Apakah Bertemu dengan Kostumer
                                questionID = "MS_Q20190226172031880";
                                answer = strMeetup;
                            } else if (i == 2) {
                                //Nama Kontak Person
                                questionID = "MS_Q20190226172302530";
                                answer = strContactName;
                            } else if (i == 3) {
                                //Hubungan Kontak Person dengan Kostumer
                                questionID = "MS_Q20190226172325360";
                                answer = strHubungan;
                            } else if (i == 4) {
                                //Alamat yang dikunjungi
                                questionID = "MS_Q20190226172343540";
                                answer = strAddress;
                            } else if (i == 5) {
                                //Apakah alamat berubah
                                questionID = "MS_Q20190226172405297";
                                answer = strQAddress;
                            } else if (i == 6) {
                                //Alamat Baru
                                questionID = "MS_Q20190226172432320";
                                answer = strNewAddress;
                            } else if (i == 7) {
                                //Apakah unit ada
                                questionID = "MS_Q20190226172447930";
                                answer = strUnit;
                            } else if (i == 8) {
                                //Apakah Kostumer Akan Membayar
                                questionID = "MS_Q20190226172517357";
                                answer = strQbayar;
                            } else if (i == 9) {
                                //Latitude Lokasi Pembayaran
                                questionID = "MS_Q20190226172558067";
                                answer = strLatPembayaran;
                            } else if (i == 10) {
                                //Longitude Lokasi Pembayaran
                                questionID = "MS_Q20190226172603397";
                                answer = strLngPembayaran;
                            } else if (i == 11) {
                                //Latitude Lokasi Pertemuan
                                questionID = "MS_Q20190226172624710";
                                answer = strLatPertemuan;
                            } else if (i == 12) {
                                //Longitude Lokasi Pertemuan
                                questionID = "MS_Q20190226172628683";
                                answer = strLngPertemuan;
                            } else if (i == 13) {
                                //Pembayaran yang diterima
                                questionID = "MS_Q20190226172644783";
                                answer = strAmount;
                            } else if (i == 14) {
                                //Foto Lokasi Pembayaran
                                questionID = "MS_Q20190226172753329";
                                answer = "";
                            } else if (i == 15) {
                                //Foto Lokasi Pertemuan
                                questionID = "MS_Q20190226172753329";
                                answer = "";
                            } else if (i == 16) {
                                //Janji Bayar
                                questionID = "MS_Q20190226172810420";
                                answer = strJanjiBayar;
                            } else if (i == 17) {
                                //Hasil Kunjungan
                                questionID = "MS_Q20190226172818070";
                                answer = strHasilKunjungan;
                            }
                            uploadResult(questionID, answer);
                            loop++;
                            Log.d(TAG, "Nilai i ->" + i);
                            Log.d(TAG, "Looping ke ->" + loop);
                            Log.d(TAG, "Question ->" + questionID);
                            Log.d(TAG, "Answer ->" + answer);

                            dbhelper = new DBHelper(getActivity());
                            SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
                            String Sql = "update DKH set IS_COLLECT=1, DailyCollectibility='Coll non Harian' where NOMOR_KONTRAK=" + txtContractID.getText().toString();
                            String Sql2 = "update COLLECTED set IS_COLLECT=1, DailyCollectibility='Coll non Harian' where CONTRACT_ID=" + txtContractID.getText().toString();
                            //dbInsert.execSQL(Sql);
                            dbInsert.execSQL(Sql2);
                        }

                        //SaveResultHeader();
                        if(loop == 17){
                            Log.e(TAG, "Data berhasil dikirim ke server");
                        }

                        if(strLatPembayaran.equals("Lat  : ") && strLngPembayaran.equals("Long : ")){
                            Log.d(TAG, "lokasi pembayaran Kosong");
                        }else if (!strLatPembayaran.equals("Lat  : ") && !strLngPembayaran.equals("Long : ")){
                            uploadRoute(strLatPembayaran,strLngPembayaran);
                            //SaveRoute(strLatPembayaran,strLngPembayaran);
                            Log.d(TAG, "lokasi pembayaran Ada");
                        }

                        if(strLatPertemuan.equals("Lat  : ") && strLngPertemuan.equals("Long : ")){
                            Log.d(TAG, "lokasi Pertemuan Kosong");
                        }else if(!strLatPertemuan.equals("Lat  : ") && !strLngPertemuan.equals("Long : ")){
                            uploadRoute(strLatPertemuan,strLngPertemuan);
                            //SaveRoute(strLatPertemuan,strLngPertemuan);
                            Log.d(TAG, "lokasi pertemuan Ada");
                        }

                        UploadFragment fragment = new UploadFragment();
                        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_container_wrapper, fragment).commit();
                    }
                });
                adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();
            }
        });
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        bitMapImage = BitmapFactory.decodeByteArray(
                byteArrayToBeCOnvertedIntoBitMap, 0,
                byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }

   /* private void SaveResultHeader(){
        String getDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String strContractID = txtContractID.getText().toString();
        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
        String Saved = "INSERT INTO RESULT_HEADER (CONTRACT_ID,STATUS,PIC,CREATE_DATE,UPLOAD_DATE,PERIOD) VALUES('"+ strContractID +"','"+txt_status.getText().toString()+"','"+txt_pic.getText().toString()+"','"+getDate+"','"+getDate+"','"+txt_period.getText().toString()+"')";
        dbInsert.execSQL(Saved);
    }

    private void SaveRoute(final String strLat,final String strLng){
        String getDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String strPIC = txt_pic.getText().toString();
        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
        String Saved = "INSERT INTO ROUTE (PIC,LAT,LNG,CREATE_DATE) VALUES('"+ strPIC +"','"+strLat+"','"+strLng+"','"+getDate+"')";
        dbInsert.execSQL(Saved);
    }*/

    private void uploadResult(final String strQuestion, final String strAnswer) {
        String urlUploadData = ConnectionHelper.URL+"saveResult.php";
        String tag_json = "tag_json";

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang upload Data...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUploadData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("response", response.toString());
                hideDialog();
                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
                    } else {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
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
                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();

                //*//**//**//**//*** set session to variable ***//**//**//**//*
                //*//**//**//**//*** end set session to variable ***//**//**//**//*
                Log.d(TAG,"Masuk Upload result detail");

                param.put("contractID",txtContractID.getText().toString());
                param.put("questionID",strQuestion);
                param.put("answer",strAnswer);
                param.put("savedDate",strCreateDate);
                param.put("period",txt_period.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }

    private void uploadResultHeader() {
        String urlUploadResultHeader = ConnectionHelper.URL+"SaveResulHeader.php";
        String tag_json = "tag_json";

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang upload Data...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUploadResultHeader, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("response", response.toString());
                hideDialog();

                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
                    } else {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
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
                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();

                //*//**//**//**//*** set session to variable ***//**//**//**//*
                //*//**//**//**//*** end set session to variable ***//**//**//**//*
                Log.d(TAG,"Masuk Upload header");

                param.put("contractID", txtContractID.getText().toString());
                param.put("status",txt_status.getText().toString());
                param.put("pic", txt_pic.getText().toString());
                param.put("savedDate", strCreateDate);
                param.put("period",txt_period.getText().toString());
                param.put("branchID", txt_branch.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }

    private void uploadAddress() {
        String urlUploadAdress = ConnectionHelper.URL+"";
        String tag_json = "tag_json";

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang upload Data...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUploadAdress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("response", response.toString());
                hideDialog();

                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
                    } else {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
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

                //*//**//**//**//*** set session to variable ***//**//**//**//*
                //*//**//**//**//*** end set session to variable ***//**//**//**//*
                Log.d(TAG,"Masuk Sini adress");

                param.put("contractID", txtContractID.getText().toString());
                param.put("addressType", strCreateDate);
                param.put("addressNew",txt_status.getText().toString());
                param.put("pic", txt_pic.getText().toString());
                param.put("branchID", txt_branch.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }

    private void uploadRoute(final String strLat, final String strLng) {
        String urlUploadRoute = ConnectionHelper.URL+"saveRouteNew.php";
        String tag_json = "tag_json";

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang upload Data...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUploadRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("response", response.toString());
                hideDialog();

                try {
                    JSONObject jObject = new JSONObject(response);
                    String pesan = jObject.getString("pesan");
                    String hasil = jObject.getString("result");
                    if (hasil.equalsIgnoreCase("true")) {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
                    } else {
                        //Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,pesan);
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

                //*//**//**//**//*** set session to variable ***//**//**//**//*
                //*//**//**//**//*** end set session to variable ***//**//**//**//*
                Log.d(TAG,"Masuk sini Route");

                param.put("pic", txt_pic.getText().toString());
                param.put("lat", strLat.replaceAll("Lat : ",""));
                param.put("lng", strLng.replaceAll("Long : ",""));
                param.put("contract_id", txtContractID.getText().toString());
                param.put("period", txt_period.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json);
    }
//------------------------
    /*abstract class Asyn_Task extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        // can use UI thread here
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Void doInBackground(String... params) {
            uploadData(strQuestion,strAnswer );
            // Do your all Stuffs
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }*/

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}