package sfi.mobile.collection.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sfi.mobile.collection.R;

public class QuestionCustResult extends Fragment {

    public QuestionCustResult(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_cust_result, container, false);

        return view;
    }
}
