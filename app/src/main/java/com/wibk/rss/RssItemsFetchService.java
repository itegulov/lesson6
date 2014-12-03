package com.wibk.rss;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.Date;

public class RssItemsFetchService extends IntentService {
    private static final long UPDATE_INTERVAL = 60L * 1000L;
    private static Handler handler;
    public static final String LINK_EXTRA = "link";
    public static final String ID_EXTRA = "channel_id";
    public static final int UPDATING = 0;
    public static final int UPDATED = 1;
    public static final int ERROR = -1;

    public static void loadItems(Context context, String link, long id) {
        context.startService(new Intent(context, RssItemsFetchService.class).putExtra(LINK_EXTRA, link).putExtra(ID_EXTRA, id));
    }

    public static void setHandler(Handler handler) {
        RssItemsFetchService.handler = handler;
    }

    public RssItemsFetchService() {
        super(RssItemsFetchService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("RssItemsFetchService", "Loading " + intent.toString());
        String link = intent.getStringExtra(LINK_EXTRA);
        long id = intent.getLongExtra(ID_EXTRA, -1);
        Cursor cursor = getContentResolver().query(RssContentProvider.CONTENT_URI_FEEDS, null,
                DatabaseRssHelper.CHANNELS_KEY_ID + " = " + id, null, null);
        cursor.moveToNext();
        if (cursor.isAfterLast()) {
            if (handler != null) {
                handler.obtainMessage(ERROR).sendToTarget();
            }
            return;
        }
        RssChannel channel = DatabaseRssHelper.RssChannelCursor.getRssChannel(cursor);

        if (!needsUpdate(channel)) {
            return;
        }
        channel.setLastUpdate(new Date().getTime());
        getContentResolver().update(RssContentProvider.CONTENT_URI_FEEDS, channel.getContentValues(),
                DatabaseRssHelper.CHANNELS_KEY_ID + " = " + id, null);
        if (handler != null) {
            handler.obtainMessage(UPDATING).sendToTarget();
        }
        RssChannel rssChannel = RssFeedFetcher.fetchRssFeed(link);
        if (update(rssChannel, id)) {
            if (handler != null) {
                handler.obtainMessage(UPDATED).sendToTarget();
            }
        } else {
            if (handler != null) {
                handler.obtainMessage(ERROR).sendToTarget();
            }
        }
    }

    private boolean needsUpdate(RssChannel rssChannel) {
        return new Date().getTime() - rssChannel.getLastUpdate() > UPDATE_INTERVAL;
    }

    private boolean update(RssChannel rssChannel, long id) {
        if (rssChannel == null) {
            return false;
        }
        getContentResolver().delete(RssContentProvider.CONTENT_URI_POSTS,
                DatabaseRssHelper.ITEMS_CHANNEL_ID + " = " + id, null);
        for (RssItem rssItem : rssChannel.getRssItemList()) {
            Log.d("RssItemsFetchService", "Updating " + rssItem.toString() + " " + id);
            ContentValues cv = rssItem.getContentValues();
            cv.put(DatabaseRssHelper.ITEMS_CHANNEL_ID, id);
            Uri uri = getContentResolver().insert(RssContentProvider.CONTENT_URI_POSTS, cv);
            Log.d("RssItemsFetchService", "Inserted " + uri);
        }
        return true;
    }
}
