package com.example.logintest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DiaryDetailActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewContent;
    private Button buttonEdit, buttonDelete;
    private String entryId, imageUrl;
    private ImageView imageViewDiary; // 이미지를 표시할 ImageView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);
        imageViewDiary = findViewById(R.id.imagePreview);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewContent = findViewById(R.id.textViewContent);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Intent에서 데이터 가져오기
        String diaryTitle = getIntent().getStringExtra("DIARY_TITLE");
        String diaryContent = getIntent().getStringExtra("DIARY_CONTENT");
        imageUrl = getIntent().getStringExtra("DIARY_IMAGE_URL"); // 이미지 URL 받기
        entryId = getIntent().getStringExtra("DIARY_ID");

        // 데이터 표시
        textViewTitle.setText(diaryTitle);
        textViewContent.setText(diaryContent);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageViewDiary);
            imageViewDiary.setVisibility(View.VISIBLE); // ImageView를 보이게 설정
        }


        // 수정 버튼 클릭 리스너
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여기에 수정 페이지로 이동하는 로직 추가
                Intent intent = new Intent(DiaryDetailActivity.this, DiaryWriteActivity.class);
                intent.putExtra("DIARY_ID", entryId); // ID도 함께 전달
                intent.putExtra("DIARY_TITLE", diaryTitle);
                intent.putExtra("DIARY_CONTENT", diaryContent);
                intent.putExtra("DIARY_IMAGE_URL", imageUrl);
                startActivity(intent);
            }
        });

        // 삭제 버튼 클릭 리스너
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 사용자의 UID
                if (entryId != null) {
                    // Firebase Storage에서 이미지 삭제
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                        imageRef.delete().addOnSuccessListener(aVoid -> {
                            // 이미지 삭제 성공 로그
                            Log.d("DiaryDetailActivity", "Image successfully deleted from Storage.");
                        }).addOnFailureListener(e -> {
                            // 이미지 삭제 실패 로그
                            Log.e("DiaryDetailActivity", "Failed to delete image from Storage.", e);
                        });
                    }

                    // Firebase Realtime Database에서 일기 삭제
                    FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).child(entryId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DiaryDetailActivity.this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish(); // 화면 종료
                            })
                            .addOnFailureListener(e -> Toast.makeText(DiaryDetailActivity.this, "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(DiaryDetailActivity.this, "삭제할 일기가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryDetailActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryDetailActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryDetailActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryDetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryDetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
    }

}
