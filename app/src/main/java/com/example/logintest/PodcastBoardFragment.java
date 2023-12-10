package com.example.logintest;

import android.content.Intent;
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
    List<Podcast> podcastList = new ArrayList<>();




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
                        List<Podcast> podcastList = new ArrayList<>();

                        // 데이터를 리스트에 추가
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String title = snapshot.child("title").getValue(String.class);
                            String description = snapshot.child("description").getValue(String.class);
                            String userID = snapshot.child("userID").getValue(String.class);
                            String youtubeLink = snapshot.child("youtubeLink").getValue(String.class);

                            // Podcast 객체를 생성하여 리스트에 추가
                            Podcast podcast = new Podcast(title, description, userID, youtubeLink);
                            podcastList.add(podcast);
                        }

                        // 리스트뷰에 데이터 표시
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, getTitles(podcastList));
                        podcastListView.setAdapter(adapter);

                        // WebView에 첫 번째 유튜브 링크 로드
                        if (!podcastList.isEmpty()) {
                            Podcast firstItem = podcastList.get(0);
                            loadYoutubeVideo(firstItem.getYoutubeLink());

                            // ListView 아이템 클릭 이벤트 설정
                            podcastListView.setOnItemClickListener((parent, view, position, id) -> {
                                // 클릭된 아이템의 데이터 가져오기
                                Podcast selectedPodcast = podcastList.get(position);

                                // 디테일 페이지로 이동하는 메서드 호출
                                navigateToDetailPage(selectedPodcast);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PodcastBoardFragment", "Error reading data from Firebase", error.toException());
                        Toast.makeText(requireContext(), "Error reading data from Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Podcast 리스트에서 title 목록을 추출하는 메서드
    private List<String> getTitles(List<Podcast> podcasts) {
        List<String> titles = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            if (podcast != null && podcast.getTitle() != null) {
                titles.add(podcast.getTitle());
            }
        }
        return titles;
    }



    private void loadYoutubeVideo(String youtubeLink) {
        if (youtubeLink != null) {
            String youtubeVideoUrl = "https://www.youtube.com/embed/" + extractVideoId(youtubeLink);
            String html = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + youtubeVideoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
            podcastWebView.loadData(html, "text/html", null);
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

    // 디테일 페이지로 이동하는 메서드
    private void navigateToDetailPage(Podcast podcast) {
        Intent intent = new Intent(requireContext(), PodcastDetailActivity.class);
        intent.putExtra("podcast", podcast); // Podcast 객체를 디테일 페이지로 전달
        startActivity(intent);
    }
}