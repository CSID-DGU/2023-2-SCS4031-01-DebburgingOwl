package com.example.logintest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class CoffeeMissionActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_mission);

        Button recognizeTextButton = findViewById(R.id.recognizeTextButton);


        recognizeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.daily_mission);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(CoffeeMissionActivity.this, BoardActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(CoffeeMissionActivity.this, CommunityActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(CoffeeMissionActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(CoffeeMissionActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(CoffeeMissionActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
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

    private Set<String> coffeeMenu = new HashSet<>(Arrays.asList("아메리카노","아이스아메리카노", "라떼", "카푸치노", "모카", "에스프레소","한우 샤브")); // 커피 메뉴 리스트 준비

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

                        if (date != null){ // && date.equals(currentDate)) {
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
                                resultTextView.setText("주문하신 " + matchedMenu + " 맛있게 드세요!" );
                                Toast.makeText(getApplicationContext(), "미션 성공!!", Toast.LENGTH_SHORT).show();
                                //TODO:이후 미션달성여부를 사용자 개인의 데이터베이스등과 연동해야 함.



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



}