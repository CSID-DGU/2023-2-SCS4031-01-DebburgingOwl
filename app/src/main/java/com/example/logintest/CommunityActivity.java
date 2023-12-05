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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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

    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private ImageAdapter imageAdapter;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private GridView gridView;
    private List<ImageModel> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.community);
        checkAndRequestPermissions();
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(CommunityActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(CommunityActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(CommunityActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(CommunityActivity.this, MyPageActivity.class);
                startActivity(intent);


            } else {
                return false;
            }
            return true;
        });
        FloatingActionButton btnUpload = findViewById(R.id.btn_upload);

        gridView = findViewById(R.id.imageGridView);
        imageList = new ArrayList<>();


        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        imageAdapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(imageAdapter);
        mAuth = FirebaseAuth.getInstance();
        loadPublicImages();

        btnUpload.setOnClickListener(v -> showImageImportDialog());
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // 클릭된 이미지의 상세 정보 가져오기
            ImageModel clickedImage = imageList.get(position);

            // 상세보기 액티비티로 데이터 전달
            Intent detailIntent = new Intent(CommunityActivity.this, DetailActivity.class);
            detailIntent.putExtra("imageUrl", clickedImage.getImageUrl()); // 이미지 URL 전달
            detailIntent.putExtra("uploaderId", clickedImage.getUploader()); // 업로더 ID 또는 사용자 이름 전달
            detailIntent.putExtra("imageId", clickedImage.getImageId()); // 이미지의 고유 ID 추가
            startActivity(detailIntent);
        });

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
                        // ImageModel 객체를 사용하여 이미지 메타데이터 생성
                        ImageModel imageModel = new ImageModel(uri.toString(), false, userId);

                        // 데이터베이스에 ImageModel 객체 저장
                        databaseRef.child(modelId).setValue(imageModel).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommunityActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                updateExp(userId, 1);
                                updatePoint(userId,1);
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
    private void initializeLikeDataForImage(String imageId) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");

        // 해당 이미지 ID에 대한 좋아요 노드 생성
        likesRef.child(imageId).setValue(new HashMap<String, Boolean>())
                .addOnSuccessListener(aVoid -> {
                    // 초기화 성공 로그
                })
                .addOnFailureListener(e -> {
                    // 초기화 실패 로그
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void loadPublicImages() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel imageModel = snapshot.getValue(ImageModel.class);
                    if (imageModel != null && imageModel.getPublicStatus()) {
                        imageModel.setImageId(snapshot.getKey());
                        imageList.add(0, imageModel); // 리스트의 시작 부분에 추가
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CommunityActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
    private void updateExp(String userId, int additionalExp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                // 사용자의 현재 경험치를 추가합니다.
                user.setExp(user.getExp() + additionalExp);

                // 레벨을 업데이트합니다.
                user.updateLevel();

                // 변경된 사용자 객체를 데이터베이스에 다시 씁니다.
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    // 트랜잭션이 성공적으로 커밋되었습니다. UI를 업데이트하거나 사용자에게 알림을 줄 수 있습니다.

                } else {
                    // 트랜잭션이 실패했습니다.

                }
            }
        });
    }
    private void updatePoint(String userId, int additionalPoint){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                // 사용자의 현재 경험치를 추가합니다.
                user.setPoint(user.getPoint() + additionalPoint);


                // 변경된 사용자 객체를 데이터베이스에 다시 씁니다.
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    // 트랜잭션이 성공적으로 커밋되었습니다. UI를 업데이트하거나 사용자에게 알림을 줄 수 있습니다.

                } else {
                    // 트랜잭션이 실패했습니다.

                }
            }
        });
    }
}
