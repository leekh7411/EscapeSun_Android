package com.example.leekwangho.escapesunapp.CallList;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.leekwangho.escapesunapp.Database.MainDBHelper;
import com.example.leekwangho.escapesunapp.Dialog.DeleteCallListDialog;
import com.example.leekwangho.escapesunapp.R;
import java.util.ArrayList;

public class CallListActivity extends AppCompatActivity {
    MainDBHelper mainDBHelper;
    private Button addListBTN;
    private Button exitBTN;
    private ListView callList;
    private CallListAdapter adapter = null;
    private final String TAG = "CallListActivity";
    private final int ADD_CALL_LIST = 100;
    private DeleteCallListDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);
        mainDBHelper = new MainDBHelper(getApplicationContext());
        callList = (ListView)findViewById(R.id.callList);
        adapter = new CallListAdapter();
        adapter.callListItems = mainDBHelper.__callList_get_all_data_ArrayList();
        callList.setAdapter(adapter);
        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CallListItem item = adapter.getItem(i);
                if (item != null) {
                    Log.d(TAG,"Click -> Name: " + item.getName() + " , Phone : " + item.getPhone_number());
                    deleteDialog(item.getName(),item.getPhone_number(),i);
                }
            }
        });

        addListBTN = (Button)findViewById(R.id.call_list_add);
        addListBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListItem();
            }
        });
        exitBTN = (Button)findViewById(R.id.call_list_exit);
        exitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
            }
        });
    }
    private void deleteDialog(String name, String num, final int idx){
        dialog = new DeleteCallListDialog(
                CallListActivity.this,
                "해당 연락처를 삭제 하시겠습니까?", name + "\n" + num,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.callListItems.remove(idx);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                },
                cancelBTNListener
        );
        dialog.show();
    }
    private View.OnClickListener okBTNListener = new View.OnClickListener() {
        public void onClick(View v) {

            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelBTNListener = new View.OnClickListener() {
        public void onClick(View v) {

            dialog.dismiss();
        }
    };




    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(adapter!=null)adapter.notifyDataSetChanged();
    }

    private void addListItem(){
        Intent intent = new Intent(getApplicationContext(),AddCallListActivity.class);
        startActivityForResult(intent,ADD_CALL_LIST);
        overridePendingTransition(R.anim.translate_right_in,R.anim.translate_left_out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_CALL_LIST){
            Log.d(TAG,"Activity Result Code : " + ADD_CALL_LIST);
            if(adapter!=null)adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left_in,R.anim.translate_right_out);
    }

    private class CallListAdapter extends BaseAdapter{
        public ArrayList<CallListItem> callListItems = new ArrayList<>();
        public void addItem(CallListItem item){
            callListItems.add(item);
        }
        public void addItem(String name,String phone){
            CallListItem item = new CallListItem(name,phone);
            adapter.addItem(item);
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                final Context context = viewGroup.getContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.call_listitem_device, viewGroup, false);
                TextView name = (TextView)view.findViewById(R.id.callList_name);
                TextView phone_number = (TextView)view.findViewById(R.id.callList_phone_number);
                name.setText(callListItems.get(i).getName());
                phone_number.setText(callListItems.get(i).getPhone_number());
            }else{
                TextView name = (TextView)view.findViewById(R.id.callList_name);
                TextView phone_number = (TextView)view.findViewById(R.id.callList_phone_number);
                name.setText(callListItems.get(i).getName());
                phone_number.setText(callListItems.get(i).getPhone_number());
            }
            return view;
        }
        @Override
        public CallListItem getItem(int i) {
            return callListItems.get(i);
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public int getCount() {
            return callListItems.size();
        }
    }
}
