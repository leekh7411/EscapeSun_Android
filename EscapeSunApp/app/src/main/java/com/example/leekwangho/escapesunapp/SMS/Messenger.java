package com.example.leekwangho.escapesunapp.SMS;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by LeeKwangho on 2017-09-15.
 */
public class Messenger {

    private Context mContext;

    public Messenger(Context mContext) {
        this.mContext = mContext;
    }

    public void sendMessageTo(String phoneNum, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);

    }

    public void Send_MMS_To(String phonenumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        String sendTo = phonenumber;
        ArrayList<String> partMessage = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(sendTo, null, partMessage, null, null);

    }

}
