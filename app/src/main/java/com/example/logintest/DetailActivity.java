
package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Picasso 라이브러리 임포트
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    // 뷰들을 위한 변수 선언
    private ImageView imageViewDetail;
    private TextView textViewUploader, textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 뷰 찾기
        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewUploader = findViewById(R.id.textViewUploader);
        textViewDescription = findViewById(R.id.textViewDescription);

        String uploaderId = getIntent().getStringExtra("uploaderId");
        // 인텐트에서 이미지 URL 가져오기
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Picasso를 사용하여 이미지 로딩 및 표시
        Picasso.get().load(imageUrl).into(imageViewDetail);

        // 추가 데이터 설정 (예: 업로더 이름, 이미지 설명)
        // 여기서는 예시로 텍스트를 설정합니다. 실제 앱에서는 Firebase에서 데이터를 가져와야 할 수 있습니다.
        textViewUploader.setText("Uploader Name");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uploaderId).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.getValue(String.class);
                textViewUploader.setText("photo by "+nickname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DetailActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DetailActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                return true;


            } else {
                return false;
            }
            return true;
        });
    }

}
