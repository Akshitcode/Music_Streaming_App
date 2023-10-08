package com.example.musicstreamingapplication.Model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String USERS_TABLE = "Users";
    private static final String ACCOUNT_KEY = "accountId";
    private static final String NAME_KEY = "name";
    private static final String BALANCE_KEY = "balance";
    private static final String PHONE_KEY = "phoneNumber";

    public DownloadDatabase (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
