package com.example.logintest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int GALLERY_PERMISSION_REQUEST = 101;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        FloatingActionButton btnUpload = findViewById(R.id.btn_upload);



        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mAuth = FirebaseAuth.getInstance();

        btnUpload.setOnClickListener(v -> showImageImportDialog());

    }


    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, (dialogInterface, i) -> {
            if (i == 0) {
                // 카메라 선택 시
                openCamera();
            } else if (i == 1) {
                // 갤러리 선택 시
                openGallery();
            }
        });
        dialog.show();
    }



    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // 카메라에서 가져온 이미지를 처리합니다 (예: 파일로 저장)
                imageUri = getImageUri(imageBitmap); // Uri를 얻는 사용자 정의 메소드
                uploadImage();
            } else if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                uploadImage();
            }
        }
    }

    private Uri getImageUri(Bitmap inImage) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void uploadImage() {
        if (imageUri != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = storageRef.child(userId + "/" + System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String modelId = databaseRef.push().getKey();
                        // 이미지 메타데이터 생성
                        HashMap<String, Object> imageData = new HashMap<>();
                        imageData.put("imageUrl", uri.toString());
                        imageData.put("isPublic", true); // 유저가 이 이미지를 공개로 설정했다고 가정합니다.
                        imageData.put("uploader", userId);
                        // 데이터베이스에 이미지 메타데이터 저장
                        databaseRef.child(modelId).setValue(imageData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommunityActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommunityActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(CommunityActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
