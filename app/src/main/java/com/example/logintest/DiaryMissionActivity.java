package com.example.logintest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.util.Date;
import java.util.Locale;

public class DiaryMissionActivity extends AppCompatActivity {

    private Button missionCompleteButton;
    private Drawable originalButtonBackground;
    private String originalButtonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_mission);
        checkMissionStatusAndUpdateButton();

        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        originalButtonBackground = missionCompleteButton.getBackground();
        originalButtonText = missionCompleteButton.getText().toString();
        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMissionCompletion();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryMissionActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryMissionActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryMissionActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryMissionActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryMissionActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });

    }

    private void checkMissionCompletion() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId);

        // 오늘 날짜 설정
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ref.orderByChild("timestamp").startAt(todayDate + " 00:00:00").endAt(todayDate + " 23:59:59")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int maxLength = 0; // 가장 긴 일기의 길이
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DiaryEntry entry = snapshot.getValue(DiaryEntry.class);
                                if (entry != null && entry.getContent().length() > maxLength) {
                                    maxLength = entry.getContent().length();
                                }
                            }

                            int minLength = 100; // 미션 조건 길이 설정
                            if (maxLength >= minLength) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String currentDate = sdf.format(new Date());

                                DatabaseReference MissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                                        .child(userId)
                                        .child(currentDate)
                                        .child("diary");
                                MissionRef.setValue(true); // 미션 완료 상태로 업데이트

                                missionCompleteButton.setEnabled(false); // 버튼 비활성화
                                updateExp(userId,  10);
                                updatePoint(userId,10);

                                // AlertDialog를 사용하여 팝업 메시지 표시
                                new AlertDialog.Builder(DiaryMissionActivity.this)
                                        .setTitle("미션 완료!")
                                        .setMessage("미션을 성공적으로 완료하셨습니다.")
                                        .setPositiveButton("최고에요!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                restartActivity(); // 액티비티 재시작
                                            }
                                        })
                                        .show();


                            } else {
                                int shortLength = minLength - maxLength;
                                Toast.makeText(DiaryMissionActivity.this,  shortLength + "글자가 부족합니다. 조금만 더 기록해 보아요!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DiaryMissionActivity.this, "오늘 작성된 일기가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                    }
                });
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
                    Toast.makeText(DiaryMissionActivity.this, "포인트 적립이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(DiaryMissionActivity.this, "포인트 적립에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //경험치 올리는 메서드
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
                    Toast.makeText(DiaryMissionActivity.this, "경험치 및 레벨이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(DiaryMissionActivity.this, "경험치 및 레벨 업데이트에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkMissionStatusAndUpdateButton() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference MissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                .child(userId)
                .child(currentDate)
                .child("diary");

        MissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isMissionComplete = dataSnapshot.getValue(Boolean.class);
                    if (isMissionComplete != null && isMissionComplete) {
                        missionCompleteButton.setEnabled(false);
                        missionCompleteButton.setBackground(ContextCompat.getDrawable(DiaryMissionActivity.this, R.drawable.mission_color_background_complete)); // 새로운 배경 적용
                        missionCompleteButton.setText("내일 또 만나요!"); // 버튼 텍스트를 "CLEAR"로 변경
                    } else {
                        missionCompleteButton.setEnabled(true);
                        restoreButtonToOriginalState();

                        // 필요한 경우, 여기서 버튼의 원래 상태(색상 및 텍스트)로 복원할 수 있습니다.
                    }
                } else {
                    missionCompleteButton.setEnabled(true); // 노드가 없으면 활성화 (미션을 아직 수행하지 않았다고 가정)
                    // 필요한 경우, 여기서 버튼의 원래 상태(색상 및 텍스트)로 복원할 수 있습니다.
                    restoreButtonToOriginalState();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }
    private void restoreButtonToOriginalState() {
        missionCompleteButton.setEnabled(true);
        missionCompleteButton.setBackground(ContextCompat.getDrawable(DiaryMissionActivity.this, R.drawable.mission_color_background)); // 원래 배경으로 복원
        missionCompleteButton.setText(originalButtonText); // 원래 텍스트로 복원
    }

}
