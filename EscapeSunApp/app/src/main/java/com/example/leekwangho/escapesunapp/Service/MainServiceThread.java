package com.example.leekwangho.escapesunapp.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUtil;
import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid;
import com.example.leekwangho.escapesunapp.CallList.CallListItem;
import com.example.leekwangho.escapesunapp.DataReadActivity;
import com.example.leekwangho.escapesunapp.Database.MainDBHelper;
import com.example.leekwangho.escapesunapp.GPS.GPSmanager;
import com.example.leekwangho.escapesunapp.R;
import com.example.leekwangho.escapesunapp.SMS.Messenger;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.DISTANCE;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.EMERGENCY;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.LIMIT_BODY_HEAT;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.LIMIT_DISTANCE;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.LIMIT_HEART_RATE;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.LIMIT_HUMIDITY;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.LIMIT_TEMPERATURE;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.MODE_SWITCH;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.MY_SERVICE;
import static com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid.SENSOR_DATA_CHAR_ARRAY;
import static com.example.leekwangho.escapesunapp.DataReadActivity.IsActivityRun;
import static com.example.leekwangho.escapesunapp.DataReadActivity.alarm_body_heat;
import static com.example.leekwangho.escapesunapp.DataReadActivity.alarm_distance;
import static com.example.leekwangho.escapesunapp.DataReadActivity.alarm_humidity;
import static com.example.leekwangho.escapesunapp.DataReadActivity.alarm_temperature;
import static com.example.leekwangho.escapesunapp.DataReadActivity.distance;
import static com.example.leekwangho.escapesunapp.DataReadActivity.sensorChart;
import static com.example.leekwangho.escapesunapp.DebuggingActivity.IsDebug;
import static com.example.leekwangho.escapesunapp.DebuggingActivity.sensorChart_debug;

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
    public static boolean IsEmergencyMessageSend = false;
    private final String TAG = "MainService_thread";
    private boolean IsRun = false;
    private boolean IsShowNotification = false;
    private ArrayList<String> Noti_Messages01 = new ArrayList<>();
    private ArrayList<String> Noti_Messages02 = new ArrayList<>();
    private ArrayList<String> EMG_Messages =  new ArrayList<>();
    private int Level_01 = 0;
    private GPSmanager gps_manager;
    private Messenger messenger;
    private MainDBHelper mainDBHelper = null;

    public static boolean IsDistanceOn = false;
    public static boolean IsHeartRateOn =false;
    public static boolean IsHeatScanOn = false;
    public static boolean IsHumidityOn = false;
    public static boolean IsTemperatureOn = false;
    public static boolean IsBodyHeatOn = false;

    public static boolean IsDistanceOFF = false;
    public static boolean IsHeartRateOFF =false;
    public static boolean IsHeatScanOFF = false;
    public static boolean IsHumidityOFF = false;
    public static boolean IsTemperatureOFF = false;
    public static boolean IsBodyHeatOFF = false;

    public static int DistanceValue = 0;
    public static int HeartRateValue = 0;
    public static int HumidityValue = 0;
    public static int TemperatureValue = 0;
    public static int BodyHeatValue = 0;
    public static boolean WriteSensorForDebugging = false;

    public MainServiceThread(Context _mContext){
        IsRun = true;
        mContext = _mContext;
        gps_manager = new GPSmanager(mContext,(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE));
        messenger = new Messenger(mContext);
        Noti_Messages01.add(mContext.getResources().getString(R.string.emg_lv01_text00));
        Noti_Messages01.add(mContext.getResources().getString(R.string.emg_lv01_text01));
        Noti_Messages01.add(mContext.getResources().getString(R.string.emg_lv01_text02));
        Noti_Messages01.add(mContext.getResources().getString(R.string.emg_lv01_text03));
        Noti_Messages02.add(mContext.getResources().getString(R.string.emg_lv02_text00));
        Noti_Messages02.add(mContext.getResources().getString(R.string.emg_lv02_text01));
        Noti_Messages02.add(mContext.getResources().getString(R.string.emg_lv02_text02));
        Noti_Messages02.add(mContext.getResources().getString(R.string.emg_lv02_text03));
        EMG_Messages.add(mContext.getResources().getString(R.string.emg_lv01_msg01));
        EMG_Messages.add(mContext.getResources().getString(R.string.emg_lv01_msg02));
        EMG_Messages.add(mContext.getResources().getString(R.string.emg_lv01_msg03));
        mainDBHelper = new MainDBHelper(mContext);
        init();
    }
    @Override
    public void run() {
        super.run();
        Log.d(TAG,"... Start -> main service thread");
        try{
            StartSensorReadThread();
            while(IsRun){
                sleep(1000);
                // TODO: 2017-09-16 모드 변경 시 여기서 블루투스로 설정 값 전달!
                BleSendCheck();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //TODO: 2017-09-14 완전히 서비스를 끝내는 경우 아래 코드를 실행 할 것(현재는 무조건 실행 됨)
        Log.d(TAG,"... Finish -> main service thread");
        mConnGatt.close();
        SensorReadThreadFlag = false;
    }
    private void BleSendCheck(){
        /**
         * todo: Ble를 통해 모드값(1개 정수 변수)와 설정 값(1개 정수 변수)를 전달
         * Mode value code
         * 0000 : default, nothing
         * 0001 : check distance
         * 0010 : check heart rate
         * 0100 : check heat scan
         * 1000 : check humidity
         * */
        if(IsDistanceOn){ // code : 1
            IsDistanceOn = false;
            //Log.d(TAG,"Ble를 통해 이동거리 설정 전달");
            MainService.current_mode += (byte)0x01;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // TODO: 2017-09-18 알람 설정 값 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_DISTANCE,(byte)DistanceValue);
        }
        if(IsHeartRateOn){ // code : 2
            IsHeartRateOn = false;
            //Log.d(TAG,"Ble를 통해 심박수 설정 전달");
            MainService.current_mode += (byte)0x02;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // TODO: 2017-09-18 알람 설정 값 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_HEART_RATE,(byte)HeartRateValue);
        }
        if(IsHeatScanOn){ // code : 3
            IsHeatScanOn = false;
            MainService.current_mode += (byte)0x04;
            //Log.d(TAG,"Ble를 통해 온열손상 설정 전달");
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
        }
        if(IsHumidityOn){ // code : 4
            IsHumidityOn = false;
            //Log.d(TAG,"Ble를 통해 습도 설정 전달");
            MainService.current_mode += (byte)0x08;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // TODO: 2017-09-18 알람 설정 값 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_HUMIDITY,(byte)HumidityValue);
        }
        if(IsTemperatureOn){
            IsTemperatureOn = false;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_TEMPERATURE,(byte)TemperatureValue);
        }
        if(IsBodyHeatOn){
            IsBodyHeatOn = false;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_BODY_HEAT,(byte)BodyHeatValue);
        }


        if(IsDistanceOFF){ // code : 1
            IsDistanceOFF = false;
            //Log.d(TAG,"Ble를 통해 이동거리 설정 취소 전달");
            MainService.distance = 0;
            MainService.current_mode -= (byte)0x01;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // TODO: 2017-09-18 알람 설정 초기화 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_DISTANCE,(byte)0);
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(DISTANCE,(byte)0);
            try{sleep(50);}catch (Exception e){e.printStackTrace();}
        }
        if(IsHeartRateOFF){ // code : 2
            IsHeartRateOFF = false;
            //Log.d(TAG,"Ble를 통해 심박수 설정 취소 전달");
            MainService.current_mode -= (byte)0x02;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // TODO: 2017-09-18 알람 설정 초기화 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_HEART_RATE,(byte)0);
        }
        if(IsHeatScanOFF){ // code : 3
            IsHeatScanOFF = false;
            //Log.d(TAG,"Ble를 통해 온열손상 설정 취소 전달");
            MainService.current_mode -= (byte)0x04;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
        }
        if(IsHumidityOFF){ // code : 4
            IsHumidityOFF = false;
            //Log.d(TAG,"Ble를 통해 습도 설정 취소 전달");
            MainService.current_mode -= (byte)0x08;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData((byte)(MainService.current_mode));
            // 알람 설정 초기화 전달 할 것
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_HUMIDITY,(byte)0);
        }
        if(IsTemperatureOFF){
            IsTemperatureOFF = false;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_TEMPERATURE,(byte)0);
        }
        if(IsBodyHeatOFF){
            IsBodyHeatOFF = false;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteData(LIMIT_BODY_HEAT,(byte)0);
        }


        if(WriteSensorForDebugging){
            Log.d(TAG,"WriteSensorForDebugging !!");
            WriteSensorForDebugging = false;
            try{sleep(300);}catch (Exception e){e.printStackTrace();}
            BLEWriteDebuggingSensorData();
        }
    }
    public void setThreadRun(boolean IsRun){
        this.IsRun = IsRun;
    }

    private void SendEmergencyMessage(){

        ArrayList<CallListItem> items = new ArrayList<>();
        if(mainDBHelper != null){
            items = mainDBHelper.__callList_get_all_data_ArrayList();
        }else items = null;

        if(items != null){
            // Test 해야함
            for(int i = 0 ; i < items.size(); i++){
                try{Thread.sleep(1000);}catch (Exception e){e.printStackTrace();}
                try {

                    if(Level_01 < 0 || Level_01 > 2){
                        Log.d("MSG",items.get(i).getPhone_number() + " 전송");
                        messenger.Send_MMS_To(
                                items.get(i).getPhone_number(),
                                ""
                                        + "[위치 : " + gps_manager.myLocation + "]"
                                        + "[체온 : " + MainService.body_heat+"]"
                                        + "[외부기온 : "+MainService.temperature + "]"
                                        + "[습도 : " + MainService.humidity+"]"
                                        + "[누적 이동거리 : " +MainService.distance+ "]"
                                        + " 즉시 도움 부탁드립니다!"
                        );
                    }else{
                        Log.d("MSG",items.get(i).getPhone_number() + " 전송");
                        messenger.Send_MMS_To(
                                items.get(i).getPhone_number(),
                                ""
                                        + "[위치 : " + gps_manager.myLocation + "]"
                                        + "[체온 : " + MainService.body_heat+"]"
                                        + "[외부기온 : "+MainService.temperature + "]"
                                        + "[습도 : " + MainService.humidity+"]"
                                        + "[누적 이동거리 : " +MainService.distance+ "]"
                                        + "[예상되는 질환 : " +EMG_Messages.get(Level_01)+"]"
                                        + " 즉시 도움 부탁드립니다!"
                        );
                    }

                }catch (Exception e){e.printStackTrace();}
                try{Thread.sleep(1000);}catch (Exception e){e.printStackTrace();}
            }
        }else{
            Log.d("MSG","전화번호부가 null");
        }
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

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;

            }
        };

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
                //Log.d(TAG, "Read GATT_SUCCESS!");
                if (BleUuid.CHAR_MANUFACTURER_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData : " + name);

                } else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData : " + name);

                }
                if (EMERGENCY
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    Log.d(TAG, "Read EMERGENCY VALUE : " + val);
                    int level01 = val % 10;
                    Level_01 = level01;
                    int level02 = (val % 100 - level01)/10;

                    switch (level01){
                        case 0:{
                            IsShowNotification = true;
                            String msg = mContext.getResources().getString(R.string.emg_lv01_text00);
                            //DataReadActivity.heatScan_text01.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text01);
                            break;
                        }
                        case 1:{
                            IsShowNotification = true;
                            String msg = mContext.getResources().getString(R.string.emg_lv01_text01);
                            Log.d("MSG",msg);
                            //DataReadActivity.heatScan_text01.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text01);
                            break;
                        }
                        case 2:{
                            IsShowNotification = true;
                            String msg = mContext.getResources().getString(R.string.emg_lv01_text02);
                            Log.d("MSG",msg);
                            //DataReadActivity.heatScan_text01.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text01);
                            break;
                        }
                        case 3:{

                            IsShowNotification = true;
                            String msg = mContext.getResources().getString(R.string.emg_lv01_text03);
                            Log.d("MSG",msg);
                            //DataReadActivity.heatScan_text01.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text01);
                            break;
                        }
                    }

                    switch (level02){
                        case 0:{
                            IsShowNotification = true;
                            IsEmergencyMessageSend = false;
                            String msg = mContext.getResources().getString(R.string.emg_lv02_text00);
                            //DataReadActivity.heatScan_text02.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text02);
                            break;
                        }
                        case 1:{

                            IsShowNotification = true;
                            IsEmergencyMessageSend = false;
                            String msg = mContext.getResources().getString(R.string.emg_lv02_text01);
                            //DataReadActivity.heatScan_text02.setText(msg);
                            Log.d("MSG",msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text02);
                            break;
                        }
                        case 2:{

                            IsShowNotification = true;
                            IsEmergencyMessageSend = false;
                            String msg = mContext.getResources().getString(R.string.emg_lv02_text02);

                            Log.d("MSG",msg);
                            //DataReadActivity.heatScan_text02.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text02);
                            break;
                        }
                        case 3:{
                            IsShowNotification = true;
                            String msg = mContext.getResources().getString(R.string.emg_lv02_text03);
                            Log.d("MSG",msg);
                            //DataReadActivity.heatScan_text02.setText(msg);
                            SetTextView(msg,-1,DataReadActivity.heatScan_text02);
                            // TODO: 2017-09-18 긴급 상황 문자 전송 여기서!

                            if(!IsEmergencyMessageSend && gps_manager.myLocation != null){
                                SendEmergencyMessage();
                                IsEmergencyMessageSend = true;
                            }
                            break;
                        }
                    }

                    //Notification(Test)
                    if(level01 > 0 && level01 < 4 && level02 > 0 && level02 < 4 && IsShowNotification){
                        ShowNotification(
                                Noti_Messages01.get(level01),
                                Noti_Messages02.get(level02),
                                (level01 * 10 + level02)
                        );
                        IsShowNotification = false;
                    }
                }
                if (MODE_SWITCH
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    Log.d(TAG, "Read MODE : " + val);

                }
                if (BleUuid.DISTANCE.equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    // val -> distance
                    if(val > 0){
                        characteristic.setValue(new byte[] { (byte) 0x00 });
                        mConnGatt.writeCharacteristic(characteristic);
                        MainService.distance = val;
                        Log.d(TAG,"누적 이동거리 [DISTANCE] : " + MainService.distance+ " (M)");
                    }else{
                        Log.d(TAG,"[DISTANCE NOT CHANGED] / CUR : " + MainService.distance + " (M)");
                    }

                    if(DataReadActivity.IsActivityRun) {
                        SensorAsyncTask task0 = new SensorAsyncTask( MainService.distance , DataReadActivity.distance, "DISTANCE");
                        task0.execute();
                    }
                }

                if (BleUuid.SENSOR_DATA_CHAR_ARRAY.equalsIgnoreCase(characteristic.getUuid().toString())) { // Testing

                    ArrayList<Integer> datas = readCharacteristicDataArray(characteristic, 4);
                    /*Log.d(TAG,
                            "\nSensor data[0] = " + datas.get(0) +
                            "\nSensor data[1] = " + datas.get(1) +
                            "\nSensor data[2] = " + datas.get(2) +
                            "\nSensor data[3] = " + datas.get(3)
                    );*/
                    MainService.temperature = datas.get(0);
                    MainService.body_heat = datas.get(1);
                    MainService.heart_rate = datas.get(2);
                    MainService.humidity = datas.get(3);
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
                        Thread.sleep(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                // Test
                if (LIMIT_DISTANCE
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    Log.d(TAG, "LIMIT_DISTANCE VALUE : " + val);
                }
                if (LIMIT_HEART_RATE
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    Log.d(TAG, "LIMIT_HEART_RATE VALUE : " + val);
                }
                if (LIMIT_HUMIDITY
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    int val = readIntCharacteristicData(characteristic);
                    Log.d(TAG, "LIMIT_HUMIDITY VALUE : " + val);
                }



            } else {
                Log.d(TAG, "Read Failed!");
            }
        }
        int readIntCharacteristicData(BluetoothGattCharacteristic characteristic){
            int val= -1;
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                //.d(TAG, "Sensor format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                //.d(TAG, "Sensor format UINT8.");
            }
            val = characteristic.getIntValue(format,0);
            return val;
        }
        ArrayList<Integer> readCharacteristicDataArray(BluetoothGattCharacteristic characteristic, int array_len) {
            ArrayList<Integer> datas = new ArrayList<>();
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                //Log.d(TAG, "Sensor format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                //Log.d(TAG, "Sensor format UINT8.");
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
    private void SetTextView(String msg,int val,TextView textView){
        SensorAsyncTask task = new SensorAsyncTask(val,textView,msg);
        task.execute();
    }
    public class SensorAsyncTask extends AsyncTask<Integer, Integer, Integer>{
        TextView sensor;
        int value;
        String TAG;

        public SensorAsyncTask(int val, TextView s0, String _TAG){
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
        protected void onProgressUpdate(Integer... params){

        }

        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
            if(TAG.equals("DISTANCE")){
                sensor.setText("지금까지 누적 이동거리 : " + (float)(value * 0.8) + "(M)");
            }else{
                if(value == -1)sensor.setText(TAG);
                else sensor.setText(TAG + value);
            }

        }
    }
    private void setSwitchAsyncTask(boolean b,Switch s){
        SwitchAsyncTask task = new SwitchAsyncTask(b,s);
        task.execute();
    }
    private class SwitchAsyncTask extends AsyncTask<Integer, Integer, Integer>{
        Switch aSwitch;
        boolean value;
        public SwitchAsyncTask(boolean val, Switch s0){
            aSwitch = s0;
            value = val;
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
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
            aSwitch.setChecked(value);
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
        SensorReadThread thread = new SensorReadThread(MY_SERVICE,BleUuid.SENSOR_DATA_CHAR_ARRAY);
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

                try {
                    // Read Sensor data
                    Thread.sleep(400);
                    if (!BLEReadData((service_uuid), (sensor_uuid))) {
                        Log.d(TAG, "Error Occurred! Reinitializing start !..");
                        if (BleUtil.selectedDevice != null) {
                            BLEInit();
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Read Distance data
                    Thread.sleep(400);
                    //Log.d(TAG,"[DISTANCE] start!");
                    BLEReadData(service_uuid,DISTANCE);

                    // Read Mode data
                    Thread.sleep(400);
                    BLEReadData(service_uuid,MODE_SWITCH);

                    // Read Emergency Data
                    Thread.sleep(400);
                    BLEReadData(service_uuid,EMERGENCY);

                    // Read LIMIT DATA
                    Thread.sleep(400);
                    BLEReadData(service_uuid,LIMIT_DISTANCE);
                    Thread.sleep(400);
                    BLEReadData(service_uuid,LIMIT_HEART_RATE);
                    Thread.sleep(400);
                    BLEReadData(service_uuid,LIMIT_HUMIDITY);

                    // Charting
                    if(IsActivityRun){
                        sensorChart.AddEntry(
                                MainService.temperature,
                                MainService.body_heat,
                                MainService.heart_rate,
                                MainService.humidity,
                                (float)(MainService.distance * 0.8)
                        );
                    }

                    if(IsDebug){
                        sensorChart_debug.AddEntry(
                                MainService.temperature,
                                MainService.body_heat,
                                MainService.heart_rate,
                                MainService.humidity,
                                MainService.distance
                        );
                    }

                    // Checking the alarm limit!
                    CheckLimitValues();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void CheckLimitValues(){
        if(DistanceValue != 0 && DistanceValue < MainService.distance){
            ShowNotification("이동거리 알람",DistanceValue + "(M)가 초과 되었습니다.",1);
            DistanceValue = 0;
            if(DataReadActivity.alarm_distance != null)setSwitchAsyncTask(false,alarm_distance);
        }

        if(HumidityValue != 0 && HumidityValue < MainService.humidity){
            ShowNotification("습도 알람",HumidityValue + "(%)가 초과 되었습니다.",2);
            HumidityValue = 0;
            if(DataReadActivity.alarm_humidity != null)setSwitchAsyncTask(false,alarm_humidity);
        }

        if(TemperatureValue != 0 && TemperatureValue < MainService.temperature){
            ShowNotification("외부 온도 알람",TemperatureValue + "(℃)가 초과 되었습니다",3);
            TemperatureValue = 0;
            if(DataReadActivity.alarm_temperature != null)setSwitchAsyncTask(false,alarm_temperature);
        }

        if(BodyHeatValue != 0 && BodyHeatValue < MainService.body_heat){
            ShowNotification("체온 알림",BodyHeatValue + "(℃)가 초과 되었습니다",4);
            BodyHeatValue = 0 ;
            if(DataReadActivity.alarm_body_heat != null)setSwitchAsyncTask(false,alarm_body_heat);
        }

    }


    private boolean BLEWriteDebuggingSensorData(){
        if(mConnGatt == null){
            Log.d(TAG, "BLEWriteData Failed");
            return false;
        }
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(MY_SERVICE));
        //Log.d(TAG, "BLEWriteData Start !");
        if (disService == null) {
            Log.d(TAG, "-> Dis service not found!");
            IsBLEInit = false;
            return false;
        }
        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(SENSOR_DATA_CHAR_ARRAY));
        if (characteristic == null){
            Log.d(TAG, "-> Charateristic not found!");
            return false;
        }
        characteristic.setValue(new byte[] { (byte)MainService.temperature, (byte)MainService.body_heat, (byte)MainService.heart_rate, (byte)MainService.humidity });
        if (mConnGatt.writeCharacteristic(characteristic)) {

        }
        Log.d(TAG, "-> BLEWriteDebuggingSensorData() --> Write Success!");
        return true;
    }
    private boolean BLEWriteData(String sensor_uuid,byte value){
        if(mConnGatt == null){
            Log.d(TAG, "BLEWriteData Failed");
            return false;
        }
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(MY_SERVICE));
        if (disService == null){
            Log.d(TAG, "-> Dis service not found!");
            IsBLEInit = false;
            return false;
        }
        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(sensor_uuid));
        if (characteristic == null){
            Log.d(TAG, "-> Charateristic not found!");
            return false;
        }
        characteristic.setValue(new byte[] { value });
        if (mConnGatt.writeCharacteristic(characteristic)){

        }
        return true;
    }


    private boolean BLEWriteData(byte value){
        if(mConnGatt == null){
            Log.d(TAG, "BLEWriteData Failed");
            return false;
        }
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(MY_SERVICE));
        //Log.d(TAG, "BLEWriteData Start !");
        if (disService == null){
            Log.d(TAG, "-> Dis service not found!");
            IsBLEInit = false;
            return false;
        }
        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(MODE_SWITCH));
        if (characteristic == null){
            Log.d(TAG, "-> Charateristic not found!");
            return false;
        }
        characteristic.setValue(new byte[] { (byte) value });
        if (mConnGatt.writeCharacteristic(characteristic)){

        }
        return true;
    }

    private boolean BLEReadData(String service_uuid, String sensor_uuid){
        if(mConnGatt == null){
            SensorReadThreadFlag = false;
            return false;
        }
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(service_uuid));

        if (disService == null){
            Log.d(TAG, "Dis service not found!");
            IsBLEInit = false;
            return false;
        }


        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(sensor_uuid));
        if (characteristic == null){
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

    public void printToast(String msg){
        ToastAsyncTask task = new ToastAsyncTask(msg);
        task.execute();
    }

    private void ShowNotification(String title,String msg,int ID){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_menu_gallery);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_menu_gallery)
                .setContentTitle(title)
                .setContentText(msg).setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager manager =
                (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(ID,builder.build());
        Log.d(TAG,"ShowNotification finish");
    }
}
