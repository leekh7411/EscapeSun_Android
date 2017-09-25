package com.example.leekwangho.escapesunapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.Chart.SensorChart;
import com.example.leekwangho.escapesunapp.Service.MainService;
import com.example.leekwangho.escapesunapp.Service.MainServiceThread;

public class DebuggingActivity extends Activity {
    private Button resetBTN;
    private Button plusTemp,minusTemp;
    private Button plusBody,minusBody;
    private Button plusHeart,minusHeart;
    private Button plusHumi,minusHumi;
    private TextView Temp,Body,Heart,Humi;
    private final int DEFAULT_TEMP = 28;
    private final int DEFAULT_BODY = 35;
    private final int DEFAULT_HEART = 75;
    private final int DEFAULT_HUMI = 30;
    public static SensorChart sensorChart_debug;
    public static boolean IsDebug = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugging);
        sensorChart_debug = new SensorChart(this,R.id.sensor_chart_debug,-1);
        resetBTN = findViewById(R.id.resetDebugBTN);
        plusTemp = findViewById(R.id.debug_temp_plus);
        minusTemp = findViewById(R.id.debug_temp_minus);
        plusBody = findViewById(R.id.debug_body_heat_plus);
        minusBody = findViewById(R.id.debug_body_heat_minus);
        plusHeart = findViewById(R.id.debug_heart_rate_plus);
        minusHeart = findViewById(R.id.debug_heart_rate_minus);
        plusHumi = findViewById(R.id.debug_humidity_plus);
        minusHumi = findViewById(R.id.debug_humidity_minus);
        Temp = (TextView)findViewById(R.id.debug_temp_value);
        Body = (TextView)findViewById(R.id.debug_body_heat_value);
        Heart = (TextView)findViewById(R.id.debug_heart_rate_value);
        Humi = (TextView)findViewById(R.id.debug_humidity_value);
        resetBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsDebug = true;
                MainService.temperature = DEFAULT_TEMP;
                MainService.body_heat = DEFAULT_BODY;
                MainService.heart_rate = DEFAULT_HEART;
                MainService.humidity = DEFAULT_HUMI;
                Temp.setText(""+MainService.temperature);
                Heart.setText(""+MainService.heart_rate);
                Body.setText(""+MainService.body_heat);
                Humi.setText(""+MainService.humidity);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        plusTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.temperature++;
                Temp.setText(""+MainService.temperature);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        minusTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.temperature--;
                Temp.setText(""+MainService.temperature);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        plusBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.body_heat++;
                Body.setText(""+MainService.body_heat);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        minusBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.body_heat--;
                Body.setText(""+MainService.body_heat);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        plusHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.heart_rate++;
                Heart.setText(""+MainService.heart_rate);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        minusHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.heart_rate--;
                Heart.setText(""+MainService.heart_rate);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        plusHumi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.humidity++;
                Humi.setText(""+MainService.humidity);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });
        minusHumi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainService.humidity--;
                Humi.setText(""+MainService.humidity);
                MainServiceThread.WriteSensorForDebugging = true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
        IsDebug = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        IsDebug = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsDebug = false;
    }
}
