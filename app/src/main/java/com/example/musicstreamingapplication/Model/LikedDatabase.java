package com.example.musicstreamingapplication.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class LikedDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LikedSongsDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String LIKED_SONG_TABLE = "LIKED_SONGS";
    private static final String SONG_KEY = "SONG_Id";


    public LikedDatabase (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDatabaseSQL = "create table " + LIKED_SONG_TABLE +
                "(" +  SONG_KEY + " text )";
        db.execSQL(createDatabaseSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if EXISTS " + LIKED_SONG_TABLE);
        onCreate(db);
    }
    public void addLikedSongToDatabase (String str) {

        SQLiteDatabase database = getWritableDatabase();
        String addUserToDatabaseSQLCommand = "insert into " + LIKED_SONG_TABLE + " values('" +str +"')";

        database.execSQL(addUserToDatabaseSQLCommand);
        database.close();

    }
    public void deleteSongFromDatabaseByID(String str) {

        SQLiteDatabase database = getWritableDatabase();
        String deleteSQLCommand = "delete from " + LIKED_SONG_TABLE + " where " + SONG_KEY + " ='" + str+"' ";
        database.execSQL(deleteSQLCommand);
        database.close();
    }

    public ArrayList<String> returnAllLikedSongs () {
        SQLiteDatabase database = getWritableDatabase();
        String sqlQueryCommand = "Select * from "+ LIKED_SONG_TABLE;
        Cursor cursor = database.rawQuery(sqlQueryCommand, null);

        ArrayList<String> songs = new ArrayList<>();
        while (cursor.moveToNext()) {

            songs.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return songs;
    }
}
