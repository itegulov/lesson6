package com.wibk.rss;

import android.util.Log;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssFeedSaxParser extends DefaultHandler {
    public static final String LOG_TAG = RssFeedSaxParser.class.getSimpleName();
    private enum Tag {ITEM, DESCRIPTION, DATE, LINK, TITLE}
    private final static Map<String, Tag> NAME_TO_TAG;

    static {
        Map<String, Tag> map = new HashMap<String, Tag>();
        map.put("entry", Tag.ITEM);
        map.put("item", Tag.ITEM);
        map.put("description", Tag.DESCRIPTION);
        map.put("summary", Tag.DESCRIPTION);
        map.put("title", Tag.TITLE);
        map.put("pubdate", Tag.DATE);
        map.put("updated", Tag.DATE);
        map.put("link", Tag.LINK);
        NAME_TO_TAG = Collections.unmodifiableMap(map);
    }

    public static List<RssItem> parseInputSource(InputSource is) throws SAXException {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            RssFeedSaxParser handler = new RssFeedSaxParser();
            saxParser.parse(is, handler);
            return handler.getRssItemList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error occurred due to invalid parser configuration");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error occurred during parsing");
        }
        return new ArrayList<RssItem>();
    }

    public List<RssItem> getRssItemList() {
        return rssItemList;
    }

    private List<RssItem> rssItemList = new ArrayList<RssItem>();

    private boolean bTitle = false;
    private boolean bDescription = false;
    private boolean bDate = false;
    private boolean bItem = false;
    private boolean bLink = false;

    private RssItem rssItem;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (!NAME_TO_TAG.containsKey(qName.toLowerCase())) {
            return;
        }

        switch (NAME_TO_TAG.get(qName.toLowerCase())) {
            case ITEM:
                rssItem = new RssItem();
                bItem = true;
                break;
        }

        if (bItem) {
            switch (NAME_TO_TAG.get(qName.toLowerCase())) {
                case DESCRIPTION:
                    bDescription = true;
                    break;
                case TITLE:
                    bTitle = true;
                    break;
                case DATE:
                    bDate = true;
                    break;
                case LINK:
                    bLink = true;
                    if (attributes.getValue("href") == null) {
                        rssItem.setLink(attributes.getValue("href"));
                    }
                    break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!NAME_TO_TAG.containsKey(qName.toLowerCase())) {
            return;
        }
        switch (NAME_TO_TAG.get(qName.toLowerCase())) {
            case ITEM:
                rssItemList.add(rssItem);
                bItem = false;
                break;
        }

        if (bItem) {
            switch (NAME_TO_TAG.get(qName.toLowerCase())) {
                case DESCRIPTION:
                    bDescription = false;
                    break;
                case TITLE:
                    bTitle = false;
                    break;
                case DATE:
                    bDate = false;
                    break;
                case LINK:
                    bLink = false;
                    break;
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (bTitle) {
            rssItem.setTitle((rssItem.getTitle() == null ? "" : rssItem.getTitle()) + new String(ch, start, length));
            bTitle = false;
        } else if (bDescription) {
            rssItem.setDescription((rssItem.getDescription() == null ? "" : rssItem.getDescription()) + new String(ch, start, length));
        } else if (bDate) {
            rssItem.setDate(new String(ch, start, length));
        } else if (bLink) {
            rssItem.setLink(new String(ch, start, length));
        }
    }
}