package com.example.logintest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class CoffeeMissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Drawable originalButtonBackground;
    private String originalButtonText;


    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    private  Button recognizeTextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_mission);

        checkAndRequestPermissions();
        recognizeTextButton = findViewById(R.id.recognizeTextButton);
        originalButtonBackground = recognizeTextButton.getBackground();
        originalButtonText = recognizeTextButton.getText().toString();


        checkMissionStatusAndUpdateButton();


        recognizeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }

        });

        // 바텀 네비게이션 뷰 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(CoffeeMissionActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(CoffeeMissionActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(CoffeeMissionActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(CoffeeMissionActivity.this, DailyMissionActivity.class);
                startActivity(intent);

                // 일간 미션 액티비티가 현재 액티비티라면, 새로운 인텐트를 시작할 필요가 없습니다.
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(CoffeeMissionActivity.this, MyPageActivity.class);
                startActivity(intent);
            } else {
                return false;
            }
            return true;
        });

    }

    private void selectImage() {
        final CharSequence[] options = {"사진 찍기", "갤러리에서 선택하기", "취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CoffeeMissionActivity.this);
        builder.setTitle("이미지 추가 방법 선택");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("사진 찍기")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_PHOTO);
                } else if (options[item].equals("갤러리에서 선택하기")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE);
                } else if (options[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView receiptImageView = findViewById(R.id.receiptImageView);

        if (resultCode == RESULT_OK) {
            InputImage image;
            try {
                switch (requestCode) {
                    case PICK_IMAGE:
                        // 갤러리에서 이미지를 선택한 경우
                        image = InputImage.fromFilePath(this, data.getData());
                        recognizeTextFromImage(image);

                        receiptImageView.setImageURI(data.getData());
                        break;


                    case TAKE_PHOTO:
                        // 사진을 찍은 경우
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        image = InputImage.fromBitmap(bitmap, 0);
                        recognizeTextFromImage(image);
                        receiptImageView.setImageURI(data.getData());
                        break;


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<String> coffeeMenu = new HashSet<>(Arrays.asList("아메리카노","아이스아메리카노", "라떼", "카푸치노", "모카", "에스프레소", "카페라떼",
            "카라멜마끼아또", "바닐라라떼", "카페모카", "아이스티", "레몬에이드", "자몽에이드", "딸기스무디", "망고스무디", "카페모카", "초코라떼",
            "그린티라떼", "민트초코라떼", "아이스그린티라떼", "아이스민트초코라떼", "바닐라아메리카노", "헤이즐넛라떼", "아이스헤이즐넛라떼",
            "카페브레베", "카페모카프라페", "초코칩프라페", "아이스초코칩프라페", "바나나스무디", "파인애플주스", "오렌지주스", "자몽주스")); // 커피 메뉴 리스트 준비

    private void recognizeTextFromImage(InputImage image) {
        TextRecognizer recognizer =
                TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(@NonNull Text result) {
                        String resultText = result.getText();
                        String matchedMenu = null;
                        String date = null;

                        // 날짜 추출
                        Pattern datePattern = Pattern.compile("\\b\\d{4}[-/]\\d{2}[-/]\\d{2}\\b");
                        Matcher dateMatcher = datePattern.matcher(resultText);
                        if (dateMatcher.find()) {
                            date = dateMatcher.group();
                            // date 변수에 날짜가 저장됩니다.
                        }

                        // 현재 날짜 가져오기
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String currentDate = sdf.format(new Date());

                        // 결과를 TextView에 표시
                        TextView resultTextView = findViewById(R.id.resultTextView);

                        if (date != null){ //&& date.equals(currentDate)){//todo 이부분 각주풀면 영수증에 날짜까지 인식합니다.

                            // 한글 메뉴 이름 추출 및 비교
                            Pattern menuPattern = Pattern.compile("[가-힣]+");
                            Matcher menuMatcher = menuPattern.matcher(resultText);
                            while (menuMatcher.find()) {
                                String menuItem = menuMatcher.group();
                                if (coffeeMenu.contains(menuItem)) {
                                    matchedMenu = menuItem; // 일치하는 메뉴를 찾았으므로 변수에 저장
                                    break; // 일치하는 메뉴를 찾았으므로 더 이상 반복할 필요가 없습니다.
                                }
                            }

                            if (matchedMenu != null) {
                                resultTextView.setText("주문하신 " + matchedMenu + " 맛있게 드세요!");
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                updateExp(userId, 100); // 경험치
                                updatePoint(userId, 100); // 포인트

                                DatabaseReference coffeeMissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                                        .child(userId)
                                        .child(currentDate)
                                        .child("coffee");
                                coffeeMissionRef.setValue(true); // 미션 완료 상태로 업데이트

                                recognizeTextButton.setEnabled(false); // 버튼 비활성화

                                // AlertDialog를 사용하여 팝업 메시지 표시
                                new AlertDialog.Builder(CoffeeMissionActivity.this)
                                        .setTitle("미션 완료!")
                                        .setMessage("미션을 성공적으로 완료하셨습니다.")
                                        .setPositiveButton("최고에요!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                restartActivity(); // 액티비티 재시작
                                            }
                                        })
                                        .show();



//                                Intent intent = new Intent(CoffeeMissionActivity.this, MyPageActivity.class);
//                                startActivity(intent);
//                                finish();




                            } else {
                                resultTextView.setText("메뉴를 찾을 수 없습니다.");
                            }
                        } else {
                            resultTextView.setText("오늘 찍은 영수증이 맞는지 다시 한 번 학인해주세요!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 텍스트 인식 실패시 처리
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
                    Toast.makeText(CoffeeMissionActivity.this, "포인트 적립이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(CoffeeMissionActivity.this, "포인트 적립에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // 현재 액티비티를 재시작하는 메서드
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //경험치 올리는 메서드
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
                    Toast.makeText(CoffeeMissionActivity.this, "경험치 및 레벨이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 트랜잭션이 실패했습니다.
                    Toast.makeText(CoffeeMissionActivity.this, "경험치 및 레벨 업데이트에 실패했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkMissionStatusAndUpdateButton() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference coffeeMissionRef = FirebaseDatabase.getInstance().getReference("userMissions")
                .child(userId)
                .child(currentDate)
                .child("coffee");

        coffeeMissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isCoffeeMissionComplete = dataSnapshot.getValue(Boolean.class);
                    if (isCoffeeMissionComplete != null && isCoffeeMissionComplete) {
                        recognizeTextButton.setEnabled(false);
                        recognizeTextButton.setBackground(ContextCompat.getDrawable(CoffeeMissionActivity.this, R.drawable.mission_color_background_complete)); // 새로운 배경 적용
                        recognizeTextButton.setText("내일 또 만나요!"); // 버튼 텍스트를 "CLEAR"로 변경
                    } else {
                        recognizeTextButton.setEnabled(true);
                        restoreButtonToOriginalState();

                        // 필요한 경우, 여기서 버튼의 원래 상태(색상 및 텍스트)로 복원할 수 있습니다.
                    }
                } else {
                    recognizeTextButton.setEnabled(true); // 노드가 없으면 활성화 (미션을 아직 수행하지 않았다고 가정)
                    // 필요한 경우, 여기서 버튼의 원래 상태(색상 및 텍스트)로 복원할 수 있습니다.
                    restoreButtonToOriginalState();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }
    private void restoreButtonToOriginalState() {
        recognizeTextButton.setEnabled(true);
        recognizeTextButton.setBackground(ContextCompat.getDrawable(CoffeeMissionActivity.this, R.drawable.mission_color_background)); // 원래 배경으로 복원
        recognizeTextButton.setText(originalButtonText); // 원래 텍스트로 복원
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

}