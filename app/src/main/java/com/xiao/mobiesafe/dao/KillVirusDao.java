package com.xiao.mobiesafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 杀毒
 * Created by xiao on 2016/5/1.
 */
public class KillVirusDao {

    public static boolean isVirus(String md5) {

        boolean res = false;
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/com.xiao.mobiesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = sqLiteDatabase.rawQuery("select 1 from datable where md5=?", new String[]{md5});

        if (cursor.moveToNext()) {
            res = true;
        }

        sqLiteDatabase.close();
        cursor.close();

        return res;
    }


    public static void addVirus(String md5, String desc) {

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/com.xiao.mobiesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);

        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", 6);
        values.put("name", "Android.Hack.CarrierIQ.a");
        values.put("desc", desc);

        sqLiteDatabase.insert("datable", null, values);
    }

    public static boolean isNewVirus(int version) {
        boolean res = false;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                "/data/data/com.xiao.mobiesafe/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = database.rawQuery("select 1 from version where subcnt=?", new String[]{version + ""});
        if (cursor.moveToNext()) {
            res = true;
        }
        cursor.close();
        database.close();

        return res;
    }
}
