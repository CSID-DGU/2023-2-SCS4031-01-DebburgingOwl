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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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

        stepCounterTextView = findViewById(R.id.stepCounter);
        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 초기 버튼 상태를 비활성화합니다.
        //missionCompleteButton.setEnabled(false);

        // 걸음 수를 수동으로 증가시키는 버튼
        Button btnIncreaseSteps = findViewById(R.id.btnIncreaseSteps);
        btnIncreaseSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps += 100; // 스텝을 100씩 증가
                updateStepCounter(); // 스텝 업데이트 메서드 호출
            }
        });

        // 미션 완료 버튼 리스너
        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (steps >= 1000) {
                    // 경험치를 업데이트하는 메소드 호출
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    updateExp(userId, 100);

                    // 메인 액티비티로 이동
                    Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(WorkoutActivity.this, "아직 " + (1000 - steps) + " 걸음이 부족해요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 바텀 네비게이션 뷰 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(WorkoutActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(WorkoutActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(WorkoutActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                // 일간 미션 액티비티가 현재 액티비티라면, 새로운 인텐트를 시작할 필요가 없습니다.
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(WorkoutActivity.this, MyPageActivity.class);
                startActivity(intent);
            } else {
                return false;
            }
            return true;
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
        } else {
            Toast.makeText(this, "Step counter sensor not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps = (int) event.values[0];
            updateStepCounter();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도가 변경될 때 호출됩니다. 여기서는 처리할 필요가 없습니다.
    }

    private void updateExp(String userId, int additionalExp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                // 사용자의 현재 경험치를 추가합니다.
                user.setExp(user.getExp() + additionalExp);

                // 레벨을 업데이트합니다.
                user.updateLevel();

                // 변경된 사용자 객체를 데이터베이스에 다시 씁니다.
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    // 트랜잭션이 성공적으로 커밋되었습니다. UI를 업데이트하거나 사용자에게 알림을 줄 수 있습니다.
                    Toast.makeText(WorkoutActivity.this, "경험치 및 레벨이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(WorkoutActivity.this, "경험치 및 레벨 업데이트에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
