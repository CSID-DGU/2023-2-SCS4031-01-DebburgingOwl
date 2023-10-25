package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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


    }
}