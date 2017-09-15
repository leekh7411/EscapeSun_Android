package com.example.leekwangho.escapesunapp.CallList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.leekwangho.escapesunapp.Database.MainDBHelper;
import com.example.leekwangho.escapesunapp.R;

public class AddCallListActivity extends AppCompatActivity {
    private TextView addCallText;
    private Button addBTN;
    private EditText name,phone_num;
    private MainDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_call_list);
        Animation alpha05inf = AnimationUtils.loadAnimation(this,R.anim.alpha_0_5_infinite);
        addCallText = (TextView)findViewById(R.id.add_callList_text);
        addCallText.setAnimation(alpha05inf);
        addBTN = (Button)findViewById(R.id.addCallListBTN);
        name = (EditText)findViewById(R.id.edit_text_name);
        phone_num = (EditText)findViewById(R.id.edit_text_phone);

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCallList();
            }
        });

        dbHelper = new MainDBHelper(getApplicationContext());
    }
    private void addCallList(){
        String _name = name.getText().toString();
        String _phone = phone_num.getText().toString();
        dbHelper.__callList__insert_new_list(_name,_phone);
        Intent intent = new Intent(getApplicationContext(),CallListActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }
}
