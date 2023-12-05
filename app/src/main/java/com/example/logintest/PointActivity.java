package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PointActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ListView pointListView;
    private  String[] storeNames = {
            "스타벅스",
            "스타벅스",
            "스타벅스",
            "투썸플레이스",
            "투썸플레이스",
            "공차",
            "메가박스"
    };
    private String[] names = {
            "아이스 카페 아메리카노 T",
            "카페 라떼 T",
            "콜드 브루 T",
            "ICE 아메리카노 (R)",
            "떠먹는 스트로베리 초콜릿 생크림",
            "블랙 밀크티 + 펄 L",
            "1인 관람권(주중/주말)"
    };
    private Integer[] images = {
            R.drawable.starbucks_americano,
            R.drawable.starbucks_latte,
            R.drawable.starbucks_coldbrew,
            R.drawable.twosome_americano,
            R.drawable.twosome_cake,
            R.drawable.gongcha_milktea,
            R.drawable.megabox
    };

    private String[] points = {
            "100P",
            "200P",
            "150P",
            "100P",
            "200P",
            "150P",
            "300P"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        backBtn = (ImageButton) findViewById(R.id.pointBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 액티비티를 종료하여 뒤로가기
                finish();
            }
        });

        pointListView = (ListView) findViewById(R.id.pointListView);
        CustomList adapter = new CustomList(PointActivity.this);
        pointListView.setAdapter(adapter);

    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;
        public CustomList(Activity context) {
            super(context, R.layout.activity_point_list_item, storeNames);
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.activity_point_list_item, null, true);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView storeName = (TextView) rowView.findViewById(R.id.storeName);
            TextView name = (TextView) rowView.findViewById(R.id.name);
            TextView point = (TextView) rowView.findViewById(R.id.point);
            Button buyBtn = rowView.findViewById(R.id.buyBtn);

            storeName.setText(storeNames[position]);
            imageView.setImageResource(images[position]);
            name.setText(names[position].toString());
            point.setText(points[position]);

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, names[position]+"가 교환되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });


            return rowView;
        }
    }
}