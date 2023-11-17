package com.example.logintest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PodcastBoardFragment extends Fragment {

    WebView podcastBtn;
    ListView PodcastListView;
    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_board, container, false);

        podcastBtn = view.findViewById(R.id.podcastBtn);
        PodcastListView = view.findViewById(R.id.PodcastListView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadPodcasts();

        return view;
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

                // 리스트뷰에 데이터 표시
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, podcastLinks);
                PodcastListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                
            }
        });
    }
}