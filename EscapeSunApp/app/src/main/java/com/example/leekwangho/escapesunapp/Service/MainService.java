package com.example.leekwangho.escapesunapp.Service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.leekwangho.escapesunapp.BLEMenu.Utils.ScannedDevice;
import com.example.leekwangho.escapesunapp.DataReadActivity;

/**
 * Created by LeeKwangho on 2017-09-09.
 */

public class MainService extends Service {
    private MainServiceThread mainServiceThread;
    private final String TAG = "MainService";
    public static ScannedDevice selectedDevice = null;
    public static BluetoothDevice selectedBleDevice = null;
    private Context mContext;

    /* data values */
    public static int temperature;
    public static int body_heat;
    public static int heart_rate;

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"main service start");
        mContext = this;
        mainServiceThread = new MainServiceThread(mContext);
        mainServiceThread.start();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"main service destroy");
        mainServiceThread.setThreadRun(false);
        super.onDestroy();
    }
}
