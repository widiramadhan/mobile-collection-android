package sfi.mobile.collection.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.PocketPos;
import sfi.mobile.collection.helper.P25ConnectionException;
import sfi.mobile.collection.helper.P25Connector;
import sfi.mobile.collection.util.FontDefine;
import sfi.mobile.collection.util.Printer;

public class PrintFragment extends Fragment {

    TextView txtcontractid;
    Button btnPrint, btnConnect;
    Spinner mDeviceSp;

    private P25Connector mConnector;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;
    private static OutputStream outputStream;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    public PrintFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print, container, false);

        txtcontractid = (TextView) view.findViewById(R.id.contract_id);

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

        btnPrint = (Button) getActivity().findViewById(R.id.btn_print);
        btnConnect = (Button) getActivity().findViewById(R.id.btn_connect);
        mDeviceSp = (Spinner) getActivity().findViewById(R.id.sp_device);

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
        String titleStr = "\n\n"+"   STRUK PEMBAYARAN ANGSURAN" + "\n\n";

        StringBuilder contentSb = new StringBuilder();

        contentSb.append("Terminal ID   : 78099962" + "\n");
        contentSb.append("No Transaksi  : 1001021900001" + "\n");
        contentSb.append("Tgl Transaksi : "+new SimpleDateFormat("dd MMM yyyy").format(new Date()) + "\n");
        contentSb.append("\n\n");
        contentSb.append("No Kontrak    : 1005050001501" + "\n");
        contentSb.append("Nama Kostumer : Supriyadi" + "\n");
        contentSb.append("Angsuran Ke   : 2" + "\n");
        contentSb.append("Jatuh Tempo   : 02 Feb 2019" + "\n");
        contentSb.append("Pembayaran    : Rp. 750.000" + "\n");
        contentSb.append("PIC           : Wawan" + "\n");

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

        //1D barcode format (hex): 1d 6b 02 0d + barcode data
        byte[] formats	= {(byte) 0x1d, (byte) 0x6b, (byte) 0x02, (byte) 0x0d};
        byte[] contents	= content.getBytes();
        byte[] bytes = new byte[titleByte.length + content1Byte.length + formats.length + contents.length + messageByte.length];

        int offset = 0;
        System.arraycopy(titleByte, 0, bytes, offset, titleByte.length);
        offset += titleByte.length;
        System.arraycopy(content1Byte, 0, bytes, offset, content1Byte.length);
        offset += content1Byte.length;
        System.arraycopy(formats, 0, bytes, offset, formats.length);
        offset += formats.length;
        System.arraycopy(contents, 0, bytes, offset, contents.length);
        offset += contents.length;
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
