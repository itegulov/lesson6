package com.wibk.rss;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.List;

public class RssFeed {
    private final List<RssItem> rssItemList;

    public RssFeed(InputSource is) throws SAXException {
        rssItemList = RssFeedSaxParser.parseInputSource(is);
    }

    public List<RssItem> getRssItemList() {
        return rssItemList;
    }
}
