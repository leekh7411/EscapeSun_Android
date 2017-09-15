package com.example.leekwangho.escapesunapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.leekwangho.escapesunapp.CallList.CallListItem;

import java.util.ArrayList;

/**
 * Created by LeeKwangho on 2017-09-07.
 */

public class CallListDB extends SQLiteOpenHelper {
    String table = "call_list"; // what is this?
    private final String TAG = "CallListDB";
    public CallListDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE IF NOT EXISTS "+table+" (phone_number TEXT, key_name TEXT);");
        Log.d(TAG,"create call list database table");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @_CALL_메시지_알림을_위한_데이터베이스_함수들
     * **/
    public void insert_call_msg(String code, String room) {
        // 읽고 쓰기가 가능하게 DB 열기

        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("DELETE FROM call_list;");
        db.execSQL("INSERT INTO call_list (code,room) VALUES( '" +code +"', '" + room + "');");
        db.close();
    }

    public void delete_call_msg( ) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM call_msg;");
        db.close();
    }

    public String getResult_From_CallMSG() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = null;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM call_msg", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + ","
                    + cursor.getString(1)
            ;
        }
        cursor.close();
        return result;
    }


    public void insert_new_list(String phone_number, String key_name) {
        // 읽고 쓰기가 가능하게 DB 열기
        Log.d(TAG,"Insert new list data");
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO call_list (phone_number,key_name) VALUES( '" +phone_number +"','"+ key_name +"');");
        db.close();
    }

    public void delete_call_list_all( ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM call_list;");
        db.close();
    }

    public void delete_call_list_using_key_name(String key_name ){
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM call_list where key_name like " + key_name +";");
        db.close();
    }

    public void update__phone_number__using__key_name(String new_phone_number,String key_name){
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("UPDATE call_list SET phone_number = "+ new_phone_number +" where key_name like " + key_name +";");
        db.close();
    }

    public void update__key_name__using__phone_number(String phone_number,String new_key_name){
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("UPDATE call_list SET key_name = "+ new_key_name +" where phone_number like " + phone_number+";");
        db.close();
    }

    public String getCallList() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM "+ table, null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " | "
                    + cursor.getString(1)
                    + " | "
            ;
        }
        cursor.close();
        return result;
    }

    public ArrayList<CallListItem> getCallArrayList() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        ArrayList<CallListItem> items = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM "+ table, null);
        while (cursor.moveToNext()) {
            items.add(new CallListItem(cursor.getString(0) , cursor.getString(1)));
        }
        cursor.close();
        return items;
    }
}
