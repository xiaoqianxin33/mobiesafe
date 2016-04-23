package com.xiao.mobiesafe.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xiao on 2016/4/24.
 */
public class ReadContactEngine {

    public static ArrayList<HashMap<String, String>> readContact(Context context) {

        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        Cursor rawCursor = context.getContentResolver().
                query(rawContactsUri, new String[]{"contact_id"}, null, null, null);

        if (rawCursor != null) {
            while (rawCursor.moveToNext()) {
                String contact_id = rawCursor.getString(0);
                Cursor dataCursor = context.getContentResolver().query(dataUri, new String[]{"data1", "mimetype"},
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
        return list;
    }

    public static ArrayList<HashMap<String, String>> readSmsLog(Context context) {
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, " _id desc");
        ArrayList<HashMap<String, String>> datas = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();

                String phone = cursor.getString(0);
                map.put("phone", phone);

                datas.add(map);

            }
        }
        return datas;
    }


    public static ArrayList<HashMap<String, String>> readCallLog(Context context) {

        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"number", "name"}, null, null, " _id desc");
        ArrayList<HashMap<String, String>> datas = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();

                String phone = cursor.getString(0);
                String name = cursor.getString(1);
                map.put("phone", phone);
                map.put("name", name);

                datas.add(map);
            }
        }
        return datas;
    }


}
