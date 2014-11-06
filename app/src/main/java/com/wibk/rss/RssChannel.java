package com.wibk.rss;

public class RssChannel {
    private String title;
    private String link;
    private String description;

    public RssChannel() {

    }

    public RssChannel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
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
}
