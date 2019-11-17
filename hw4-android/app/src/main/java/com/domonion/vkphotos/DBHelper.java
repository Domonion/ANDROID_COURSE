package com.domonion.vkphotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "vkPhotosTable";
    private static final String DESCRIPTION_1 = "description";
    private static final String URL_2 = "URL";
    private static final String DB_LOG_TAG = LOG_TAG;
    private static final int DESCRIPTION_1_id = 1;
    private static final int URL_2_id = 2;
    private static SQLiteDatabase db;
    private static DBHelper helper;

    private DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        Log.d(LOG_TAG, "DB private constructor");
    }

    static DBHelper getInstance(Context context) {
        Log.d(LOG_TAG, "DB getInstance");
        if (helper == null) {
            helper = new DBHelper(context);
            db = helper.getWritableDatabase();
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "DB onCreate");
        db.execSQL("create table " + TABLE_NAME + " (id integer primary key autoincrement, " +
                DESCRIPTION_1 + " text, " +
                URL_2 + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "DB onUpgrade (why?)");
    }

    private String getDescription(Cursor cr) {
        Log.d(LOG_TAG, "DB getDescription");
        String ans = cr.getString(DESCRIPTION_1_id);
        Log.d(DB_LOG_TAG, ans);
        return ans;
    }

    private String getURL(Cursor cr) {
        Log.d(LOG_TAG, "DB getURL");
        String ans = cr.getString(URL_2_id);
        Log.d(DB_LOG_TAG, ans);
        return ans;
    }

    private Cursor getAllData() {
        Log.d(LOG_TAG, "DB getAllData");
        Cursor cr = db.rawQuery("select * from " + TABLE_NAME, null);
        Log.d(DB_LOG_TAG, "DB size: " + Integer.toString(cr.getCount()));
        return cr;
    }

    private int getId(String descr, String url) {
        Log.d(LOG_TAG, "DB getId");
        Log.d(DB_LOG_TAG, descr + "\n" + url);
        Cursor cr = getAllData();
        int ans = -1;
        while (cr.moveToNext()) {
            if (getDescription(cr).equals(descr) && getURL(cr).equals(url)) {
                ans = cr.getShort(0);
                break;
            }
        }
        cr.close();
        Log.d(DB_LOG_TAG, "Description: " + descr + "\nurl: " + url + "\nID: " + Integer.toString(ans));
        return ans;
    }

    boolean check(String descr, String url) {
        Log.d(LOG_TAG, "DB check");
        boolean ans = getId(descr, url) != -1;
        Log.d(DB_LOG_TAG, "Check result: " + Boolean.toString(ans));
        return ans;
    }

    ImageData[] getData() {
        Log.d(LOG_TAG, "DB getData");
        Cursor cr = getAllData();
        ImageData[] ans = new ImageData[cr.getCount()];
        Log.d(DB_LOG_TAG, Integer.toString(cr.getCount()));
        int ind = 0;
        while (cr.moveToNext()) {
            ans[ind] = new ImageData(ind, getURL(cr), getDescription(cr));
            Log.d(DB_LOG_TAG, ans[ind].description + " " + ans[ind].URL);
            ind++;
        }
        cr.close();
        return ans;
    }

    void delete(String descr, String url) {
        Log.d(LOG_TAG, "DB delete");
        if (!check(descr, url))
            return;
        int ind = getId(descr, url);
        if (ind != -1) {
            Log.d(LOG_TAG, "executing delete SQL");
            db.execSQL("delete from " + TABLE_NAME + " where id = " + Integer.toString(ind) + ";");
        }
    }

    void add(String descr, String url) {
        Log.d(LOG_TAG, "DB add");
        if (!check(descr, url)) {
            db.close();
            db = this.getWritableDatabase();
            Log.d(DB_LOG_TAG, "adding " + descr + " " + url);
            ContentValues cv = new ContentValues();
            cv.put(DESCRIPTION_1, descr);
            cv.put(URL_2, url);
//            db.insertOrThrow(TABLE_NAME, null, cv);
            long res = db.insert(TABLE_NAME, null, cv);
            if (res == -1) {
                Log.d(DB_LOG_TAG, "not added(why?) " + descr);
            } else {
                Log.d(DB_LOG_TAG, "item added");
            }
        }
    }

}
