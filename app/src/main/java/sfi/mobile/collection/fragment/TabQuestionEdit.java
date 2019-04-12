package sfi.mobile.collection.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;

public class TabQuestionEdit extends Fragment implements LocationListener {
    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID = "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String branchID, employeeID;

    protected Cursor cursor;
    DBHelper dbhelper;
    Calendar calendar = Calendar.getInstance();
    Intent intent;
    Uri fileUri;
    Bitmap bitmap, decoded;
    Byte byteimage;
    ImageView imageView, imageViewPembayaran;
    EditText contactperson, alamatbaru, pembayaran_diterima, hasilkunjungan;
    double intPembayaran,intTotaltagihan;
    String strHasilpembayaran,strHasiltotaltagihan;
    int biaya_admin = 10000;

    public final int REQUEST_CAMERA = 0;

    int bitmap_size = 100; // image quality 1 - 100;
    int max_resolution_image = 800;

    LocationManager locationManager;

    String flagFoto = "";

    String strMeetup, strContactName, strHubungan, strAddress, strQAddress, strNewAddress, strUnit, strQbayar, strAmount, strSisa, strLatPembayaran, strLngPembayaran, strLatPertemuan, strLngPertemuan, strJanjiBayar, strHasilKunjungan, strCreateDate,strTotal;

    LinearLayout ln_pembayaran_ya, ln_pembayaran_tidak, ln_alamatbaru, ln_contactperson, ln_AlmtKunjungi, ln_unit, ln_customerbayar, ln_alamatberubah,ln_tanggaljanjibayar;
    Spinner spinner_name, spinner_alamatbaru, spinner_unit, spinner_custbayar, spinner_hubungan, spiner_alamat;
    CardView cardcontact, cardalamatbaru, cardunit, cardpembayaran_ya, cardpembayaran_tidak;
    TextView txtcontract_id, txtcostumername, txttotaltagihan, txttgljanjibayar, txtlat_pembayaran, txtlng_pembayaran, txtlat_pertemuan, txtlng_pertemuan,txt_angsuran,txt_biayaadmin,txtDenda_tagihan,txt_totalTagihan2,txt_sisa,txtTotalTagihanAll;
    Button btnsetlokasi_pertemuan, btnsetlokasi_pembayaran, btnsetfotolokasipertemuan, btnsetfotolokasipembayaran, btnupdate;

    private static final String TAG = TabQuestionEdit.class.getSimpleName();

    public TabQuestionEdit() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_question_edit, container, false);
        dbhelper = new DBHelper(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** set session to variable ***/
        sharedpreferences = this.getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        branchID = sharedpreferences.getString(TAG_BRANCH_ID, null);
        employeeID = sharedpreferences.getString(TAG_EMP_ID, null);
        /*** end set session to variable ***/

        /*** get contract id from fragment ***/
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        final TextView contractID = (TextView) getActivity().findViewById(R.id.contract_id);

        txtcontract_id = (TextView) getActivity().findViewById(R.id.nomor_kontrak2);
        txtcostumername = (TextView) getActivity().findViewById(R.id.nama_kostumer2);
        txttotaltagihan = (TextView) getActivity().findViewById(R.id.total_tagihan2);
        txt_angsuran = (TextView) getActivity().findViewById(R.id.angsuran);
        txt_biayaadmin = (TextView) getActivity().findViewById(R.id.biaya_admin);
        txtDenda_tagihan = (TextView) getActivity().findViewById(R.id.denda_tagihan);
        txt_sisa = (TextView) getActivity().findViewById(R.id.sisa_tagihan);
        txt_totalTagihan2 = (TextView) getActivity().findViewById(R.id.total_tagihan3);
        txtTotalTagihanAll = (TextView) getActivity().findViewById(R.id.totalTagihanAll);

        txttgljanjibayar = (TextView) getActivity().findViewById(R.id.tgljanjibayar);
        txtlat_pertemuan = (TextView) getActivity().findViewById(R.id.lat_pertemuan);
        txtlng_pertemuan = (TextView) getActivity().findViewById(R.id.lng_pertemuan);
        txtlat_pembayaran = (TextView) getActivity().findViewById(R.id.lat_pembayaran);
        txtlng_pembayaran = (TextView) getActivity().findViewById(R.id.lng_pembayaran);

        contactperson = (EditText) getActivity().findViewById(R.id.contactperson);
        alamatbaru = (EditText) getActivity().findViewById(R.id.alamatbaru);
        pembayaran_diterima  = (EditText) getActivity().findViewById(R.id.pembayaran_diterima);
        hasilkunjungan  = (EditText) getActivity().findViewById(R.id.hasilkunjungan);

        ln_pembayaran_ya = (LinearLayout) getActivity().findViewById(R.id.LnPembayaran_ya);
        ln_pembayaran_tidak = (LinearLayout) getActivity().findViewById(R.id.LnPembayaran_tidak);
        ln_alamatbaru = (LinearLayout) getActivity().findViewById(R.id.ln_alamatbaru);
        ln_contactperson = (LinearLayout) getActivity().findViewById(R.id.ln_contactperson);
        ln_AlmtKunjungi = (LinearLayout) getActivity().findViewById(R.id.ln_AlmtKunjungi);
        ln_unit = (LinearLayout) getActivity().findViewById(R.id.ln_unit);
        ln_customerbayar = (LinearLayout) getActivity().findViewById(R.id.ln_customerbayar);
        ln_alamatberubah = (LinearLayout) getActivity().findViewById(R.id.ln_alamatberubah);
        ln_tanggaljanjibayar = (LinearLayout) getActivity().findViewById(R.id.ln_tanggaljanjibayar);


        spinner_name = (Spinner) getActivity().findViewById(R.id.sp_name);
        spinner_alamatbaru = (Spinner) getActivity().findViewById(R.id.sp_pilihanalamat);
        spinner_unit = (Spinner) getActivity().findViewById(R.id.sp_unit);
        spinner_custbayar = (Spinner) getActivity().findViewById(R.id.sp_custBayar);
        spinner_hubungan = (Spinner) getActivity().findViewById(R.id.sp_hubungancontact);
        spiner_alamat  = (Spinner) getActivity().findViewById(R.id.sp_almtkunjungi);

        cardcontact = (CardView) getActivity().findViewById(R.id.cardcontact);
        cardalamatbaru = (CardView) getActivity().findViewById(R.id.cardalamatbaru);
        cardunit = (CardView) getActivity().findViewById(R.id.cardunit);
        cardpembayaran_ya = (CardView) getActivity().findViewById(R.id.cardpembayaran_ya);
        cardpembayaran_tidak = (CardView) getActivity().findViewById(R.id.cardpembayaran_tidak);

        btnsetlokasi_pertemuan = (Button) getActivity().findViewById(R.id.btnsetlokasi_pertemuan);
        btnsetlokasi_pembayaran = (Button) getActivity().findViewById(R.id.btnsetlokasi_pembayaran);
        btnsetfotolokasipembayaran = (Button) getActivity().findViewById(R.id.btnsetfotolokasipembayaran);
        btnsetfotolokasipertemuan = (Button) getActivity().findViewById(R.id.btnsetfotolokasipertemuan);
        btnupdate = (Button) getActivity().findViewById(R.id.btnupdate);
        imageView = (ImageView) getActivity().findViewById(R.id.image_view);
        imageViewPembayaran = (ImageView) getActivity().findViewById(R.id.image_view_pembayaran);

        //pembayaran_diterima.addTextChangedListener(onTextChanedListener());
        txt_biayaadmin.setText(formatRupiah.format((double)biaya_admin).replaceAll("Rp",""));

        //------GET DATA KOSTUMER----------
        getDataCustomerByContract((String) contractID.getText());
        //---------------

        final DatePickerDialog.OnDateSetListener datePickerJanjiBayar = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                txttgljanjibayar.setText(sdf.format(calendar.getTime()));
            }
        };
        txttgljanjibayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerJanjiBayar,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 18);
                datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        btnsetlokasi_pertemuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TabQuestionEdit.this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    txtlat_pertemuan.setText("Lat : " + String.valueOf(lat));
                    txtlng_pertemuan.setText("Long : " + String.valueOf(lng));
                }
            }
        });

        btnsetlokasi_pembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TabQuestionEdit.this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    txtlat_pembayaran.setText("Lat : " + String.valueOf(lat));
                    txtlng_pembayaran.setText("Long : " + String.valueOf(lng));
                }
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_name.getSelectedItemPosition() != 0){
                    //Toast.makeText(getActivity(), "Sudah Memilih",Toast.LENGTH_LONG).show();
                    /*if(TextUtils.isEmpty(hasilkunjungan.getText().toString())){
                        //Toast.makeText(getActivity(), "Hasil Kunjungan Kosong",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Hasil kunjungan belum terisi");
                        *//*if(intPembayaran > intTotaltagihan){
                            Toast.makeText(getActivity(), "Pembayaran Melebihi Total Tagihan",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Pembayaran sudah benar",Toast.LENGTH_LONG).show();
                            SaveDataResult();
                        }*//*
                    }else{
                        //Toast.makeText(getActivity(), "Hasil Kunjungan ada",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Hasil kunjungan sudah terisi");
                        SaveDataResult();
                    }*/
                    UpdateDataResult();
                }else {
                    Toast.makeText(getActivity(), "Silahkan pilih bertemu customer",Toast.LENGTH_LONG).show();
                    //Log.d(TAG,"bertemu customer belum di pilih");
                }
            }
        });

        //------------------------------------------------------------//
        pembayaran_diterima.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                // angkaPertama = Double.parseDouble( editText.getText().toString());
                pembayaran_diterima.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    pembayaran_diterima.setText(formattedString);
                    pembayaran_diterima.setSelection(pembayaran_diterima.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                pembayaran_diterima.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                double hasil;
                String pembayaran = pembayaran_diterima.getText().toString();
                //intTotaltagihan = 5000000;
                if (pembayaran.equals("")){
                    pembayaran_diterima.setText("0");
                }
                hasil = ((biaya_admin + Double.parseDouble(txtTotalTagihanAll.getText().toString())) - Double.parseDouble(pembayaran_diterima.getText().toString().replaceAll(",","")));
                txt_sisa.setText(formatRupiah.format((double)hasil).replaceAll("Rp",""));
            }
        });
        //------------------------------------------------------------//
        dbhelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.QUESTION, A.ANSWER, A.CREATE_DATE,B.TOTAL_TAGIHAN FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID=B.NOMOR_KONTRAK WHERE A.CONTRACT_ID ='" + txtcontract_id.getText().toString() +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            cursor.moveToPosition(0);
            /*txtcontract_id.setText(cursor.getString(0));
            txtcostumername.setText(cursor.getString(1));
            strCreateDate = cursor.getString(4);
            txtTotalTagihanStatus.setText(String.valueOf(Double.parseDouble(cursor.getString(5))));*/

            do {
                String questionID = cursor.getString(2);

                //Apakah bertemu dengan customer ?
                if (questionID.equals("MS_Q20190226172031880")) {
                    strMeetup = cursor.getString(3);
                }
                //Nama kontak person
                if (questionID.equals("MS_Q20190226172302530")) {
                    strContactName = cursor.getString(3);
                }
                //Hubungan Kontak Person dengan Kostumer
                if (questionID.equals("MS_Q20190226172325360")) {
                    strHubungan = cursor.getString(3);
                }
                //Alamat yang dikunjungi
                if (questionID.equals("MS_Q20190226172343540")) {
                    strAddress = cursor.getString(3);
                }
                //Apakah alamat berubah
                if (questionID.equals("MS_Q20190226172405297")) {
                    strQAddress = cursor.getString(3);
                }
                //Alamat Baru
                if (questionID.equals("MS_Q20190226172432320")) {
                    strNewAddress = cursor.getString(3);
                }
                //Apakah unit ada
                if (questionID.equals("MS_Q20190226172447930")) {
                    strUnit = cursor.getString(3);
                }
                //Apakah customer akan membayar
                if (questionID.equals("MS_Q20190226172517357")) {
                    strQbayar = cursor.getString(3);
                }
                //Latitude Lokasi Pembayaran
                if (questionID.equals("MS_Q20190226172558067")) {
                    strLatPembayaran = cursor.getString(3);
                }
                //Longitude Lokasi Pembayaran
                if (questionID.equals("MS_Q20190226172603397")) {
                    strLngPembayaran = cursor.getString(3);
                }
                //Latitude Lokasi Pertemuan
                if (questionID.equals("MS_Q20190226172624710")) {
                    strLatPertemuan = cursor.getString(3);
                }
                //Longitude Lokasi Pertemuan
                if (questionID.equals("MS_Q20190226172628683")) {
                    strLngPertemuan = cursor.getString(3);
                }
                //Pembayaran yang diterima
                if (questionID.equals("MS_Q20190226172644783")) {
                    strAmount = cursor.getString(3);
                }
                //Foto Lokasi Pembayaran
                if (questionID.equals("MS_Q20190226172753329")) {
                    //imgPembayaran = cursor.getString(3);
                }
                //Foto Lokasi Pertemuan
                if (questionID.equals("MS_Q20190226172753330")) {
                    //imgPertemuan = cursor.getString(3);
                }
                //Janji Bayar
                if (questionID.equals("MS_Q20190226172810420")) {
                    strJanjiBayar = cursor.getString(3);
                }
                //Hasil Kunjungan
                if (questionID.equals("MS_Q20190226172818070")) {
                    strHasilKunjungan = cursor.getString(3);
                }
            } while (cursor.moveToNext());

            txttgljanjibayar.setText(strJanjiBayar);
            hasilkunjungan.setText(strHasilKunjungan);
            pembayaran_diterima.setText(strAmount);
            contactperson.setText(strContactName);
            alamatbaru.setText(strNewAddress);
            txtlat_pembayaran.setText(strLatPembayaran);
            txtlng_pembayaran.setText(strLngPembayaran);
            txtlat_pertemuan.setText(strLatPertemuan);
            txtlng_pertemuan.setText(strLngPertemuan);

            //Bertemu Customer atau tidak
            if (strMeetup.equals("Ya, bertemu dengan customer")){
                spinner_name.setSelection(1);
            }else if(strMeetup.equals("Tidak, bertemu dengan orang lain")){
                spinner_name.setSelection(2);
            }else{
                spinner_name.setSelection(3);
            }

            //Hubungan Customer
            if(strHubungan.equals("Orang Tua")){
                spinner_hubungan.setSelection(1);
            }else if (strHubungan.equals("Suami / Istri")){
                spinner_hubungan.setSelection(2);
            }else if (strHubungan.equals("Kakak / Adik")){
                spinner_hubungan.setSelection(3);
            }else if (strHubungan.equals("Saudara")){
                spinner_hubungan.setSelection(4);
            }else if (strHubungan.equals("DLL")){
                spinner_hubungan.setSelection(5);
            }else{
                spinner_hubungan.setSelection(0);
            }

            //Alamat Customer
            if(strAddress.equals("Alamat KTP")){
                spiner_alamat.setSelection(1);
            }else if (strAddress.equals("Alamat Kantor")){
                spiner_alamat.setSelection(2);
            }else if (strAddress.equals("Alamat Surat")){
                spiner_alamat.setSelection(3);
            }else{
                spiner_alamat.setSelection(0);
            }
            //Alama question
            if(strQAddress.equals("Ya")){
                spinner_alamatbaru.setSelection(1);
            }else if(strQAddress.equals("Tidak")){
                spinner_alamatbaru.setSelection(2);
            }else{
                spinner_alamatbaru.setSelection(0);
            }

            //unit ada atau tidak
            if(strUnit.equals("Ya")){
                spinner_unit.setSelection(1);
            }else if (strUnit.equals("Tidak")){
                spinner_unit.setSelection(2);
            }else{
                spinner_unit.setSelection(0);
            }

            //customer akan membayar
            if(strQbayar.equals("Ya")){
                spinner_custbayar.setSelection(1);
            }else if (strQbayar.equals("Tidak")){
                spinner_custbayar.setSelection(2);
            }else{
                spinner_custbayar.setSelection(0);
            }

            Log.d(TAG,"Unit ->" + strUnit);
            Log.d(TAG,"bayar ->" + strQbayar);
        }

        //Spinner Apakah ketemu Dengan Costumer //
        spinner_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//Pilih
                    ln_contactperson.setVisibility(View.GONE);
                    ln_AlmtKunjungi.setVisibility(View.GONE);
                    ln_alamatberubah.setVisibility(View.GONE);
                    cardcontact.setVisibility(View.GONE);
                    cardalamatbaru.setVisibility(View.GONE);
                    cardunit.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : " );
                    txtlng_pertemuan.setText("Long : " );
                    spiner_alamat.setSelection(0);
                    spinner_alamatbaru.setSelection(0);
                    spinner_hubungan.setSelection(0);
                    contactperson.setText("");
                } else if (position == 1) { //ya
                    ln_contactperson.setVisibility(View.GONE);
                    ln_AlmtKunjungi.setVisibility(View.VISIBLE);
                    ln_alamatberubah.setVisibility(View.VISIBLE);
                    cardcontact.setVisibility(View.VISIBLE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : " );
                    txtlng_pertemuan.setText("Long : " );
                    contactperson.setText("");
                    spinner_hubungan.setSelection(0);
                    btnupdate.setEnabled(true);
                } else if (position == 2) { //bertemu orang lain
                    ln_contactperson.setVisibility(View.VISIBLE);
                    ln_AlmtKunjungi.setVisibility(View.VISIBLE);
                    ln_alamatberubah.setVisibility(View.VISIBLE);
                    cardcontact.setVisibility(View.VISIBLE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : " );
                    txtlng_pertemuan.setText("Long : " );
                    btnupdate.setEnabled(true);
                } else if (position == 3) { //tidak bertemu
                    ln_contactperson.setVisibility(View.GONE);
                    ln_AlmtKunjungi.setVisibility(View.GONE);
                    ln_alamatberubah.setVisibility(View.GONE);
                    cardcontact.setVisibility(View.GONE);
                    cardalamatbaru.setVisibility(View.GONE);
                    cardunit.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_pembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_tanggaljanjibayar.setVisibility(View.GONE);
                    spiner_alamat.setSelection(0);
                    spinner_alamatbaru.setSelection(0);
                    spinner_hubungan.setSelection(0);
                    contactperson.setText("");
                    /*spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);*/
                    btnupdate.setEnabled(true);
                }

                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner Alamat Baru atau Tidak //
        spinner_alamatbaru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                if (position == 0) { //Pilih
                    ln_alamatbaru.setVisibility(View.GONE);
                    ln_unit.setVisibility(View.GONE);
                    cardunit.setVisibility(View.GONE);
                    ln_customerbayar.setVisibility(View.GONE);
                    cardalamatbaru.setVisibility(View.GONE);
                    cardunit.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    alamatbaru.setText("");
                } else if (position == 1) { //Ya
                    ln_alamatbaru.setVisibility(View.VISIBLE);
                    ln_unit.setVisibility(View.VISIBLE);
                    ln_customerbayar.setVisibility(View.VISIBLE);
                    cardunit.setVisibility(View.VISIBLE);
                    cardalamatbaru.setVisibility(View.VISIBLE);
                    /*spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);*/
                } else if (position == 2) { //Tidak
                    ln_alamatbaru.setVisibility(View.GONE);
                    ln_unit.setVisibility(View.VISIBLE);
                    ln_customerbayar.setVisibility(View.VISIBLE);
                    cardunit.setVisibility(View.VISIBLE);
                    cardalamatbaru.setVisibility(View.GONE);
                    alamatbaru.setText("");
                    /*spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);*/
                }
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Spinner Customer Bayar atau Tidak //
        spinner_custbayar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { //pilih
                    ln_pembayaran_tidak.setVisibility(View.GONE);
                    ln_pembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pembayaran.setText("Lat  : ");
                    txtlng_pembayaran.setText("Long : ");
                    txtlat_pertemuan.setText("Lat  : ");
                    txtlng_pertemuan.setText("Long : ");
                    pembayaran_diterima.setText("");
                    txttgljanjibayar.setText("");
                } else if (position == 1) { //ya
                    ln_pembayaran_ya.setVisibility(View.VISIBLE);
                    ln_pembayaran_tidak.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.VISIBLE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : ");
                    txtlng_pertemuan.setText("Long : ");
                    txttgljanjibayar.setText("");
                } else if (position == 2) { //tidak
                    ln_pembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_pembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_tanggaljanjibayar.setVisibility(View.VISIBLE);
                    txtlat_pembayaran.setText("Lat  : ");
                    txtlng_pembayaran.setText("Long : ");
                    pembayaran_diterima.setText("");
                }
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getDataCustomerByContract(String ContractID) {

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM DKH WHERE NOMOR_KONTRAK='" + ContractID + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            txtcontract_id.setText(cursor.getString(4));
            txtcostumername.setText(cursor.getString(5));
            txttotaltagihan.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(15)))).replaceAll("Rp", ""));
            txt_angsuran.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(11)))).replaceAll( "Rp", "" ));
            txtDenda_tagihan.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(13))).replaceAll("Rp","")));
            txt_totalTagihan2.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(15)))).replaceAll( "Rp", "" ));
            txtTotalTagihanAll.setText(String.valueOf(Double.parseDouble(cursor.getString(15))));

            intTotaltagihan = Double.parseDouble(txtTotalTagihanAll.getText().toString()) + biaya_admin;
            txt_totalTagihan2.setText(formatRupiah.format((double)intTotaltagihan).replaceAll("Rp",""));
        }
    }

    private void UpdateDataResult(){
        String StrContractID = txtcontract_id.getText().toString();
        String getDate = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        dbhelper = new DBHelper(getActivity());
        SQLiteDatabase dbUpdate = dbhelper.getWritableDatabase();
        String Update1 = "UPDATE RESULT Set ANSWER ='"+ spinner_name.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172031880' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update2 = "UPDATE RESULT Set ANSWER ='"+ contactperson.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172302530' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update3 = "UPDATE RESULT Set ANSWER ='"+ spinner_hubungan.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172325360' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update4 = "UPDATE RESULT Set ANSWER ='"+ spiner_alamat.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172343540' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update5 = "UPDATE RESULT Set ANSWER ='"+ spinner_alamatbaru.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172405297' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update6 = "UPDATE RESULT Set ANSWER ='"+ alamatbaru.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172432320' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update7 = "UPDATE RESULT Set ANSWER ='"+ spinner_unit.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172447930' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update8 = "UPDATE RESULT Set ANSWER ='"+ spinner_custbayar.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172517357' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update9 = "UPDATE RESULT Set ANSWER ='"+ txtlat_pembayaran.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172558067' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update10 = "UPDATE RESULT Set ANSWER ='"+ txtlng_pembayaran.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172603397' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update11 = "UPDATE RESULT Set ANSWER ='"+ txtlat_pertemuan.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172624710' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update12 = "UPDATE RESULT Set ANSWER ='"+ txtlng_pertemuan.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172628683' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update13 = "UPDATE RESULT Set ANSWER ='"+ pembayaran_diterima.getText().toString().replaceAll(",","") +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172644783' and CONTRACT_ID ='"+ StrContractID +"'";

       /*  String Update14 = "UPDATE RESULT Set ANSWER ='"+ spinner_alamatbaru.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172405297' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update15 = "UPDATE RESULT Set ANSWER ='"+ spinner_alamatbaru.getSelectedItem().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172405297' and CONTRACT_ID ='"+ StrContractID +"'";*/

        String Update14 = "UPDATE RESULT Set ANSWER ='"+ txttgljanjibayar.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172810420' and CONTRACT_ID ='"+ StrContractID +"'";
        String Update15 = "UPDATE RESULT Set ANSWER ='"+ hasilkunjungan.getText().toString() +"',CREATE_DATE = '"+ getDate +"' WHERE QUESTION = 'MS_Q20190226172818070' and CONTRACT_ID ='"+ StrContractID +"'";

        String Sql = "update DKH set IS_COLLECT=2 where NOMOR_KONTRAK = '"+ StrContractID +"'";
        //Exec DB update
        dbUpdate.execSQL(Update1);
        dbUpdate.execSQL(Update2);
        dbUpdate.execSQL(Update3);
        dbUpdate.execSQL(Update4);
        dbUpdate.execSQL(Update5);
        dbUpdate.execSQL(Update6);
        dbUpdate.execSQL(Update7);
        dbUpdate.execSQL(Update8);
        dbUpdate.execSQL(Update9);
        dbUpdate.execSQL(Update10);
        dbUpdate.execSQL(Update11);
        dbUpdate.execSQL(Update12);
        dbUpdate.execSQL(Update13);
        dbUpdate.execSQL(Update14);
        dbUpdate.execSQL(Update15);
        dbUpdate.execSQL(Sql);
        // Log.d(TAG,"Byte -> " + bitmapdata);
        //String contract_id =  ((TextView) getActivity().findViewById(R.id.nomor_kontrak2)).getText().toString();
        ResultFragment fragment = new ResultFragment();
        Bundle arguments = new Bundle();
        arguments.putString( "paramId" , StrContractID);
        Log.d(TAG,"Contract ID -> " + StrContractID);
        fragment.setArguments(arguments);
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment).commit();
    }

    @Override
    public void onLocationChanged(Location location) {
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
