package com.example.logintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<ImageModel> imageList;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, List<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int size = parent.getWidth() / 3 - 10 * 2; // 10dp의 여백을 양쪽에 고려

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.grid_item_community, parent, false);
            holder.imageView = convertView.findViewById(R.id.image_view_item_community);

            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(size, size);
            convertView.setLayoutParams(frameParams);

            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageModel imageModel = imageList.get(position);

        Glide.with(context)
                .load(imageModel.getImageUrl())
                .override(size, size) // 위에서 계산한 크기로 리사이즈
                .centerCrop()
                .into(holder.imageView);

        return convertView;
    }





    private static class ViewHolder {
        ImageView imageView;
    }
}
