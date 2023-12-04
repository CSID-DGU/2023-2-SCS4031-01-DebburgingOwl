package com.example.logintest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private List<DiaryEntry> diaryEntries;

    public DiaryAdapter(List<DiaryEntry> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }
    public interface OnItemClickListener {
        void onItemClick(DiaryEntry diaryEntry);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry diaryEntry = diaryEntries.get(position);
        holder.textViewTitle.setText(diaryEntry.getTitle());


        try {
            // 원본 타임스탬프 포맷 (예: "yyyy-MM-dd HH:mm:ss")
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // 변경할 포맷 (년월일)
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date date = originalFormat.parse(diaryEntry.getTimestamp());
            String formattedDate = targetFormat.format(date);

            holder.textViewDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.textViewDate.setText(diaryEntry.getTimestamp()); // 포맷 변환 실패 시 원본 타임스탬프 사용
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(diaryEntries.get(holder.getAdapterPosition()));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDate;

        DiaryViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }


}
