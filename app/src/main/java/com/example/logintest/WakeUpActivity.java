package com.example.logintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WakeUpActivity extends AppCompatActivity {
    private Drawable originalButtonBackground;
    private String originalButtonText;
    private Button missionCompleteButton;
    private DatabaseReference missionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        checkMissionStatusAndInitialize();
        originalButtonBackground = missionCompleteButton.getBackground();
        originalButtonText = missionCompleteButton.getText().toString();

        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMissionTime()) {
                    completeMission();
                } else {
                    Toast.makeText(WakeUpActivity.this, "미션 시간이 아닙니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(WakeUpActivity.this, BoardActivity.class);
                startActivity(intent);

            } else if (itemId == R.id.community) {
                intent = new Intent(WakeUpActivity.this, CommunityActivity.class);
                startActivity(intent);

            } else if (itemId == R.id.home) {
                intent = new Intent(WakeUpActivity.this, MainActivity.class);
                startActivity(intent);

            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(WakeUpActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(WakeUpActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                return false;
            }
            return true;
        });
    }

    private void checkMissionStatusAndInitialize() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        missionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                .child(userId)
                .child(todayDate)
                .child("earlymorning");

        missionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class))) {
                    missionCompleteButton.setEnabled(false);
                    missionCompleteButton.setBackground(ContextCompat.getDrawable(WakeUpActivity.this, R.drawable.mission_color_background_complete)); // 새로운 배경 적용
                    missionCompleteButton.setText("내일 또 만나요!");
                } else {
                    updateButtonStatus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }

    private void updateButtonStatus() {
        if (isMissionTime()) {
            restoreButtonToOriginalState();

        } else {
            missionCompleteButton.setEnabled(false);
            missionCompleteButton.setText("미션 시간이 아닙니다");
        }
    }
    private void restoreButtonToOriginalState() {
        missionCompleteButton.setEnabled(true);
        missionCompleteButton.setBackground(ContextCompat.getDrawable(WakeUpActivity.this, R.drawable.mission_color_background)); // 원래 배경으로 복원
        missionCompleteButton.setText(originalButtonText); // 원래 텍스트로 복원
    }
    private boolean isMissionTime() {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        return currentHour == 7 && currentMinute >= 30 && currentMinute <= 40;
    }

    private void completeMission() {
        missionRef.setValue(true); // 미션 완료 상태로 업데이트
        missionCompleteButton.setEnabled(false);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateExp(userId, 100);
        updatePoint(userId,100);
        missionCompleteButton.setBackground(ContextCompat.getDrawable(WakeUpActivity.this, R.drawable.mission_color_background_complete)); // 새로운 배경 적용
        missionCompleteButton.setText("내일 또 만나요!"); // 버튼 텍스트를 "CLEAR"로 변경
        // AlertDialog를 사용하여 팝업 메시지 표시
        new AlertDialog.Builder(WakeUpActivity.this)
                .setTitle("미션 완료!")
                .setMessage("미션을 성공적으로 완료하셨습니다.")
                .setPositiveButton("최고에요!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        restartActivity(); // 액티비티 재시작
                    }
                })
                .show();



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
                    Toast.makeText(WakeUpActivity.this, "경험치 및 레벨이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(WakeUpActivity.this, "경험치 및 레벨 업데이트에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePoint(String userId, int additionalPoint){
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
                user.setPoint(user.getPoint() + additionalPoint);


                // 변경된 사용자 객체를 데이터베이스에 다시 씁니다.
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    // 트랜잭션이 성공적으로 커밋되었습니다. UI를 업데이트하거나 사용자에게 알림을 줄 수 있습니다.
                    Toast.makeText(WakeUpActivity.this, "포인트 적립이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(WakeUpActivity.this, "포인트 적립에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
