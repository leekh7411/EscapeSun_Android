package com.example.leekwangho.escapesunapp.CallList;

import android.widget.TextView;

/**
 * Created by LeeKwangho on 2017-09-15.
 */

public class CallListItem {
    private String name;
    private String phone_number;
    public CallListItem(){

    }
    public CallListItem(String _name,String _phone_number){
        name = _name;
        phone_number = _phone_number;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
