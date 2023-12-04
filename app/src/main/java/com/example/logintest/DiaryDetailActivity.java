package com.example.logintest;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DiaryDetailActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewContent = findViewById(R.id.textViewContent);

        // Intent에서 데이터 가져오기
        String diaryTitle = getIntent().getStringExtra("DIARY_TITLE");
        String diaryContent = getIntent().getStringExtra("DIARY_CONTENT");

        // 데이터 표시
        textViewTitle.setText(diaryTitle);
        textViewContent.setText(diaryContent);
    }

    //todo: 수정,삭제 버튼 추가 UI 수정
}
