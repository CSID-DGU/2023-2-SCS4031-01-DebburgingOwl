
package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Picasso 라이브러리 임포트
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    // 전체공개여부 수정 스위치
    private Switch switchPublicPrivate;
    private String imageId; // 이미지의 고유 ID를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 뷰 찾기
        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewUploader = findViewById(R.id.textViewUploader);
        textViewDescription = findViewById(R.id.textViewDescription);
        // 인텐트에서 업로더 ID 가져오기
        String uploaderId = getIntent().getStringExtra("uploaderId");
        // 인텐트에서 이미지 URL 가져오기
        String imageUrl = getIntent().getStringExtra("imageUrl");
        imageId = getIntent().getStringExtra("imageId"); // 인텐트에서 이미지 ID 가져오기

        // Picasso를 사용하여 이미지 로딩 및 표시
        Picasso.get().load(imageUrl).into(imageViewDetail);
        switchPublicPrivate = findViewById(R.id.switchPublicPrivate);


        // 현재 로그인한 사용자의 ID 가져오기
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 업로더 ID와 현재 사용자 ID 비교
        if (uploaderId != null && uploaderId.equals(currentUserId)) {
            // 현재 사용자가 업로더일 경우 스위치 표시
            switchPublicPrivate.setVisibility(View.VISIBLE);
            switchPublicPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // 여기에 스위치 상태 변경 시 로직 추가
                updateImagePublicStatus(isChecked);

            });
        } else {
            // 현재 사용자가 업로더가 아닐 경우 스위치 숨기기
            switchPublicPrivate.setVisibility(View.GONE);
        }

        // ... 나머지 코드



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

    private void updateImagePublicStatus(boolean publicStatus) {
        if (imageId == null) {
            Toast.makeText(this, "Image ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("uploads").child(imageId);

        // 이미지의 'publicStatus' 필드 업데이트
        imageRef.child("publicStatus").setValue(publicStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(DetailActivity.this, "Visibility updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DetailActivity.this, "Failed to update visibility", Toast.LENGTH_SHORT).show());
    }


}
