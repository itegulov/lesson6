package com.wibk.rss;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RssChannelAdapter extends BaseAdapter {
    private List<RssChannel> rssChannelList;
    private LayoutInflater inflater;

    public RssChannelAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        rssChannelList = new ArrayList<RssChannel>();
    }

    public void add(RssChannel rssChannel) {
        rssChannelList.add(rssChannel);
    }

    public void clear() {
        rssChannelList.clear();
    }

    public void remove(int pos) {
        rssChannelList.remove(pos);
    }

    @Override
    public int getCount() {
        return rssChannelList.size();
    }

    @Override
    public RssChannel getItem(int i) {
        return rssChannelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            v = inflater.inflate(R.layout.rss_item_layout, viewGroup, false);
        } else {
            v = view;
        }
        TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);
        if (rssChannelList.get(i) != null) {
            titleTextView.setText(rssChannelList.get(i).getTitle());
            descriptionTextView.setText(rssChannelList.get(i).getDescription());
        }

        if (i % 2 == 0) {
            v.setBackgroundColor(Color.LTGRAY);
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
