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

public class NavMenuFragment extends Fragment {
    public View view;
    private Context mContext;
    EditText idInput, passwordInput;
    Button loginBtn, signUpBtn;
    CheckBox autoLogin;
    public NavMenuFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = inflater.getContext();
        final View innerview = inflater.inflate(R.layout.activity_login, container, false);
        idInput = (EditText)innerview.findViewById(R.id.idInput);
        passwordInput = (EditText) innerview.findViewById(R.id.passwordInput);

        loginBtn = (Button) innerview.findViewById((R.id.loginButton));
        signUpBtn = (Button) innerview.findViewById((R.id.signupButton));

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });
        return innerview;
    }



}
