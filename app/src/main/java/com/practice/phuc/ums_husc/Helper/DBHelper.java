package com.practice.phuc.ums_husc.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practice.phuc.ums_husc.Model.THONGBAO;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "umshuscdb";

    public static final String NEWS = "news";
    private static final String NEWS_ID = "news_id";
    private static final String NEWS_TITLE = "news_title";
    private static final String NEWS_BODY = "news_body";
    private static final String NEWS_POST_TIME = "news_post_time";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + NEWS + "("
                + NEWS_ID + " INTEGER,"
                + NEWS_TITLE + " TEXT,"
                + NEWS_BODY + " TEXT,"
                + NEWS_POST_TIME + " TEXT" + ")";
        db.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NEWS);
        onCreate(db);
    }

    /*
     * Method for news table
     */
    public List<THONGBAO> getAllNews() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<THONGBAO> list = new ArrayList<>();
        THONGBAO thongbao;
        Cursor cursor = db.rawQuery("SELECT * FROM " + NEWS
                + " ORDER BY datetime(" + NEWS_POST_TIME + ") DESC", null);
        while (cursor.moveToNext()) {
            thongbao = new THONGBAO();
            thongbao.setMaThongBao(cursor.getInt(cursor.getColumnIndex(NEWS_ID)));
            thongbao.setTieuDe(cursor.getString(cursor.getColumnIndex(NEWS_TITLE)));
            thongbao.setNoiDung(cursor.getString(cursor.getColumnIndex(NEWS_BODY)));
            thongbao.setThoiGianDang(cursor.getString(cursor.getColumnIndex(NEWS_POST_TIME)));
            list.add(thongbao);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertNews(THONGBAO thongbao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NEWS_ID, thongbao.getMaThongBao());
        contentValues.put(NEWS_TITLE, thongbao.getTieuDe());
        contentValues.put(NEWS_BODY, thongbao.getNoiDung());
        contentValues.put(NEWS_POST_TIME, thongbao.getThoiGianDang());
        db.insert(NEWS, null, contentValues);
        db.close();
    }

    /*
     * Public method
     */
    public long countRow(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + tableName;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        cursor.close();
        db.close();
        return icount;
    }

    public void deleteAllRecord(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }
}
