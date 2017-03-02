package com.architecture.realarchitecture.datasource.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liushuo on 16/1/12.
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String DB_NAME = "compat_nosql.db";
    private static final int DB_VERSION = 1;

    private static DBHelper mDBHelper;

    public static DBHelper getInstance(Context context) {
        synchronized (DBHelper.class) {
            if (mDBHelper == null) {
                mDBHelper = new DBHelper(context);
            }
            return mDBHelper;
        }
    }


    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, 1);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建模拟的nosql数据表
        createCompatibleNosqlTable(db);

        //创建其他的关系数据表
        createTestTable(db);
    }


    private void createCompatibleNosqlTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TableDef.TableNosql.DB_TABLE);
            db.execSQL("CREATE TABLE " + TableDef.TableNosql.DB_TABLE + "(" + TableDef.TableNosql.Column.COLUMN_ID
                    + " INT NOT NULL DEFAULT 0 AUTO_INCREMENT PRIMARY KEY,"
                    + TableDef.TableNosql.Column.COLUMN_CONTENT_ID + " TEXT, "
                    + TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE + " TEXT, "
                    + TableDef.TableNosql.Column.COLUMN_CONTENT + " TEXT); ");
        } catch (SQLException ex) {
            Log.e(TAG,
                    String.format("couldn't create table in %s database", DB_NAME));
            throw ex;
        }
    }

    private void createTestTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TableDef.TableTest.DB_TABLE);
            db.execSQL("CREATE TABLE " + TableDef.TableTest.DB_TABLE + "(" + TableDef.TableTest.Column.COLUMN_ID
                    + " TEXT PRIMARY KEY,"
                    + TableDef.TableTest.Column.COLUMN_CHILD_ID + " TEXT, "
                    + TableDef.TableTest.Column.COLUMN_PARENT_ID + " TEXT); ");
        } catch (SQLException ex) {
            Log.e(TAG,
                    String.format("couldn't create table in %s database", DB_NAME));
            throw ex;
        }
    }

}
