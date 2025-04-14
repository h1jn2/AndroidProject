package com.example.androidproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidproject.databinding.ActivityScoreAddBinding;
import com.example.androidproject.db.DBHelper;

public class ScoreAddActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityScoreAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityScoreAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.key0.setOnClickListener(this);
        binding.key1.setOnClickListener(this);
        binding.key2.setOnClickListener(this);
        binding.key3.setOnClickListener(this);
        binding.key4.setOnClickListener(this);
        binding.key5.setOnClickListener(this);
        binding.key6.setOnClickListener(this);
        binding.key7.setOnClickListener(this);
        binding.key8.setOnClickListener(this);
        binding.key9.setOnClickListener(this);
        binding.keyBack.setOnClickListener(this);
        binding.keyAdd.setOnClickListener(this);
    }

    // 여러 뷰의 이벤트를 하나의 함수에서 모두 처리
    // 매개변수가 현재 이벤트가 발생한 객체
    @Override
    public void onClick(View v) {
        if (v == binding.keyAdd) {
            Intent intent = getIntent();
            int id = intent.getIntExtra("id", 0);

            long date = System.currentTimeMillis();

            String score = binding.keyEdit.getText().toString();

            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("insert into tb_score (student_id, date, score) values (?, ?, ?)",
                    new String[]{String.valueOf(id), String.valueOf(date), score});
            db.close();

            intent.putExtra("score", score);
            intent.putExtra("date", date);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v == binding.keyBack) {
            String score = binding.keyEdit.getText().toString();
            if (score.length() == 1) {
                binding.keyEdit.setText("0");
            } else {
                String newScore = score.substring(0, score.length() - 1);
                binding.keyEdit.setText(newScore);
            }
        } else {
            Button btn = (Button) v;
            String txt = btn.getText().toString();

            String score = binding.keyEdit.getText().toString();

            if (score.equals("0")) {
                binding.keyEdit.setText(txt);
            } else {
                String newScore = score + txt;
                int intScore = Integer.parseInt(newScore);
                if (intScore > 100) {
                    Toast.makeText(this, R.string.read_add_score_over, Toast.LENGTH_SHORT).show();
                } else {
                    binding.keyEdit.setText(newScore);
                }
            }
        }
    }
}