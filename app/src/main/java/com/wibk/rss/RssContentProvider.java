package com.wibk.rss;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class RssContentProvider extends ContentProvider {
    private static final String AUTHORITY = RssContentProvider.class.getName();
    private static final String PATH_TO_CHANNELS = DatabaseRssHelper.CHANNELS_TABLE_NAME;
    public static final Uri CONTENT_URI_FEEDS = Uri.parse("content://" + AUTHORITY + "/" + PATH_TO_CHANNELS);
    private static final String PATH_TO_ITEMS = DatabaseRssHelper.ITEMS_TABLE_NAME;
    public static final Uri CONTENT_URI_POSTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_TO_ITEMS);
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static final String CHANNELS_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + DatabaseRssHelper.CHANNELS_TABLE_NAME;
    static final String POSTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." +
            DatabaseRssHelper.ITEMS_TABLE_NAME;

    private enum TableType {CHANNELS, ITEMS, ITEM, CHANNEL}

    static {
        //Get all channels
        URI_MATCHER.addURI(AUTHORITY, PATH_TO_CHANNELS, TableType.CHANNELS.ordinal());
        //Get item by channel id
        URI_MATCHER.addURI(AUTHORITY, PATH_TO_ITEMS, TableType.ITEMS.ordinal());
        //Get item by channel id
        URI_MATCHER.addURI(AUTHORITY, PATH_TO_ITEMS + "/#", TableType.ITEM.ordinal());
        //Get channel by it's id
        URI_MATCHER.addURI(AUTHORITY, PATH_TO_CHANNELS + "/#", TableType.CHANNEL.ordinal());
    }

    private DatabaseRssHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseRssHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        try {
            TableType tableType = TableType.values()[URI_MATCHER.match(uri)];
            switch (tableType) {
                case CHANNEL:
                    queryBuilder.setTables(DatabaseRssHelper.CHANNELS_TABLE_NAME);
                    queryBuilder.appendWhere(
                            DatabaseRssHelper.CHANNELS_KEY_ID + "=" + uri.getLastPathSegment());
                    break;
                case ITEM:
                    queryBuilder.setTables(DatabaseRssHelper.ITEMS_TABLE_NAME);
                    queryBuilder.appendWhere(DatabaseRssHelper.ITEMS_KEY_ID + "=" + uri.getLastPathSegment());
                    break;
                case ITEMS:
                    queryBuilder.setTables(DatabaseRssHelper.ITEMS_TABLE_NAME);
                    break;
                case CHANNELS:
                    queryBuilder.setTables(DatabaseRssHelper.CHANNELS_TABLE_NAME);
                    break;
            }
            Cursor cursor = queryBuilder.query(dbHelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        try {
            TableType tableType = TableType.values()[URI_MATCHER.match(uri)];
            switch (tableType) {
                case CHANNELS:
                case CHANNEL:
                    return CHANNELS_CONTENT_TYPE;
                case ITEMS:
                case ITEM:
                    return POSTS_CONTENT_TYPE;
            }
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        try {
            TableType tableType = TableType.values()[URI_MATCHER.match(uri)];
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            long id = -1;
            switch (tableType) {
                case CHANNEL:
                    //Do nothing?
                    return null;
                case ITEM:
                    contentValues.put(DatabaseRssHelper.ITEMS_KEY_ID, uri.getLastPathSegment());
                    id = database.insert(DatabaseRssHelper.ITEMS_TABLE_NAME, null, contentValues);
                    break;
                case ITEMS:
                    id = database.insert(DatabaseRssHelper.ITEMS_TABLE_NAME, null, contentValues);
                    break;
                case CHANNELS:
                    id = database.insert(DatabaseRssHelper.CHANNELS_TABLE_NAME, null, contentValues);
                    break;
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.withAppendedPath(uri, Long.toString(id));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            TableType tableType = TableType.values()[URI_MATCHER.match(uri)];
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            int deleted = 0;
            switch (tableType) {
                case CHANNELS:
                    deleted = database.delete(DatabaseRssHelper.CHANNELS_TABLE_NAME, selection, selectionArgs);
                    break;
                case ITEM:
                    String id = uri.getLastPathSegment();
                    deleted = database.delete(DatabaseRssHelper.ITEMS_TABLE_NAME, DatabaseRssHelper.ITEMS_KEY_ID + "=" + id + " and " + selection, selectionArgs);
                    break;
                case ITEMS:
                    deleted = database.delete(DatabaseRssHelper.ITEMS_TABLE_NAME, selection, selectionArgs);
                    break;
                case CHANNEL:
                    id = uri.getLastPathSegment();
                    deleted = database.delete(DatabaseRssHelper.CHANNELS_TABLE_NAME, DatabaseRssHelper.CHANNELS_KEY_ID + "=" + id + " and " + selection, selectionArgs);
                    break;
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return deleted;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            TableType tableType = TableType.values()[URI_MATCHER.match(uri)];
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            int updated = 0;
            switch (tableType) {
                case CHANNEL:
                    updated = database.update(DatabaseRssHelper.CHANNELS_TABLE_NAME, values, selection, selectionArgs);
                    break;
                case ITEM:
                    String id = uri.getLastPathSegment();
                    updated = database.update(DatabaseRssHelper.ITEMS_TABLE_NAME, values, DatabaseRssHelper.ITEMS_KEY_ID + "=" + id + " and " + selection, selectionArgs);
                    break;
                case ITEMS:
                    updated = database.update(DatabaseRssHelper.ITEMS_TABLE_NAME, values, selection, selectionArgs);
                    break;
                case CHANNELS:
                    id = uri.getLastPathSegment();
                    updated = database.update(DatabaseRssHelper.CHANNELS_TABLE_NAME, values, DatabaseRssHelper.CHANNELS_KEY_ID + "=" + id + " and " + selection, selectionArgs);
                    break;
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return updated;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
    }
}
