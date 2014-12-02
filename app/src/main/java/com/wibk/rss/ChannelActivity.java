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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ChannelActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String UPDATING_MESSAGE = "Updating";
    private EditText channelEditText;
    private ListView channelListView;
    private RssChannelAdapter channelAdapter;
    private List<RssChannel> channelList = new ArrayList<RssChannel>();
    private Handler handler;
    public final static String URL_EXTRA = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        channelEditText = (EditText) findViewById(R.id.channelEditText);
        channelListView = (ListView) findViewById(android.R.id.list);
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChannelActivity.this, RssActivity.class);
                RssChannelAdapter rssChannelAdapter = (RssChannelAdapter) channelListView.getAdapter();
                RssChannel rssChannel = rssChannelAdapter.getItem(i);
                intent.putExtra(URL_EXTRA, rssChannel.getLink());
                startActivity(intent);
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.d("ChannelActivity", "Message: " + message);
                switch (message.what) {
                    case RssChannelFetchService.UPDATED:
                        getLoaderManager().restartLoader(75436789, null, ChannelActivity.this);
                        stopLoading();
                        break;
                    case RssChannelFetchService.UPDATING:
                        ActionBar actionBar = getActionBar();
                        if (actionBar != null) {
                            actionBar.setSubtitle(UPDATING_MESSAGE);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void beginLoading(String link) {
        RssChannelFetchService.loadChannel(this, link);
    }

    public void stopLoading() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(null);
        }
    }

    public void onAdd(View v) {
        String channelUrl = channelEditText.getText().toString();
        beginLoading(channelUrl);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, RssContentProvider.CONTENT_URI_FEEDS, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        channelList.clear();
        while (cursor.moveToNext()) {
            RssChannel c = DatabaseRssHelper.RssChannelCursor.getRssChannel(cursor);
            channelList.add(c);
        }
        if (channelAdapter == null) {
            channelAdapter = new RssChannelAdapter(getBaseContext());
            channelListView.setAdapter(channelAdapter);
        }
        channelAdapter.clear();
        for (RssChannel rssChannel : channelList) {
            channelAdapter.add(rssChannel);
        }
        channelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        channelAdapter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        RssChannelFetchService.setHandler(handler);
    }

    @Override
    public void onPause() {
        super.onPause();

        RssChannelFetchService.setHandler(null);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(null);
        }
    }
}
