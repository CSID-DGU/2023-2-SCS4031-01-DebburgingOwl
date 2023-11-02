package com.example.logintest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.logintest.User;

import java.text.BreakIterator;

public class RegisterActivity extends Activity {
    private EditText editTextName, editTextEmail, editTextPassword, editTextNickname;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button buttonRegister, buttonUploadImage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextNameRegister);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextNickname = findViewById(R.id.editTextNicknameRegister);
        Button buttonRegister = findViewById(R.id.buttonRegister);


        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nickname = editTextNickname.getText().toString().trim();

        if (name.isEmpty() || name.length() < 2 || name.length() > 30) {
            editTextName.setError("Please enter a valid name (2~30 characters)");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password should be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }


        if (nickname.isEmpty() || nickname.length() < 2 || nickname.length() > 30) {
            editTextNickname.setError("Please enter a valid nickname (2~30 characters)");
            editTextNickname.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        if (user != null) {
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            String userId = user.getUid();
                                            User userData = new User(name, email, nickname);
                                            databaseReference.child("users").child(userId).setValue(userData);
                                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MyPageActivity.class);
                                            intent.putExtra("nickname", nickname);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
