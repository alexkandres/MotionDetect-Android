package com.example.android.motiondetect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class LiveActivityMainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_main);

        String data = "";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
            data = extras.getString("Camera Name");

        webView = (WebView) findViewById(R.id.liveViewId);
        webView.loadUrl(data);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
    }
}
