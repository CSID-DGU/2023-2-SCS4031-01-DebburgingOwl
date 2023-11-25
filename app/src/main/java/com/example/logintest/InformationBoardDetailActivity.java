package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InformationBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton bookmarkBtn;
    TextView title;
    TextView content;
    ImageView infoUserPicture;
    TextView infoUserType;
    TextView infoUserNickname;
    Button mentorInfoBtn;
    BottomSheetFragment bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_board_detail);

        backBtn = findViewById(R.id.infoBackBtn);
        bookmarkBtn = findViewById(R.id.infoBookmarkBtn);
        title = findViewById(R.id.infoTitle);
        content = findViewById(R.id.infoContent);
        infoUserPicture = findViewById(R.id.infoUserPicture);
        infoUserType = findViewById(R.id.infoUserType);
        infoUserNickname = findViewById(R.id.infoUserNickname);
        mentorInfoBtn = findViewById(R.id.mentorInfoBtn);

        // Information 객체를 받아오는 코드
        Intent intent = getIntent();
        Information information = (Information) intent.getSerializableExtra("selectedItem");

        // 가져온 정보를 TextView에 설정
        title.setText(information.getTitle());
        content.setText(information.getContent());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 액티비티를 종료하여 뒤로가기 역할
                finish();
            }
        });

        mentorInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // mentorInfoBtn 클릭 이벤트 설정
            public void onClick(View view) {
                // BottomSheetFragment 인스턴스 생성
                bottomSheet = new BottomSheetFragment();
                // BottomSheetFragment를 화면에 표시
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.board);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent navigationIntent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, BoardActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.community) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, CommunityActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.home) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, MainActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, DailyMissionActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.mypage) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, MyPageActivity.class);
                startActivity(navigationIntent);
                return true;
            } else {
                return false;
            }
        });
    }
}