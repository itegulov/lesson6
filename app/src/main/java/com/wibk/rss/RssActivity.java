package com.wibk.rss;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RssActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LINK_EXTRA = "Link";
    public static final String UPDATING_MESSAGE = "Updating";
    private RssItemAdapter rssItemAdapter;
    private String url;
    private ListView listView;
    private long id;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale.setDefault(Locale.US);
        setContentView(R.layout.activity_rss);
        listView = (ListView) findViewById(android.R.id.list);
        url = getIntent().getStringExtra(ChannelActivity.URL_EXTRA);
        id = getIntent().getLongExtra(ChannelActivity.ID_EXTRA, -1);
        Log.d("RssActivity", "Created: " + url + ", " + id);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case RssItemsFetchService.UPDATING:
                        ActionBar actionBar = getActionBar();
                        if (actionBar != null) {
                            actionBar.setSubtitle(UPDATING_MESSAGE);
                        }
                        break;
                    case RssItemsFetchService.UPDATED:
                        getLoaderManager().restartLoader(75436789, null, RssActivity.this);
                        stopLoading();
                        break;
                }
                return true;
            }
        });
        beginLoading();
        getLoaderManager().restartLoader(394723, null, RssActivity.this);
    }

    public void beginLoading() {
        RssItemsFetchService.loadItems(this, url, id);
    }

    public void stopLoading() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(null);
        }
    }

    public void onItemClick(int number) {
        Intent intent = new Intent(this, ContentActivity.class);
        RssItem rssItem = rssItemAdapter.getItem(number);
        intent.putExtra(LINK_EXTRA, rssItem.getLink());
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getBaseContext(), RssContentProvider.CONTENT_URI_POSTS, null, DatabaseRssHelper.ITEMS_CHANNEL_ID + " = " + id,
                null, DatabaseRssHelper.ITEMS_KEY_DATE + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<RssItem> itemList = new ArrayList<RssItem>();
        while (cursor.moveToNext()) {
            RssItem c = DatabaseRssHelper.RssItemCursor.getRssItem(cursor);
            itemList.add(c);
        }
        if (rssItemAdapter == null) {
            rssItemAdapter = new RssItemAdapter(getBaseContext(), this);
            listView.setAdapter(rssItemAdapter);
        }
        rssItemAdapter.clear();
        for (RssItem rssItem : itemList) {
            Log.d("RssActivity", "Add: " + rssItem.toString() + " " + id);
            rssItemAdapter.add(rssItem);
        }
        rssItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        rssItemAdapter = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RssItemsFetchService.setHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RssItemsFetchService.setHandler(null);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(null);
        }
    }
}
