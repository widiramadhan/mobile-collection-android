package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;

public class ProgressDetailFragmentHistory extends Fragment {

    private static Bitmap bitMapImage;

    public ProgressDetailFragmentHistory(){

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

    TextView txtContractID, txtCustomerName, txtResult, txtMeetup, txtContactName, txtHubungan, txtAddress, txtNewAddress, txtUnit, txtAmount, txtSisa, txtLokasiPembayaran, txtLokasiPertemuan, txtJanjiBayar, txtHasilKunjungan,txtTotalTagihanStatus,txtpembayaranStatus,txt_pic,txt_branch;
    double biaya_admin = 10000;
    double intSisa,intTotal;
    ImageView imgPembayaran, imgPertemuan;
    Button btnUpload;
    ImageButton btnEdit,btnPrint;

    String strMeetup, strContactName, strHubungan, strAddress, strQAddress, strNewAddress, strUnit, strQbayar, strAmount, strSisa, strLatPembayaran, strLngPembayaran, strLatPertemuan, strLngPertemuan, strJanjiBayar, strHasilKunjungan, strCreateDate;

    LinearLayout ln_ketemudengankosumen,ln_contactpersonname,ln_hubungancostumer,ln_alamatkunjungan,ln_alamatbaru,ln_apakah_unitada,ln_pembayaranditerima,ln_sisa_tagihan,ln_lokasiPembayaran,ln_lokasipertemuan,ln_tgljanjibayar,ln_hasilKunjungan,ln_bertemukonsumen,ln_editData,ln_saveStrukDisable,ln_saveStruk,ln_printStruk,ln_sendEmail,ln_btnUpload,ln_print_disable;

    protected Cursor cursor, cursor2;
    DBHelper dbhelper;
    private static final String TAG = ProgressDetailFragmentHistory.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_detail_history, container, false);

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
        ln_btnUpload = (LinearLayout) view.findViewById(R.id.buttons);
        ln_saveStruk = (LinearLayout) view.findViewById(R.id.ln_saveStruk);
        ln_saveStrukDisable = (LinearLayout) view.findViewById(R.id.ln_saveStrukDisable);
        ln_print_disable = (LinearLayout) view.findViewById(R.id.ln_printStruk_disable);

    return view;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        ln_btnUpload.setVisibility(View.GONE);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        /*** end Get parameter dari halaman sebelumnya ***/

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        String strPic = sharedpreferences.getString(TAG_EMP_ID, null);
        String strBranchID = sharedpreferences.getString(TAG_BRANCH_ID, null);

       /* txt_pic.setText(strPic);
        txt_pic.setText(strBranchID);*/

        dbhelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.QUESTION, A.ANSWER, A.CREATE_DATE,B.TOTAL_TAGIHAN FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID=B.NOMOR_KONTRAK WHERE A.CONTRACT_ID ='" + paramId +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            cursor.moveToPosition(0);
            txtContractID.setText(cursor.getString(0));
            txtCustomerName.setText(cursor.getString(1));
            strCreateDate = cursor.getString(4);
            txtTotalTagihanStatus.setText(String.valueOf(Double.parseDouble(cursor.getString(5))));

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

            txtAmount.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(strAmount.toString())).replaceAll("Rp","")));
            /*Log.d(TAG,"Amount->"+txtAmount.getText().toString());
            Log.d(TAG,"Amount2 ->"+txtpembayaranStatus.getText().toString());*/
            intTotal = ((Double.parseDouble(txtTotalTagihanStatus.getText().toString()) + biaya_admin)) - Double.parseDouble(txtpembayaranStatus.getText().toString());
            txtSisa.setText("Rp ."+formatRupiah.format((double)intTotal).replaceAll("Rp",""));

            /*Log.d(TAG,"Sisa ->" + intTotal);*/
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
        }else if (txtResult.getText().equals("Customer Membayar")){
            ln_sisa_tagihan.setVisibility(View.GONE);
            ln_lokasipertemuan.setVisibility(View.GONE);
            ln_tgljanjibayar.setVisibility(View.GONE);
            ln_printStruk.setVisibility(View.VISIBLE);
            ln_print_disable.setVisibility(View.GONE);
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

        cursor2 = db.rawQuery("SELECT * FROM TBimage WHERE CONTRACT_ID ='" + paramId +"'",null);
        cursor2.moveToFirst();
        if(cursor2.getCount()>0) {
            cursor2.moveToPosition(0);
            if(txtResult.getText().toString().equals("Janji Bayar")){
                byte[] IMAGE = cursor2.getBlob(2);
                Bitmap bmp= BitmapFactory.decodeByteArray(IMAGE, 0 , IMAGE.length);
                imgPertemuan.setImageBitmap(bmp);
                //return BitmapFactory.decodeByteArray(IMAGE, 0, IMAGE.length);
                //imgPembayaran.setImageResource(convertByteArrayToBitmap(IMAGE));
                //imgPembayaran.setImageBitmap(BitmapFactory.decodeByteArray(IMAGE,0,IMAGE.length));
                Log.d(TAG,"IMAGE -> "+imgPertemuan);
            }else{
                byte[] IMAGE = cursor2.getBlob(2);
                Bitmap bmp= BitmapFactory.decodeByteArray(IMAGE, 0 , IMAGE.length);
                imgPembayaran.setImageBitmap(bmp);
                //return BitmapFactory.decodeByteArray(IMAGE, 0, IMAGE.length);
                //imgPembayaran.setImageResource(convertByteArrayToBitmap(IMAGE));
                //imgPembayaran.setImageBitmap(BitmapFactory.decodeByteArray(IMAGE,0,IMAGE.length));
                Log.d(TAG,"IMAGE -> "+imgPembayaran);
            }
        }

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
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        bitMapImage = BitmapFactory.decodeByteArray(
                byteArrayToBeCOnvertedIntoBitMap, 0,
                byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }
}
