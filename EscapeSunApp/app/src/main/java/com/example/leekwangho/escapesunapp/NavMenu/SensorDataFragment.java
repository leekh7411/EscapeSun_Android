package com.example.leekwangho.escapesunapp.NavMenu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUtil;
import com.example.leekwangho.escapesunapp.BLEMenu.Utils.BleUuid;
import com.example.leekwangho.escapesunapp.MainActivity;
import com.example.leekwangho.escapesunapp.R;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by 이광호 on 2017-07-03.
 */

public class SensorDataFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "SensorDataFragment";
    public View view;
    private Context mContext;
    private Activity mActivity;
    TextView mainText, tempText, bodyTempText, gyroText, heartRateText, humidityText;
    ImageButton refreshButton;
    Button onBTN;
    Button offBTN;
    Button readBTN;
    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mDevice = null;
    private BluetoothGatt mConnGatt;
    private int mStatus;
    private boolean IsBLEInit = false;
    private boolean SensorReadThreadFlag = false;
    public SensorDataFragment() {
        mActivity = MainActivity.mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = inflater.getContext();
        final View innerview = inflater.inflate(R.layout.activity_fragment_main, container, false);
        mainText = (TextView) innerview.findViewById(R.id.mainText);
        tempText = (TextView) innerview.findViewById(R.id.temperature_data);
        bodyTempText = (TextView) innerview.findViewById(R.id.bodyheat_data);
        gyroText = (TextView) innerview.findViewById(R.id.gyroscope_data);
        heartRateText = (TextView) innerview.findViewById(R.id.heart_rate_data);
        humidityText = (TextView) innerview.findViewById(R.id.humidity_data);
        refreshButton = (ImageButton) innerview.findViewById(R.id.mainRefresh);
        refreshButton.setOnClickListener(this);
        onBTN = (Button) innerview.findViewById(R.id.mainOn);
        onBTN.setOnClickListener(this);
        offBTN = (Button) innerview.findViewById(R.id.mainOff);
        offBTN.setOnClickListener(this);
        readBTN = (Button) innerview.findViewById(R.id.mainReadBTN);
        readBTN.setOnClickListener(this);
        init();
        return innerview;
    }

    private void init() {
        if (BleUtil.selectedDevice != null) {
            mainText.setText(
                    "Address  : " + BleUtil.selectedDevice.getAddress() + "\n" +
                            "Name     : " + BleUtil.selectedDevice.getName() + "\n"
            );
            BLEInit();

        } else {
            mainText.setText("None connected device\n");
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

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        /*mReadManufacturerNameButton.setEnabled(false);
                        mReadSerialNumberButton.setEnabled(false);
                        mWriteAlertLevelButton.setEnabled(false);*/
                    }

                });
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
                    /*mReadManufacturerNameButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING)));
                    mReadSerialNumberButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING)));*/
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            //mReadManufacturerNameButton.setEnabled(true);
                            //mReadSerialNumberButton.setEnabled(true);
                        }

                        ;
                    });
                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            //mWriteAlertLevelButton.setEnabled(true);
                        }

                        ;
                    });
                    // mWriteAlertLevelButton.setTag(service
                    //         .getCharacteristic(UUID
                    //                .fromString(BleUuid.CHAR_ALERT_LEVEL)));
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
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mainText.setText(name);
                        }
                    });
                } else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData : " + name);
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mainText.setText(name);
                        }
                    });
                }

                if (BleUuid.MODE_SWITCH
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    Log.d(TAG, "ReadData in LED : " + characteristic.getStringValue(0));
                }

                if (BleUuid.SENSOR_DATA_CHAR_ARRAY.equalsIgnoreCase(characteristic.getUuid().toString())) { // Testing

                    ArrayList<Integer> datas = readCharacteristicDataArray(characteristic, 4);
                    SensorAsyncTask task0 = new SensorAsyncTask(datas.get(0), tempText, "Temperature : ");
                    task0.execute();
                    SensorAsyncTask task1 = new SensorAsyncTask(datas.get(1), bodyTempText, "Body Heat : ");
                    task1.execute();
                    SensorAsyncTask task2 = new SensorAsyncTask(datas.get(2), heartRateText, "Heart Rate : ");
                    task2.execute();
                    SensorAsyncTask task3 = new SensorAsyncTask(datas.get(3), humidityText, "Humidity : ");
                    task3.execute();
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            } else {
                Log.d(TAG, "Read Failed!");
            }
        }
    };

    int readCharacteristicData(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Sensor format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Sensor format UINT8.");
        }
        final int val = characteristic.getIntValue(format, 0);
        Log.d(TAG, String.format("Received Sensor: %d", val));
        return val;
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mainText) {
        } else if (v.getId() == R.id.mainRefresh) {
            doRefresh();
        } else if (v.getId() == R.id.mainOn) {
            doLEDon();
        } else if (v.getId() == R.id.mainOff) {
            doLEDoff();
        } else if (v.getId() == R.id.mainReadBTN) {
            StartSensorReadThread();

        } else {
            Log.d(TAG, "Else Case");
        }
    }

    private void doLEDon() {
        if (BleUtil.selectedDevice == null || mConnGatt == null) return;
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(BleUuid.MY_SERVICE));
        if (disService == null) {
            Log.d(TAG, "Dis service not found!");
            printToast("Dis service not found!");
            IsBLEInit = false;
            return;
        }


        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(BleUuid.MODE_SWITCH));

        if (characteristic == null) {
            Log.d(TAG, "charateristic not found!");
            printToast("charateristic not found!");
            return;
        }


        characteristic.setValue(new byte[]{(byte) 0x01});
        if (mConnGatt.writeCharacteristic(characteristic)) {
            printToast("LED ON write success!");
            Log.d(TAG, "LED ON write success!");
        } else {
            printToast("LED ON write fail!");
            Log.d(TAG, "LED ON write fail!");
        }
    }

    private void doLEDoff() {
        if (BleUtil.selectedDevice == null || mConnGatt == null) return;
        BluetoothGattService disService = mConnGatt.getService(UUID.fromString(BleUuid.MY_SERVICE));
        if (disService == null) {
            Log.d(TAG, "Dis service not found!");
            printToast("Dis service not found!");
            IsBLEInit = false;
            return;
        }


        BluetoothGattCharacteristic characteristic = disService.getCharacteristic(UUID.fromString(BleUuid.MODE_SWITCH));

        if (characteristic == null) {
            Log.d(TAG, "firmware revison charateristic not found!");
            printToast("firmware revison charateristic not found!");
            return;
        }


        characteristic.setValue(new byte[]{(byte) 0x00});
        if (mConnGatt.writeCharacteristic(characteristic)) {
            Log.d(TAG, "OFF write success!");
            printToast("LED off write Success!");
        } else {
            Log.d(TAG, "OFF write fail!");
            printToast("LED off write Failed!");
        }

    }

    private void doRefresh() {
        if (BleUtil.selectedDevice != null) {
            init();
            printToast("Refresh finished!");
        } else {
            Toast.makeText(mContext, "Not connected", Toast.LENGTH_SHORT).show();
            mainText.setText("Not connected device\n");
            printToast("Not connected device");
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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorReadThreadFlag = false;
        if (mConnGatt != null) {
            if ((mStatus != BluetoothProfile.STATE_DISCONNECTING)
                    && (mStatus != BluetoothProfile.STATE_DISCONNECTED)) {
                mConnGatt.disconnect();
            }
            mConnGatt.close();
            mConnGatt = null;
        }
    }

    public void printToast(String msg) {
       ToastAsyncTask task = new ToastAsyncTask(msg);
        task.execute();
    }

    /**
     * Created by leekwangho on 17. 2. 24.
     */
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
                Log.d(TAG,"You can excute only one thread");
                printToast("You can excute only one thread");
                return;
            }
            else SensorReadThreadFlag = true;
            printToast("Sensor data reading thread is started!");
            while(SensorReadThreadFlag){
                if (!BLEReadData((service_uuid), (sensor_uuid))) {
                    Log.d(TAG, "Error Occured! Reinitializing start !..");
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
}