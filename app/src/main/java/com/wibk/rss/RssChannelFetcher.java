package com.wibk.rss;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RssChannelFetcher extends AsyncTask<String, Void, RssChannel> {
    private RssChannelAdapter adapter;
    private Context context;
    private String errorMessage;

    public RssChannelFetcher(RssChannelAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    protected RssChannel doInBackground(String... strings) {
        Uri uri = Uri.parse(strings[0]);
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
                int endIndex;
                for (endIndex = index;
                     endIndex < contentType.length() && contentType.charAt(endIndex) != ' ' &&
                             contentType.charAt(endIndex) != ';'; endIndex++);
                Log.d("RssChannelFetcher", contentType);
                Log.d("RssChannelFetcher", contentType.substring(index, endIndex));
                encoding = contentType.substring(index, endIndex);
            }
            Reader reader = new InputStreamReader(inputStream, encoding);

            InputSource is = new InputSource(reader);
            is.setEncoding(encoding);
            RssChannel rssChannel = RssChannelSaxParser.parseInputSource(is);
            rssChannel.setLink(strings[0]);
            return rssChannel;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            errorMessage = "Invalid URL";
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = "No network connection";
        } catch (SAXException e) {
            e.printStackTrace();
            errorMessage = "Couldn't parse RSS from web page";
        }
        return null;
    }

    @Override
    protected void onPostExecute(RssChannel rssChannel) {
        if (rssChannel == null) {
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            adapter.add(rssChannel);
        }
    }
}
