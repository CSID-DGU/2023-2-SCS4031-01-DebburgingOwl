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

        // Intent로부터 전달받은 Notice 객체를 가져옴
        Notice selectedNotice = (Notice) getIntent().getSerializableExtra("selectedNotice");

        // Notice 객체의 데이터를 화면에 표시
        if (selectedNotice != null) {
            title.setText(selectedNotice.getTitle());
            content.setText(selectedNotice.getContent());
        }
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
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(NoticeBoardDetailActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(NoticeBoardDetailActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(NoticeBoardDetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(NoticeBoardDetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else {
                return false;
            }
        });
    }
    // 다른 액티비티로 이동하는 메서드
    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(NoticeBoardDetailActivity.this, cls);
        startActivity(intent);
    }
}