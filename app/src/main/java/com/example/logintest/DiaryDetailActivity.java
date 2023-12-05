package com.example.logintest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DiaryDetailActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewContent;
    private Button buttonEdit, buttonDelete;
    private String entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewContent = findViewById(R.id.textViewContent);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Intent에서 데이터 가져오기
        String diaryTitle = getIntent().getStringExtra("DIARY_TITLE");
        String diaryContent = getIntent().getStringExtra("DIARY_CONTENT");
        entryId = getIntent().getStringExtra("DIARY_ID");

        // 데이터 표시
        textViewTitle.setText(diaryTitle);
        textViewContent.setText(diaryContent);

        // 수정 버튼 클릭 리스너
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여기에 수정 페이지로 이동하는 로직 추가
                Intent intent = new Intent(DiaryDetailActivity.this, DiaryWriteActivity.class);
                intent.putExtra("DIARY_ID", entryId); // ID도 함께 전달
                intent.putExtra("DIARY_TITLE", diaryTitle);
                intent.putExtra("DIARY_CONTENT", diaryContent);
                startActivity(intent);
            }
        });

        // 삭제 버튼 클릭 리스너
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 일기의 ID를 가져옵니다. 이 예제에서는 'entryId' 변수를 사용합니다.
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자의 UID
                if (entryId != null) {
                    // Firebase에서 해당 일기 삭제
                    FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).child(entryId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DiaryDetailActivity.this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish(); // 화면 종료
                            })
                            .addOnFailureListener(e -> Toast.makeText(DiaryDetailActivity.this, "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(DiaryDetailActivity.this, "삭제할 일기가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryDetailActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryDetailActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryDetailActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryDetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryDetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
    }

}
