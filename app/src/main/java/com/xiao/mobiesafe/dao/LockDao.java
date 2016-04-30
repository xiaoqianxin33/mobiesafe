package com.xiao.mobiesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiao.mobiesafe.db.LockDB;
import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.domain.LockTable;

import java.util.ArrayList;
import java.util.List;

public class LockDao {

    private LockDB lockDB;
    private Context context;

    public LockDao(Context context) {

        this.lockDB = new LockDB(context);
        this.context = context;
    }

    public void add(String packname) {
        SQLiteDatabase database = lockDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LockTable.PACKNAME, packname);

        database.insert(LockTable.LOCKTABLE, null, values);
        database.close();

        context.getContentResolver().notifyChange(LockTable.uri, null);

    }


    public void delete(String packname) {
        SQLiteDatabase db = this.lockDB.getWritableDatabase();
        db.delete(LockTable.LOCKTABLE, LockTable.PACKNAME + "=?", new String[]{packname});
        context.getContentResolver().notifyChange(LockTable.uri, null);
        db.close();
    }

    public List<String> getAllDatas() {
        List<String> datas = new ArrayList<>();
        SQLiteDatabase database = lockDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + LockTable.PACKNAME
                + " from " + LockTable.LOCKTABLE, null);
        while (cursor.moveToNext()) {
            datas.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return datas;
    }


    public boolean isLocked(String packname) {
        boolean res = false;

        SQLiteDatabase database = lockDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select 1 from "
                + LockTable.LOCKTABLE + " where " + LockTable.PACKNAME
                + "=?", new String[]{packname});

        while (cursor.moveToNext()) {
            res = true;
        }

        cursor.close();
        database.close();
        return res;
    }

}
