package com.example.tpnote21;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongsDatabase {

    private static FavoriteSongsDatabase instance;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "favorite_songs.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorite_songs";
    private static final String COLUMN_SONG_TITLE = "song_title";

    private FavoriteSongsDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized FavoriteSongsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteSongsDatabase(context.getApplicationContext());
        }
        return instance;
    }


    public void addFavoriteSong(String songTitle) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_TITLE, songTitle);

        database.insert(TABLE_NAME, null, values);
    }

    public void removeFavoriteSong(Context applicationContext, String songTitle) {
        database = dbHelper.getWritableDatabase();

        String selection = COLUMN_SONG_TITLE + "=?";
        String[] selectionArgs = { songTitle };

        database.delete(TABLE_NAME, selection, selectionArgs);
    }

    public List<String> getFavoriteSongs() {
        List<String> favoriteSongs = new ArrayList<>();

        database = dbHelper.getReadableDatabase();

        String[] projection = { COLUMN_SONG_TITLE };

        Cursor cursor = database.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));
            favoriteSongs.add(songTitle);
        }

        cursor.close();

        return favoriteSongs;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SONG_TITLE + " TEXT)";

            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop the table if it exists and create a new one
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
