package com.example.logintest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

public class UserTypeActivity extends AppCompatActivity {

    ImageView userImage;
    EditText nickname;
    RadioButton mentorRadio, menteeRadio;
    Button userTypeOkBtn;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        userImage = findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();
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
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
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