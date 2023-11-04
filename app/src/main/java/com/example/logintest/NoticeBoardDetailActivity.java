package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 액티비티를 종료하여 뒤로가기
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.board);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(NoticeBoardDetailActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(NoticeBoardDetailActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(NoticeBoardDetailActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(NoticeBoardDetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(NoticeBoardDetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }
}