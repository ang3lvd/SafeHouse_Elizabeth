package com.divinesecurity.safehouse.dbAdapterPackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ang3l on 3/3/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "divseccapp_db.db";
    private static final int DATABASE_VERSION = 2;

    DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyDataBaseAdapter.DATABASE_CREATE_I);
        db.execSQL(MyDataBaseAdapter.DATABASE_CREATE_A);
        db.execSQL(MyDataBaseAdapter.DATABASE_CREATE_Z);
        db.execSQL(MyDataBaseAdapter.DATABASE_CREATE_G);
        db.execSQL(MyDataBaseAdapter.DATABASE_CREATE_ACC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyDataBaseAdapter.I_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyDataBaseAdapter.A_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyDataBaseAdapter.Z_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyDataBaseAdapter.G_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyDataBaseAdapter.ACC_TABLE_NAME);
        onCreate(db);
    }
}
