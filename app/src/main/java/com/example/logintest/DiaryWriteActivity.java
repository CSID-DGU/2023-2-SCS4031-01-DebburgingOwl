package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryWriteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private Button buttonWrite, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        buttonWrite = findViewById(R.id.writebtn);
        buttonCancel = findViewById(R.id.cancelBtn);

        // "기록하기" 버튼 이벤트
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryEntry();
            }
        });

        // "취소" 버튼 이벤트
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

    private void saveDiaryEntry() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (!title.isEmpty() && !content.isEmpty()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자 UID 가져오기
            String entryId = FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).push().getKey();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            DiaryEntry entry = new DiaryEntry(entryId, title, content, timestamp); // DiaryEntry 모델 수정 필요

            // 데이터베이스에 저장
            FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).child(entryId).setValue(entry)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DiaryWriteActivity.this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 저장 후 화면 종료
                    })
                    .addOnFailureListener(e -> Toast.makeText(DiaryWriteActivity.this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
