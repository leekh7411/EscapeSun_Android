package com.example.leekwangho.escapesunapp.Dialog;

/**
 * Created by LeeKwangho on 2017-09-15.
 */
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.R;
import com.example.leekwangho.escapesunapp.Service.MainService;
import com.example.leekwangho.escapesunapp.Service.MainServiceThread;

public class AlarmSettingDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button mOkBTN;
    private Button mCancelBTN;
    private Button plusBTN,minusBTN;
    private String mTitle;
    private String mContent;
    public static final int ALARM_DISTANCE = 100;
    public static final int ALARM_HEART_RATE = 200;
    public static final int ALARM_HEAT = 300;
    public static final int ALARM_HUMIDITY = 400;
    public static final int ALARM_TEMPERATURE = 500;
    public static final int ALARM_BODY_HEAT = 600;
    private int MY_ALARM;
    private int MY_VALUE = 0;
    private View.OnClickListener mOkBTNListener;
    private View.OnClickListener mCancelBTNListener;
    private Context mContext;
    public AlarmSettingDialog(Context context, String title,int ALARM_INFO) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.mTitle = title;
        this.MY_ALARM = ALARM_INFO;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.alarm_setting_dialog);

        mTitleView = (TextView) findViewById(R.id.alarm_setting_title);
        mContentView = (TextView) findViewById(R.id.alarm_setting_value);
        mOkBTN = (Button) findViewById(R.id.alarm_setting_yes);
        mCancelBTN = (Button) findViewById(R.id.alarm_setting_no);
        plusBTN = (Button)findViewById(R.id.alarm_setting_plus);
        minusBTN = (Button)findViewById(R.id.alarm_setting_minus);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mContentView.setText(mContent);

        // Plus 클릭 이벤트 셋팅
        plusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MY_ALARM){
                    case ALARM_DISTANCE:{
                        MY_VALUE+=10;
                        if(MY_VALUE > 1000000)MY_VALUE = 1000000;
                        mContentView.setText(MY_VALUE+"(M)");
                        break;
                    }
                    case ALARM_HEART_RATE:{
                        MY_VALUE++;
                        if(MY_VALUE > 200)MY_VALUE = 200;
                        mContentView.setText(MY_VALUE+"(bpm)");
                        break;
                    }
                    case ALARM_HEAT:{
                        // There's not settings
                        break;
                    }
                    case ALARM_HUMIDITY:{
                        MY_VALUE++;
                        if(MY_VALUE > 100)MY_VALUE = 100;
                        mContentView.setText(MY_VALUE+"(%)");
                        break;
                    }
                    case ALARM_TEMPERATURE:{
                        MY_VALUE++;
                        if(MY_VALUE > 100)MY_VALUE = 100;
                        mContentView.setText(MY_VALUE+"(℃)");
                        break;
                    }
                    case ALARM_BODY_HEAT:{
                        MY_VALUE++;
                        if(MY_VALUE > 100)MY_VALUE = 100;
                        mContentView.setText(MY_VALUE+"(℃)");
                        break;
                    }
                }
            }
        });

        // Minus Button
        minusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MY_ALARM){
                    case ALARM_DISTANCE:{
                        MY_VALUE-=10;
                        if(MY_VALUE < 1 )MY_VALUE = 1;
                        mContentView.setText(MY_VALUE+"(M)");
                        break;
                    }
                    case ALARM_HEART_RATE:{
                        MY_VALUE--;
                        if(MY_VALUE < 1)MY_VALUE = 1;
                        mContentView.setText(MY_VALUE+"(bpm)");
                        break;
                    }
                    case ALARM_HEAT:{
                        // there's no setting
                        break;
                    }
                    case ALARM_HUMIDITY:{
                        MY_VALUE--;
                        if(MY_VALUE < 1)MY_VALUE = 1;
                        mContentView.setText(MY_VALUE+"(%)");
                        break;
                    }
                    case ALARM_TEMPERATURE:{
                        MY_VALUE--;
                        if(MY_VALUE < 1)MY_VALUE = 1;
                        mContentView.setText(MY_VALUE+"(℃)");
                        break;
                    }
                    case ALARM_BODY_HEAT:{
                        MY_VALUE--;
                        if(MY_VALUE < 1)MY_VALUE = 1;
                        mContentView.setText(MY_VALUE+"(℃)");
                        break;
                    }
                }

            }
        });

        // Ok Button
        mOkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MY_ALARM){
                    case ALARM_DISTANCE:{
                        MainServiceThread.IsDistanceOn = true;
                        MainServiceThread.DistanceValue = MY_VALUE;
                        break;
                    }
                    case ALARM_HEART_RATE:{
                        MainServiceThread.IsHeartRateOn = true;
                        MainServiceThread.HeartRateValue = MY_VALUE;
                        break;
                    }
                    case ALARM_HEAT:{

                        break;
                    }
                    case ALARM_HUMIDITY:{
                        MainServiceThread.IsHumidityOn = true;
                        MainServiceThread.HumidityValue = MY_VALUE;
                        break;
                    }
                    case ALARM_TEMPERATURE:{
                        MainServiceThread.IsTemperatureOn = true;
                        MainServiceThread.TemperatureValue = MY_VALUE;
                        break;
                    }
                    case ALARM_BODY_HEAT:{
                        MainServiceThread.IsBodyHeatOn = true;
                        MainServiceThread.BodyHeatValue = MY_VALUE;
                        break;
                    }

                }
                dismiss();
            }
        });

        // Nope Button
        mCancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MY_ALARM){
                    case ALARM_DISTANCE:{
                        break;
                    }
                    case ALARM_HEART_RATE:{
                        break;
                    }
                    case ALARM_HEAT:{
                        break;
                    }
                    case ALARM_HUMIDITY:{
                        break;
                    }

                }
                dismiss();
            }
        });

        // Default value settings
        switch (MY_ALARM){
            case ALARM_DISTANCE:{
                mContentView.setText(MY_VALUE+"(M)");
                break;
            }
            case ALARM_HEART_RATE:{
                mContentView.setText(MY_VALUE+"(bpm)");
                break;
            }
            case ALARM_HEAT:{
                // there's no setting
                break;
            }
            case ALARM_HUMIDITY:{
                mContentView.setText(MY_VALUE+"(%)");
                break;
            }
            case ALARM_TEMPERATURE:{
                mContentView.setText(MY_VALUE+"(℃)");
                break;
            }
            case ALARM_BODY_HEAT:{
                mContentView.setText(MY_VALUE+"(℃)");
                break;
            }
        }
    }



}

