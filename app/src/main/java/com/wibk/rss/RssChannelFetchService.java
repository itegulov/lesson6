package com.wibk.rss;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssChannelFetchService extends IntentService {

    public RssChannelFetchService() {
        super(RssChannelFetchService.class.getSimpleName());
    }

    private static final List<String> tasks = new ArrayList<String>();
    private static Handler handler;
    //Handler codes:
    public static final int UPDATED = 0;
    public static final int UPDATING = 1;
    public static final int ERROR = -1;

    public static void loadChannel(Context context, String link) {
        Log.d("RssChannelFetchService", "Want to load: " + link);
        context.startService(new Intent(context, RssChannelFetchService.class).putExtra("link", link));
        Log.d("RssChannelFetchService", "Yep: " + link);
    }

    public static void setHandler(Handler handler) {
        RssChannelFetchService.handler = handler;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("RssChannelFetchService", "Start loading: " + startId);
        String link = intent.getStringExtra("link");
        if (!tasks.contains(link)) {
            tasks.add(link);
            super.onStart(intent, startId);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String link = intent.getStringExtra("link");
        Log.d("RssChannelFetchService", "Loading: " + link);
        if (handler != null) {
            //We began updating
            handler.obtainMessage(UPDATING).sendToTarget();
        }

        RssChannel weatherData = RssChannelFetcher.fetchRssChannel(link);
        if (update(weatherData)) {
            if (handler != null) {
                //We successfully updated the database
                handler.obtainMessage(UPDATED).sendToTarget();
            }
        } else {
            if (handler != null) {
                //Error has occurred
                handler.obtainMessage(ERROR).sendToTarget();
            }
        }
        tasks.remove(0); //Proceeded one task
    }

    private boolean update(RssChannel rssChannel) {
        if (rssChannel == null) {
            return false;
        }

        getContentResolver().delete(RssContentProvider.CONTENT_URI_FEEDS,
                DatabaseRssHelper.CHANNELS_KEY_LINK + " = '" + rssChannel.getLink() + "'", null);
        getContentResolver().insert(RssContentProvider.CONTENT_URI_FEEDS, rssChannel.getContentValues());
        return true;
    }
}
