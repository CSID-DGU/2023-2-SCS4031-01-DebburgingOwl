package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class NoticeBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton bookmarkBtn;
    TextView title;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_detail);

        backBtn = (ImageButton) findViewById(R.id.noticeBackBtn);
        bookmarkBtn =(ImageButton) findViewById(R.id.noticeBookmarkBtn);
        title = (TextView) findViewById(R.id.noticeTitle);
        content = (TextView) findViewById(R.id.noticeContent);
    }
}