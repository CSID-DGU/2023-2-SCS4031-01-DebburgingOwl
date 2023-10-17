package com.example.logintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        Button registerButton = btnRegister;
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Intent moveToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                            // TODO: 메인 액티비티로 이동하는 코드를 작성해주세요.
                            // 로그인 성공 후 실행되는 코드 부분

                            startActivity(moveToMainActivity);
                            finish();



                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 등록 성공
                            Toast.makeText(LoginActivity.this, "계정 생성 성공!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 등록 실패
                            Toast.makeText(LoginActivity.this, "계정 생성 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
