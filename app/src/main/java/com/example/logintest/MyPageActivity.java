package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Button myPageEditButton = findViewById(R.id.myPageEditBtn);
        myPageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyPageEditActivity.class);
                startActivity(intent);
            }
        });

        Button myPostButton = findViewById(R.id.myPostBtn);
        myPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyPostActivity.class);
                startActivity(intent);
            }
        });

        Button myBookmarkButton = findViewById(R.id.myBookmarkBtn);
        myBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyBookmarkActivity.class);
                startActivity(intent);
            }
        });
    }
}
