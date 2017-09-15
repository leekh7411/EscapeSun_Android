package com.example.leekwangho.escapesunapp.BLEMenu;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.BLEMenu.Utils.ScannedDevice;
import com.example.leekwangho.escapesunapp.R;
import com.example.leekwangho.escapesunapp.Service.MainService;


import java.util.List;

/**
 * Created by root on 17. 7. 13.
 */

public class DeviceAdapter extends ArrayAdapter<ScannedDevice> {
    private static final String PREFIX_RSSI = "RSSI:";
    private List<ScannedDevice> mList;
    private LayoutInflater mInflater;
    private int mResId;
    private final String TAG = "DeviceAdapter";
    public DeviceAdapter(Context context, int resId, List<ScannedDevice> objects) {
        super(context, resId, objects);
        mResId = resId;
        mList = objects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScannedDevice item = (ScannedDevice) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(mResId, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.device_name);
        name.setText(item.getDisplayName());
        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        address.setText(item.getDevice().getAddress());
        TextView rssi = (TextView) convertView.findViewById(R.id.device_address);
        rssi.setText(PREFIX_RSSI + Integer.toString(item.getRssi()));

        return convertView;
    }

    /** add or update BluetoothDevice */
    public void update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            return;
        }

        boolean contains = false;
        for (ScannedDevice device : mList) {
            if(device.getDisplayName().equals("SUN")){
               Log.d(TAG,"A) SUN device detected : " + device.getDisplayName() + " / " + device.getDevice().getAddress());

            }else{
                Log.d(TAG,"A) other device detected : " + device.getDisplayName());
            }

            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                contains = true;
                device.setRssi(rssi); // update
                break;
            }
        }
        if (!contains) {
            // add new BluetoothDevice
            ScannedDevice device = new ScannedDevice(newDevice, rssi);
            if(device.getDisplayName().equals("SUN")){
                mList.add(device);
                Log.d(TAG,"B) SUN device detected : " + device.getDisplayName()+ " / " + device.getDevice().getAddress());

            }else{
                mList.add(device);
                Log.d(TAG,"B) other device detected : " + device.getDisplayName());
            }
        }
        notifyDataSetChanged();
    }
}
