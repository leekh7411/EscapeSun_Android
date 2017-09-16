package com.example.leekwangho.escapesunapp.BLEMenu.Utils;

/**
 * Created by root on 17. 7. 20.
 */

/** BLE UUID Strings */
public class BleUuid {
    // 180A Device Information
    public static final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_SERIAL_NUMBEAR_STRING = "00002a25-0000-1000-8000-00805f9b34fb";

    // 1802 Immediate Alert
    public static final String SERVICE_IMMEDIATE_ALERT = "00001802-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";

    // 180F Battery Service
    public static final String SERVICE_BATTERY_SERVICE = "0000180F-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    // 19B10001-E8F2-537E-4F6C-D104768A1214 - LED
    public static final String MY_SERVICE = "19B10000-E8F2-537E-4F6C-D104768A1214";
    public static final String MODE_SWITCH = "19B10001-E8F2-537E-4F6C-D104768A1214";
    public static final String SENSOR_DATA_CHAR_ARRAY = "19B10006-E8F2-537E-4F6C-D104768A1214";
}