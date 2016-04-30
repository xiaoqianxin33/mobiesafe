package com.xiao.mobiesafe.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiao on 2016/4/22.
 */
public class LockDB extends SQLiteOpenHelper {

    public LockDB(Context context) {

        super(context,"lock.db",null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table locktb(_id integer primary key autoincrement,packname text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table locktb");
        onCreate(db);
    }
}
