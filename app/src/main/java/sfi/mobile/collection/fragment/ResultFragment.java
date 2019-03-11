package sfi.mobile.collection.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sfi.mobile.collection.R;

public class ResultFragment extends Fragment {

    private static final String TAG = ResultFragment.class.getSimpleName();
    TextView txt_contractId;

    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        Button btnBack = (Button) view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contract_id =  ((TextView) getActivity().findViewById(R.id.txtcontractid)).getText().toString();
                StatusDetailFragment fragment = new StatusDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putString( "paramId" , contract_id);
                Log.d(TAG,"Contract ID -> " + contract_id);
                fragment.setArguments(arguments);
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container_wrapper, fragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        txt_contractId = (TextView) getActivity().findViewById(R.id.txtcontractid);
        Bundle arguments = getArguments();
        final String paramId = arguments.getString("paramId");
        txt_contractId.setText(paramId);
    }
}
