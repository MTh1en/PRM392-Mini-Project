package com.example.miniproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    SeekBar seekBar1, seekBar2, seekBar3;
    Button btnStart, btnNapTien;
    CheckBox checkBox1, checkBox2, checkBox3;
    TextView txtTien;
    EditText editText1, editText2, editText3;
    VideoView vdv;
    int tien, cuoc1, cuoc2, cuoc3;

    private ListView lvKetQua;
    private ArrayList<String> results;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        vdv  = findViewById(R.id.videoBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backgroundong);
        vdv.setVideoURI(videoUri);
        vdv.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f); // Tắt tiếng video
        });
        vdv.start();

        //Chạy nhạc
        mediaPlayer = MediaPlayer.create(this, R.raw.background_main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        // Ánh xạ
        initializeViews();

        // Tạo danh sách và adapter cho kết quả bet
        results = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        lvKetQua.setAdapter(adapter);

        //Tính tiền
        tien = getIntent().getIntExtra("tienKhoiDau", 0);
        txtTien.setText(String.valueOf(tien));
        tien = getIntent().getIntExtra("soTien", getTienFromText());
        txtTien.setText(String.valueOf(tien));

        //Set hành động cho nút
        btnNapTien.setOnClickListener(view -> navigateToPayment());
        btnStart.setOnClickListener(view -> startRace());

        // Không cho người dùng can thiệp vào SeekBars
        disableSeekBars();

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

    private void initializeViews() {
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        txtTien = findViewById(R.id.txtTien);
        btnStart = findViewById(R.id.btnStart);
        btnNapTien = findViewById(R.id.btnNapTien);
        lvKetQua = findViewById(R.id.lvKetQua);
    }

    private int getTienFromText() {
        try {
            return Integer.parseInt(txtTien.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void navigateToPayment() {
        Intent intent = new Intent(MainActivity.this, Payment.class);
        intent.putExtra("napTien", tien);
        startActivity(intent);
    }

    private void startRace() {
        if (!anyCheckBoxChecked()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 con", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateBets()) return;

        resetSeekBars();
        disableInputs();
        btnStart.setVisibility(View.INVISIBLE);

        createCountDownTimer().start();
        onPause();
        mediaPlayer = MediaPlayer.create(this, R.raw.backgrounduathu);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private boolean anyCheckBoxChecked() {
        return checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked();
    }

    private boolean validateBets() {
        try {
            cuoc1 = checkBox1.isChecked() ? getBetFromEditText(editText1) : 0;
            cuoc2 = checkBox2.isChecked() ? getBetFromEditText(editText2) : 0;
            cuoc3 = checkBox3.isChecked() ? getBetFromEditText(editText3) : 0;

            if (cuoc1 + cuoc2 + cuoc3 > tien) {
                Toast.makeText(this, "Không thể đặt cược quá số tiền hiện có", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Không thể bỏ trống số tiền cược", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private int getBetFromEditText(EditText editText) throws NumberFormatException {
        int bet = Integer.parseInt(editText.getText().toString());
        if (bet <= 0) {
            throw new NumberFormatException("Tiền cược không hợp lệ");
        }
        return bet;
    }
    private CountDownTimer createCountDownTimer() {
        return new CountDownTimer(30000, 300) {
            final Random random = new Random();

            @Override
            public void onTick(long millisUntilFinished) {
                updateSeekBarProgress();
                checkForWinner();
            }

            @Override
            public void onFinish() {
                // Không làm gì khi hết giờ mà không có ai thắng
            }

            private void updateSeekBarProgress() {
                seekBar1.setProgress(seekBar1.getProgress() + random.nextInt(5));
                seekBar2.setProgress(seekBar2.getProgress() + random.nextInt(5));
                seekBar3.setProgress(seekBar3.getProgress() + random.nextInt(5));
            }

            private void checkForWinner() {
                if (seekBar1.getProgress() >= seekBar1.getMax()) {
                    announceWinner(1, "DOG WIN");
                } else if (seekBar2.getProgress() >= seekBar2.getMax()) {
                    announceWinner(2, "CAT WIN");
                } else if (seekBar3.getProgress() >= seekBar3.getMax()) {
                    announceWinner(3, "MOUSE WIN");
                }
            }

            private void announceWinner(int winner, String message) {
                this.cancel();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                calculateTien(winner);
                processRaceResults(winner);
                enableInputs();
                btnStart.setVisibility(View.VISIBLE);
            }
        };
    }

    private void calculateTien(int winner) {
        switch (winner) {
            case 1: // DOG thắng
                tien += checkBox1.isChecked() ? cuoc1 : 0;
                tien -= checkBox2.isChecked() ? cuoc2 : 0;
                tien -= checkBox3.isChecked() ? cuoc3 : 0;
                break;
            case 2: // CAT thắng
                tien += checkBox2.isChecked() ? cuoc2 : 0;
                tien -= checkBox1.isChecked() ? cuoc1 : 0;
                tien -= checkBox3.isChecked() ? cuoc3 : 0;
                break;
            case 3: // MOUSE thắng
                tien += checkBox3.isChecked() ? cuoc3 : 0;
                tien -= checkBox1.isChecked() ? cuoc1 : 0;
                tien -= checkBox2.isChecked() ? cuoc2 : 0;
                break;
        }
        txtTien.setText(String.valueOf(tien));
    }
    private void processRaceResults(int winner) {
        String result;
        int winnings = 0;
        int losses = 0;

        switch (winner) {
            case 1:
                result = "Chó thắng!";
                winnings = cuoc1;
                losses = cuoc2 + cuoc3;
                break;
            case 2:
                result = "Mèo thắng!";
                winnings = cuoc2;
                losses = cuoc1 + cuoc3;
                break;
            case 3:
                result = "Chuột thắng!";
                winnings = cuoc3;
                losses = cuoc1 + cuoc2;
                break;
            default:
                result = "Không có ai thắng!";
        }
        // Thêm kết quả vào danh sách
        results.add(result + " Thắng: " + winnings + " - Thua: " + losses);
        adapter.notifyDataSetChanged();
    }
    private void resetSeekBars() {
        seekBar1.setProgress(0);
        seekBar2.setProgress(0);
        seekBar3.setProgress(0);
    }

    private void disableInputs() {
        setInputsEnabled(false);
    }

    private void enableInputs() {
        setInputsEnabled(true);
    }

    private void setInputsEnabled(boolean isEnabled) {
        editText1.setEnabled(isEnabled);
        editText2.setEnabled(isEnabled);
        editText3.setEnabled(isEnabled);
        checkBox1.setEnabled(isEnabled);
        checkBox2.setEnabled(isEnabled);
        checkBox3.setEnabled(isEnabled);
    }

    private void disableSeekBars() {
        seekBar1.setEnabled(false);
        seekBar2.setEnabled(false);
        seekBar3.setEnabled(false);
    }
}
