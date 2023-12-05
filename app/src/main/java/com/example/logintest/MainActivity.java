package com.example.logintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Handler handler;
    ViewPager2 viewPager;
    Runnable slideRunnable;

    private ImageView imagePlaceholder;
    private ImageAdapter imageAdapter;
    WebView podcastWebView;
    private ImageModel mainImageModel;
    DatabaseReference databaseReference;
    // 상수 추가
    private static final long DELAY_MS = 3000;
    private static final long PERIOD_MS = 3000;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        // LayoutInflater 가져오기
        LayoutInflater inflater = getLayoutInflater();
        imagePlaceholder = findViewById(R.id.imagePlaceholder);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        // WebView 초기화
        podcastWebView = findViewById(R.id.podcastWebView);
        WebSettings webSettings = podcastWebView.getSettings();

        webSettings.setJavaScriptEnabled(true); // JavaScript 활성화
        loadTopLikedImage();
        imagePlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainImageModel != null) {
                    startDetailActivity();
                }
            }
        });


        loadPodcasts();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(MainActivity.this, BoardActivity.class);
                startActivity(intent);

                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);

                return true;
            } else if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(MainActivity.this, DailyMissionActivity.class);
                startActivity(intent);

                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else {
                return false;
            }
        });


        viewPager = findViewById(R.id.viewPager);

        List<String> quotesList = Arrays.asList(
                "실패는 성공으로 가는 임시적인 지체에 불과하다",
                "네 생각을 바꾸면 네 세계가 바뀐다",
                "할 수 있다고 믿는 것은 반쯤 성공한 것이다",
                "끝까지 포기하지 않는 것이 성공이다",
                "오늘 할 수 있는 일을 내일로 미루지 마라",
                "작은 기회로부터 종종 위대한 업적이 시작된다",
                "인생에서 가장 중요한 것은 결코 굴복하지 않는 것이다",
                "자신감은 성공의 첫번째 비밀이다",
                "목표를 세우고 도전하라. 그것이 삶을 살아가는 방식이다",
                "용기는 두려움에 맞서는 것이지, 두려움이 없는 것이 아니다"
        );

        QuotesAdapter adapter = new QuotesAdapter(quotesList);
        viewPager.setAdapter(adapter);

        // 자동 슬라이드 코드 시작
        handler = new Handler(Looper.getMainLooper());
        slideRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                } else {
                    viewPager.setCurrentItem(0, true); // 마지막 페이지에서 첫 페이지로 롤백
                }
                handler.postDelayed(this, PERIOD_MS);
            }
        };


    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(slideRunnable, DELAY_MS); // 앱이 화면에 보일 때 자동 슬라이드 시작
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(slideRunnable); // 앱이 화면에서 벗어날 때 자동 슬라이드 중지
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000); // 2초 동안 대기
    }
    private void loadTopLikedImage() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        databaseRef.orderByChild("likesCount").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel image = snapshot.getValue(ImageModel.class);
                    if (image != null) {
                        mainImageModel = image;
                        mainImageModel.setImageId(snapshot.getKey());
                        displayImage(image.getImageUrl());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }
    private void startDetailActivity() {
        if (mainImageModel != null) {
            String imageUrl = mainImageModel.getImageUrl();
            String uploaderId = mainImageModel.getUploader();
            String imageId = mainImageModel.getImageId();
            String nullFields = "";

            if (imageUrl == null) {
                nullFields += "imageUrl ";
            }
            if (uploaderId == null) {
                nullFields += "uploaderId ";
            }
            if (imageId == null) {
                nullFields += "imageId ";
            }

            if (nullFields.isEmpty()) {
                // 모든 필드가 유효한 경우에만 Intent 시작
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra("imageUrl", imageUrl);
                detailIntent.putExtra("uploaderId", uploaderId);
                detailIntent.putExtra("imageId", imageId);
                startActivity(detailIntent);
            } else {
                // 어느 필드가 null인지 토스트 메시지로 표시
                Toast.makeText(MainActivity.this, nullFields + "이(가) null입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void displayImage(String imageUrl) {
        // Glide를 사용하여 ImageView에 이미지 로드
        Glide.with(MainActivity.this).load(imageUrl).into(imagePlaceholder);
    }
    private void loadPodcasts() {
        // "podcasts" 그룹 내의 데이터를 읽어오기
        databaseReference.child("podcasts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> podcastLinks = new ArrayList<>();

                // 데이터를 리스트에 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String youtubeLink = snapshot.child("youtubeLink").getValue(String.class);
                    podcastLinks.add(youtubeLink);
                }

                // WebView에 첫 번째 유튜브 링크 로드
                if (!podcastLinks.isEmpty()) {
                    String firstYoutubeLink = podcastLinks.get(0);
                    loadYoutubeVideo(firstYoutubeLink);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Error reading data from Firebase", error.toException());
                Toast.makeText(MainActivity.this, "Error reading data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadYoutubeVideo(String youtubeLink) {
        // 유튜브 동영상을 로드하기 위한 코드
        String youtubeVideoUrl = "https://www.youtube.com/embed/" + extractVideoId(youtubeLink);
        String html = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + youtubeVideoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        podcastWebView.loadData(html, "text/html", null);
    }

    private String extractVideoId(String youtubeLink) {
        // 유튜브 링크에서 동영상 ID 추출
        String videoId = "";
        if (youtubeLink != null && youtubeLink.trim().length() > 0) {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed&v=|%2Fv%2F|(?<=youtu.be%2F|youtu.be%2F|.be%2F|(?<=\\?v=|&v=|\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\/|youtu.be\\/|%2Fv%2F)|/videos/|embed\\/|youtu.be\\/|(?<=\\?v=|&v=|\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\/|youtu.be\\/|%2Fv%2F)|/videos/|embed\\/|youtu.be\\/|%2Fv%2F))([\\w-]{11})";
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(youtubeLink);
            if (matcher.find()) {
                videoId = matcher.group();
            }
        }
        return videoId;
    }



}

