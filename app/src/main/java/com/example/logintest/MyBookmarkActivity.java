package com.example.logintest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookmarkActivity extends AppCompatActivity {

    private ListView bookmarkListView;
    private List<String> bookmarkTitles;
    private ArrayAdapter<String> adapter;

    // Firebase 초기화
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference bookmarksRef = database.getReference("Bookmark");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);

        bookmarkListView = findViewById(R.id.bookmarkListView);
        bookmarkTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookmarkTitles);
        bookmarkListView.setAdapter(adapter);

        // 마이페이지에서 북마크한 게시물의 제목을 가져오는 메서드 호출
        loadBookmarkTitles();

        // 리스트뷰 아이템 클릭 이벤트 설정
        bookmarkListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTitle = bookmarkTitles.get(position);
            // 선택된 아이템의 상세 정보를 보여주는 액티비티로 이동하는 메서드 호출
            navigateToDetailActivity(selectedTitle);
        });
    }

    // 북마크한 게시물의 제목을 가져오는 메서드
    private void loadBookmarkTitles() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // 현재 사용자의 북마크 정보를 가져와서 제목만 리스트에 추가
            bookmarksRef.orderByChild("userID").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {
                        Bookmark bookmark = bookmarkSnapshot.getValue(Bookmark.class);

                        if (bookmark != null && bookmark.getIsBookmarked()) {
                            String contentKey = bookmark.getContentKey();
                            bookmarkTitles.add(contentKey);
                        }
                    }
                    // 리스트뷰 갱신
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 오류 처리
                    Toast.makeText(MyBookmarkActivity.this, "북마크 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 게시물의 상세 정보를 보여주는 액티비티로 이동하는 메서드
    private void navigateToDetailActivity(String selectedTitle) {
        // TODO: 선택된 게시물의 상세 정보를 보여주는 액티비티로 이동하는 코드 추가
        // 예를 들어, NoticeBoardDetailActivity로 이동하는 코드를 작성
    }
}
