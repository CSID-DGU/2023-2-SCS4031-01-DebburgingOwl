package com.example.logintest;


import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logintest.Podcast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PodcastDetailActivity extends AppCompatActivity {

    private WebView podcastDetailWebView;
    private Button podcastBackBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);

        podcastDetailWebView = findViewById(R.id.podcastDetailBtn);
        podcastBackBtn = findViewById(R.id.podcastBackBtn);


        WebSettings webSettings = podcastDetailWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // JavaScript 활성화

        // 인텐트에서 Podcast 데이터 가져오기
        Podcast selectedPodcast = (Podcast) getIntent().getSerializableExtra("podcast");

        if (selectedPodcast != null) {
            // Podcast 데이터를 사용하여 WebView 업데이트
            loadYoutubeVideo(selectedPodcast.getYoutubeLink());
        }

        // "podcastBackBtn" 버튼 클릭 이벤트 처리
        podcastBackBtn.setOnClickListener(view -> onBackPressed());
    }

    private void loadYoutubeVideo(String youtubeLink) {
        if (youtubeLink != null) {
            String youtubeVideoUrl = "https://www.youtube.com/embed/" + extractVideoId(youtubeLink);
            String html = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + youtubeVideoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
            podcastDetailWebView.loadData(html, "text/html", null);
        } else {
            // youtubeLink가 null인 경우에 대한 처리
        }
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
