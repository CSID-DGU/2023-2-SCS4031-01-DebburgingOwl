package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_edit);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(MyPageEditActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(MyPageEditActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(MyPageEditActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(MyPageEditActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }
}