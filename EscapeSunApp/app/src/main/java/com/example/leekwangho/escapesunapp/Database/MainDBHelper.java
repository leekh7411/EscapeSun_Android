package com.example.leekwangho.escapesunapp.Database;

import android.content.Context;
import android.util.Log;

import com.example.leekwangho.escapesunapp.CallList.CallListItem;

import java.util.ArrayList;

/**
 * Created by LeeKwangho on 2017-09-07.
 */

public class MainDBHelper {
    private final String TAG = "MainDBHelper";
    private CallListDB callListDB = null;
    private Context appContext;
    public MainDBHelper(Context appContext){
        this.appContext = appContext;
    }

    /**
     *  Call List database methods
     * **/
    public void __callList__insert_new_list(String name,String phone_num){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        callListDB.insert_new_list(name,phone_num);
        callListDB.close();
    }

    public void __callList__delete_using_name(String name){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        callListDB.delete_call_list_using_key_name(name);
        callListDB.close();
    }

    public void __callList__update_data_using_name(String name,String new_phone_number){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        callListDB.update__phone_number__using__key_name(new_phone_number,name);
        callListDB.close();
    }

    public void __callList__update_data_using_phone_number(String phone_number,String new_name){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        callListDB.update__key_name__using__phone_number(phone_number,new_name);
        callListDB.close();
    }

    public void __callList__delete_all_data(){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        callListDB.delete_call_list_all();
        callListDB.close();
    }

    public void __callList_read_all_data(){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        String resultData = callListDB.getCallList();
        Log.d(TAG,resultData);
        callListDB.close();
    }

    public ArrayList<CallListItem> __callList_get_all_data_ArrayList(){
        callListDB = new CallListDB(appContext,"CALL_LIST_DB",null,1);
        ArrayList<CallListItem> items = new ArrayList<>();
        items = callListDB.getCallArrayList();
        callListDB.close();
        return items;
    }
    /**
     *  Sensor data database methods
     * **/
}
