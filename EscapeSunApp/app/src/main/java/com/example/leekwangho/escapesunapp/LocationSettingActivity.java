package com.example.leekwangho.escapesunapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.BLEMenu.ScanActivity;
import com.example.leekwangho.escapesunapp.GPS.GPSmanager;

public class LocationSettingActivity extends AppCompatActivity {
    private Button setLocationBTN;
    private Button nextBTN;
    private GPSmanager gps_manager;
    private ImageView locationIMG;
    private TextView locaText,locaText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);
        locaText = (TextView)findViewById(R.id.LocationText);
        locaText2 = (TextView)findViewById(R.id.LocationText2);
        setLocationBTN = (Button)findViewById(R.id.SetLocationBTN);
        setLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocation();
            }
        });
        nextBTN = (Button)findViewById(R.id.LocationNextBTN);
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
        gps_manager = new GPSmanager(getApplicationContext(),(LocationManager)getSystemService(Context.LOCATION_SERVICE));
        locationIMG = (ImageView)findViewById(R.id.location_image);
        Animation alpha20 = AnimationUtils.loadAnimation(this,R.anim.alpha_2_0);
        Animation alpha10 = AnimationUtils.loadAnimation(this,R.anim.alpha_1_0);
        Animation alpha05 = AnimationUtils.loadAnimation(this,R.anim.alpha_0_5);
        locaText.startAnimation(alpha05);
        locaText2.startAnimation(alpha10);
        locationIMG.startAnimation(alpha20);
        setLocationBTN.startAnimation(alpha20);
        nextBTN.startAnimation(alpha20);
    }

    private void setLocation(){
        chkGpsService();
    }

    private void goNext(){
        Intent intent = new Intent(getApplicationContext(),ScanActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_right_in,R.anim.translate_left_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }


    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }
}
