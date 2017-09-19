package com.example.leekwangho.escapesunapp.Database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LeeKwangho on 2017-09-19.
 */

public class SharedPreferenceUtil {
    public static final String APP_SHARED_PREFS = "ESCAPE_SUN.SharedPreference";
    public final String EMERGENCY_KEY = "EMG";
    public final String ON = "ON";
    public final String OFF = "OFF";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    public SharedPreferenceUtil(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void setData(String key,String Data) {
        editor.putString(key,Data);
        editor.commit();
    }

    public void removeData(String key){
        SharedPreferences pref = context.getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, "null"); // "test"는 키, "defValue"는 키에 대한 값이 없을 경우 리턴해줄 값
    }
}