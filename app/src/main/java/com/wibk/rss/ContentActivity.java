package com.wibk.rss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


public class ContentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Intent intent = getIntent();
        String link = intent.getStringExtra(RssActivity.LINK_EXTRA);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(link);
    }
}
