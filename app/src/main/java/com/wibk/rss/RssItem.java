package com.wibk.rss;

import android.content.ContentValues;
import android.provider.ContactsContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RssItem {
    private String title;
    private String description;
    private Date date;
    private String link;
    private RssChannel channel;
    private long id = -1;
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    private static final DateFormat secondDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static final DateFormat thirdDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
    private static final DateFormat fourthDateFormat = new SimpleDateFormat("EEE, d MMM", Locale.US);

    public RssItem() {

    }

    public RssItem(String title, String description, Date date, String link) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String dateString) {
        try {
            setDate(dateFormat.parse(dateString));
        } catch (ParseException e) {
            try {
                setDate(secondDateFormat.parse(dateString));
            } catch (ParseException e1) {
                try {
                    setDate(thirdDateFormat.parse(dateString));
                } catch (ParseException e2) {
                    try {
                        setDate(fourthDateFormat.parse(dateString));
                    } catch (ParseException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public RssChannel getChannel() {
        return channel;
    }

    public void setChannel(RssChannel channel) {
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseRssHelper.ITEMS_KEY_LINK, link);
        if (date != null) {
            cv.put(DatabaseRssHelper.ITEMS_KEY_DATE, date.getTime());
        } else {
            cv.put(DatabaseRssHelper.ITEMS_KEY_DATE, new Date().getTime());
        }
        cv.put(DatabaseRssHelper.ITEMS_KEY_DESCRIPTION, description);
        cv.put(DatabaseRssHelper.ITEMS_KEY_TITLE, title);
        return cv;
    }

    @Override
    public String toString() {
        return "RssItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", link='" + link + '\'' +
                '}';
    }
}
