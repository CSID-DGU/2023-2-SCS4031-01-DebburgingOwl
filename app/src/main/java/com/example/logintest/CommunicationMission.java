package com.example.logintest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private Drawable originalButtonBackground;
    private String originalButtonText;

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
        originalButtonBackground = missionCompleteButton.getBackground();
        originalButtonText = missionCompleteButton.getText().toString();
        missionCompleteButton.setEnabled(false);
        checkMissionStatusAndUpdateButton();
        loadUserActivity();
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
            // AlertDialog를 사용하여 팝업 메시지 표시
            new AlertDialog.Builder(CommunicationMission.this)
                    .setTitle("미션 완료!")
                    .setMessage("미션을 성공적으로 완료하셨습니다.")
                    .setPositiveButton("최고에요!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            restartActivity(); // 액티비티 재시작
                        }
                    })
                    .show();



        });

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
        DatabaseReference missionStatusRef = FirebaseDatabase.getInstance().getReference("userMissions").child(userId).child(currentDate).child("communicate");

        missionStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 미션 완료 여부 확인
                boolean missionCompleted = dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class));

                activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserActivity userActivity = dataSnapshot.getValue(UserActivity.class);
                            if (userActivity != null) {
                                tvLikesCount.setText("오늘의 좋아요 수: " + userActivity.getLikes());
                                tvCommentsCount.setText("오늘의 댓글 수: " + userActivity.getComments());

                                // 미션 완료 조건 및 미션 완료 여부 확인
                                if (!missionCompleted && userActivity.getLikes() >= MISSION_TARGET && userActivity.getComments() >= MISSION_TARGET) {
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
                        missionCompleteButton.setBackground(ContextCompat.getDrawable(CommunicationMission.this, R.drawable.mission_color_background_complete)); // 새로운 배경 적용
                        missionCompleteButton.setText("내일 또 만나요!"); // 버튼 텍스트를 "CLEAR"로 변경
                    } else {
                        loadUserActivity();

                    }
                } else {
                    missionCompleteButton.setEnabled(true);
                    // 노드가 없으면 활성화 (미션을 아직 수행하지 않았다고 가정)
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
        missionCompleteButton.setBackground(ContextCompat.getDrawable(CommunicationMission.this, R.drawable.mission_color_background)); // 원래 배경으로 복원
        missionCompleteButton.setText(originalButtonText); // 원래 텍스트로 복원
    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
