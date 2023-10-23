package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DailyMissionDetailActivity extends AppCompatActivity {

    TextView missionDay = (TextView) findViewById(R.id.missionDay);
    TextView missionName = (TextView) findViewById(R.id.missionName);
    TextView missionContent = (TextView) findViewById(R.id.missionContent);
    Button missionConfirmBtn = (Button) findViewById(R.id.missionConfirmBtn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission_detail);
    }
}