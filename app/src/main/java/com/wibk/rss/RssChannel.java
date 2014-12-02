package com.wibk.rss;

import android.content.ContentValues;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public class RssChannel {
    private String title;
    private String link;
    private String description;
    private long id = -1;
    private final List<RssItem> rssItemList;

    public RssChannel(InputSource is) throws SAXException {
        rssItemList = RssFeedSaxParser.parseInputSource(is);
    }

    public List<RssItem> getRssItemList() {
        return rssItemList;
    }

    public RssChannel() {
        rssItemList = new ArrayList<RssItem>();
    }

    public RssChannel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
        rssItemList = new ArrayList<RssItem>();
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseRssHelper.CHANNELS_KEY_LINK, link);
        cv.put(DatabaseRssHelper.CHANNELS_KEY_DESCRIPTION, description);
        cv.put(DatabaseRssHelper.CHANNELS_KEY_TITLE, title);
        return cv;
    }
}
