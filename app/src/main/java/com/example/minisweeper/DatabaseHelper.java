package com.example.minisweeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static private String db_name = "MinesweeperRecords";
    static private int db_version = 1;
    static private String table_name = "best_record";
    static private String ROW_COUNT = "ROW_COUNT";
    static private String COL_COUNT = "COLUMN_COUNT";
    static private String BEST_TIME = "TIME";
    DatabaseHelper(Context context){
        super(context, db_name, null, db_version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + table_name + " (\n" +
                "    " + ROW_COUNT + " INTEGER NOT NULL,\n" +
                "    " + COL_COUNT + " INTEGER NOT NULL,\n" +
                "    " + BEST_TIME + " INTEGER NOT NULL\n" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + table_name + ";";
        db.execSQL(sql);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + table_name + ";";
        db.execSQL(sql);
        onCreate(db);
    }


    Cursor getRecord(int row_count, int col_count){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT "+ BEST_TIME +" FROM "+ table_name +" WHERE "+ROW_COUNT+" = "+row_count+" AND "+ COL_COUNT +" = "+col_count;
        return db.rawQuery(sql, null);
    }

    boolean updateBestTime(int row_count, int col_count, long best_time){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BEST_TIME, best_time);
        return db.update(table_name, values, ROW_COUNT+"= ? AND "+COL_COUNT+"= ?", new String[]{String.valueOf(row_count), String.valueOf(col_count)}) == 1;
    }

    boolean insertBestTime(int row_count, int col_count, long best_time){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROW_COUNT, row_count);
        values.put(COL_COUNT, col_count);
        values.put(BEST_TIME, best_time);
        return db.insert(table_name, null, values) != -1;
    }
}