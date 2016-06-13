package com.example.jaskirat.alternate_parking.database;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jaskirat.alternate_parking.R;


public class RecentSearchesDB extends SQLiteOpenHelper {

    private static String DBNAME = "searches";

    private static int VERSION = 1;

    public static final String ROW_ID = "_id";

    private static final String DATABASE_TABLE = "recent_searches";

    public static final String PLACE_DETAILS = "place_details";

    private SQLiteDatabase mDB;

    public RecentSearchesDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + DATABASE_TABLE + " ( " +
                ROW_ID + " integer primary key autoincrement , " +
                SearchManager.SUGGEST_COLUMN_ICON_1 + " text , " +
                SearchManager.SUGGEST_COLUMN_TEXT_1 + " text , " +
                SearchManager.SUGGEST_COLUMN_TEXT_2 + " text , " +
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA + " text , " +
                PLACE_DETAILS + " text " +
                " ) ";

        db.execSQL(sql);
    }

    public long insert(ContentValues contentValues) {
        long rowID = mDB.insert(DATABASE_TABLE, null, contentValues);
        return rowID;
    }

    public int del() {
        int cnt = mDB.delete(DATABASE_TABLE, null, null);
        return cnt;
    }

    public Cursor getAllLocations() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchManager.SUGGEST_COLUMN_ICON_1, Integer.toString(R.drawable.recent_location));

        mDB.update(DATABASE_TABLE, contentValues, null, new String[]{});
        return mDB.query(DATABASE_TABLE, new String[]{
                ROW_ID,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                PLACE_DETAILS,
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
        }, null, null, SearchManager.SUGGEST_COLUMN_TEXT_1, null, ROW_ID + " DESC", "10");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
