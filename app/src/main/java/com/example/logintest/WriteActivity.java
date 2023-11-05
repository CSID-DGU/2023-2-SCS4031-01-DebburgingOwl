package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WriteActivity extends AppCompatActivity {

    Button cancelBtn;
    Button registerBtn;
    EditText title;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 뒤로가기 버튼 역할 수행
                finish();
            }
        });
    }
}