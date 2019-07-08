package com.example.minisweeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static private String db_name = "MinesweeperRecords";
    static private int db_version = 1;
    static private String table_name = "best_record";
    static private String row_count = "ROW_COUNT";
    static private String col_count = "COLUMN_COUNT";
    static private String time = "TIME";
    DatabaseHelper(Context context){
        super(context, db_name, null, db_version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + table_name + " (\n" +
                "    " + row_count + " INTEGER NOT NULL,\n" +
                "    " + col_count + " INTEGER NOT NULL,\n" +
                "    " + time + " INTEGER NOT NULL\n" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + table_name + ";";
        db.execSQL(sql);
        onCreate(db);
    }
}
