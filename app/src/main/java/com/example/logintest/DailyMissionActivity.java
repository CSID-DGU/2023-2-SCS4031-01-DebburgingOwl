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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DailyMissionActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    TextView missonRewardContent;
    Button workoutMission;
    Button coffeeMission;
    Button earlyMorningMission;
    Button communicationMission;
    Button missionBtn5;
    Button missionBtn6;
    Button missionBtn7;

    // User의 레벨에 따라 미션을 보여주는 메소드를 추가합니다.
    private void updateMissionVisibility(int userLevel) {
        // 이 부분에서는 userLevel에 따라 미션 버튼의 가시성을 설정합니다.
        workoutMission.setVisibility(userLevel >= 1 ? View.VISIBLE : View.GONE);
        coffeeMission.setVisibility(userLevel >= 3 ? View.VISIBLE : View.GONE);
        earlyMorningMission.setVisibility(userLevel >= 1 ? View.VISIBLE : View.GONE);
        communicationMission.setVisibility(userLevel >= 2 ? View.VISIBLE : View.GONE);
        missionBtn5.setVisibility(userLevel >= 5 ? View.VISIBLE : View.GONE);
        missionBtn6.setVisibility(userLevel >= 6 ? View.VISIBLE : View.GONE);
        missionBtn7.setVisibility(userLevel >= 7 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission);

        missonRewardContent = (TextView) findViewById(R.id.missonRewardContent);
        workoutMission = (Button) findViewById(R.id.missionBtn1);
        coffeeMission = (Button) findViewById(R.id.missionBtn2);
        earlyMorningMission = (Button) findViewById(R.id.missionBtn3);
        communicationMission = (Button) findViewById(R.id.missionBtn4);
        missionBtn5 = (Button) findViewById(R.id.missionBtn5);
        missionBtn6 = (Button) findViewById(R.id.missionBtn6);
        missionBtn7 = (Button) findViewById(R.id.missionBtn7);

        // 다른 버튼들에 대한 onClickListener 설정이 필요하면 여기에 추가합니다.
        // 예: missionBtn3.setOnClickListener(...);
        initializeDailyMissionData();
        // 사용자 인증 정보를 가져옵니다.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        int userLevel = user.getLevel(); // 사용자 레벨을 가져옵니다.
                        updateMissionVisibility(userLevel); // 레벨에 맞는 미션을 보여주기 위해 메소드를 호출합니다.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DailyMissionActivity.this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 로그인하지 않은 사용자에 대한 처리를 해야합니다.
            // 여기에 로그인 화면으로 이동하거나 로그인 요청을 하는 코드를 추가할 수 있습니다.
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        // 하단 네비게이션 뷰의 아이템 선택 리스너를 설정합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DailyMissionActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(DailyMissionActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(DailyMissionActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                // 현재 활성화된 탭이므로 아무것도 하지 않습니다.
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DailyMissionActivity.this, MyPageActivity.class);
                startActivity(intent);

                return true;
            } else {
                return false;
            }
        });

        // 각 미션 버튼에 대한 onClickListener를 설정합니다.
        workoutMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WorkoutActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, WorkoutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

            }
        });

        coffeeMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CoffeeMissionActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, CoffeeMissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

            }
        });

        earlyMorningMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CoffeeMissionActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, WakeUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

            }
        });
        communicationMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CoffeeMissionActivity 시작하는 Intent
                Intent intent = new Intent(DailyMissionActivity.this, CommunicationMission.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

            }
        });

        // 다른 버튼들에 대한 onClickListener 설정이 필요하면 여기에 추가합니다.
        // 예: missionBtn3.setOnClickListener(...);
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
    private void initializeDailyMissionData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference userMissionsRef = FirebaseDatabase.getInstance().getReference("userMissions").child(userId).child(currentDate);

        userMissionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // 날짜 노드가 없으면 새로운 노드를 생성합니다.
                    Map<String, Object> missions = new HashMap<>();
                    missions.put("earlymorning", false);
                    missions.put("communicate", false);
                    missions.put("workout", false);
                    missions.put("coffee", false);

                    userMissionsRef.setValue(missions); // 초기 미션 상태 설정
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }

}
