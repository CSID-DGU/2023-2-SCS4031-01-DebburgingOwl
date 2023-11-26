package com.example.logintest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.logintest.User;

import java.text.BreakIterator;

public class RegisterActivity extends Activity {
    private EditText editTextName, editTextEmail, editTextPassword, editTextNickname;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button buttonRegister, buttonUploadImage;
    private ImageView userImage;

    private String userType;
    private Uri selectedImageUri;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private RadioGroup radioGroup;
    private RadioButton mentor,mentee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextName = findViewById(R.id.editTextNameRegister);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextNickname = findViewById(R.id.editTextNicknameRegister);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        userImage = findViewById(R.id.userImage);
        radioGroup = findViewById(R.id.radioGroup);
        mentee = findViewById(R.id.menteeRadio);
        mentor = findViewById(R.id.mentorRadio);


        int selectedUserType = radioGroup.getCheckedRadioButtonId();




        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.mentorRadio) {
                userType = "MENTOR";
            } else if (checkedId == R.id.menteeRadio) {
                userType = "MENTEE";
            }
        });

        buttonRegister.setOnClickListener(v -> registerUser());


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();
            }
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nickname = editTextNickname.getText().toString().trim();



        if (name.isEmpty() || name.length() < 2 || name.length() > 30) {
            editTextName.setError("Please enter a valid name (2~30 characters)");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password should be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }


        if (nickname.isEmpty() || nickname.length() < 2 || nickname.length() > 30) {
            editTextNickname.setError("Please enter a valid nickname (2~30 characters)");
            editTextNickname.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        if (user != null) {
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            String userId = user.getUid();
                                            User userData = new User(name, email, nickname, userType);
                                            databaseReference.child("users").child(userId).setValue(userData);
                                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MyPageActivity.class);
                                            intent.putExtra("nickname", nickname);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                // 카메라에서 가져온 이미지 처리
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    userImage.setImageBitmap(imageBitmap);
                }
            } else if (requestCode == REQUEST_GALLERY) {
                // 갤러리에서 선택한 이미지 처리
                // data.getData()를 통해 이미지 URI를 얻을 수 있음
                userImage.setImageURI(data.getData());
            }
        }
    }
}
