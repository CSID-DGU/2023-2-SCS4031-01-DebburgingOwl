package com.example.logintest;
import android.content.Intent;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class WorkoutActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView stepCounterTextView;
    private Button missionCompleteButton;
    private ProgressBar stepProgressBar;
    private int steps = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Button btnIncreaseSteps = findViewById(R.id.btnIncreaseSteps);

        stepCounterTextView = findViewById(R.id.stepCounter);

        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        btnIncreaseSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps += 10; // 스텝을 10씩 증가
                updateStepCounter(); // 스텝 업데이트 메서드 호출
            }
        });

        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (steps >= 1000) {
                    Toast.makeText(WorkoutActivity.this, "미션 완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(WorkoutActivity.this, "아직 " + (1000 - steps) + " 걸음이 부족해요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(WorkoutActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(WorkoutActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(WorkoutActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(WorkoutActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(WorkoutActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });


    }

    private void updateStepCounter() {
        stepCounterTextView.setText("오늘 " + steps + "보 걸었어요!");
        stepProgressBar.setProgress(steps);
        if (steps >= 1000) {
            missionCompleteButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int) event.values[0];
        stepCounterTextView.setText("Steps: " + steps);
        stepProgressBar.setProgress(steps);
//        if (steps >= 1000) {
//            missionCompleteButton.setEnabled(true);
//
//        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 필요한 경우 여기에 코드를 추가합니다.
    }
}

