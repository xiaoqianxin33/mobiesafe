package com.xiao.mobiesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xiao.telephony.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseContactCallSmsActivity extends AppCompatActivity {

    @Bind(R.id.lv_contact)
    ListView lvContact;
    private ArrayList<HashMap<String, String>> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        list2 = readContact();
        lvContact.setAdapter(new SimpleAdapter(this, list2, R.layout.contact_listview_item,
                new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = list2.get(position).get("phone");
                Intent intent = new Intent();
                intent.putExtra("phone", phone);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    public abstract ArrayList<HashMap<String, String>> readContact();
}
