package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.PocketPos;
import sfi.mobile.collection.helper.DBHelper;
import sfi.mobile.collection.helper.P25ConnectionException;
import sfi.mobile.collection.helper.P25Connector;
import sfi.mobile.collection.util.FontDefine;
import sfi.mobile.collection.util.Printer;

public class PrintFragment extends Fragment {

    TextView txtcontractid;
    Button btnPrint, btnConnect;
    Spinner mDeviceSp;
    protected Cursor cursor;
    DBHelper dbhelper;
    String strContractId,strCostumername,strtgljatuhtempo,strAngsuranKe,strDevice,strTotaltagihan;
    int strtotal,strDenda,strAngsuran;
    Double strHasil,strAmount;

    TextView txt_costumername,txt_angsuranke,txt_jatuhtempo,txt_amount,txt_Id,txt_pic,txt_total;

    private P25Connector mConnector;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;
    private static OutputStream outputStream;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    private static final String TAG = DashboardTabTasklist.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String fullName, branchName;
    /*** end memanggil session yang terdaftar ***/

    public PrintFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print, container, false);

        txtcontractid = (TextView) view.findViewById(R.id.contract_id);
       /* txt_costumername = (TextView) view.findViewById(R.id.txt_costumername);
        txt_angsuranke = (TextView) view.findViewById(R.id.txt_angsuranke);
        txt_jatuhtempo = (TextView) view.findViewById(R.id.txt_jatuhtempo);
        txt_amount = (TextView) view.findViewById(R.id.txt_amount);
        txt_Id = (TextView) view.findViewById(R.id.txt_Id);
        txt_pic = (TextView) view.findViewById(R.id.txt_PIC);
        txt_total = (TextView) view.findViewById(R.id.txt_total);*/

        /*DashboardTabPriority fragment = new DashboardTabPriority();
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.addToBackStack("A_B_TAG");
        fragmentTransaction.commit();*/

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        /*** end Get parameter dari halaman sebelumnya ***/
        txtcontractid.setText(paramId);

        /*** set session to variable ***/
        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);
       // txt_pic.setText(fullName);

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
            /*txt_Id.setText(strContractId);
            txt_costumername.setText(strCostumername);
            txt_angsuranke.setText(strAngsuranKe);
            txt_jatuhtempo.setText(strtgljatuhtempo);
            txt_amount.setText(String.valueOf(strAmount));
            txt_total.setText(String.valueOf(strtotal));*/
        }

        Log.d("Data","contract_ID ->" + strContractId);
        Log.d("Data","Costumer Name ->" + strCostumername);
        Log.d("Data","strTglJatuhTempo ->" + strtgljatuhtempo);
        Log.d("Data","Angsuran ke ->" + strAngsuranKe);
        Log.d("Data","Bayar ->" + strAmount);
        Log.d("Data","PIC ->" + fullName);
        Log.d("Data","Denda ->" + strDenda);
        Log.d("Data","Angsuran Berjalan ->" + strAngsuran);
        Log.d("Data","Total Tagihan ->" + strtotal);


        btnPrint = (Button) getActivity().findViewById(R.id.btn_print);
        btnConnect = (Button) getActivity().findViewById(R.id.btn_connect);
        mDeviceSp = (Spinner) getActivity().findViewById(R.id.sp_device);

        strHasil = strtotal - strAmount ;
        Log.d("Data","Hasil->" + strHasil);

        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                btnConnect.setText("SCAN");
            } else {
                btnConnect.setText("CONNECT");
            }
        }*/


        mConnectingDlg 	= new ProgressDialog(getActivity());
        mConnectingDlg.setMessage("Connecting...");
        mConnectingDlg.setCancelable(false);
        mConnector 	= new P25Connector(new P25Connector.P25ConnectionListener() {
            @Override
            public void onStartConnecting() {
                mConnectingDlg.show();
            }

            @Override
            public void onConnectionSuccess() {
                mConnectingDlg.dismiss();

                showConnected();
            }

            @Override
            public void onConnectionFailed(String error) {
                mConnectingDlg.dismiss();
            }

            @Override
            public void onConnectionCancelled() {
                mConnectingDlg.dismiss();
            }

            @Override
            public void onDisconnected() {
                showDisonnected();
            }
        });

        //enable bluetooth
        /*mEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(intent, 1000);
            }
        });*/
        btnPrint.setEnabled(false);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String strBtnConnect = btnConnect.getText().toString();
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) { //jika device tidak mempunyai bluetooth
                    showUnsupported();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) { //jika bluetooth mati
                        showDisabled();
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1000);
                    } else { //jika bluetooth nyala
                        showEnabled();
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                        if (pairedDevices != null) {
                            mDeviceList.addAll(pairedDevices);
                            updateDeviceList();
                            connect();
                            btnPrint.setEnabled(true);
                        }
                    }
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                printStruk();
                /*DashboardTabPriority fragment = new DashboardTabPriority();
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
                fragmentTransaction.addToBackStack("A_B_TAG");
                fragmentTransaction.commit();*/
            }
        });
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size	= data.size();
        list		= new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }

    private void showDisabled() {
        Toast.makeText(getActivity(), "Bluetooth disabled, mohon nyalakan bluetooth terlebih dahulu", Toast.LENGTH_LONG).show();
    }

    private void showEnabled() {
        Toast.makeText(getActivity(), "Bluetooth enabled", Toast.LENGTH_LONG).show();
    }


    public void showUnsupported(){
        Toast.makeText(getActivity(), "Bluetooth is unsupported by this device", Toast.LENGTH_LONG).show();
        btnPrint.setEnabled(false);
    }

    private void showConnected(){
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();
        btnPrint.setEnabled(true);
    }

    private void showDisonnected() {
        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_LONG).show();
        btnPrint.setEnabled(false);
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }
        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Failed to pair device", Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();
                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {
        try {
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};
            Method method 	= cl.getMethod("createBond", par);
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void printStruk() {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String titleStr = "\n\n"+"   STRUK PEMBAYARAN ANGSURAN" + "\n\n";

        StringBuilder contentSb = new StringBuilder();

        contentSb.append("Terminal ID   : 78099962" + "\n");
        contentSb.append("No Transaksi  : 1001021900001" + "\n");
        contentSb.append("Tgl Transaksi : "+new SimpleDateFormat("dd MMM yyyy").format(new Date()) + "\n");
        contentSb.append("\n\n");
        contentSb.append("No Kontrak    : "+ strContractId + "\n");
        contentSb.append("Nama Kostumer : "+ strCostumername + "\n");
        contentSb.append("Angsuran Ke   : "+ strAngsuranKe + "\n");
        contentSb.append("Jatuh Tempo   : "+ strtgljatuhtempo + "\n");
        contentSb.append("Angsuran      : Rp. "+ String.valueOf(formatRupiah.format((double)strAngsuran).replaceAll( "Rp", "" )) + "\n");
        contentSb.append("Denda         : Rp. "+ String.valueOf(formatRupiah.format((double)strDenda).replaceAll( "Rp", "" )) + "\n");
        contentSb.append("Total Tagihan : Rp. "+ String.valueOf(formatRupiah.format((double)strtotal).replaceAll( "Rp", "" )) + "\n");
        contentSb.append("Pembayaran    : Rp. "+ String.valueOf(formatRupiah.format((double)strAmount).replaceAll( "Rp", "" )) + "\n");
        contentSb.append("Sisa          : Rp. "+ String.valueOf(formatRupiah.format((double)strHasil).replaceAll( "Rp", "" ))+ "\n");
        contentSb.append("PIC           : "+ fullName + "\n");

        String message   = "\n  Simpan bukti pembayaran ini\nSebagai tanda pembayaran yg sah" + "\n\n\n";

        //barcode
        String content	= "1001021900001";
        //image
       // byte[] imageLogo = "";
        String imageLogo = "";



        //header
        byte[] titleByte    = Printer.printfont(titleStr, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        //content
        byte[] content1Byte = Printer.printfont(contentSb.toString(), FontDefine.FONT_24PX,FontDefine.Align_LEFT,
                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        //footer
        byte[] messageByte  = Printer.printfont(message, FontDefine.FONT_24PX,FontDefine.Align_CENTER,
                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] bytes = new byte[titleByte.length + content1Byte.length  + messageByte.length];

        int offset = 0;
        System.arraycopy(titleByte, 0, bytes, offset, titleByte.length);
        offset += titleByte.length;
        System.arraycopy(content1Byte, 0, bytes, offset, content1Byte.length);
        offset += content1Byte.length;
        System.arraycopy(messageByte, 0, bytes, offset, messageByte.length);
        offset += messageByte.length;

        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, bytes, 0, bytes.length);
        sendData(senddata);
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

}
