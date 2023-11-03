package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class PodcastDetailActivity extends AppCompatActivity {

    WebView podcastDetailBtn = (WebView) findViewById(R.id.podcastDetailBtn);
    TextView podcastDayText = (TextView) findViewById(R.id.podcastDayText);
    Button podcastBackBtn = (Button) findViewById(R.id.podcastBackBtn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);
    }
}