package com.drizzle.drizzledaily.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drizzle.drizzledaily.bean.CollectBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 收藏夹本地数据库操作类
 */
public class CollectDB {
    private static final String COLLECT_DB_NAME = "collectlist";
    private static final int COLLECT_VERSION = 1;
    private static CollectDB collectDB;
    private SQLiteDatabase db;

    private CollectDB(Context context) {
        CollectOpenHelper dbHelper = new CollectOpenHelper(context, COLLECT_DB_NAME, null, COLLECT_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CollectDB getInstance(Context context) {
        if (collectDB == null) {
            collectDB = new CollectDB(context);
        }
        return collectDB;
    }

    public void saveCollect(CollectBean collect) {
        if (collect != null) {
            ContentValues values = new ContentValues();
            values.put("collect_id", collect.getId());
            values.put("collect_title", collect.getTitle());
            values.put("collect_type", collect.getType());
            db.insert("Collect", null, values);
        }
    }

    public void wipeCollect() {
        db.execSQL("DELETE FROM Collect");
    }

    public void deleteCollect(int collectid) {
        db.delete("Collect", "collect_id=?", new String[]{collectid + ""});
    }

    public List<CollectBean> findCollects() {
        List<CollectBean> list = new ArrayList<CollectBean>();
        Cursor cursor = db.query("Collect", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CollectBean collect = new CollectBean();
                collect.setId(cursor.getInt(cursor.getColumnIndex("collect_id")));
                collect.setTitle(cursor.getString(cursor.getColumnIndex("collect_title")));
                collect.setType(cursor.getInt(cursor.getColumnIndex("collect_type")));
                list.add(collect);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 获取set集合
     * @return
     */
    public Set<CollectBean> findSetCollects(){
        Set<CollectBean> collectBeanSet = new HashSet<>();
        Cursor cursor = db.query("Collect", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CollectBean collect = new CollectBean();
                collect.setId(cursor.getInt(cursor.getColumnIndex("collect_id")));
                collect.setTitle(cursor.getString(cursor.getColumnIndex("collect_title")));
                collect.setType(cursor.getInt(cursor.getColumnIndex("collect_type")));
                collectBeanSet.add(collect);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return collectBeanSet;
    }

}
