package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPostActivity extends AppCompatActivity {

    GridView myPostGridView = (GridView) findViewById(R.id.myPostGridView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

    }
}