package com.example.leekwangho.escapesunapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.BLEMenu.ScanActivity;

public class LogoActivity extends AppCompatActivity {
    private Button goNextBTN;
    private TextView logoMain,logoSub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_logo);
        logoMain = (TextView)findViewById(R.id.LogoText);
        logoSub = (TextView)findViewById(R.id.LogoText2);
        goNextBTN = (Button)findViewById(R.id.LogoNextBTN);
        goNextBTN.setOnClickListener(getNextListener());
        Animation alpha20 = AnimationUtils.loadAnimation(this,R.anim.alpha_2_0);
        Animation alpha10 = AnimationUtils.loadAnimation(this,R.anim.alpha_1_0);
        Animation alpha05 = AnimationUtils.loadAnimation(this,R.anim.alpha_0_5);
        logoMain.startAnimation(alpha05);
        logoSub.startAnimation(alpha10);
        goNextBTN.startAnimation(alpha20);
    }

    private Button.OnClickListener getNextListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check Permission
                if (!hasPermissions(PERMISSIONS)) {
                    requestNecessaryPermissions(PERMISSIONS);

                } else {
                    // Already Granted
                    Intent intent = new Intent(getApplicationContext(),LocationSettingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_right_in,R.anim.translate_left_out);
                }

            }
        };
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
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.SEND_SMS"
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }
}
