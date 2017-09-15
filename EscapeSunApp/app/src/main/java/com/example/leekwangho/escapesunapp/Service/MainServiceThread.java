package com.example.leekwangho.escapesunapp.Service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUtil;
import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid;
import com.example.leekwangho.escapesunapp.DataReadActivity;
import com.example.leekwangho.escapesunapp.GPS.GPSmanager;
import com.example.leekwangho.escapesunapp.R;
import com.example.leekwangho.escapesunapp.SMS.Messenger;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by LeeKwangho on 2017-09-09.
 **/

public class MainServiceThread extends Thread {
    /* TODO: 2017-09-09
      1. ble connection control
      2. read ble characteristic values
      3. send emergency message
    */
    private Context mContext;
    private BluetoothAdapter mBTAdapter;
    private BluetoothGatt mConnGatt;
    private BluetoothDevice mDevice = null;
    private int mStatus;
    private boolean SensorReadThreadFlag = false;
    public static boolean IsEmergencyMessageSend = true;
    private final String TAG = "MainService_thread";
    private boolean IsRun = false;
    private GPSmanager gps_manager;
    private Messenger messenger;
    public MainServiceThread(Context _mContext){
        IsRun = true;
        mContext = _mContext;
        gps_manager = new GPSmanager(mContext,(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE));
        messenger = new Messenger(mContext);
        init();
    }
    @Override
    public void run() {
        super.run();
        Log.d(TAG,"... Start -> main service thread");
        try{
            StartSensorReadThread();
            while(IsRun){
                Log.d(TAG,"... running in main service thread");
                sleep(1000);

                if(IsEmergencyMessageSend){

                    // TODO: 2017-09-15 Send Emergency SMS here!
                    if(gps_manager.myLocation != null){
                        IsEmergencyMessageSend = false;
                        Log.d(TAG,"Location : " + gps_manager.myLocation);
                        sleep(500);
                        /*messenger.Send_MMS_To("01062429674","애국가 1절 " + gps_manager.myLocation + " 도옹해애무울과 배액" +
                                "두~산이 마아~르고 닳~~~도록 . .  하아아느님--이 보오-우하사 우-우리 나라 만세");
                        Log.d(TAG,"Location SMS send complete to 럼상");
                        sleep(500);
                        messenger.Send_MMS_To("01055760452","애국가 2절 " + gps_manager.myLocation + " 나아암사안 위에 저 쏘오나무" +
                                "처얼가블 두우른드읏 바람서어리 부울벼언하암은 우우리 기상일세");
                        Log.d(TAG,"Location SMS send complete to 건민");*/
                        sleep(500);

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //TODO: 2017-09-14 완전히 서비스를 끝내는 경우 아래 코드를 실행 할 것(현재는 무조건 실행 됨)
        Log.d(TAG,"... Finish -> main service thread");
        mConnGatt.close();
        SensorReadThreadFlag = false;
    }

    public void setThreadRun(boolean IsRun){
        this.IsRun = IsRun;

    }

    /*---------------------------------------------------------------------------------------------*/
    private boolean IsBLEInit = false;

    private void init() {
        if (BleUtil.selectedDevice != null) {
            BLEInit();
        }else{
            IsBLEInit = false;
        }
    }
    private void BLEInit() {
        if (!IsBLEInit) {
            IsBLEInit = true;
        } else {
            return;
        }
        // BLE check
        if (!BleUtil.isBLESupported(mContext)) {
            Toast.makeText(mContext, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(mContext);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(mContext, R.string.bt_unavailable, Toast.LENGTH_SHORT)
                    .show();
        }

        if (mDevice == null) {
            mDevice = BleUtil.selectedDevice;
        }

        // connect to Gatt
        if ((mConnGatt == null)
                && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            // try to connect
            mConnGatt = mDevice.connectGatt(mContext, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {

                return;
            }
        }
    }

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;

            }
        }

        ;

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service
                        .getUuid().toString())) {

                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {

                }
            }

        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Read GATT_SUCCESS!");
                if (BleUuid.CHAR_MANUFACTURER_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData : " + name);

                } else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData : " + name);

                }

                if (BleUuid.LED_CHAR
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData in LED : " + characteristic.getStringValue(0));
                }

                if (BleUuid.SENSOR_DATA_CHAR_ARRAY.equalsIgnoreCase(characteristic.getUuid().toString())) { // Testing

                    ArrayList<Integer> datas = readCharacteristicDataArray(characteristic, 4);
                    Log.d(TAG,
                            "\nSensor data[0] = " + datas.get(0) +
                            "\nSensor data[1] = " + datas.get(1) +
                            "\nSensor data[2] = " + datas.get(2) +
                            "\nSensor data[3] = " + datas.get(3)
                    );
                    MainService.temperature = datas.get(0);
                    MainService.body_heat = datas.get(1);
                    MainService.heart_rate = datas.get(2);
                    if(DataReadActivity.IsActivityRun){
                        SensorAsyncTask task0 = new SensorAsyncTask(datas.get(0), DataReadActivity.temperature, "");
                        task0.execute();
                        SensorAsyncTask task1 = new SensorAsyncTask(datas.get(1), DataReadActivity.body_temp, "");
                        task1.execute();
                        SensorAsyncTask task2 = new SensorAsyncTask(datas.get(2), DataReadActivity.heart_rate, "");
                        task2.execute();
                        SensorAsyncTask task3 = new SensorAsyncTask(datas.get(3), DataReadActivity.humidity,"");
                        task3.execute();
                    }

                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Log.d(TAG, "Read Failed!");
            }
        }
        ArrayList<Integer> readCharacteristicDataArray(BluetoothGattCharacteristic characteristic, int array_len) {
            ArrayList<Integer> datas = new ArrayList<>();
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Sensor format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Sensor format UINT8.");
            }
            for (int i = 0; i < array_len; i++) {
                final int val = characteristic.getIntValue(format, i);
                Log.d(TAG, String.format("Received Data Array, Sensor(%d): %d", i, val));
                datas.add(val);
            }

            return datas;
        }


    };



    /**
     * Created by leekwangho on 17. 2. 24.
     **/
    public class SensorAsyncTask extends AsyncTask<Integer, Integer, Integer> {
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
            sensor.setText(TAG + value);
        }
    }

    public class ReadDataAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        String service_uuid, sensor_uuid;

        public ReadDataAsyncTask(String _service_uuid, String _sensor_uuid) {
            service_uuid = _service_uuid;
            sensor_uuid = _sensor_uuid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            BLEReadData((service_uuid), (sensor_uuid));
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);


        }
    }

    public class ToastAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        String msg;
        public ToastAsyncTask(String msg) {
            this.msg = msg;
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
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void StartSensorReadThread(){
        SensorReadThread thread = new SensorReadThread(BleUuid.MY_SERVICE,BleUuid.SENSOR_DATA_CHAR_ARRAY);
        thread.start();
    }

    private class SensorReadThread extends Thread {
        String service_uuid, sensor_uuid;
        public SensorReadThread(String _service_uuid,String _sensor_uuid){
            service_uuid = _service_uuid;
            sensor_uuid = _sensor_uuid;
        }
        @Override
        public void run() {
            super.run();
            if(SensorReadThreadFlag){
                Log.d(TAG,"You can execute only one thread");
                printToast("You can execute only one thread");
                return;
            }
            else SensorReadThreadFlag = true;
            printToast("Sensor data reading thread is started!");
            while(SensorReadThreadFlag){
                if (!BLEReadData((service_uuid), (sensor_uuid))) {
                    Log.d(TAG, "Error Occurred! Reinitializing start !..");
                    if (BleUtil.selectedDevice != null) {
                        BLEInit();
                    }
                    try {
                        Thread.sleep(4000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private boolean BLEReadData(String service_uuid, String sensor_uuid) {
        if(mConnGatt == null){
            printToast("Please read after Scan and refresh!");
            SensorReadThreadFlag = false;
            return false;
        }
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(service_uuid));
        Log.d(TAG, "BLEReadData Start !");

        if (disService == null) {
            Log.d(TAG, "Dis service not found!");
            //printToast("Dis service not found!");
            IsBLEInit = false;
            return false;
        }


        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(sensor_uuid));
        if (characteristic == null) {
            Log.d(TAG, "Charateristic not found!");
            //printToast("Charateristic not found!");
            IsBLEInit = false;
            return false;
        }

        boolean result = mConnGatt.readCharacteristic(characteristic);

        if (result == false) {
            Log.d(TAG, "Reading is failed!");
            //printToast("Reading is failed!");
            IsBLEInit = false;
            return false;
        }

        return true;
    }

    public void printToast(String msg) {
        ToastAsyncTask task = new ToastAsyncTask(msg);
        task.execute();
    }
}
