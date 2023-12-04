package com.example.logintest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PodcastBoardFragment extends Fragment {

    WebView podcastWebView;
    ListView podcastListView;
    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_board, container, false);

        // WebView 초기화
        podcastWebView = view.findViewById(R.id.podcastWebView);
        WebSettings webSettings = podcastWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // JavaScript 활성화

        // ListView 초기화
        podcastListView = view.findViewById(R.id.PodcastListView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadPodcasts();

        return view;
    }

    private void loadPodcasts() {
        // "podcasts" 그룹 내의 특정 사용자(userID가 "iys04003@naver.com"인) 데이터를 읽어오기
        databaseReference.child("podcasts")
                .orderByChild("userID")
                .equalTo("iys04003@naver.com")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> podcastTitles = new ArrayList<>(); // 수정된 부분

                        // 데이터를 리스트에 추가
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String title = snapshot.child("title").getValue(String.class); // 수정된 부분
                            podcastTitles.add(title); // 수정된 부분
                        }

                        // 리스트뷰에 데이터 표시
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, podcastTitles); // 수정된 부분
                        podcastListView.setAdapter(adapter);

                        // WebView에 첫 번째 유튜브 링크 로드
                        if (!podcastTitles.isEmpty()) {
                            String firstTitle = podcastTitles.get(0);
                            loadYoutubeVideo(firstTitle); // 수정된 부분
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PodcastBoardFragment", "Error reading data from Firebase", error.toException());
                        Toast.makeText(requireContext(), "Error reading data from Firebase", Toast.LENGTH_SHORT).show();
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