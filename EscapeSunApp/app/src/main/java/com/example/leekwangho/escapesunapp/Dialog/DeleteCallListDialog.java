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

public class DeleteCallListDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button mOkBTN;
    private Button mCancelBTN;
    private String mTitle;
    private String mContent;

    private View.OnClickListener mOkBTNListener;
    private View.OnClickListener mCancelBTNListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_delete_dialog);

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mContentView = (TextView) findViewById(R.id.txt_content);
        mOkBTN = (Button) findViewById(R.id.btn_left);
        mCancelBTN = (Button) findViewById(R.id.btn_right);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mContentView.setText(mContent);

        // 클릭 이벤트 셋팅
        if (mOkBTNListener != null && mCancelBTNListener != null) {
            mOkBTN.setOnClickListener(mOkBTNListener);
            mCancelBTN.setOnClickListener(mCancelBTNListener);
        } else if (mOkBTNListener != null
                && mCancelBTNListener == null) {
            mOkBTN.setOnClickListener(mOkBTNListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public DeleteCallListDialog(Context context, String title,
                                View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mOkBTNListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public DeleteCallListDialog(Context context, String title,
                                String content, View.OnClickListener leftListener,
                                View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.mOkBTNListener = leftListener;
        this.mCancelBTNListener = rightListener;
    }
}

