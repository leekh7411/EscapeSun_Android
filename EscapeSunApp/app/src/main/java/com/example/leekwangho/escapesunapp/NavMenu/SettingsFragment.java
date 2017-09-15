package com.example.leekwangho.escapesunapp.NavMenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.leekwangho.escapesunapp.R;
/**
 * Created by 이광호 on 2017-07-03.
 */

public class SettingsFragment extends Fragment {
    public View view;
    private Context mContext;
    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = inflater.getContext();
        final View innerview = inflater.inflate(R.layout.activity_fragment_settings, container, false);
        return innerview;
    }



}
