package com.example.logintest;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList = new ArrayList<>();
    private String currentUserId;
    private Context context; // Context 추가
    private String imageId; // Image ID 추가
    public CommentAdapter(Context context, String currentUserId, String imageId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.imageId = imageId;
    }


    // 댓글 데이터를 담을 ViewHolder 클래스
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNickname;
        public TextView textViewComment;
        public TextView textViewDate;
        public ImageButton buttonDeleteComment; // 삭제 버튼

        public CommentViewHolder(View itemView) {
            super(itemView);
            textViewNickname = itemView.findViewById(R.id.textViewNickname);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonDeleteComment = itemView.findViewById(R.id.buttonDeleteComment);
        }
    }

    // 새로운 뷰 홀더 생성
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    // 뷰 홀더의 내용을 대체 (레이아웃에 데이터 바인딩)
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = commentList.get(position);
        holder.textViewNickname.setText(currentComment.getNickname());
        holder.textViewComment.setText(currentComment.getCommentText());
        holder.textViewDate.setText(currentComment.getCurrentDate());

        // 안전한 null 체크를 수행
        if (currentUserId != null && currentComment.getUserId() != null && currentUserId.equals(currentComment.getUserId())) {
            holder.buttonDeleteComment.setVisibility(View.VISIBLE);
            holder.buttonDeleteComment.setOnClickListener(v -> deleteComment(currentComment.getCommentId(), position));
        } else {
            holder.buttonDeleteComment.setVisibility(View.GONE);
        }
    }

    // 댓글 삭제 메서드
    private void deleteComment(String commentId, int position) {
        if (position < 0 || position >= commentList.size()) {
            // 인덱스가 유효하지 않음
            Toast.makeText(context, "삭제할 댓글을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(imageId);
        commentsRef.child(commentId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // UI 스레드에서 실행
                ((Activity) context).runOnUiThread(() -> {
                    // 댓글 목록에서 해당 댓글 삭제
                    if (position < commentList.size()) {
                        commentList.remove(position);
                        notifyItemRemoved(position);
                    }
                    Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                });
            } else {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "댓글 삭제 실패.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }




    // 데이터 세트의 크기 반환
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // 외부에서 댓글 데이터를 설정할 수 있는 메서드
    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

}
