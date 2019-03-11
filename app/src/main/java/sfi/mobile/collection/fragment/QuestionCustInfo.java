package sfi.mobile.collection.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sfi.mobile.collection.R;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.model.DKHC;

public class QuestionCustInfo extends Fragment {
    protected Cursor cursor;
    DBHelper dbhelper;
    ProgressDialog progressDialog;
    List<DKHC> itemList = new ArrayList<>();
    TextView txtcontract_id, txtcostumername, txttotaltagihan,
            txttgljatuhtempo, txtoverduedays, txtangsuranke, txtjmlangsuranoverdue, txttenor, txtangsuranberjalan, txtangsurantertunggak, txtdenda, txttitipan, txtoustandingar,
            txtalamagktp, txtnotlp, txtalamatkantor, txtnotlpkantor, txtalamatsurat, txtnotlpsurat, txtpicterakhir, txtpenangananterakhir, txttgljanjibayar ;

    public QuestionCustInfo(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_cust_info, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** get contract id from fragment ***/
        TextView contractID = (TextView) getActivity().findViewById(R.id.contract_id);

        txtcontract_id = (TextView) getActivity().findViewById(R.id.nomor_kontrak);
        txtcostumername = (TextView) getActivity().findViewById(R.id.nama_kostumer);
        txttotaltagihan = (TextView) getActivity().findViewById(R.id.total_tagihan);
        txttgljatuhtempo = (TextView) getActivity().findViewById(R.id.duedate);
        txtoverduedays = (TextView) getActivity().findViewById(R.id.overdue_days);
        txtangsuranke = (TextView) getActivity().findViewById(R.id.angsuranke);
        txtjmlangsuranoverdue = (TextView) getActivity().findViewById(R.id.jml_angsuran_overdue_days);
        txttenor = (TextView) getActivity().findViewById(R.id.tenor);
        txtangsuranberjalan = (TextView) getActivity().findViewById(R.id.angsuran_berjalan);
        txtangsurantertunggak = (TextView) getActivity().findViewById(R.id.angsuran_tertunggak);
        txtdenda = (TextView) getActivity().findViewById(R.id.denda);
        txttitipan = (TextView) getActivity().findViewById(R.id.titipan);
        txtoustandingar = (TextView) getActivity().findViewById(R.id.outstanding_ar);
        txtalamagktp = (TextView) getActivity().findViewById(R.id.alamat_ktp);
        txtnotlp = (TextView) getActivity().findViewById(R.id.nomor_telepon);
        txtalamatkantor = (TextView) getActivity().findViewById(R.id.alamat_kantor);
        txtnotlpkantor = (TextView) getActivity().findViewById(R.id.nomor_telepon_kantor);
        txtalamatsurat = (TextView) getActivity().findViewById(R.id.alamat_surat);
        txtnotlpsurat = (TextView) getActivity().findViewById(R.id.nomor_telepon_surat);
        txtpicterakhir = (TextView) getActivity().findViewById(R.id.pic_terakhir);
        txtpenangananterakhir = (TextView) getActivity().findViewById(R.id.penanganan_terakhir);
        txttgljanjibayar = (TextView) getActivity().findViewById(R.id.tanggal_janji_bayar);

        getDataCustomerByContract((String) contractID.getText());

    }

    private void getDataCustomerByContract(String ContractID){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        dbhelper = new DBHelper(getActivity());

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM DKH WHERE NOMOR_KONTRAK='" + ContractID +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            cursor.moveToPosition(0);
            txtcontract_id.setText(cursor.getString(4));
            txtcostumername.setText(cursor.getString(5));
            txttotaltagihan.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(15)))).replaceAll( "Rp", "" ));
            txttgljatuhtempo.setText(cursor.getString(6));
            txtoverduedays.setText(cursor.getString(7)+" Hari");
            txtangsuranke.setText(cursor.getString(8));
            txtjmlangsuranoverdue.setText(String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(9)))).replaceAll( "Rp", "" )+" Bulan");
            txttenor.setText(cursor.getString(10)+" Bulan");
            txtangsuranberjalan.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(11)))).replaceAll( "Rp", "" ));
            txtangsurantertunggak.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(12)))).replaceAll( "Rp", "" ));
            txtdenda.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(13)))).replaceAll( "Rp", "" ));
            txttitipan.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(14)))).replaceAll( "Rp", "" ));
            txtoustandingar.setText("Rp. "+String.valueOf(formatRupiah.format(Double.parseDouble(cursor.getString(16)))).replaceAll( "Rp", "" ));
            txtalamagktp.setText(cursor.getString(17));
            txtnotlp.setText(cursor.getString(19));
            txtalamatkantor.setText(cursor.getString(20));
            txtnotlpkantor.setText(cursor.getString(21));
            txtalamatsurat.setText(cursor.getString(22));
            txtnotlpsurat.setText(cursor.getString(23));
            txtpicterakhir.setText(cursor.getString(24));
            txtpenangananterakhir.setText(cursor.getString(25));
            txttgljanjibayar.setText(cursor.getString(26));
        }

    }
}

