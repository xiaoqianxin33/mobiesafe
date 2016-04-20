package com.xiao.mobiesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.xiao.mobiesafe.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    @Bind(R.id.lv_contact)
    ListView lvContact;
    private ArrayList<HashMap<String, String>> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        list2 = readContact();
        lvContact.setAdapter(new SimpleAdapter(this,list2,R.layout.contact_listview_item,
                new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = list2.get(position).get("phone");
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

    }

    public ArrayList<HashMap<String, String>> readContact() {
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        Cursor rawCursor = getContentResolver().
                query(rawContactsUri, new String[]{"contact_id"}, null, null, null);

        if (rawCursor != null) {
            while (rawCursor.moveToNext()) {
                String contact_id = rawCursor.getString(0);
                Cursor dataCursor = getContentResolver().query(dataUri, new String[]{"data1", "mimetype"},
                        "contact_id=?", new String[]{contact_id}, null);
                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name"
                                .equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawCursor.close();

        }
        return  list;
    }
}