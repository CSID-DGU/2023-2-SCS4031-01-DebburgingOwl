package com.example.logintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> images;
    private RequestManager glide;

    public ImageAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.images = new ArrayList<>();
        this.glide = glide;
    }

    public void add(String image) {
        images.add(image);
    }

    public void clear() {
        images.clear();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        glide.load(images.get(position)).into(imageView);

        return convertView;
    }
}
