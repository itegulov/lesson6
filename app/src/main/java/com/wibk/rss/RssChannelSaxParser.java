package com.wibk.rss;

import android.util.Log;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssChannelSaxParser extends DefaultHandler {
    public static final String LOG_TAG = RssFeedSaxParser.class.getSimpleName();
    private enum Tag {TITLE, DESCRIPTION, LINK, CHANNEL, ITEM}
    private final static Map<String, Tag> NAME_TO_TAG;

    static {
        Map<String, Tag> map = new HashMap<String, Tag>();
        map.put("entry", Tag.ITEM);
        map.put("item", Tag.ITEM);
        map.put("description", Tag.DESCRIPTION);
        map.put("subtitle", Tag.DESCRIPTION);
        map.put("title", Tag.TITLE);
        map.put("link", Tag.LINK);
        map.put("channel", Tag.CHANNEL);
        map.put("feed", Tag.CHANNEL);
        NAME_TO_TAG = Collections.unmodifiableMap(map);
    }

    public static RssChannel parseInputSource(InputSource is) throws SAXException {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            RssChannelSaxParser handler = new RssChannelSaxParser();
            saxParser.parse(is, handler);
            return handler.getRssChannel();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error occurred due to invalid parser configuration");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error occurred during parsing");
        }
        return new RssChannel();
    }

    private String title = null;
    private String link = null;
    private String description = null;
    private boolean bTitle = false;
    private boolean bItem = false;
    private boolean bLink = false;
    private boolean bDescription = false;
    private RssChannel rssChannel;

    public RssChannel getRssChannel() {
        return rssChannel;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (!NAME_TO_TAG.containsKey(qName.toLowerCase())) {
            return;
        }
        switch (NAME_TO_TAG.get(qName.toLowerCase())) {
            case ITEM:
                bItem = true;
                break;
            case TITLE:
                bTitle = true;
                break;
            case DESCRIPTION:
                bDescription = true;
                break;
            case LINK:
                bLink = true;
                if (attributes.getValue("href") != null) {
                    link = attributes.getValue("href");
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!NAME_TO_TAG.containsKey(qName.toLowerCase())) {
            return;
        }
        if (NAME_TO_TAG.get(qName.toLowerCase()) == Tag.CHANNEL) {
            rssChannel = new RssChannel(title, link, description, -1);
        }
        switch (NAME_TO_TAG.get(qName.toLowerCase())) {
            case ITEM:
                bItem = false;
                break;
            case TITLE:
                bTitle = false;
                break;
            case DESCRIPTION:
                bDescription = false;
                break;
            case LINK:
                bLink = false;
                break;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (bTitle && !bItem) {
            title = (title == null ? "" : title) + new String(ch, start, length);
        } else if (bDescription && !bItem) {
            description = (description == null ? "" : description) + new String(ch, start, length);
        } else if (bLink && !bItem) {
            link = new String(ch, start, length);
        }
    }
}