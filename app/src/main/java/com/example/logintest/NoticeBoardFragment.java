package com.example.logintest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoticeBoardFragment extends Fragment {

    ListView listView;
    SearchView searchView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 레이아웃을 inflate하고 그 결과를 view 변수에 저장
        View view = inflater.inflate(R.layout.fragment_notice_board, container, false);

        // 게시글 데이터를 임의로 생성
        List<String> postList = new ArrayList<>();
        postList.add("공지 게시글 1");
        postList.add("공지 게시글 2");
        postList.add("공지 게시글 3");
        postList.add("공지 게시글 4");
        postList.add("공지 게시글 5");
        postList.add("나의 게시글 1");
        postList.add("나의 게시글 2");
        postList.add("나의 게시글 3");

        // ArrayAdapter를 생성하고 리스트뷰에 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, postList);
        listView = view.findViewById(R.id.informationListView);
        listView.setAdapter(adapter);

        // SearchView에 검색 이벤트 리스너 추가
        searchView = view.findViewById(R.id.search_view);
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
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // 클릭 이벤트 처리 부분 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트 아이템이 클릭되면 해당 포지션의 데이터 가져오기
                String selectedItem = (String) parent.getItemAtPosition(position);

                // 클릭된 아이템에 대한 추가 작업 수행
                // 예를 들면, 선택된 아이템을 다음 액티비티로 전달하여 보여주기
                Intent intent = new Intent(getActivity(), NoticeBoardDetailActivity.class);
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });

        return view;
    }

    public class InformationAdapter extends ArrayAdapter<String> {
        private List<String> originalData;
        private List<String> filteredData;
        private LayoutInflater inflater;

        public InformationAdapter(Context context, List<String> data) {
            super(context, android.R.layout.simple_list_item_1, data);
            this.originalData = new ArrayList<>(data);
            this.filteredData = new ArrayList<>(data);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            // 게시글 텍스트를 가져와서 설정
            String item = getItem(position);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(item);

            return view;
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public String getItem(int position) {
            return filteredData.get(position);
        }

        public void filter(String query) {
            filteredData.clear();
            query = query.toLowerCase(Locale.getDefault());

            if (query.length() == 0) {
                filteredData.addAll(originalData);
            } else {
                for (String item : originalData) {
                    if (item.toLowerCase(Locale.getDefault()).contains(query)) {
                        filteredData.add(item);
                    }
                }
            }

            notifyDataSetChanged();
        }
    }
}