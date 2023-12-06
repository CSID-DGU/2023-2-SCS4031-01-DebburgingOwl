package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class InformationBoardDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton bookmarkBtn;
    TextView title;
    TextView content;
    ImageView infoUserPicture;
    TextView infoUserType;
    TextView infoUserNickname;
    Button mentorInfoBtn;
    BottomSheetFragment bottomSheet;
    private String currentUserId;
    private Information selectedInformation;



    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference bookmarksRef = database.getReference("Bookmark");

    private DatabaseReference informaionsRef = database.getReference("informations");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_board_detail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        backBtn = findViewById(R.id.infoBackBtn);
        bookmarkBtn = findViewById(R.id.infoBookmarkBtn);
        title = findViewById(R.id.infoTitle);
        content = findViewById(R.id.infoContent);
        infoUserPicture = findViewById(R.id.infoUserPicture);
        infoUserType = findViewById(R.id.infoUserType);
        infoUserNickname = findViewById(R.id.infoUserNickname);
        mentorInfoBtn = findViewById(R.id.mentorInfoBtn);


        // Intent로부터 전달받은 Information 객체를 가져옴
        Information selectedInformation = (Information) getIntent().getSerializableExtra("selectedInformation");

        if (selectedInformation == null) {
            // Information 객체가 null인 경우 예외 처리
            Toast.makeText(InformationBoardDetailActivity.this, "게시물을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료 또는 다른 처리 수행
            return;
        }

        checkAndUpdateBookmarkStatus(selectedInformation);


        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 북마크 버튼 클릭 처리
                handleBookmarkButtonClick(selectedInformation);
            }
        });

        // 가져온 정보를 TextView에 설정
        title.setText(selectedInformation.getTitle());
        content.setText(selectedInformation.getContent());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 액티비티를 종료하여 뒤로가기 역할
                finish();
            }
        });

        mentorInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // mentorInfoBtn 클릭 이벤트 설정
            public void onClick(View view) {
                // BottomSheetFragment 인스턴스 생성
                bottomSheet = new BottomSheetFragment();
                // BottomSheetFragment를 화면에 표시
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.board);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent navigationIntent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, BoardActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.community) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, CommunityActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.home) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, MainActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.daily_mission) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, DailyMissionActivity.class);
                startActivity(navigationIntent);
                return true;
            } else if (itemId == R.id.mypage) {
                navigationIntent = new Intent(InformationBoardDetailActivity.this, MyPageActivity.class);
                startActivity(navigationIntent);
                return true;
            } else {
                return false;
            }
        });
    }

    private void handleBookmarkButtonClick(Information selectedInformation) {
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

                        if (bookmark != null && bookmark.getContentKey().equals(selectedInformation.getTitle())) {
                            // 이미 북마크가 있는 경우 setIsBookmarked 값을 변경
                            boolean isBookmarked = !bookmark.getIsBookmarked();
                            bookmark.setIsBookmarked(isBookmarked);
                            bookmarkSnapshot.getRef().setValue(bookmark);
                            updateBookmarkStatus(selectedInformation, isBookmarked);
                            return;
                        }
                    }

                    // 북마크가 없는 경우 새로운 북마크를 추가
                    addNewBookmark(selectedInformation);
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmarked);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 오류 처리
                    Toast.makeText(InformationBoardDetailActivity.this, "북마크 정보를 확인하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 북마크를 추가하는 메서드
    private void addNewBookmark(Information selectedInformation) {
        if (currentUserId == null) {
            // 사용자 ID가 없을 경우 처리 (예: 로그인이 되어있지 않은 상태)
            return;
        }
        Bookmark newBookmark = new Bookmark();
        newBookmark.setContentType("Information");
        newBookmark.setUserID(currentUserId);
        newBookmark.setContentKey(selectedInformation.getTitle());
        newBookmark.setIsBookmarked(true);

        // Firebase Realtime Database에 새로운 북마크 추가
        String bookmarkKey = bookmarksRef.push().getKey();
        bookmarksRef.child(bookmarkKey).setValue(newBookmark);

    }

    private void checkAndUpdateBookmarkStatus(Information selectedInformation) {
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

                        if (bookmark != null && bookmark.getContentKey().equals(selectedInformation.getTitle())) {
                            // 이미 북마크가 있는 경우 setIsBookmarked 값을 확인하여 UI 업데이트
                            boolean isBookmarked = bookmark.getIsBookmarked();
                            updateBookmarkStatus(selectedInformation, isBookmarked);
                            return;
                        }
                    }

                    // 북마크가 없는 경우
                    updateBookmarkStatus(selectedInformation, false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 오류 처리
                    Toast.makeText(InformationBoardDetailActivity.this, "북마크 정보를 확인하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateBookmarkStatus(Information selectedInformation, boolean isBookmarked) {
        int currentBookmarks = selectedInformation.getBookmarks();
        if (isBookmarked) {
            // 북마크 추가
            bookmarkBtn.setImageResource(R.drawable.ic_bookmarked);
            currentBookmarks++;
        } else {
            // 북마크 제거
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark);
            currentBookmarks--;
        }
        selectedInformation.setBookmarks(currentBookmarks);



    }
}