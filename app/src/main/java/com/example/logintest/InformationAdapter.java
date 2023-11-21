package com.example.logintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class InformationAdapter extends ArrayAdapter<Information> {

    private List<Information> originalData;
    private List<Information> filteredData;
    private LayoutInflater inflater;

    public InformationAdapter(Context context, List<Information> data) {
        super(context, R.layout.grid_item, data);
        this.originalData = data;
        this.filteredData = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, parent, false);
        }

        // Information 객체에서 데이터 가져오기
        Information information = getItem(position);

        // 가져온 데이터를 리스트뷰 아이템에 설정
        TextView titleTextView = view.findViewById(R.id.infoTitle);

        if (information != null) {
            titleTextView.setText(information.getTitle());
        }

        return view;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Information getItem(int position) {
        return filteredData.get(position);
    }

    public void filter(String query) {
        filteredData.clear();
        query = query.toLowerCase();

        if (query.length() == 0) {
            filteredData.addAll(originalData);
        } else {
            for (Information item : originalData) {
                if (item.getTitle().toLowerCase().contains(query)) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }
}
