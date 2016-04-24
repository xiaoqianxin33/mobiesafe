package com.xiao.mobiesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiao.mobiesafe.domain.BlackBean;
import com.xiao.mobiesafe.domain.BlackTable;
import com.xiao.mobiesafe.db.BlackDB;

import java.util.ArrayList;
import java.util.List;

public class BlackDao {

    private BlackDB blackDB;

    public BlackDao(Context context) {
        this.blackDB = new BlackDB(context);
    }

    public void add(String phone, int mode) {
        SQLiteDatabase database = blackDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BlackTable.PHONE, phone);
        values.put(BlackTable.MODE, mode);

        database.insert(BlackTable.BLACKTABLE, null, values);
        database.close();
    }

    public void add(BlackBean bean) {
        add(bean.getPhone(), bean.getMode());
    }

    public void delete(String phone) {
        SQLiteDatabase db = this.blackDB.getWritableDatabase();
        db.delete(BlackTable.BLACKTABLE, BlackTable.PHONE + "=?", new String[]{phone});
        db.close();
    }

    public List<BlackBean> getAllDatas() {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase database = blackDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackTable.PHONE + ","
                + BlackTable.MODE + " from " + BlackTable.BLACKTABLE, null);

        while (cursor.moveToNext()) {
            BlackBean bean = new BlackBean();

            bean.setPhone(cursor.getString(0));

            bean.setMode(cursor.getInt(1));

            datas.add(bean);
        }
        cursor.close();
        database.close();

        return datas;
    }

    public List<BlackBean> getMoreDatas(int datasNumber, int startIndex) {

        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase database = blackDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackTable.PHONE + ","
                        + BlackTable.MODE + " from " + BlackTable.BLACKTABLE + " order by _id desc limit ?,?",
                new String[]{startIndex + "", datasNumber + ""});

        while (cursor.moveToNext()) {
            BlackBean bean = new BlackBean();

            bean.setPhone(cursor.getString(0));

            bean.setMode(cursor.getInt(1));

            datas.add(bean);
        }
        cursor.close();
        database.close();

        return datas;
    }


    public int getMode(String number) {
        int mode;
        SQLiteDatabase database = blackDB.getReadableDatabase();

        Cursor cursor = database.rawQuery("select " +
                        BlackTable.MODE + " from " + BlackTable.BLACKTABLE + " where " + BlackTable.PHONE + "=?",
                new String[]{number});

        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        } else {
            mode = 0;
        }
        database.close();
        cursor.close();
        return mode;
    }
}
