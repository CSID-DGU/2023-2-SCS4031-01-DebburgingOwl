package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyPageEditActivity extends AppCompatActivity {

    ImageView userImage = (ImageView) findViewById(R.id.userImage);
    EditText myPageName = (EditText) findViewById(R.id.myPageName);
    EditText myPageNickname = (EditText) findViewById(R.id.myPageNickname);
    EditText myPagePassword = (EditText) findViewById(R.id.myPagePassword);
    EditText myPagePasswordCheck = (EditText) findViewById(R.id.myPagePasswordCheck);
    EditText myPageEmail = (EditText) findViewById(R.id.myPageEmail);
    Button myPageEditOkBtn = (Button) findViewById(R.id.myPageEditOkBtn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_edit);

        // 뷰 초기화
        userImage = findViewById(R.id.userImage);
        myPageName = findViewById(R.id.myPageName);
        myPageNickname = findViewById(R.id.myPageNickname);
        myPagePassword = findViewById(R.id.myPagePassword);
        myPagePasswordCheck = findViewById(R.id.myPagePasswordCheck);
        myPageEmail = findViewById(R.id.myPageEmail);
        myPageEditOkBtn = findViewById(R.id.myPageEditOkBtn);

        // Firebase에서 사용자 정보 불러와서 화면에 표시
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            myPageName.setText(name);
            myPageEmail.setText(email);
        }

    }
}