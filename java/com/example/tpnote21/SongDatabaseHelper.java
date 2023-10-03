package com.example.tpnote21;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
public class SongDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "song_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_DURATION = "duration";

    public SongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_ARTIST + " TEXT,"
                + COLUMN_DURATION + " INTEGER"
                // Add more columns as per your schema
                + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    public void insertSongs(List<Song> songs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Song song : songs) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, song.getTitle());
                values.put(COLUMN_ARTIST, song.getArtist());
                values.put(COLUMN_DURATION, song.getDuration());
                // Add more columns as per your schema

                db.insert(TABLE_SONGS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));
                // Add more columns as per your schema


                Song song = new Song(id, title, artist, duration);
                songs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }
}
