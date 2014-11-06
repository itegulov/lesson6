package com.wibk.rss;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


public class ChannelActivity extends ListActivity {
    private EditText channelEditText;
    private ListView channelListView;
    private RssChannelAdapter channelAdapter;
    public final static String URL_EXTRA = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        channelEditText = (EditText) findViewById(R.id.channelEditText);
        channelListView = (ListView) findViewById(android.R.id.list);
        channelAdapter = new RssChannelAdapter(getBaseContext());
        channelListView.setAdapter(channelAdapter);
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
    }

    public void onAdd(View v) {
        String channelUrl = channelEditText.getText().toString();
        new RssChannelFetcher(channelAdapter, getBaseContext()).execute(channelUrl);
    }
}
