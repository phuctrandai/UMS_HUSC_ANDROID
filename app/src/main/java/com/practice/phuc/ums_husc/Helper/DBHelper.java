package com.practice.phuc.ums_husc.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.Model.TINNHAN;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    private static final String DB_NAME = "umshuscdb";

    public static final String NEWS = "news";
    private static final String NEWS_ID = "news_id";
    private static final String NEWS_TITLE = "news_title";
    private static final String NEWS_BODY = "news_body";
    private static final String NEWS_POST_TIME = "news_post_time";

    public static final String MESSAGE = "message";
    private static final String MESSAGE_ID = "message_id";
    private static final String MESSAGE_TITLE = "message_title";
    private static final String MESSAGE_SENDER = "message_sender";
    private static final String MESSAGE_BODY = "message_body";
    private static final String MESSAGE_SEND_TIME = "message_send_time";
    private static final String MESSAGE_RECEIVER = "message_receiver";
    private static final String MESSAGE_SEEN_TIME = "message_seen_time";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        try (SQLiteDatabase db = getWritableDatabase()) {
            createNewsTable(db);
            createMessageTable(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNewsTable(db);
        createMessageTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        DB_VERSION = oldVersion + 1;
//        db.execSQL("DROP TABLE IF EXISTS " + NEWS);
//        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_SEEN_TIME);
//        onCreate(db);
    }

    /*
     * Method for news table
     */
    private void createNewsTable(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE IF NOT EXISTS " + NEWS + "("
                + NEWS_ID + " INTEGER,"
                + NEWS_TITLE + " TEXT,"
                + NEWS_BODY + " TEXT,"
                + NEWS_POST_TIME + " TEXT" + ")";
        db.execSQL(CREATE_NEWS_TABLE);
    }

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
     * Method for message table
     */
    private void createMessageTable(SQLiteDatabase db) {
        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + MESSAGE + "("
                + MESSAGE_ID + " INTERGER,"
                + MESSAGE_TITLE + " TEXT,"
                + MESSAGE_BODY + " TEXT,"
                + MESSAGE_SENDER + " TEXT,"
                + MESSAGE_SEND_TIME + " TEXT,"
                + MESSAGE_RECEIVER + " TEXT,"
                + MESSAGE_SEEN_TIME + " TEXT" + ")";

        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    public List<TINNHAN> getAllMessage() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<TINNHAN> list = new ArrayList<>();
        TINNHAN tinnhan;
        Cursor cursor = db.rawQuery("SELECT * FROM " + MESSAGE
                + " ORDER BY datetime(" + MESSAGE_SEND_TIME + ") DESC", null);
        while (cursor.moveToNext()) {
            tinnhan = new TINNHAN();
            tinnhan.setMaTinNhan(cursor.getInt(cursor.getColumnIndex(MESSAGE_ID)));
            tinnhan.setTieuDe(cursor.getString(cursor.getColumnIndex(MESSAGE_TITLE)));
            tinnhan.setNoiDung(cursor.getString(cursor.getColumnIndex(MESSAGE_BODY)));
            tinnhan.setHoTenNguoiGui(cursor.getString(cursor.getColumnIndex(MESSAGE_SENDER)));
//            tinnhan.setMaNguoiGui(cursor.getString(cursor.getColumnIndex(MESSAGE_RECEIVER)));
            tinnhan.setThoiDiemGui(cursor.getString(cursor.getColumnIndex(MESSAGE_SEND_TIME)));
//            tinnhan.setThoiDiemGui(cursor.getString(cursor.getColumnIndex(MESSAGE_SEEN_TIME)));
            list.add(tinnhan);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertMessage(TINNHAN tinnhan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID, tinnhan.getMaTinNhan());
        contentValues.put(MESSAGE_TITLE, tinnhan.getTieuDe());
        contentValues.put(MESSAGE_BODY, tinnhan.getNoiDung());
//        contentValues.put(MESSAGE_SENDER, tinnhan.getNguoiGui());
//        contentValues.put(MESSAGE_RECEIVER, tinnhan.getNguoiNhan());
        contentValues.put(MESSAGE_SEND_TIME, tinnhan.getThoiDiemGui());
//        contentValues.put(MESSAGE_SEEN_TIME, tinnhan.getThoiDiemXem());
        db.insert(MESSAGE, null, contentValues);
        db.close();
    }

    /*
     * Public method
     */
    boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

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
