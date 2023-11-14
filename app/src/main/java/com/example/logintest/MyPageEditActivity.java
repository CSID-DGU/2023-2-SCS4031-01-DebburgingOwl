package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageEditActivity extends AppCompatActivity {

    ImageView userImage;
    EditText myPageName, myPageNickname, myPagePassword, myPagePasswordCheck, myPageEmail;
    Button myPageEditOkBtn;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_edit);

        userImage = findViewById(R.id.mypageUserImage);
        myPageName = findViewById(R.id.myPageName);
        myPageNickname = findViewById(R.id.myPageNickname);
        myPagePassword = findViewById(R.id.myPagePassword);
        myPagePasswordCheck = findViewById(R.id.myPagePasswordCheck);
        myPageEmail = findViewById(R.id.myPageEmail);
        myPageEditOkBtn = findViewById(R.id.myPageEditOkBtn);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
                        myPageName.setText(userData.getName());
                        myPageNickname.setText(userData.getNickname());
                        myPageEmail.setText(userData.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 처리
                    Toast.makeText(MyPageEditActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        myPageEditOkBtn.setOnClickListener(v -> {
            // Get updated information
            String updatedName = myPageName.getText().toString().trim();
            String updatedNickname = myPageNickname.getText().toString().trim();
            String updatedEmail = myPageEmail.getText().toString().trim();

            // Update the information in the database
            if (user != null) {
                String userId = user.getUid();
                databaseReference.child("users").child(userId).child("name").setValue(updatedName);
                databaseReference.child("users").child(userId).child("nickname").setValue(updatedNickname);
                databaseReference.child("users").child(userId).child("email").setValue(updatedEmail);

                // Get the updated password
                String updatedPassword = myPagePassword.getText().toString().trim();

                // Check if the password field is not empty
                if (!updatedPassword.isEmpty()) {
                    // Update the password
                    user.updatePassword(updatedPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MyPageEditActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyPageEditActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                Toast.makeText(MyPageEditActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(MyPageEditActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(MyPageEditActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(MyPageEditActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(MyPageEditActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(MyPageEditActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, (dialogInterface, i) -> {
            if (i == 0) {
                openCamera();
            } else if (i == 1) {
                openGallery();
            }
        });
        dialog.show();
    }

    private void openCamera() {
        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않았으면 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            // 이미 권한이 부여되어 있다면 카메라 열기
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    userImage.setImageBitmap(imageBitmap);
                }
            } else if (requestCode == REQUEST_GALLERY) {
                userImage.setImageURI(data.getData());
            }
        }
    }

}