package com.example.logintest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

public class PodcastBoardFragment extends Fragment {

    WebView podcastBtn;
    ListView PodcastListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_board, container, false);

        podcastBtn = view.findViewById(R.id.podcastBtn);
        PodcastListView = view.findViewById(R.id.PodcastListView);

        return view;
    }
}