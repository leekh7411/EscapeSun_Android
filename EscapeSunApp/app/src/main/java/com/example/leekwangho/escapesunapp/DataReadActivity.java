package com.example.leekwangho.escapesunapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leekwangho.escapesunapp.CallList.CallListActivity;
import com.example.leekwangho.escapesunapp.Chart.SensorChart;
import com.example.leekwangho.escapesunapp.Database.SharedPreferenceUtil;
import com.example.leekwangho.escapesunapp.Dialog.AlarmSettingDialog;
import com.example.leekwangho.escapesunapp.Service.MainService;
import com.example.leekwangho.escapesunapp.Service.MainServiceThread;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class DataReadActivity extends Activity {
    private TextView title;
    public static TextView temperature,body_temp,heart_rate,humidity,distance;
    public static TextView heatScan_text01,heatScan_text02;
    private ImageButton callListBTN,bleBTN,refreshBTN;
    private Button debugBTN;
    public static Switch alarm_distance,alarm_heart_rate,alarm_heat,alarm_humidity,service_switch,alarm_temperature,alarm_body_heat;
    private Intent serviceIntent = null;
    private SharedPreferenceUtil sharedPreferenceUtil;
    public static boolean IsActivityRun = false;
    public static ArrayList<Entry> sensorChartDatas01;
    public static Activity mActivity;
    public static SensorChart sensorChart;
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
        mActivity = this;
        sharedPreferenceUtil = new SharedPreferenceUtil(DataReadActivity.this);
        IsActivityRun = true;
        title = findViewById(R.id.titleText);
        if(MainService.selectedDevice != null && MainService.selectedBleDevice != null){
            title.setText(MainService.selectedDevice.getDisplayName() + " " + MainService.selectedBleDevice.getAddress());
        }
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
        distance = findViewById(R.id.alarm_distance_text);
        debugBTN = findViewById(R.id.debuggingBTN);
        debugBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDebugMode();
            }
        });
        heatScan_text01 = findViewById(R.id.alarm_heat_text01);
        heatScan_text02 = findViewById(R.id.alarm_heat_text02);
        sensorChart = new SensorChart(mActivity,R.id.sensor_chart,R.id.SensorCharts);
        sensorChart.AddEntry(10,10,10,10,10);

        // Switch
        if(sharedPreferenceUtil.getData("Is_distance_value").equals("null"))sharedPreferenceUtil.setData("Is_distance_value","0");
        if(sharedPreferenceUtil.getData("Is_heart_rate_value").equals("null"))sharedPreferenceUtil.setData("Is_heart_rate_value","0");
        if(sharedPreferenceUtil.getData("Is_humidity_value").equals("null"))sharedPreferenceUtil.setData("Is_humidity_value","0");
        if(sharedPreferenceUtil.getData("Is_temperature_value").equals("null"))sharedPreferenceUtil.setData("Is_temperature_value","0");
        if(sharedPreferenceUtil.getData("Is_body_heat_value").equals("null"))sharedPreferenceUtil.setData("Is_body_heat_value","0");
        switchInit();
    }



    private void switchInit(){
        alarm_distance = (Switch)findViewById(R.id.alarm_distance_switch);
        alarm_distance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("distance","on");
                    if(sharedPreferenceUtil.getData("Is_distance_value").equals("0")){
                        AlarmSettingDialog dialog = new AlarmSettingDialog(
                                DataReadActivity.this,"이동거리 알람 기준을 설정하세요", AlarmSettingDialog.ALARM_DISTANCE);
                        dialog.show();
                    }

                }else{
                    sharedPreferenceUtil.setData("distance","off");
                    sharedPreferenceUtil.setData("Is_distance_value","0");
                    MainServiceThread.IsDistanceOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alarm_heart_rate = (Switch)findViewById(R.id.alarm_heart_rate_switch);
        alarm_heart_rate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("heart_rate","on");
                    if(sharedPreferenceUtil.getData("Is_heart_rate_value").equals("0")){
                        AlarmSettingDialog dialog = new AlarmSettingDialog(
                                DataReadActivity.this,"심박수 알람 기준을 설정하세요", AlarmSettingDialog.ALARM_HEART_RATE);
                        dialog.show();
                    }

                }else{
                    sharedPreferenceUtil.setData("heart_rate","off");
                    sharedPreferenceUtil.setData("Is_heart_rate_value","0");
                    MainServiceThread.IsHeartRateOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alarm_heat = (Switch)findViewById(R.id.alarm_heat_switch);
        alarm_heat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("heat","on");
                    MainServiceThread.IsHeatScanOn = true;
                    Toast.makeText(getApplicationContext(),"Heat scan ON",Toast.LENGTH_SHORT).show();
                    sensorChart.setVisible(true);
                }else{
                    sharedPreferenceUtil.setData("heat","off");
                    MainServiceThread.IsHeatScanOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                    heatScan_text01.setText(getResources().getString(R.string.emg_lv01_text));
                    heatScan_text02.setText(getResources().getString(R.string.emg_lv02_text));
                    sensorChart.setVisible(false);
                }
            }
        });
        alarm_humidity = (Switch)findViewById(R.id.alarm_humidity_switch);
        alarm_humidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("humidity","on");
                    if(sharedPreferenceUtil.getData("Is_humidity_value").equals("0")){
                        AlarmSettingDialog dialog = new AlarmSettingDialog(
                                DataReadActivity.this,"습도 알람 기준을 설정하세요", AlarmSettingDialog.ALARM_HUMIDITY);
                        dialog.show();
                    }

                }else{
                    sharedPreferenceUtil.setData("humidity","off");
                    sharedPreferenceUtil.setData("Is_humidity_value","0");
                    MainServiceThread.IsDistanceOFF = true;
                    MainServiceThread.IsHumidityOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });
        service_switch = (Switch)findViewById(R.id.service_title);
        service_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("service","on");
                    startMainService();
                }else{
                    sharedPreferenceUtil.setData("service","off");
                    stopMainService();
                }
            }
        });

        ///
        alarm_temperature = findViewById(R.id.alarm_temperature_switch);
        alarm_temperature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("temperature","on");
                    if(sharedPreferenceUtil.getData("Is_temperature_value").equals("0")){
                        AlarmSettingDialog dialog = new AlarmSettingDialog(
                                DataReadActivity.this,"외부 온도 알람 기준을 정하세요", AlarmSettingDialog.ALARM_TEMPERATURE);
                        dialog.show();
                    }
                }else{
                    sharedPreferenceUtil.setData("temperature","off");
                    sharedPreferenceUtil.setData("Is_temperature_value","0");
                    MainServiceThread.IsTemperatureOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        alarm_body_heat = findViewById(R.id.alarm_bodyheat_switch);
        alarm_body_heat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferenceUtil.setData("body_heat","on");
                    if(sharedPreferenceUtil.getData("Is_body_heat_value").equals("0")){
                        AlarmSettingDialog dialog = new AlarmSettingDialog(
                                DataReadActivity.this,"체온 알람 기준을 정하세요", AlarmSettingDialog.ALARM_BODY_HEAT);
                        dialog.show();
                    }
                }else{
                    sharedPreferenceUtil.setData("body_heat","off");
                    sharedPreferenceUtil.setData("Is_body_heat_value","0");
                    MainServiceThread.IsBodyHeatOFF = true;
                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///

        if(sharedPreferenceUtil.getData("distance").equals("on"))alarm_distance.setChecked(true);
        if(sharedPreferenceUtil.getData("heart_rate").equals("on"))alarm_heart_rate.setChecked(true);
        if(sharedPreferenceUtil.getData("heat").equals("on")){
            alarm_heat.setChecked(true);
            sensorChart.setVisible(true);
        }else{
            sensorChart.setVisible(false);
        }
        if(sharedPreferenceUtil.getData("humidity").equals("on"))alarm_humidity.setChecked(true);
        if(sharedPreferenceUtil.getData("service").equals("on"))service_switch.setChecked(true);
        if(sharedPreferenceUtil.getData("temperature").equals("on"))alarm_temperature.setChecked(true);
        if(sharedPreferenceUtil.getData("body_heat").equals("on"))alarm_body_heat.setChecked(true);


    }


    private void startReadingTask(){
        ReadingSensor readingSensor = new ReadingSensor();
        readingSensor.execute();
    }
    // Service Methods
    private void startMainService(){
        serviceIntent = new Intent(getApplicationContext(),MainService.class);
        startService(serviceIntent);
    }
    private void stopMainService(){
        //Intent intent = new Intent(getApplicationContext(),MainService.class);
        if(serviceIntent == null)serviceIntent = new Intent(getApplicationContext(),MainService.class);
        stopService(serviceIntent);
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
    private void goDebugMode(){
        Intent intent = new Intent(getApplicationContext(),DebuggingActivity.class);
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
}
