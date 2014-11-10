package com.wibk.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RssDBAdapter {
    private DatabaseRssHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    private final String[] channelColumns = {DatabaseRssHelper.CHANNELS_KEY_ID, DatabaseRssHelper.CHANNELS_KEY_TITLE,
            DatabaseRssHelper.CHANNELS_KEY_DESCRIPTION, DatabaseRssHelper.CHANNELS_KEY_LINK};
    private final String[] itemsColumns = {DatabaseRssHelper.ITEMS_KEY_ID, DatabaseRssHelper.ITEMS_KEY_TITLE,
            DatabaseRssHelper.ITEMS_KEY_DESCRIPTION, DatabaseRssHelper.ITEMS_KEY_LINK, DatabaseRssHelper.ITEMS_KEY_DATE,
            DatabaseRssHelper.ITEMS_CHANNEL_ID};

    public RssDBAdapter(Context context) {
        this.context = context;
    }

    public void openConnection() {
        dbHelper = new DatabaseRssHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void closeConnection() {
        dbHelper.close();
    }

    public long addChannel(RssChannel channel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseRssHelper.CHANNELS_KEY_TITLE, channel.getTitle());
        values.put(DatabaseRssHelper.CHANNELS_KEY_DESCRIPTION, channel.getDescription());
        values.put(DatabaseRssHelper.CHANNELS_KEY_LINK, channel.getLink());
        long id = database.insert(DatabaseRssHelper.CHANNELS_TABLE_NAME, null, values);
        channel.setId(id);
        return id;
    }

    public long addItem(RssItem item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseRssHelper.ITEMS_KEY_TITLE, item.getTitle());
        values.put(DatabaseRssHelper.ITEMS_KEY_DESCRIPTION, item.getDescription());
        values.put(DatabaseRssHelper.ITEMS_KEY_LINK, item.getLink());
        values.put(DatabaseRssHelper.ITEMS_KEY_DATE, item.getDate().toString());
        values.put(DatabaseRssHelper.ITEMS_CHANNEL_ID, item.getChannel().getId());
        long id = database.insert(DatabaseRssHelper.ITEMS_TABLE_NAME, null, values);
        item.setId(id);
        return id;
    }

    public Cursor getAllChannels() {
        return database.query(DatabaseRssHelper.CHANNELS_TABLE_NAME, channelColumns,
                null, null, null, null, null);
    }

    public RssChannel getChannel(long id) {
        Cursor cursor = database.query(DatabaseRssHelper.CHANNELS_TABLE_NAME, channelColumns,
                DatabaseRssHelper.CHANNELS_KEY_ID + "=" + id, null, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        RssChannel channel = new RssChannel(cursor.getString(cursor.getColumnIndex(DatabaseRssHelper.CHANNELS_KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(DatabaseRssHelper.CHANNELS_KEY_LINK)),
                cursor.getString(cursor.getColumnIndex(DatabaseRssHelper.CHANNELS_KEY_DESCRIPTION)));
        channel.setId(id);
        return channel;
    }

    public Cursor getItems(long channelId) {
        return database.query(DatabaseRssHelper.ITEMS_TABLE_NAME, itemsColumns, DatabaseRssHelper.ITEMS_CHANNEL_ID + "=" + channelId, null, null, null, null);
    }

    public boolean fillRssChannel(RssChannel channel) {
        Cursor c = getItems(channel.getId());
        if (c.getCount() > 0) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                RssItem rssItem = new RssItem();
                rssItem.setTitle(c.getString(c.getColumnIndex(DatabaseRssHelper.ITEMS_KEY_TITLE)));
                rssItem.setDescription(c.getString(c.getColumnIndex(DatabaseRssHelper.ITEMS_KEY_DESCRIPTION)));
                rssItem.setLink(c.getString(c.getColumnIndex(DatabaseRssHelper.ITEMS_KEY_LINK)));
                rssItem.setDate(c.getString(c.getColumnIndex(DatabaseRssHelper.ITEMS_KEY_DATE)));
                rssItem.setId(c.getLong(c.getColumnIndex(DatabaseRssHelper.ITEMS_KEY_ID)));
                rssItem.setChannel(channel);
                channel.getRssItemList().add(rssItem);
            }
            return true;
        } else {
            return false;
        }
    }
}
