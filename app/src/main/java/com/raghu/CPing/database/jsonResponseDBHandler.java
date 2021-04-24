package com.raghu.CPing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class jsonResponseDBHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "summerPDFListDB.db";
    private static final int DATABASE_VERSION = 1;

    public jsonResponseDBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
