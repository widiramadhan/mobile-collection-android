package sfi.mobile.collection.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import sfi.mobile.collection.R;

import sfi.mobile.collection.adapter.CustomFragmentPageQuestionAdapter;
import sfi.mobile.collection.helper.DBHelper;

public class QuestionFragment3 extends Fragment {

    private static final String TAG = QuestionFragment3.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ProgressDialog pDialog;

    protected Cursor cursor;
    DBHelper dbhelper;

    EditText tgljatuhtempo,overduedays,tenor,jmloverdue,angsuranberjalan,angsurantertunggak,denda,titipan,totaltagihan,outstandingAR,
            alamatktp,mobilephone,alamatkantor,nomorkantor,alamatsurat,nomorsurat,picterakhir,penanganganterakhir,tgljanjibayar;
    TextView contract_id,costumername,amount;

    public QuestionFragment3() {


    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_customer, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs_2);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager_2);

        viewPager.setAdapter(new CustomFragmentPageQuestionAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        dbhelper = new DBHelper(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        /*** Get parameter dari halaman sebelumnya ***/
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        TextView contractID = (TextView) getActivity().findViewById(R.id.contract_id);
        contractID.setText(paramId);
        /*** end Get parameter dari halaman sebelumnya ***/

        //getInfoDataCustomerByContract(paramId);
    }

    /*private void getInfoDataCustomerByContract(String ContractID){
        dbhelper = new DBHelper(getActivity());
        contract_id = (TextView) getActivity().findViewById(R.id.nomorkontrak);

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM dkhc WHERE NOMOR_KONTRAK='" + ContractID +"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
        {
            cursor.moveToPosition(0);
            contract_id.setText(cursor.getString(3));
        }

    }*/

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
