package com.app.seam.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.seam.TableStructure.UserTable;

/**
 * Created by Colinares on 3/28/2018.
 */

public class MyDatabase extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "SEAM.sqlite";

    public static MyDatabase mInstance = null;

    public MyDatabase(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static MyDatabase getInstance(Context context){
        if (mInstance == null){
            mInstance = new MyDatabase(context.getApplicationContext());
        }

        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
