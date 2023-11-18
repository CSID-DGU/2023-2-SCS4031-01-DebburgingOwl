package com.example.logintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends ArrayAdapter<Notice>{
    private List<Notice> originalData;
    private List<Notice> filteredData;
    private LayoutInflater inflater;

    public NoticeAdapter(Context context, List<Notice> data) {
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

        // Notice 객체에서 제목을 가져와서 설정
        Notice notice = getItem(position);
        if (notice != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(notice.getTitle());
        }

        return view;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Notice getItem(int position) {
        return filteredData.get(position);
    }

    public void filter(String query) {
        filteredData.clear();
        query = query.toLowerCase(Locale.getDefault());

        if (query.length() == 0) {
            filteredData.addAll(originalData);
        } else {
            for (Notice item : originalData) {
                if (item.getTitle().toLowerCase(Locale.getDefault()).contains(query)) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }
}
