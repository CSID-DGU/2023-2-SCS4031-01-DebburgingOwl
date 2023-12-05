package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryWriteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private Button buttonWrite, buttonCancel;
    private String entryId; // 일기 ID를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        buttonWrite = findViewById(R.id.writebtn);
        buttonCancel = findViewById(R.id.cancelBtn);

        // 인텐트에서 데이터 가져오기 (수정 시)
        if (getIntent().hasExtra("DIARY_ID")) {
            entryId = getIntent().getStringExtra("DIARY_ID");
            String title = getIntent().getStringExtra("DIARY_TITLE");
            String content = getIntent().getStringExtra("DIARY_CONTENT");

            editTextTitle.setText(title);
            editTextContent.setText(content);
            buttonWrite.setText("수정하기"); // 버튼 텍스트 변경
        }

        // "기록하기/수정하기" 버튼 이벤트
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateDiaryEntry();
            }
        });

        // "취소" 버튼 이벤트
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryWriteActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryWriteActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryWriteActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryWriteActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryWriteActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
    }

    private void saveOrUpdateDiaryEntry() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (!title.isEmpty() && !content.isEmpty()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자 UID 가져오기
            boolean isNewEntry = (entryId == null);

            if (isNewEntry) {
                // 새 일기 작성
                entryId = FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).push().getKey();
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            DiaryEntry entry = new DiaryEntry(entryId, title, content, timestamp); // DiaryEntry 모델 생성

            // 데이터베이스에 저장 또는 업데이트
            FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).child(entryId).setValue(entry)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DiaryWriteActivity.this, isNewEntry ? "일기가 저장되었습니다." : "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(DiaryWriteActivity.this, DiaryListActivity.class);
                        startActivity(intent);
                        finish();
                         // 저장 후 화면 종료
                    })
                    .addOnFailureListener(e -> Toast.makeText(DiaryWriteActivity.this, "실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

}
