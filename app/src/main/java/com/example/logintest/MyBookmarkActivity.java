package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

public class MyBookmarkActivity extends AppCompatActivity {

    GridView bookmarkGridView = (GridView) findViewById(R.id.bookmarkGridView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);
    }
}