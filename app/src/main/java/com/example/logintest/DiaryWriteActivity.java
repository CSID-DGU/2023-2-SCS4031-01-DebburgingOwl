package com.example.logintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DiaryWriteActivity extends AppCompatActivity {
    private Button buttonDeleteImage;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextTitle;
    private NoCopyPasteEditText   editTextContent;
    private Button buttonWrite, buttonCancel;
    private String entryId; // 일기 ID를 저장할 변수
    private ImageView imagePreview;
    private Uri imageUri; // 사용자가 선택한 이미지의 URI를 저장할 변수
    private String originalImageUrl; // 기존 이미지의 URL
    private boolean isNewEntry; // 클래스 멤버 변수로 정의

    private Button buttonSelectImage; // 이미지 선택 버튼
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);
        buttonDeleteImage = findViewById(R.id.buttonDeleteImage);
        buttonDeleteImage.setVisibility(View.GONE); // 초기에는 버튼 숨김

        editTextTitle = findViewById(R.id.title);
        editTextContent = findViewById(R.id.content);
        buttonWrite = findViewById(R.id.writebtn);
        buttonCancel = findViewById(R.id.cancelBtn);
        imagePreview = findViewById(R.id.imagePreview); // ImageView 바인딩
        checkAndRequestPermissions();
        buttonSelectImage = findViewById(R.id.selectImageBtn);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        // 인텐트에서 데이터 가져오기 (수정 시)
        if (getIntent().hasExtra("DIARY_ID")) {
            isNewEntry = false; // 수정하는 경우
            entryId = getIntent().getStringExtra("DIARY_ID");
            String title = getIntent().getStringExtra("DIARY_TITLE");
            String content = getIntent().getStringExtra("DIARY_CONTENT");
            String imageUrl = getIntent().getStringExtra("DIARY_IMAGE_URL");
            originalImageUrl=getIntent().getStringExtra("DIARY_IMAGE_URL");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(imagePreview);
                imagePreview.setVisibility(View.VISIBLE);
                buttonDeleteImage.setVisibility(View.VISIBLE); // 버튼 보이게 설정
            }
            editTextTitle.setText(title);
            editTextContent.setText(content);
            buttonWrite.setText("수정하기"); // 버튼 텍스트 변경
        }
        else {
            isNewEntry = true; // 새로 작성하는 경우
        }

        // "기록하기/수정하기" 버튼 이벤트
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateDiaryEntry();
            }
        });
        buttonDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalImageUrl != null) {
                    deleteImageFromStorage(originalImageUrl);
                }
                imagePreview.setVisibility(View.GONE); // 이미지뷰 숨김
                buttonDeleteImage.setVisibility(View.GONE); // 버튼 숨김
                imageUri = null; // 이미지 URI 초기화
                originalImageUrl = null; // 원래 이미지 URL 초기화
            }
        });


        // "취소" 버튼 이벤트
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DiaryWriteActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DiaryWriteActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DiaryWriteActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DiaryWriteActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DiaryWriteActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
    }

    private void saveOrUpdateDiaryEntry() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (!title.isEmpty() && !content.isEmpty()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (isNewEntry) {
                entryId = FirebaseDatabase.getInstance().getReference().child("diaryEntries").child(userId).push().getKey();
            }

            if (imageUri != null) {
                uploadImageToFirebase(imageUri, userId, title, content, isNewEntry);
            } else {
                saveDiaryEntry(userId, title, content, originalImageUrl, isNewEntry);
            }
        } else {
            Toast.makeText(this, "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String userId, String title, String content, boolean isNewEntry) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference()
                .child("diaryImages")
                .child(userId)
                .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newImageUrl = uri.toString();
                    saveDiaryEntry(userId, title, content, newImageUrl, isNewEntry);

                    // 기존 이미지 삭제
                    if (originalImageUrl != null && !originalImageUrl.equals(newImageUrl)) {
                        deleteImageFromStorage(originalImageUrl);
                    }
                }))
                .addOnFailureListener(e -> Toast.makeText(DiaryWriteActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void saveDiaryEntry(String userId, String title, String content, String imageUrl, boolean isNewEntry) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        DiaryEntry entry = new DiaryEntry(entryId, title, content, timestamp, imageUrl);

        FirebaseDatabase.getInstance().getReference().child("diaryEntries")
                .child(userId)
                .child(entryId)
                .setValue(entry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DiaryWriteActivity.this, isNewEntry ? "일기가 저장되었습니다." : "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(DiaryWriteActivity.this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void deleteImageFromStorage(String imageUrl) {
        try {
            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            oldImageRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d("DiaryWriteActivity", "Old image deleted successfully"))
                    .addOnFailureListener(e -> Log.e("DiaryWriteActivity", "Error deleting old image", e));
        } catch (IllegalArgumentException e) {
            Log.e("DiaryWriteActivity", "Invalid URL for deleting image", e);
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            HashMap<String, Integer> permissionsResult = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsResult.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount > 0) {
                // 하나 이상의 권한이 거부된 경우, 사용자에게 추가 설명을 제공하거나 앱 기능의 제한을 처리합니다.
            } else {
                // 모든 권한이 승인된 경우, 필요한 작업을 계속 진행합니다.
            }
        }
    }
    private void openImageSelector() {
        // 이미지 선택을 위한 인텐트를 생성하고 startActivityForResult를 호출합니다.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // 새로운 이미지 URI
            imagePreview.setImageURI(imageUri); // ImageView에 이미지 설정
            imagePreview.setVisibility(View.VISIBLE); // ImageView 보이게 설정
            buttonDeleteImage.setVisibility(View.VISIBLE); // 이미지 삭제 버튼 보이게 설정
            originalImageUrl = null; // 기존 이미지 URL 초기화
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
