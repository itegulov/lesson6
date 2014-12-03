package com.wibk.rss;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;

public class DatabaseRssHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "rss_fetcher_data";
    public static final int DATABASE_VERSION = 2;

    //Channels table
    public static final String CHANNELS_TABLE_NAME = "channels";
    public static final String CHANNELS_KEY_ID = _ID;
    public static final String CHANNELS_KEY_TITLE = "title";
    public static final String CHANNELS_KEY_DESCRIPTION = "description";
    public static final String CHANNELS_KEY_LINK = "link";

    public static final String CHANNELS_TABLE_CREATE_REQUEST = "CREATE TABLE " + CHANNELS_TABLE_NAME + " (" +
            CHANNELS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CHANNELS_KEY_TITLE + " TEXT, " +
            CHANNELS_KEY_DESCRIPTION + " TEXT, " +
            CHANNELS_KEY_LINK + " TEXT)";


    //Items table
    public static final String ITEMS_TABLE_NAME = "items";
    public static final String ITEMS_KEY_ID = _ID;
    public static final String ITEMS_KEY_TITLE = "title";
    public static final String ITEMS_KEY_DESCRIPTION = "description";
    public static final String ITEMS_KEY_LINK = "link";
    public static final String ITEMS_KEY_DATE = "date";
    public static final String ITEMS_CHANNEL_ID = "channel_id";

    public static final String ITEMS_TABLE_CREATE_REQUEST = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
            ITEMS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEMS_KEY_TITLE + " TEXT, " +
            ITEMS_KEY_DESCRIPTION + " TEXT, " +
            ITEMS_KEY_LINK + " TEXT, " +
            ITEMS_KEY_DATE + " INTEGER, " +
            ITEMS_CHANNEL_ID + " INTEGER, " +
            "FOREIGN KEY (" + ITEMS_CHANNEL_ID + ") REFERENCES " +
            CHANNELS_TABLE_NAME + "(" + CHANNELS_KEY_ID + ") ON DELETE CASCADE, " +
            "UNIQUE (" + ITEMS_KEY_LINK + ") ON CONFLICT IGNORE)";


    public DatabaseRssHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CHANNELS_TABLE_CREATE_REQUEST);
        sqLiteDatabase.execSQL(ITEMS_TABLE_CREATE_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHANNELS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
    }

    public static class RssChannelCursor extends CursorWrapper {

        public RssChannelCursor(Cursor cursor) {
            super(cursor);
        }

        public static RssChannel getRssChannel(Cursor cursor) {
            return new RssChannel(cursor.getString(cursor.getColumnIndex(CHANNELS_KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(CHANNELS_KEY_LINK)),
                    cursor.getString(cursor.getColumnIndex(CHANNELS_KEY_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndex(CHANNELS_KEY_ID)));
        }

    }

    public static class RssItemCursor extends CursorWrapper {

        public RssItemCursor(Cursor cursor) {
            super(cursor);
        }

        public static RssItem getRssItem(Cursor cursor) {
            return new RssItem(cursor.getString(cursor.getColumnIndex(ITEMS_KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(ITEMS_KEY_DESCRIPTION)),
                    new Date(cursor.getLong(cursor.getColumnIndex(ITEMS_KEY_DATE))),
                    cursor.getString(cursor.getColumnIndex(ITEMS_KEY_LINK)));
        }

    }
}
