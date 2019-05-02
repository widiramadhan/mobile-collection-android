package sfi.mobile.collection.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import sfi.mobile.collection.util.FileUtilSS;
import sfi.mobile.collection.helper.ScreenshotUtil;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;

public class PrintImage extends Fragment {

    TextView txtContractid,txtNamacustomer,txtAngsuranke,txtJatuhtempo,txtJmlangsuran,txtDenda,txtBiayaadmin,txtTotaltagihan,txtPembayaran,txtSisaTagihan,txtPic,txtTgltransaksi;
    protected Cursor cursor;
    Button buttons;
    View v1;
    Bitmap bitmap;
    DBHelper dbhelper;
    String strContractId,strCostumername,strtgljatuhtempo,strAngsuranKe,strDevice,strTotaltagihan;
    int strtotal,strDenda,strAngsuran;
    double biaya_admin=10000;
    Double strHasil,strAmount;
    LinearLayout ln_parentview;

    SharedPreferences sharedpreferences;
    private static final String TAG = TabNormal.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String fullName, branchName;

    public PrintImage() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_image, container, false);
        txtContractid = (TextView) view.findViewById(R.id.txt_nokontrak);
        txtNamacustomer = (TextView) view.findViewById(R.id.txt_namaCustomer);
        txtAngsuranke = (TextView) view.findViewById(R.id.txt_angsuranke);
        txtJatuhtempo = (TextView) view.findViewById(R.id.txt_jatuhtempo);
        txtJmlangsuran = (TextView) view.findViewById(R.id.txt_jmlAngsuran);
        txtDenda = (TextView) view.findViewById(R.id.txt_denda);
        txtBiayaadmin = (TextView) view.findViewById(R.id.txt_biayaadmin);
        txtTotaltagihan = (TextView) view.findViewById(R.id.txt_txttotaltagihan);
        txtPembayaran = (TextView) view.findViewById(R.id.txt_pembayaran);
        txtSisaTagihan = (TextView) view.findViewById(R.id.txt_sisatagihan);
        txtPic = (TextView) view.findViewById(R.id.txt_pic);
        txtTgltransaksi = (TextView) view.findViewById(R.id.txt_tgltranskasi);
        buttons = (Button) view.findViewById(R.id.btnsave);
        ln_parentview = (LinearLayout) view.findViewById(R.id.ln_parentview);
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
        txtContractid.setText(paramId);

        /*** set session to variable ***/
        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);

        //----------------------------------------
        dbhelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.QUESTION, A.ANSWER,B.TANGGAL_JATUH_TEMPO,B.TOTAL_TAGIHAN,B.ANGSURAN_KE,B.ANGSURAN_BERJALAN,B.DENDA FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID=B.NOMOR_KONTRAK WHERE A.CONTRACT_ID ='" + paramId +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            cursor.moveToPosition(0);
            //txtcontractid.setText(cursor.getString(0));
            strContractId = cursor.getString(0);
            strCostumername = cursor.getString(1);
            strtgljatuhtempo = cursor.getString(4);
            strtotal = cursor.getInt(5);
            strAngsuranKe = cursor.getString(6);
            strAngsuran = cursor.getInt(7);
            strDenda = cursor.getInt(8);

            do {
                String questionID = cursor.getString(2);
                //Pembayaran yang diterima
                if (questionID.equals("MS_Q20190226172644783")) {
                    strAmount = Double.parseDouble(cursor.getString(3));
                }
            } while (cursor.moveToNext());
        }
        strHasil = ( strtotal + biaya_admin ) - strAmount ;
        Log.d("Data","contract_ID ->" + strContractId);
        Log.d("Data","Costumer Name ->" + strCostumername);
        Log.d("Data","strTglJatuhTempo ->" + strtgljatuhtempo);
        Log.d("Data","Angsuran ke ->" + strAngsuranKe);
        Log.d("Data","Bayar ->" + strAmount);
        Log.d("Data","PIC ->" + fullName);
        Log.d("Data","Denda ->" + strDenda);
        Log.d("Data","Angsuran Berjalan ->" + strAngsuran);
        Log.d("Data","Total Tagihan ->" + strtotal);
        Log.d("Data","Hasil->" + strHasil);

        txtTgltransaksi.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date()));
        txtContractid.setText(strContractId);
        txtNamacustomer.setText(strCostumername);
        txtAngsuranke.setText(strAngsuranKe);
        txtJatuhtempo.setText(strtgljatuhtempo);
        txtJmlangsuran.setText("Rp. " + String.valueOf(formatRupiah.format((double)strAngsuran).replaceAll( "Rp", "" )));
        txtDenda.setText("Rp. " +String.valueOf(formatRupiah.format((double)strDenda).replaceAll( "Rp", "" )));
        txtBiayaadmin.setText("Rp. 10.000");
        txtTotaltagihan.setText("Rp. " +String.valueOf(formatRupiah.format((double)strtotal).replaceAll( "Rp", "" )));
        txtPembayaran.setText("Rp. " +String.valueOf(formatRupiah.format((double)strAmount).replaceAll( "Rp", "" )));
        txtSisaTagihan.setText("Rp. " +String.valueOf(formatRupiah.format((double)strHasil).replaceAll( "Rp", "" )));
        txtPic.setText(fullName);

        buttons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(ln_parentview);
                Printscreen_struk();
                //takeScreenshot();
            }
        });
    }

    public void Printscreen_struk()
    {
        /*v1 =  getActivity().getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.buildDrawingCache(true);
        v1.setDrawingCacheEnabled(false);*/

        //Save bitmap
        //String extr = Environment.getExternalStorageDirectory().toString() + File.separator;
        DateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String strDate = df.format(Calendar.getInstance().getTime());
        String fileName = "z" + strDate + ".jpg";
        File myPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PrintStruk" + fileName);

        /*if (!myPath.exists()) {
            if (!myPath.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return;
            }
        }*/

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Screen", "screen");
            Toast.makeText(getActivity(), "Capture Berhasi Tersimpan di:" , Toast.LENGTH_LONG).show();
            Log.d(TAG,"Capture Berhasi Tersimpan : -> "+ myPath);
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //-----
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}
