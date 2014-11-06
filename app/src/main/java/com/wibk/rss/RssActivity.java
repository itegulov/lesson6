package com.wibk.rss;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Locale;


public class RssActivity extends ListActivity {
    public static final String LINK_EXTRA = "Link";
    private RssItemAdapter rssItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale.setDefault(Locale.US);
        setContentView(R.layout.activity_rss);
        ListView listView = (ListView) findViewById(android.R.id.list);
        rssItemAdapter = new RssItemAdapter(getBaseContext(), this);
        listView.setAdapter(rssItemAdapter);
        String url = getIntent().getStringExtra(ChannelActivity.URL_EXTRA);
        new RssFeedFetcher(rssItemAdapter, getBaseContext()).execute(url);
    }

    public void onItemClick(int number) {
        Intent intent = new Intent(this, ContentActivity.class);
        RssItem rssItem = rssItemAdapter.getItem(number);
        intent.putExtra(LINK_EXTRA, rssItem.getLink());
        startActivity(intent);
    }
}
