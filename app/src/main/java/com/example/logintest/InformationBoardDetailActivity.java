package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InformationBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn = (ImageButton) findViewById(R.id.infoBackBtn);
    ImageButton bookmarkBtn =(ImageButton) findViewById(R.id.infoBookmarkBtn);
    TextView title = (TextView) findViewById(R.id.infoTitle);
    TextView content = (TextView) findViewById(R.id.infoContent);
    ImageView infoUserPicture = (ImageView) findViewById(R.id.infoUserPicture);
    TextView infoUserType = (TextView) findViewById(R.id.infoUserType);
    TextView infoUserNickname = (TextView) findViewById(R.id.infoUserNickname);
    Button inforBoardEditBtn = (Button) findViewById(R.id.inforBoardEditBtn);
    Button mentorInfoBtn = (Button) findViewById(R.id.mentorInfoBtn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_board_detail);
    }
}