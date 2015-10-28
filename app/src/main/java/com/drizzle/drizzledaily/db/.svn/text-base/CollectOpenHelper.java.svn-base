package com.drizzle.drizzledaily.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *收藏夹本地数据库帮助类
 */
public class CollectOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_COLLECT="create table Collect("
            +"id integer primary key autoincrement,"
            +"collect_id integer,"
            +"collect_title text,"
            +"collect_type integer)";

    public CollectOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COLLECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
