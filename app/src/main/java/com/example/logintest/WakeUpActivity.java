package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class WakeUpActivity extends AppCompatActivity {

    private Button missionCompleteButton;
    private static final String PREFS_NAME = "MissionPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        missionCompleteButton = findViewById(R.id.missionCompleteButton);

        // 날짜 변경 시 미션 완료 상태 초기화
        checkAndResetMissionStatus();

        // 미션 시간 설정 (아침 7시 30분)
        setMissionTime(7, 30);

        // 미션 완료 버튼 클릭 시
        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 디바이스의 현재 시간 가져오기
                Calendar currentTime = Calendar.getInstance();

                // 미션으로 설정한 시간 가져오기
                Calendar missionTime = getMissionTime();

                // 두 시간이 일치하는지 확인
                if (checkMissionTime(currentTime, missionTime)) {
                    // 미션 완료 처리
                    completeMission();
                } else {
                    // 미션 시간이 아니라면 사용자에게 안내 메시지 보여주기
                    Toast.makeText(WakeUpActivity.this, "미션 시간이 아닙니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 미션 시간 설정 메서드
    private void setMissionTime(int hourOfDay, int minute) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 미션으로 설정한 시간을 저장
        Calendar missionTime = Calendar.getInstance();
        missionTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        missionTime.set(Calendar.MINUTE, minute);

        editor.putLong("MissionTime", missionTime.getTimeInMillis());
        editor.apply();
    }

    private boolean checkMissionTime(Calendar currentTime, Calendar missionTime) {
        // 두 시간이 일치하는지 확인하는 로직 추가
        return currentTime.get(Calendar.HOUR_OF_DAY) == missionTime.get(Calendar.HOUR_OF_DAY) &&
                currentTime.get(Calendar.MINUTE) == missionTime.get(Calendar.MINUTE);
    }

    private Calendar getMissionTime() {
        // SharedPreferences를 사용하여 저장된 미션 시간 가져오기
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long missionTimeMillis = preferences.getLong("MissionTime", 0);

        Calendar missionTime = Calendar.getInstance();
        missionTime.setTimeInMillis(missionTimeMillis);

        return missionTime;
    }

    private void completeMission() {
        // 미션 완료 상태를 저장하는 로직
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 현재 날짜를 저장
        Calendar currentDate = Calendar.getInstance();
        editor.putInt("LastDay", currentDate.get(Calendar.DAY_OF_YEAR));
        // 미션 완료 상태를 true로 저장
        editor.putBoolean("MissionCompleted", true);
        editor.apply();

        // 버튼의 텍스트를 '미션 완료'로 변경
        missionCompleteButton.setText("미션 완료");

        // 버튼 비활성화
        missionCompleteButton.setEnabled(false);

        Toast.makeText(WakeUpActivity.this, "미션 완료!", Toast.LENGTH_SHORT).show();
    }

    private void checkAndResetMissionStatus() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int lastDay = preferences.getInt("LastDay", 0);

        // 현재 날짜 가져오기
        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_YEAR);

        // 날짜가 변경되면 미션 완료 상태 초기화
        if (lastDay != currentDay) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("MissionCompleted", false);
            editor.putInt("LastDay", currentDay);
            editor.apply();

            // 미션 인증 버튼 활성화
            missionCompleteButton.setEnabled(true);
            missionCompleteButton.setText("미션 인증");
        }
    }
}