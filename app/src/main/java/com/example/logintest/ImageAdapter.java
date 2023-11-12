package com.example.logintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder.imageView = convertView.findViewById(R.id.image_view_item);
            // 이미지 뷰를 정사각형으로 설정합니다.
            int size = parent.getWidth() / 3; // 가정: 한 줄에 3개의 이미지가 들어감
            convertView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageModel imageModel = imageList.get(position);

        // Picasso 라이브러리를 사용하여 이미지 로드 및 표시
        Picasso.get()
                .load(imageModel.getImageUrl())
                .resize(parent.getWidth() / 3, parent.getWidth() / 3) // 이미지를 정사각형 크기로 리사이즈
                .centerCrop()
                .into(holder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
