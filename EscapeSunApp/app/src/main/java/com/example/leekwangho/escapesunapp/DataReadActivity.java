package com.example.leekwangho.escapesunapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leekwangho.escapesunapp.CallList.CallListActivity;
import com.example.leekwangho.escapesunapp.Service.MainService;

public class DataReadActivity extends Activity {
    private TextView title;
    public static TextView temperature,body_temp,heart_rate,humidity;
    private ImageButton callListBTN,bleBTN,refreshBTN;
    public static boolean IsActivityRun = false;
    @Override
    protected void onResume() {
        super.onResume();
        IsActivityRun = true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        IsActivityRun = false;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        IsActivityRun = true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsActivityRun = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_read);
        // Check Permission
        if (!hasPermissions(PERMISSIONS)) {
            requestNecessaryPermissions(PERMISSIONS);
        } else {
            // Already Granted
        }
        IsActivityRun = true;
        title = (TextView)findViewById(R.id.titleText);
        title.setText(MainService.selectedDevice.getDisplayName() + " " + MainService.selectedBleDevice.getAddress());
        callListBTN = (ImageButton)findViewById(R.id.callListBTN);
        callListBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callList();
            }
        });
        bleBTN = (ImageButton)findViewById(R.id.manageBleBTN);
        bleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBle();
            }
        });
        refreshBTN = (ImageButton)findViewById(R.id.doRefreshBTN);
        refreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRefresh();
            }
        });
        temperature = (TextView)findViewById(R.id.sensor_value_Temperature);
        body_temp = (TextView)findViewById(R.id.sensor_value_BodyTemp);
        heart_rate = (TextView)findViewById(R.id.sensor_value_HeartRate);
        humidity = (TextView)findViewById(R.id.sensor_value_Humidity);
        //Service start
        startMainService();
        //Reading AsyncTask Start
        //startReadingTask();
    }
    private void startReadingTask(){
        ReadingSensor readingSensor = new ReadingSensor();
        readingSensor.execute();
    }
    // Service Methods
    private void startMainService(){
        Intent intent = new Intent(getApplicationContext(),MainService.class);
        startService(intent);
    }
    private void isServiceDown(){
        Intent intent = new Intent(getApplicationContext(),MainService.class);
        stopService(intent);
    }

    /**
     * Button Click Event Listener
     * **/
    private void doRefresh(){
        Toast.makeText(getApplicationContext(),"Refresh",Toast.LENGTH_SHORT).show();
    }
    private void manageBle(){
        finish();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }
    private void callList(){
        Intent intent = new Intent(getApplicationContext(),CallListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_right_in,R.anim.translate_left_out);
    }
    /**
     * Permission codes ****************************************************************************
     * */
    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }
        }

        //모든 퍼미션이 허가된 경우
        return true;
    }
    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.ACCESS_COARSE_LOCATION"
    };
    @Override
    public void onBackPressed() {
        isServiceDown();
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }


    public class ReadingSensor extends AsyncTask<Integer, Integer, Integer>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers){
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params){

        }

        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);

        }
    }

    class SensorAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        TextView sensor;
        int value;
        String TAG;

        public SensorAsyncTask(int val, TextView s0, String _TAG) {
            sensor = s0;
            value = val;
            TAG = _TAG;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(value == 255){
                sensor.setText("연결 중입니다...");
            }else{
                sensor.setText(TAG + value);
            }

        }
    }
}