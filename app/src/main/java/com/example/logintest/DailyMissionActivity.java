package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DailyMissionActivity extends AppCompatActivity {

    TextView missonRewardContent;
    Button workoutMission;
    Button coffeeMission;
    Button missionBtn3;
    Button missionBtn4;
    Button missionBtn5;
    Button missionBtn6;
    Button missionBtn7;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission);

        missonRewardContent = (TextView) findViewById(R.id.missonRewardContent);
        workoutMission = (Button) findViewById(R.id.missionBtn1);
        coffeeMission = (Button) findViewById(R.id.missionBtn2);
        missionBtn3 = (Button) findViewById(R.id.missionBtn3);
        missionBtn4 = (Button) findViewById(R.id.missionBtn4);
        missionBtn5 = (Button) findViewById(R.id.missionBtn5);
        missionBtn6 = (Button) findViewById(R.id.missionBtn6);
        missionBtn7 = (Button) findViewById(R.id.missionBtn7);

        workoutMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WorkoutActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, WorkoutActivity.class);
                startActivity(intent);
            }
        });

        coffeeMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CoffeeMissionActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, CoffeeMissionActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DailyMissionActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(DailyMissionActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(DailyMissionActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DailyMissionActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

    }
}
