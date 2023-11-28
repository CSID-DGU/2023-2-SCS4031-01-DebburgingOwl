package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logintest.UserActivity;
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

public class CommunicationMission extends AppCompatActivity {

    private TextView tvLikesCount, tvCommentsCount;
    private Button missionCompleteButton;
    private static final int MISSION_TARGET = 3; // 미션 목표값 설정
    private String currentDate; // 멤버 변수로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvCommentsCount = findViewById(R.id.tvCommentsCount);
        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        missionCompleteButton.setEnabled(false);
        checkMissionStatusAndUpdateButton();
        missionCompleteButton.setOnClickListener(v -> {

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference communicateMissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                    .child(userId)
                    .child(currentDate)
                    .child("communicate");
            communicateMissionRef.setValue(true); // 미션 완료 상태로 업데이트

            missionCompleteButton.setEnabled(false); // 버튼 비활성화

            updateExp(userId, 100);//경험치
            updatePoint(userId,100);//포인트



        });

        loadUserActivity();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(CommunicationMission.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(CommunicationMission.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(CommunicationMission.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(CommunicationMission.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(CommunicationMission.this, MyPageActivity.class);
                startActivity(intent);


            } else {
                return false;
            }
            return true;
        });
    }

    private void loadUserActivity() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference("userActivity").child(userId).child(currentDate);
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserActivity userActivity = dataSnapshot.getValue(UserActivity.class);
                    if (userActivity != null) {
                        tvLikesCount.setText("오늘의 좋아요 수: " + userActivity.getLikes());
                        tvCommentsCount.setText("오늘의 댓글 수: " + userActivity.getComments());

                        // 미션 완료 조건 확인
                        if (userActivity.getLikes() >= MISSION_TARGET && userActivity.getComments() >= MISSION_TARGET) {
                            missionCompleteButton.setEnabled(true); // 버튼 활성화
                        }
                    }
                } else {
                    tvLikesCount.setText("오늘의 좋아요 수: 0");
                    tvCommentsCount.setText("오늘의 댓글 수: 0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
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
                    Toast.makeText(CommunicationMission.this, "포인트 적립이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(CommunicationMission.this, "포인트 적립에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CommunicationMission.this, "경험치 및 레벨이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(CommunicationMission.this, "경험치 및 레벨 업데이트에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkMissionStatusAndUpdateButton() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference communicateMissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                .child(userId)
                .child(currentDate)
                .child("communicate");

        communicateMissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isMissionComplete = dataSnapshot.getValue(Boolean.class);
                    if (isMissionComplete != null && isMissionComplete) {
                        missionCompleteButton.setEnabled(false);
                    } else {
                        loadUserActivity();
                    }
                } else {
                    missionCompleteButton.setEnabled(true); // 노드가 없으면 활성화 (미션을 아직 수행하지 않았다고 가정)
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }

}
