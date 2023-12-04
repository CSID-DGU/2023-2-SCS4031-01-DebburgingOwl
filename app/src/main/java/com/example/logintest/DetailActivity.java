
package com.example.logintest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Picasso 라이브러리 임포트
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vane.badwordfiltering.BadWordFiltering;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    // 뷰들을 위한 변수 선언
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private ImageView imageViewDetail;
    private TextView textViewUploader, textViewDescription;
    private Switch switchPublicPrivate;
    private String imageId; // 이미지의 고유 ID를 저장할 변수
    private ImageButton btnLike;
    private ImageButton btnComment; // 댓글 버튼
    private EditText editTextComment; // 댓글 입력 필드
    private Button buttonSubmitComment, buttonDeletePost; // 게시 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        btnLike = findViewById(R.id.btnLike1);
        btnComment = findViewById(R.id.btnComment);
        buttonDeletePost = findViewById(R.id.buttonDeletePost); // XML 레이아웃에 정의된 삭제 버튼을 찾습니다.

        // RecyclerView 초기화
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        editTextComment = findViewById(R.id.editTextComment);
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);
        // 현재 로그인한 사용자의 ID 가져오기
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        commentAdapter = new CommentAdapter(this, currentUserId, imageId);
        recyclerViewComments.setAdapter(commentAdapter);

        // 뷰 찾기
        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewUploader = findViewById(R.id.textViewUploader);
        textViewDescription = findViewById(R.id.textViewDescription);
        // 인텐트에서 업로더 ID 가져오기
        String uploaderId = getIntent().getStringExtra("uploaderId");
        // 인텐트에서 이미지 URL 가져오기
        String imageUrl = getIntent().getStringExtra("imageUrl");
        imageId = getIntent().getStringExtra("imageId"); // 인텐트에서 이미지 ID 가져오기

        // Picasso를 사용하여 이미지 로딩 및 표시$
        Glide.with(this)
                .load(imageUrl)
                .into(imageViewDetail);
        switchPublicPrivate = findViewById(R.id.switchPublicPrivate);




        // 업로더 ID와 현재 사용자 ID 비교
        if (uploaderId != null && uploaderId.equals(currentUserId)) {
            // 현재 사용자가 업로더일 경우 스위치 표시
            switchPublicPrivate.setVisibility(View.VISIBLE);
            buttonDeletePost.setVisibility(View.VISIBLE);
            switchPublicPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // 여기에 스위치 상태 변경 시 로직 추가
                updateImagePublicStatus(isChecked);

            });

        } else {
            // 현재 사용자가 업로더가 아닐 경우 스위치 숨기기
            switchPublicPrivate.setVisibility(View.GONE);
            buttonDeletePost.setVisibility(View.GONE);

        }
        buttonDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시물 삭제 확인 대화상자 표시
                showDeleteConfirmationDialog(imageId);
            }
        });

        // ... 나머지 코드
        setupLikeButton(btnLike, imageId);
        // CommentAdapter 초기화
        commentAdapter = new CommentAdapter(this, currentUserId, imageId);
        recyclerViewComments.setAdapter(commentAdapter);

        // 추가 데이터 설정 (예: 업로더 이름, 이미지 설명)
        // 여기서는 예시로 텍스트를 설정합니다. 실제 앱에서는 Firebase에서 데이터를 가져와야 할 수 있습니다.
        textViewUploader.setText("Uploader Name");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uploaderId).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.getValue(String.class);
                textViewUploader.setText("photo by "+nickname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.board) {
                intent = new Intent(DetailActivity.this, BoardActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.community) {
                intent = new Intent(DetailActivity.this, CommunityActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.daily_mission) {
                intent = new Intent(DetailActivity.this, DailyMissionActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.mypage) {
                intent = new Intent(DetailActivity.this, MyPageActivity.class);
                startActivity(intent);
                return true;


            } else {
                return false;
            }
            return true;
        });
        buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = editTextComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    postComment(commentText);
                } else {
                    Toast.makeText(DetailActivity.this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewComments.getVisibility() == View.GONE) {
                    recyclerViewComments.setVisibility(View.VISIBLE);
                    loadComments(); // 댓글 데이터 불러오기
                } else {
                    recyclerViewComments.setVisibility(View.GONE);
                }
            }
        });

    }
    private void showDeleteConfirmationDialog(String imageId) {
        new AlertDialog.Builder(this)
                .setTitle("게시물 삭제")
                .setMessage("이 게시물을 삭제하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(imageId); // 사용자가 확인을 누를 경우 게시물 삭제
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
    private void deletePost(String imageId) {
        // Firebase Database에서 해당 게시물의 참조를 가져옵니다.
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("uploads").child(imageId);

        // 게시물을 삭제합니다.
        postRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // 삭제가 성공적으로 완료되었을 때
                    Toast.makeText(DetailActivity.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료 또는 다른 화면으로 이동
                })
                .addOnFailureListener(e -> {
                    // 삭제에 실패했을 때
                    Toast.makeText(DetailActivity.this, "게시물 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setupLikeButton(ImageButton button, String imageId) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes").child(imageId);
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("uploads").child(imageId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 좋아요 상태를 초기화
        likesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 이미 좋아요를 눌렀다면 아이콘 변경
                    button.setImageResource(R.drawable.ic_liked);
                } else {
                    // 좋아요를 누르지 않았다면 기본 아이콘으로 설정
                    button.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });

        // 좋아요 버튼 클릭 이벤트 처리
        button.setOnClickListener(view -> {
            likesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 이미 좋아요를 눌렀다면 좋아요 취소 및 아이콘 변경
                        likesRef.child(userId).removeValue();
                        button.setImageResource(R.drawable.ic_like);
                        recordUserActivity(userId, false,true); // 좋아요 취소
                        updateImageLikesCount(imageRef, false); // 좋아요 수 감소


                    } else {
                        // 좋아요를 누르지 않았다면 좋아요 추가 및 아이콘 변경
                        likesRef.child(userId).setValue(true);
                        button.setImageResource(R.drawable.ic_liked);
                        recordUserActivity(userId, true,true); // 좋아요 추가
                        updateImageLikesCount(imageRef, true); // 좋아요 수 증가


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 오류 처리
                }
            });
        });
    }
    private void updateImageLikesCount(DatabaseReference imageRef, boolean increment) {
        imageRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ImageModel image = mutableData.getValue(ImageModel.class);
                if (image == null) {
                    return Transaction.success(mutableData);
                }

                int likesCount = image.getLikesCount();
                if (increment) {
                    likesCount++; // 좋아요 증가
                } else {
                    likesCount = Math.max(likesCount - 1, 0); // 좋아요 감소, 음수 방지
                }
                image.setLikesCount(likesCount);

                mutableData.setValue(image);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                // 트랜잭션이 완료된 후에 수행할 작업
            }
        });
    }
    private void updateImagePublicStatus(boolean publicStatus) {
        if (imageId == null) {
            Toast.makeText(this, "Image ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("uploads").child(imageId);

        // 이미지의 'publicStatus' 필드 업데이트
        imageRef.child("publicStatus").setValue(publicStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(DetailActivity.this, "Visibility updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DetailActivity.this, "Failed to update visibility", Toast.LENGTH_SHORT).show());
    }
    private void postComment(String commentText) {
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        List<String> bannedWords = Arrays.asList("비속어1", "비속어2", "비속어3"); // 실제 비속어 목록으로 채워야 함

        // 댓글에서 비속어 검사
        for (String word : bannedWords) {
            if (badWordFiltering.check(commentText)||badWordFiltering.blankCheck(commentText)==true ) {
                Toast.makeText(DetailActivity.this, "비속어가 포함된 댓글은 게시할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return; // 비속어가 포함되어 있으면 더 이상 진행하지 않고 메서드 종료
            }
        }
        // 현재 로그인한 사용자의 ID를 가져옵니다
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firebase Database에서 사용자의 닉네임을 조회합니다
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 닉네임을 가져옵니다
                String nickname = dataSnapshot.getValue(String.class);
                if (nickname == null) {
                    nickname = "익명"; // 닉네임이 설정되지 않은 경우 기본값
                }

                // 현재 시간을 가져옵니다
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Firebase Database 참조를 가져옵니다
                DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(imageId);

                // 고유한 키를 생성합니다
                String commentId = commentsRef.push().getKey();

                // 새 댓글 객체를 생성합니다 (아직 고유 ID를 설정하지 않음)
                Comment comment = new Comment(commentId,currentUserId, nickname, commentText, currentDate);

                // 댓글을 Database에 저장합니다 (이때 commentId가 설정됨)
                commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailActivity.this, "댓글이 게시되었습니다", Toast.LENGTH_SHORT).show();
                        recordUserActivity(currentUserId, true,false); // 댓글 추가

                    } else {
                        Toast.makeText(DetailActivity.this, "댓글 게시에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터베이스 오류 처리
                Toast.makeText(DetailActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(imageId);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.setComments(commentList); // 어댑터에 데이터 세트 변경 알림
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void recordUserActivity(String userId, boolean isActivity, boolean isLike) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference("userActivity").child(userId).child(currentDate);

        activityRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                UserActivity userActivity = mutableData.getValue(UserActivity.class);
                if (userActivity == null) {
                    userActivity = new UserActivity(0, 0); // 초기화
                }

                if (isActivity) {
                    userActivity.incrementLikesOrComments(isLike);
                } else {
                    userActivity.decrementLikesOrComments(isLike);
                }

                mutableData.setValue(userActivity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                // 데이터 업데이트 후 추가 처리 가능
            }
        });
    }
    public interface UserActivityRecorder {
        void recordUserActivity(String userId, boolean isActivity, boolean isLike);
    }





}
