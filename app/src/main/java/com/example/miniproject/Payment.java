package com.example.miniproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Payment extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    EditText edtSoTien;
    TextView txtSoTienHienCo;
    Button btnThanhToan, btnHuyThanhToan, btnRut;

    Integer soTien, tienNap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        //Chạy nhạc nền
        mediaPlayer = MediaPlayer.create(this, R.raw.background_main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        //Ánh xạ
        edtSoTien = findViewById(R.id.edtSoTien);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        btnHuyThanhToan = findViewById(R.id.btnHuyThanhToan);
        txtSoTienHienCo = findViewById(R.id.txtSoTienHienCo);
        btnRut = findViewById(R.id.btnRut);

        //Lấy tiền từ MainActivity
        Intent intent = getIntent();
        soTien = intent.getIntExtra("napTien", 0);
        txtSoTienHienCo.setText(soTien + " VNĐ");

        btnThanhToan.setOnClickListener(view -> payment(true));
        btnRut.setOnClickListener(view -> payment(false));
        btnHuyThanhToan.setOnClickListener(view -> {
            Intent intent1 = new Intent(Payment.this, MainActivity.class);
            intent1.putExtra("soTien", soTien);
            startActivity(intent1);
        });

    }

    private void payment(boolean isPayment){
        try {
            tienNap = Integer.parseInt(edtSoTien.getText().toString());
            if (tienNap <= 0) {
                Toast.makeText(Payment.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            }
            if (isPayment) {
                soTien += tienNap;
            }else{
                if(soTien < tienNap){
                    Toast.makeText(Payment.this, "Số tiền rút không đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                soTien -= tienNap;
            }
            Intent intent1 = new Intent(Payment.this, MainActivity.class);
            intent1.putExtra("soTien", soTien);
            startActivity(intent1);
        } catch (NumberFormatException ex) {
            Toast.makeText(Payment.this, "Chỉ có thể nhập số", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
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