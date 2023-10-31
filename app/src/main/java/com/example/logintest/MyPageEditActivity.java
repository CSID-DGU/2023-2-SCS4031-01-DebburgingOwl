package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageEditActivity extends AppCompatActivity {

    ImageView userImage;
    EditText myPageName, myPageNickname, myPagePassword, myPagePasswordCheck, myPageEmail;
    Button myPageEditOkBtn;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_edit);

        userImage = findViewById(R.id.userImage);
        myPageName = findViewById(R.id.myPageName);
        myPageNickname = findViewById(R.id.myPageNickname);
        myPagePassword = findViewById(R.id.myPagePassword);
        myPagePasswordCheck = findViewById(R.id.myPagePasswordCheck);
        myPageEmail = findViewById(R.id.myPageEmail);
        myPageEditOkBtn = findViewById(R.id.myPageEditOkBtn);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
                        myPageName.setText(userData.getName());
                        myPageNickname.setText(userData.getNickname());
                        myPageEmail.setText(userData.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 처리
                    Toast.makeText(MyPageEditActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}