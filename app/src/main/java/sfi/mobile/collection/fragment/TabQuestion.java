package sfi.mobile.collection.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sfi.mobile.collection.BuildConfig;
import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;

public class TabQuestion extends Fragment implements LocationListener {

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

    LinearLayout ln_pembayaran_ya, ln_pembayaran_tidak, ln_alamatbaru, ln_contactperson, ln_AlmtKunjungi, ln_unit, ln_customerbayar, ln_alamatberubah,ln_tanggaljanjibayar;
    Spinner spinner_name, spinner_alamatbaru, spinner_unit, spinner_custbayar, spinner_hubungan, spiner_alamat;
    CardView cardcontact, cardalamatbaru, cardunit, cardpembayaran_ya, cardpembayaran_tidak;
    TextView txtcontract_id, txtcostumername, txttotaltagihan, txttgljanjibayar, txtlat_pembayaran, txtlng_pembayaran, txtlat_pertemuan, txtlng_pertemuan,txt_angsuran,txt_biayaadmin,txtDenda_tagihan,txt_totalTagihan2,txt_sisa,txtTotalTagihanAll,txt_period;
    Button btnsetlokasi_pertemuan, btnsetlokasi_pembayaran, btnsetfotolokasipertemuan, btnsetfotolokasipembayaran, btnsave;

    private static final String TAG = TabQuestion.class.getSimpleName();

    public TabQuestion() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_question, container, false);
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
        txt_period = (TextView) getActivity().findViewById(R.id.txt_period);

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
        btnsave = (Button) getActivity().findViewById(R.id.btnsave);
        imageView = (ImageView) getActivity().findViewById(R.id.image_view);
        imageViewPembayaran = (ImageView) getActivity().findViewById(R.id.image_view_pembayaran);

        //pembayaran_diterima.addTextChangedListener(onTextChanedListener());
        txt_biayaadmin.setText(formatRupiah.format((double)biaya_admin).replaceAll("Rp",""));


        /*** ------------------------------------------------------------- ***/

        ln_pembayaran_ya.setVisibility(View.GONE);
        ln_pembayaran_tidak.setVisibility(View.GONE);
        ln_alamatbaru.setVisibility(View.GONE);
        ln_contactperson.setVisibility(View.GONE);
        ln_AlmtKunjungi.setVisibility(View.GONE);
        ln_unit.setVisibility(View.GONE);
        ln_customerbayar.setVisibility(View.GONE);
        ln_alamatberubah.setVisibility(View.GONE);

        cardcontact.setVisibility(View.GONE);
        cardalamatbaru.setVisibility(View.GONE);
        cardunit.setVisibility(View.GONE);
        cardpembayaran_ya.setVisibility(View.GONE);
        cardpembayaran_tidak.setVisibility(View.GONE);
        pembayaran_diterima.setText("");
       // txttotaltagihan.setText("0");
        /*intTotaltagihan = Double.parseDouble(txttotaltagihan.getText().toString());
        Log.d(TAG,"TotalTagihan -> " + intTotaltagihan);*/

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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TabQuestion.this);
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TabQuestion.this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    txtlat_pembayaran.setText("Lat : " + String.valueOf(lat));
                    txtlng_pembayaran.setText("Long : " + String.valueOf(lng));
                }
            }
        });

        btnsetfotolokasipembayaran.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 fileUri = getOutputMediaFileUri();
                 intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                 startActivityForResult(intent, REQUEST_CAMERA);
                 flagFoto = "1";

             }
        });

        btnsetfotolokasipertemuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri();
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_CAMERA);
                flagFoto = "2";
            }
        });


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
                    btnsave.setEnabled(true);
                } else if (position == 2) { //bertemu orang lain
                    ln_contactperson.setVisibility(View.VISIBLE);
                    ln_AlmtKunjungi.setVisibility(View.VISIBLE);
                    ln_alamatberubah.setVisibility(View.VISIBLE);
                    cardcontact.setVisibility(View.VISIBLE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : " );
                    txtlng_pertemuan.setText("Long : " );
                    btnsave.setEnabled(true);
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
                    spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);
                    txtlat_pembayaran.setText("Lat  : " );
                    txtlng_pembayaran.setText("Long : " );
                    btnsave.setEnabled(true);
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
                    spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);
                } else if (position == 2) { //Tidak
                    ln_alamatbaru.setVisibility(View.GONE);
                    ln_unit.setVisibility(View.VISIBLE);
                    ln_customerbayar.setVisibility(View.VISIBLE);
                    cardunit.setVisibility(View.VISIBLE);
                    cardalamatbaru.setVisibility(View.GONE);
                    alamatbaru.setText("");
                    spinner_unit.setSelection(0);
                    spinner_custbayar.setSelection(0);
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
                    imageView.setImageDrawable(null);
                    imageViewPembayaran.setImageDrawable(null);
                } else if (position == 1) { //ya
                    ln_pembayaran_ya.setVisibility(View.VISIBLE);
                    ln_pembayaran_tidak.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.VISIBLE);
                    cardpembayaran_tidak.setVisibility(View.GONE);
                    txtlat_pertemuan.setText("Lat  : ");
                    txtlng_pertemuan.setText("Long : ");
                    txttgljanjibayar.setText("");
                    imageView.setImageDrawable(null);
                    imageViewPembayaran.setImageDrawable(null);
                } else if (position == 2) { //tidak
                    ln_pembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_pembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_ya.setVisibility(View.GONE);
                    cardpembayaran_tidak.setVisibility(View.VISIBLE);
                    ln_tanggaljanjibayar.setVisibility(View.VISIBLE);
                    txtlat_pembayaran.setText("Lat  : ");
                    txtlng_pembayaran.setText("Long : ");
                    pembayaran_diterima.setText("");
                    imageView.setImageDrawable(null);
                    imageViewPembayaran.setImageDrawable(null);
                }
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (pembayaran_diterima.getText().equals("")){
                    intPembayaran = 0;
                }else{
                    intPembayaran = Double.parseDouble(pembayaran_diterima.getText().toString());
                }*/
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
                    }*/
                    SaveDataResult();
                }else {
                    Toast.makeText(getActivity(), "Silahkan pilih bertemu customer",Toast.LENGTH_LONG).show();
                    //Log.d(TAG,"bertemu customer belum di pilih");
                }
            }
        });

        getDataCustomerByContract((String) contractID.getText());

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
    }

    private void SaveDataResult(){
        String strContractID = txtcontract_id.getText().toString();
        String getDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase dbInsert = dbhelper.getWritableDatabase();
        String saved = "insert into RESULT (CONTRACT_ID, QUESTION, ANSWER, CREATE_DATE, USER, BRANCH_ID,PERIOD) values" +
                "('"+ strContractID +"','MS_Q20190226172031880','"+ spinner_name.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172302530','"+ contactperson.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172325360','"+ spinner_hubungan.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172343540','"+ spiner_alamat.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172405297','"+ spinner_alamatbaru.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172432320','"+ alamatbaru.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172447930','"+ spinner_unit.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172517357','"+ spinner_custbayar.getSelectedItem().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172558067','"+ txtlat_pembayaran.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172603397','"+ txtlng_pembayaran.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172624710','"+ txtlat_pertemuan.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172628683','"+ txtlng_pertemuan.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172644783','"+ pembayaran_diterima.getText().toString().replaceAll(",","") +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172753329','image in table TBImage','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172753330','image in table TBImage','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172810420','"+ txttgljanjibayar.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')," +
                "('"+ strContractID +"','MS_Q20190226172818070','"+ hasilkunjungan.getText().toString() +"','"+ getDate +"','"+ employeeID +"','"+ branchID +"','"+txt_period.getText().toString()+"')";

        String SavedCollected = "INSERT INTO COLLECTED (CONTRACT_ID,DailyCollectibility,IS_COLLECT,PERIOD,EMP_ID,CREATE_DATE) values('"+strContractID+"','',0,'"+ txt_period.getText().toString() +"','"+ employeeID +"','"+getDate+"')";

        String updateCollectibility = "";
        String updateCollectibility2 = "";

        Log.e(TAG,"spinner -> "+spinner_custbayar.getSelectedItem().toString());

        if(spinner_name.getSelectedItem().toString().equals("Tidak bertemu siapapun")) {
            if (imageView.getDrawable() != null) {
                dbhelper.insertDataImage(strContractID, imageViewToByte(imageView), getDate);
                updateCollectibility = "update DKH set IS_COLLECT=1, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=1, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG, "Masuk ke Done karna foto lokasi tidak kosong");
            } else {
                updateCollectibility = "update DKH set IS_COLLECT=2, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=2, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG, "Masuk ke Draft karna foto lokasi kosong");
            }
        }else if (spinner_custbayar.getSelectedItem().toString().equals("Ya") ) {
            if(pembayaran_diterima.getText().toString().equals("") || pembayaran_diterima.getText().toString().equals("0") ){
                updateCollectibility = "update DKH set IS_COLLECT=2, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=2, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG,"Masuk ke Draf Pembayaran tidak diterima");
            }else {
                updateCollectibility = "update DKH set IS_COLLECT=1, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=1, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG,"Masuk ke Done Pembayaran diterima");
            }
        }else if(spinner_custbayar.getSelectedItem().toString().equals("Tidak")){
            if(txttgljanjibayar.getText().toString().equals("")){
                updateCollectibility = "update DKH set IS_COLLECT=2, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=2, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG,"Masuk ke Done tanggal janji bayar kosong");
            }else {
                updateCollectibility = "update DKH set IS_COLLECT=1, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
                updateCollectibility2 = "update COLLECTED set IS_COLLECT=1, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
                Log.d(TAG,"Masuk ke Done tanggal janji bayar tidak kosong");
            }
        }else if(spinner_custbayar.getSelectedItem().toString().equals("Pilih")) {
            updateCollectibility = "update DKH set IS_COLLECT=2, DailyCollectibility='Coll Harian' where NOMOR_KONTRAK = " + strContractID;
            updateCollectibility2 = "update COLLECTED set IS_COLLECT=2, DailyCollectibility='Coll Harian' where CONTRACT_ID = " + strContractID;
            Log.d(TAG,"masuk draft");
        }

        dbInsert.execSQL(saved);
        //dbInsert.execSQL(SavedCollected);
        //dbInsert.execSQL(updateCollectibility2);
        dbInsert.execSQL(updateCollectibility);

        if(imageViewPembayaran.getDrawable() != null){
            dbhelper.insertDataImage(strContractID, imageViewToByte(imageViewPembayaran), getDate);
        }

        if(imageView.getDrawable() != null){
            dbhelper.insertDataImage(strContractID, imageViewToByte(imageView), getDate);
        }

        String contract_id =  ((TextView) getActivity().findViewById(R.id.nomor_kontrak2)).getText().toString();
        ResultFragment fragment = new ResultFragment();
        Bundle arguments = new Bundle();
        arguments.putString( "paramId" , contract_id);
        Log.d(TAG,"Contract ID -> " + contract_id);
        fragment.setArguments(arguments);
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment).commit();
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
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
            txt_period.setText(cursor.getString(38));
            txttotaltagihan.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(15)))).replaceAll("Rp", ""));
            txt_angsuran.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(11)))).replaceAll( "Rp", "" ));
            txtDenda_tagihan.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(13))).replaceAll("Rp","")));
            txt_totalTagihan2.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(15)))).replaceAll( "Rp", "" ));
            txtTotalTagihanAll.setText(String.valueOf(Double.parseDouble(cursor.getString(15))));

            intTotaltagihan = Double.parseDouble(txtTotalTagihanAll.getText().toString()) + biaya_admin;
            txt_totalTagihan2.setText(formatRupiah.format((double)intTotaltagihan).replaceAll("Rp",""));
        }
    }

    public Uri getOutputMediaFileUri() {
        return FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID + ".provider",getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MobileCollection");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        if(imageView.getDrawable() !=  null) {
            imageView.setImageBitmap(decoded);
        }else{
            imageView.setImageBitmap(decoded);
        }
    }

    private void setToImageViewPembayaran(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        if(imageViewPembayaran.getDrawable() !=  null) {
            imageViewPembayaran.setImageBitmap(decoded);
        }else{
            imageViewPembayaran.setImageBitmap(decoded);
        }
    }

    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio= (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("CAMERA", fileUri.getPath());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    Log.e(TAG,"Bitmap -> "+bitmap);
                    if(flagFoto == "2") {
                        setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                    }else if(flagFoto == "1"){
                        setToImageViewPembayaran(getResizedBitmap(bitmap, max_resolution_image));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}