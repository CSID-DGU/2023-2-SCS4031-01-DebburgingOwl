package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.os.Handler;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Handler handler;
    ViewPager2 viewPager;
    Runnable slideRunnable;
    // 상수 추가
    private static final long DELAY_MS = 3000;
    private static final long PERIOD_MS = 3000;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(MainActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(MainActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });


        viewPager = findViewById(R.id.viewPager);

        List<String> quotesList = Arrays.asList(
                "명언 1",
                "명언 2",
                "명언 3"
        );

        QuotesAdapter adapter = new QuotesAdapter(quotesList);
        viewPager.setAdapter(adapter);

        // 자동 슬라이드 코드 시작
        handler = new Handler(Looper.getMainLooper());
        slideRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                } else {
                    viewPager.setCurrentItem(0, true); // 마지막 페이지에서 첫 페이지로 롤백
                }
                handler.postDelayed(this, PERIOD_MS);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(slideRunnable, DELAY_MS); // 앱이 화면에 보일 때 자동 슬라이드 시작
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(slideRunnable); // 앱이 화면에서 벗어날 때 자동 슬라이드 중지
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // 2초 동안 대기
    }



}

