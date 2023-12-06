// BookmarkListAdapter.java

package com.example.logintest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookmarkListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> bookmarkTitles;

    public BookmarkListAdapter(Context context, List<String> bookmarkTitles) {
        super(context, 0, bookmarkTitles);
        this.context = context;
        this.bookmarkTitles = bookmarkTitles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView titleTextView = convertView.findViewById(android.R.id.text1);
        titleTextView.setText(bookmarkTitles.get(position));


        return convertView;
    }
}
