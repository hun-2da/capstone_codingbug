package com.capstone.codingbug.localdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDataBaseHelper extends SQLiteOpenHelper {
    public static String NAME = "user_setting.db";
    public static String table = "user_log";
    public static String my_id = "my_id";
    public static String parent_phone = "parent_mobile";

    //--------------------------------------------------------
    public static final String CTABLENAME = "cLocation";
    public static final String KEY_ID = "id";

    public static String CLATITUDE = "child_latitude";
    public static String CLOGITUDE = "child_longitude";
    public static String CDATE = "child_date";
    public static final String PHONE_NUMBER = "get_phone";

    public static int VERSION = 1;
    public LocalDataBaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists " + table + "(" +
                my_id + " text," +
                parent_phone + " text," +
                " PRIMARY KEY(" + my_id + ", " + parent_phone + "))";
        sqLiteDatabase.execSQL(sql);

        String CREATE_TABLE = "CREATE TABLE " + CTABLENAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CDATE + " TEXT,"
                + PHONE_NUMBER + " TEXT,"
                + CLATITUDE + " REAL,"
                + CLOGITUDE + " REAL" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i1 > VERSION)
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user_log");
    }
}
