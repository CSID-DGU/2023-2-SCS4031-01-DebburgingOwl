package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InformationBoardFragment extends Fragment {

    FloatingActionButton uploadBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_information_board, container, false);

        // 게시글 데이터를 임의로 생성
        List<String> postList = new ArrayList<>();
        postList.add("정보 게시글 1");
        postList.add("정보 게시글 2");
        postList.add("정보 게시글 3");
        postList.add("정보 게시글 4");
        postList.add("정보 게시글 5");
        postList.add("나의 게시글 1");
        postList.add("나의 게시글 2");
        postList.add("나의 게시글 3");


        // ArrayAdapter를 생성하고 리스트뷰에 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, postList);
        ListView listView = rootView.findViewById(R.id.informationListView);
        listView.setAdapter(adapter);

        // SearchView에 검색 이벤트 리스너 추가
        SearchView searchView = rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어를 제출했을 때의 동작
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때마다 호출
                // 어댑터의 getFilter().filter(newText)를 호출하여 리스트를 필터링하고 업데이트
                // adapter.getFilter().filter(newText);

                adapter.getFilter().filter(newText);
                return true;
            }
        });

        uploadBtn = rootView.findViewById(R.id.boardUploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}