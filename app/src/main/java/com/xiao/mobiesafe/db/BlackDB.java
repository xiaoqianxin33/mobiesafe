package com.xiao.mobiesafe.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiao on 2016/4/22.
 */
public class BlackDB extends SQLiteOpenHelper {

    public BlackDB(Context context) {
        super(context,"black.db",null,1);
    }

    public BlackDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table blacktb");
        onCreate(db);
    }
}
