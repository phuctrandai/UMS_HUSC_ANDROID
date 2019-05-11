package com.practice.phuc.ums_husc.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.ViewModel.TaiKhoan;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    private static final String DB_NAME = "umshuscdb";

    private static final String ACCOUNT = "account";
    private static final String ACCOUNT_ID = "account_id";
    private static final String ACCOUNT_NAME = "account_name";
    private static final String STUDENT_ID = "student_id";

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

    public static final String SCHEDULE = "schedule";
    private static final String CLASS_ID = "class_id";
    private static final String CLASS_NAME = "class_name";
    private static final String ROOM_NAME = "room_name";
    private static final String ROOM_ID = "room_id";
    private static final String START_HOUR = "start_hour";
    private static final String END_HOUR = "end_hour";
    private static final String DATE = "date";
    private static final String DAY_OF_WEEK = "day_of_week";
    private static final String TEACHER = "teacher";
    private static final String SEMESTER = "semester";

    public static final String REMINDER = "reminder";
    private static final String REQUEST_CODE = "request_code";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        try (SQLiteDatabase db = getWritableDatabase()) {
            createNewsTable(db);
            createMessageTable(db);
            createAccountTable(db);
            createScheduleTable(db);
            createReminderTable(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNewsTable(db);
        createMessageTable(db);
        createAccountTable(db);
        createScheduleTable(db);
        createReminderTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DB_VERSION = oldVersion + 1;
        db.execSQL("DROP TABLE IF EXISTS " + NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + REMINDER);
        onCreate(db);
    }

    /*
     * Method for schedule table
     */
    private void createScheduleTable(SQLiteDatabase db) {
        String CREATE_SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " + SCHEDULE + "("
                + CLASS_ID + " TEXT,"
                + CLASS_NAME + " TEXT,"
                + ROOM_ID + " TEXT,"
                + ROOM_NAME + " TEXT,"
                + START_HOUR + " INTEGER,"
                + END_HOUR + " INTEGER,"
                + DATE + " TEXT,"
                + DAY_OF_WEEK + " INTEGER,"
                + TEACHER + " TEXT,"
                + SEMESTER + " TEXT" + ")";
        db.execSQL(CREATE_SCHEDULE_TABLE);
    }

    public List<ThoiKhoaBieu> getSchedule() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ThoiKhoaBieu> list = new ArrayList<>();
        ThoiKhoaBieu thoiKhoaBieu;
        Cursor cursor = db.rawQuery("SELECT * FROM " + SCHEDULE
                + " ORDER BY datetime(" + DATE + ")", null);
        while (cursor.moveToNext()) {
            thoiKhoaBieu = new ThoiKhoaBieu();
            thoiKhoaBieu.MaLopHocPhan = cursor.getString(cursor.getColumnIndex(CLASS_ID));
            thoiKhoaBieu.TenLopHocPhan = cursor.getString(cursor.getColumnIndex(CLASS_NAME));
            thoiKhoaBieu.NgayHoc = cursor.getString(cursor.getColumnIndex(DATE));
            thoiKhoaBieu.NgayTrongTuan = cursor.getInt(cursor.getColumnIndex(DAY_OF_WEEK));
            thoiKhoaBieu.TietHocBatDau = cursor.getInt(cursor.getColumnIndex(START_HOUR));
            thoiKhoaBieu.TietHocKetThuc = cursor.getInt(cursor.getColumnIndex(END_HOUR));
            thoiKhoaBieu.TenPhong = cursor.getString(cursor.getColumnIndex(ROOM_NAME));
            thoiKhoaBieu.PhongHoc = cursor.getString(cursor.getColumnIndex(ROOM_ID));
            thoiKhoaBieu.HoVaTen = cursor.getString(cursor.getColumnIndex(TEACHER));
            thoiKhoaBieu.HocKy = cursor.getString(cursor.getColumnIndex(SEMESTER));
            list.add(thoiKhoaBieu);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ThoiKhoaBieu> getSchedule(String date) {
        Log.d("DEBUG", "getSchedule() called with: date = [" + date + "]");
        SQLiteDatabase db = this.getWritableDatabase();
        List<ThoiKhoaBieu> list = new ArrayList<>();
        ThoiKhoaBieu thoiKhoaBieu;
        Cursor cursor = db.rawQuery("SELECT * FROM " + SCHEDULE
                + " WHERE " + DATE + " LIKE " + "'%" + date + "'"
                + " ORDER BY datetime(" + DATE + ")", null);
        while (cursor.moveToNext()) {
            thoiKhoaBieu = new ThoiKhoaBieu();
            thoiKhoaBieu.MaLopHocPhan = cursor.getString(cursor.getColumnIndex(CLASS_ID));
            thoiKhoaBieu.TenLopHocPhan = cursor.getString(cursor.getColumnIndex(CLASS_NAME));
            thoiKhoaBieu.NgayHoc = cursor.getString(cursor.getColumnIndex(DATE));
            thoiKhoaBieu.NgayTrongTuan = cursor.getInt(cursor.getColumnIndex(DAY_OF_WEEK));
            thoiKhoaBieu.TietHocBatDau = cursor.getInt(cursor.getColumnIndex(START_HOUR));
            thoiKhoaBieu.TietHocKetThuc = cursor.getInt(cursor.getColumnIndex(END_HOUR));
            thoiKhoaBieu.TenPhong = cursor.getString(cursor.getColumnIndex(ROOM_NAME));
            thoiKhoaBieu.PhongHoc = cursor.getString(cursor.getColumnIndex(ROOM_ID));
            thoiKhoaBieu.HoVaTen = cursor.getString(cursor.getColumnIndex(TEACHER));
            thoiKhoaBieu.HocKy = cursor.getString(cursor.getColumnIndex(SEMESTER));
            list.add(thoiKhoaBieu);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertSchedule(ThoiKhoaBieu thoiKhoaBieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_ID, thoiKhoaBieu.MaLopHocPhan);
        values.put(CLASS_NAME, thoiKhoaBieu.TenLopHocPhan);
        values.put(ROOM_NAME, thoiKhoaBieu.TenPhong);
        values.put(ROOM_ID, thoiKhoaBieu.PhongHoc);
        values.put(START_HOUR, thoiKhoaBieu.TietHocBatDau);
        values.put(END_HOUR, thoiKhoaBieu.TietHocKetThuc);
        values.put(DATE, thoiKhoaBieu.NgayHoc);
        values.put(DAY_OF_WEEK, thoiKhoaBieu.NgayTrongTuan);
        values.put(TEACHER, thoiKhoaBieu.HoVaTen);
        values.put(SEMESTER, thoiKhoaBieu.HocKy);
        db.insert(SCHEDULE, null, values);
        db.close();
    }

    public void insertSchedule(List<ThoiKhoaBieu> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (ThoiKhoaBieu thoiKhoaBieu : list) {
            String ngayBatDauStr = thoiKhoaBieu.NgayHoc.substring(0, 10);

            ContentValues values = new ContentValues();
            values.put(CLASS_ID, thoiKhoaBieu.MaLopHocPhan);
            values.put(CLASS_NAME, thoiKhoaBieu.TenLopHocPhan);
            values.put(ROOM_NAME, thoiKhoaBieu.TenPhong);
            values.put(ROOM_ID, thoiKhoaBieu.PhongHoc);
            values.put(START_HOUR, thoiKhoaBieu.TietHocBatDau);
            values.put(END_HOUR, thoiKhoaBieu.TietHocKetThuc);
            values.put(DATE, ngayBatDauStr);
            values.put(DAY_OF_WEEK, thoiKhoaBieu.NgayTrongTuan);
            values.put(TEACHER, thoiKhoaBieu.HoVaTen);
            values.put(SEMESTER, thoiKhoaBieu.HocKy);
            db.insert(SCHEDULE, null, values);
        }
        db.close();
    }

    /*
     * Method for account table
     */
    private void createAccountTable(SQLiteDatabase db) {
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS " + ACCOUNT + "("
                + ACCOUNT_ID + " TEXT,"
                + ACCOUNT_NAME + " TEXT,"
                + STUDENT_ID + " TEXT" + ")";
        db.execSQL(CREATE_ACCOUNT_TABLE);
    }

    public List<TaiKhoan> getAllAccount() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<TaiKhoan> list = new ArrayList<>();
        TaiKhoan taiKhoan;
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACCOUNT
                + " ORDER BY " + ACCOUNT_NAME + " DESC", null);
        while (cursor.moveToNext()) {
            taiKhoan = new TaiKhoan();
            taiKhoan.MaTaiKhoan = cursor.getString(cursor.getColumnIndex(ACCOUNT_ID));
            taiKhoan.HoTen = cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME));
            taiKhoan.MaSinhVien = cursor.getString(cursor.getColumnIndex(STUDENT_ID));
            list.add(taiKhoan);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertAccount(TaiKhoan taiKhoan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_ID, taiKhoan.MaTaiKhoan);
        contentValues.put(ACCOUNT_NAME, taiKhoan.HoTen);
        contentValues.put(STUDENT_ID, taiKhoan.MaSinhVien);
        db.insert(ACCOUNT, null, contentValues);
        db.close();
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
                + MESSAGE_ID + " TEXT,"
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
            tinnhan.MaTinNhan = cursor.getString(cursor.getColumnIndex(MESSAGE_ID));
            tinnhan.TieuDe = cursor.getString(cursor.getColumnIndex(MESSAGE_TITLE));
            tinnhan.NoiDung = cursor.getString(cursor.getColumnIndex(MESSAGE_BODY));
            tinnhan.HoTenNguoiGui = cursor.getString(cursor.getColumnIndex(MESSAGE_SENDER));
//            tinnhan.setMaNguoiGui(cursor.getString(cursor.getColumnIndex(MESSAGE_RECEIVER)));
            tinnhan.ThoiDiemGui = cursor.getString(cursor.getColumnIndex(MESSAGE_SEND_TIME));
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
        contentValues.put(MESSAGE_ID, tinnhan.MaTinNhan);
        contentValues.put(MESSAGE_TITLE, tinnhan.TieuDe);
        contentValues.put(MESSAGE_BODY, tinnhan.NoiDung);
        contentValues.put(MESSAGE_SEND_TIME, tinnhan.ThoiDiemGui);
        db.insert(MESSAGE, null, contentValues);
        db.close();
    }

    /*
     * Method for reminder table
     */
    private void createReminderTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + REMINDER + "("
                + REQUEST_CODE + " INTEGER)";
        db.execSQL(query);
    }

    public void insertRequestCodeReminder(int requestCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REQUEST_CODE, requestCode);
        db.insert(REMINDER, null, contentValues);
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
