package com.example.logintest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InformationBoardFragment extends Fragment {

    FloatingActionButton uploadBtn;
    ListView listView;
    SearchView searchView;
    private DatabaseReference informationsRef;
    private List<Information> informationList;
    private InformationAdapter adapter; // InformationAdapter 객체 선언



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_information_board, container, false);

        // Firebase Realtime Database의 informations 참조 가져오기
        informationsRef = FirebaseDatabase.getInstance().getReference("informations");
        loadInformationData();
        // 게시글 데이터를 임의로 생성
        informationList = new ArrayList<>();
        adapter = new InformationAdapter(getContext(), informationList); // 기존의 adapter를 초기화

        listView = rootView.findViewById(R.id.informationListView);
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
                adapter.getFilter().filter(newText);
                return true;
            }
        });


        // 클릭 이벤트 처리 부분 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트 아이템이 클릭되면 해당 포지션의 데이터 가져오기
                Information selectedItem = (Information) parent.getItemAtPosition(position);

                // 클릭된 아이템에 대한 추가 작업 수행
                // 예를 들면, 선택된 아이템을 다음 액티비티로 전달하여 보여주기
                Intent intent = new Intent(getActivity(), InformationBoardDetailActivity.class);
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });
        // 현재 로그인한 사용자의 ID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            checkUserTypeAndSetButtonVisibility(currentUserId);
        }
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

    private void checkUserTypeAndSetButtonVisibility(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && "MENTOR".equals(user.getUserType())) {
                    uploadBtn.setVisibility(View.VISIBLE);
                } else {
                    uploadBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("InformationBoardFragment", "Error checking user type", databaseError.toException());
            }
        });
    }
    private void loadInformationData() {
        informationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                informationList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 각각의 데이터를 Information 객체로 변환하여 리스트에 추가
                    Information information = snapshot.getValue(Information.class);
                    if (information != null) {
                        informationList.add(information);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽어오기를 실패한 경우
                Toast.makeText(getContext(), "데이터 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                // 로그에 에러 메시지 출력
                Log.e("InformationBoardFragment", "데이터 불러오기 실패", databaseError.toException());
                Log.e("InformationBoardFragment", "Details: " + databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }
}