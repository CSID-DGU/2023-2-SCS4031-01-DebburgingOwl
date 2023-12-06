package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NoticeBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton bookmarkBtn;
    TextView title;
    TextView content;

    private String currentUserId;


    // Firebase 초기화
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference bookmarksRef = database.getReference("Bookmark");
    private DatabaseReference noticesRef = database.getReference("notices");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_detail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        backBtn = (ImageButton) findViewById(R.id.noticeBackBtn);
        bookmarkBtn =(ImageButton) findViewById(R.id.noticeBookmarkBtn);
        title = (TextView) findViewById(R.id.noticeTitle);
        content = (TextView) findViewById(R.id.noticeContent);

        // Intent로부터 전달받은 Notice 객체를 가져옴
        Notice selectedNotice = (Notice) getIntent().getSerializableExtra("selectedNotice");

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 북마크 버튼 클릭 처리
                handleBookmarkButtonClick(selectedNotice);
            }
        });

        // Notice 객체의 데이터를 화면에 표시
        if (selectedNotice != null) {
            title.setText(selectedNotice.getTitle());
            content.setText(selectedNotice.getContent());
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 액티비티를 종료하여 뒤로가기
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.board);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(NoticeBoardDetailActivity.this, BoardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                return true;
            } else if (itemId == R.id.community) {
                intent = new Intent(NoticeBoardDetailActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.home) {
                intent = new Intent(NoticeBoardDetailActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(NoticeBoardDetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else if (itemId == R.id.mypage) {
                intent = new Intent(NoticeBoardDetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            } else {
                return false;
            }
        });
    }
    // 다른 액티비티로 이동하는 메서드
    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(NoticeBoardDetailActivity.this, cls);
        startActivity(intent);
    }

    private void handleBookmarkButtonClick(Notice selectedNotice) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Firebase Realtime Database에서 현재 사용자의 북마크 정보를 확인
            bookmarksRef.orderByChild("userID").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {
                        Bookmark bookmark = bookmarkSnapshot.getValue(Bookmark.class);

                        if (bookmark != null && bookmark.getContentKey().equals(selectedNotice.getTitle())) {
                            // 이미 북마크가 있는 경우 setIsBookmarked 값을 변경
                            boolean isBookmarked = !bookmark.getIsBookmarked();
                            bookmark.setIsBookmarked(isBookmarked);
                            bookmarkSnapshot.getRef().setValue(bookmark);
                            updateBookmarkStatus(selectedNotice, isBookmarked);

                            return;
                        }
                    }

                    // 북마크가 없는 경우 새로운 북마크를 추가
                    addNewBookmark(selectedNotice);
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmarked);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 오류 처리
                    Toast.makeText(NoticeBoardDetailActivity.this, "북마크 정보를 확인하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 북마크를 추가하는 메서드
    private void addNewBookmark(Notice selectedNotice) {
        if (currentUserId == null) {
            return;
        }

        // Notice 객체의 북마크 수를 업데이트
        int currentBookmarks = selectedNotice.getBookmarks();
        currentBookmarks++;
        selectedNotice.setBookmarks(currentBookmarks);

        Bookmark newBookmark = new Bookmark();
        newBookmark.setContentType("Notice");
        newBookmark.setUserID(currentUserId);
        newBookmark.setContentKey(selectedNotice.getTitle());
        newBookmark.setIsBookmarked(true);

        // Firebase Realtime Database에 새로운 북마크 추가
        String bookmarkKey = bookmarksRef.push().getKey();
        bookmarksRef.child(bookmarkKey).setValue(newBookmark);


        Toast.makeText(this, "북마크가 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }


    private void updateBookmarkStatus(Notice selectedNotice, boolean isBookmarked) {
        int currentBookmarks = selectedNotice.getBookmarks();
        if (isBookmarked) {
            // 북마크 추가
            bookmarkBtn.setImageResource(R.drawable.ic_bookmarked);
            currentBookmarks++;
        } else {
            // 북마크 제거
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark);
            currentBookmarks--;
        }
        selectedNotice.setBookmarks(currentBookmarks);



    }
}