package com.wibk.rss;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RssChannelFetcher {

    public static RssChannel fetchRssChannel(String link) {
        Uri uri = Uri.parse(link);
        try {
            URL url = new URL(uri.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            String contentType = httpURLConnection.getContentType();
            String encoding = "utf-8";
            if (contentType.contains("charset=")) {
                int index = contentType.indexOf("charset=") + "charset=".length();
                int endIndex = index;
                while (endIndex < contentType.length() && contentType.charAt(endIndex) != ' ' &&
                        contentType.charAt(endIndex) != ';') {
                    endIndex++;
                }
                Log.d("RssChannelFetcher", contentType);
                Log.d("RssChannelFetcher", contentType.substring(index, endIndex));
                encoding = contentType.substring(index, endIndex);
            }
            Reader reader = new InputStreamReader(inputStream, encoding);

            InputSource is = new InputSource(reader);
            is.setEncoding(encoding);
            RssChannel rssChannel = RssChannelSaxParser.parseInputSource(is);
            rssChannel.setLink(link);
            return rssChannel;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
