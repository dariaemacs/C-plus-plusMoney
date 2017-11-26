package com.dariaemacs.cmoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dariaemacs on 18.11.17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "database.db";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CardEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CardEntry.TABLE_NAME);
        db.execSQL(CardEntry.CREATE_TABLE);
    }
}
