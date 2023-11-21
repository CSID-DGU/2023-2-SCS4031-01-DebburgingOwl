package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WriteActivity extends AppCompatActivity {

    Button cancelBtn;
    Button registerBtn;
    EditText title;
    EditText content;
    private DatabaseReference informationsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);

        // Firebase Realtime Database의 informations 참조 가져오기
        informationsRef = FirebaseDatabase.getInstance().getReference("informations");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 뒤로가기 버튼 역할 수행
                finish();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 게시글 등록 버튼 클릭 시 동작
                registerInformation();
            }
        });
    }
    private void registerInformation() {
        String infoTitle = title.getText().toString().trim();
        String infoContent = content.getText().toString().trim();

        // 제목과 내용이 비어있는지 확인
        if (infoTitle.isEmpty() || infoContent.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Firebase Authentication 인스턴스 가져오기
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // 현재 로그인한 사용자의 이메일 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getEmail();

        // Firebase Realtime Database에 데이터 등록
        String infoId = informationsRef.push().getKey();
        Information information = new Information(infoId, infoTitle, infoContent, userId);
        informationsRef.child(infoId).setValue(information);

        // 등록 후 액티비티 종료
        Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}