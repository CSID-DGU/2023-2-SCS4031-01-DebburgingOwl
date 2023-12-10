package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiaryListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewDiaryList;
    private DiaryAdapter diaryAdapter;
    private List<DiaryEntry> diaryEntryList;
    private FloatingActionButton fabWriteDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        fabWriteDiary = findViewById(R.id.fabWriteDiary);
        fabWriteDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryWriteActivity.class);
                startActivity(intent);
            }
        });
        loadDiaryEntries();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryListActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryListActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryListActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryListActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryListActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
        // 여기에 RecyclerView 설정 및 데이터 로드 로직 추가
    }
    private void loadDiaryEntries() {
        diaryEntryList = new ArrayList<>();
        recyclerViewDiaryList = findViewById(R.id.recyclerViewDiaryList);
        recyclerViewDiaryList.setLayoutManager(new LinearLayoutManager(this));
        diaryAdapter = new DiaryAdapter(diaryEntryList);
        recyclerViewDiaryList.setAdapter(diaryAdapter);

        // Firebase에서 데이터 로드 로직
        FirebaseDatabase.getInstance().getReference().child("diaryEntries")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        diaryEntryList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DiaryEntry entry = snapshot.getValue(DiaryEntry.class);
                            diaryEntryList.add(entry);
                        }
                        diaryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                    }
                });

        diaryAdapter.setOnItemClickListener(new DiaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DiaryEntry diaryEntry) {
                // Intent를 사용하여 일기 상세보기 액티비티로 이동
                Intent intent = new Intent(DiaryListActivity.this, DiaryDetailActivity.class);
                intent.putExtra("DIARY_ID", diaryEntry.getId());
                intent.putExtra("DIARY_TITLE", diaryEntry.getTitle());
                intent.putExtra("DIARY_CONTENT", diaryEntry.getContent());
                intent.putExtra("DIARY_IMAGE_URL", diaryEntry.getImageUrl()); // 이미지 URL 추가
                startActivity(intent);
            }
        });
    }
}