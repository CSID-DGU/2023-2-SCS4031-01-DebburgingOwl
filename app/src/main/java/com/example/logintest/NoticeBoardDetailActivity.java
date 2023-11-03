package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class NoticeBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn = (ImageButton) findViewById(R.id.noticeBackBtn);
    ImageButton bookmarkBtn =(ImageButton) findViewById(R.id.noticeBookmarkBtn);
    TextView title = (TextView) findViewById(R.id.noticeTitle);
    TextView content = (TextView) findViewById(R.id.noticeContent);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_detail);
    }
}