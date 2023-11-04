package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InformationBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton bookmarkBtn;
    TextView title;
    TextView content;
    ImageView infoUserPicture;
    TextView infoUserType;
    TextView infoUserNickname;
    Button inforBoardEditBtn;
    Button mentorInfoBtn;
    BottomSheetFragment bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_board_detail);

        backBtn = findViewById(R.id.infoBackBtn);
        bookmarkBtn = findViewById(R.id.infoBookmarkBtn);
        title = findViewById(R.id.infoTitle);
        content = findViewById(R.id.infoContent);
        infoUserPicture = findViewById(R.id.infoUserPicture);
        infoUserType = findViewById(R.id.infoUserType);
        infoUserNickname = findViewById(R.id.infoUserNickname);
        inforBoardEditBtn = findViewById(R.id.inforBoardEditBtn);
        mentorInfoBtn = findViewById(R.id.mentorInfoBtn);

        mentorInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // mentorInfoBtn 클릭 이벤트 설정
            public void onClick(View view) {
                // BottomSheetFragment 인스턴스 생성
                bottomSheet = new BottomSheetFragment();
                // BottomSheetFragment를 화면에 표시
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }
}