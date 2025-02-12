package com.example.miniproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    TextView txtSignUp, txtSignIn;
    Button btnStart, btnSignup, btnCancelSignUp, btnSignIn, btnCancelSignIn;

    EditText edtUsername, edtPassword, edtRePassword, edtNewUsername, edtNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        mediaPlayer = MediaPlayer.create(this, R.raw.background_home);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        btnStart = findViewById(R.id.btnHomeStart);
        btnStart.setOnClickListener(view -> login());
    }

    private void login() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.login, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPassword = view.findViewById(R.id.edtPassword);
        txtSignUp = view.findViewById(R.id.tvRedirectToSignup);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnCancelSignIn = view.findViewById(R.id.btnCancelSignIn);

        txtSignUp.setOnClickListener(v -> {
            dialog.dismiss();
            showSignupDialog();
        });
        btnSignIn.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            if (username.equals("admin") && password.equals("123456")) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.putExtra("tienKhoiDau", 500000);
                startActivity(intent);
            } else if (username.equals("minhtri") && password.equals("123456")) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.putExtra("tienKhoiDau", 300000);
                startActivity(intent);
            } else if (username.equals("minhthien") && password.equals("123456")) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.putExtra("tienKhoiDau", 100000);
                startActivity(intent);
            } else {
                Toast.makeText(Home.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancelSignIn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showSignupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.register, null); // Layout cho đăng ký
        builder.setView(view);

        AlertDialog dialog = builder.create();
        edtNewUsername = view.findViewById(R.id.edtNewUsername);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtRePassword = view.findViewById(R.id.edtRePassword);
        txtSignIn = view.findViewById(R.id.tvRedirectToSignIn);
        btnSignup = view.findViewById(R.id.btnSignup);
        btnCancelSignUp = view.findViewById(R.id.btnCancelSignUp);
        txtSignIn.setOnClickListener(v->{
            dialog.dismiss();
            login();
        });
        btnSignup.setOnClickListener(v -> {
            String newUsername = edtNewUsername.getText().toString();
            String newPassword = edtNewPassword.getText().toString();
            String rePassword = edtRePassword.getText().toString();
            // Kiểm tra thông tin đăng ký
            boolean check = true;
            if (TextUtils.isEmpty(newUsername)) {
                edtNewUsername.setError("Vui lòng nhập tên đăng nhập");
                check = false;
            }
            if (TextUtils.isEmpty(newPassword)) {
                edtNewPassword.setError("Vui lòng nhập mật khẩu");
                check = false;
            }
            if (TextUtils.isEmpty(rePassword)) {
                edtRePassword.setError("Vui lòng nhập lại mật khẩu");
                check = false;
            }
            if (!newPassword.equals(rePassword)) {
                Toast.makeText(Home.this, "Mật khẩu nhập lại không khớp với mật khẩu", Toast.LENGTH_SHORT).show();
                check = false;
            }
            if (check) {
                dialog.dismiss();
                login();
            }
        });

        btnCancelSignUp.setOnClickListener(v -> {
            dialog.dismiss();
            login();
        });
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tiếp tục phát nhạc khi Activity trở lại
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên khi Activity bị hủy
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}