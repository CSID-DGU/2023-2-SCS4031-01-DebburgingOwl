package com.example.logintest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DailyMissionActivity extends AppCompatActivity {

    TextView missonStamp1 = (TextView) findViewById(R.id.missonStamp1);
    TextView missonStamp2 = (TextView) findViewById(R.id.missonStamp2);
    TextView missonStamp3 = (TextView) findViewById(R.id.missonStamp3);
    TextView missonStamp4 = (TextView) findViewById(R.id.missonStamp4);
    TextView missonStamp5 = (TextView) findViewById(R.id.missonStamp5);
    TextView missonStamp6 = (TextView) findViewById(R.id.missonStamp6);
    TextView missonStamp7 = (TextView) findViewById(R.id.missonStamp7);
    TextView missonRewardContent = (TextView) findViewById(R.id.missonRewardContent);
    Button btnDay1 = (Button) findViewById(R.id.btnDay1);
    Button btnDay2 = (Button) findViewById(R.id.btnDay2);
    Button btnDay3 = (Button) findViewById(R.id.btnDay3);
    Button btnDay4 = (Button) findViewById(R.id.btnDay4);
    Button btnDay5 = (Button) findViewById(R.id.btnDay5);
    Button btnDay6 = (Button) findViewById(R.id.btnDay6);
    Button btnDay7 = (Button) findViewById(R.id.btnDay7);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission);
    }
}
