package com.wibk.rss;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class RssItemsFetchService extends IntentService {
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
        if (id == -1) {
            if (handler != null) {
                handler.obtainMessage(ERROR).sendToTarget();
            }
            return;
        }

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

    private boolean update(RssChannel rssChannel, long id) {
        if (rssChannel == null) {
            return false;
        }
        getContentResolver().delete(RssContentProvider.CONTENT_URI_POSTS,
                DatabaseRssHelper.ITEMS_CHANNEL_ID + " = " + id + "", null);
        for (RssItem rssItem : rssChannel.getRssItemList()) {
            Log.d("RssItemsFetchService", "Updating " + rssItem.toString());
            ContentValues cv = rssItem.getContentValues();
            cv.put(DatabaseRssHelper.ITEMS_CHANNEL_ID, id);
            getContentResolver().insert(RssContentProvider.CONTENT_URI_POSTS, cv);
        }
        return true;
    }
}
