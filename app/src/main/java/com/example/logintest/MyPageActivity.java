package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageActivity extends AppCompatActivity {
    private TextView gradeTextView, nickname, point, people, userType;
    private DatabaseReference userRef;
    private boolean doubleBackToExitPressedOnce = false;
    Button myPageEditButton, myPostButton, myBookmarkButton, pointStoreButton, myDiaryBtn;

    private int totalLikeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        gradeTextView = findViewById(R.id.myPage_grade);
        nickname = findViewById(R.id.myPage_nickname);
        point = findViewById(R.id.myPage_point);
        people = findViewById(R.id.myPage_people);
        myPageEditButton = findViewById(R.id.myPageEditBtn);
        myDiaryBtn = findViewById(R.id.myDiaryBtn);
        myDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, DiaryListActivity.class);
                startActivity(intent);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            totalLikeCount=0;
            loadUserLikesCount(userId);

            // Firebase Realtime Database에서 사용자의 데이터 참조를 가져옴
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // 데이터 변화를 실시간으로 감지할 리스너 추가
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // dataSnapshot을 User 객체로 변환
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        gradeTextView.setText(user.getLevel() + " star");
                        nickname.setText(user.getNickname());
                        point.setText(user.getPoint()+ " point");
                        people.setText(user.getLikeCount()+" 명");
                        //todo 여기에 모든 정보 표시
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러가 발생한 경우, 유저에게 알림
                    Toast.makeText(MyPageActivity.this, "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 사용자가 로그인하지 않았다면 로그인 화면으로 이동하거나 다른 적절한 조치를 취함
            // 예를 들어 LoginActivity로 이동하는 코드를 작성할 수 있음
            Intent moveTologinActivity = new Intent(MyPageActivity.this, LoginActivity.class);
            startActivity(moveTologinActivity);
        }

        myPageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyPageEditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

            }
        });

        myPostButton = findViewById(R.id.myPostBtn);
        myPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyPostActivity.class);
                startActivity(intent);
            }
        });

        myBookmarkButton = findViewById(R.id.myBookmarkBtn);
        myBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyBookmarkActivity.class);
                startActivity(intent);
            }
        });

        pointStoreButton = findViewById(R.id.pointStoreBtn);
        pointStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, PointActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(MyPageActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(MyPageActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(MyPageActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(MyPageActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                return true;
            } else {
                return false;
            }
        });
    }
    private void loadUserLikesCount(String userId) {
        DatabaseReference uploadsRef = FirebaseDatabase.getInstance().getReference("uploads");
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");

        uploadsRef.orderByChild("uploader").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                    String imageId = uploadSnapshot.getKey();

                    likesRef.child(imageId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot likeSnapshot) {
                            totalLikeCount += likeSnapshot.getChildrenCount();
                            updateUserLikeCount(userId, totalLikeCount); // 사용자의 좋아요 수 업데이트
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 오류 처리
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }

    private void updateUserLikeCount(String userId, int likeCount) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.child("likeCount").setValue(likeCount); // 좋아요 수 업데이트
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // 2초 동안 대기
    }
}
