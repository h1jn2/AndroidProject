package com.example.androidproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidproject.adapter.DetailAdapter;
import com.example.androidproject.databinding.ActivityDetailBinding;
import com.example.androidproject.db.DBHelper;
import com.example.androidproject.model.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    Student student;
    ArrayList<Map<String, String>> scoreList;
    DetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        int id = getIntent().getIntExtra("id", 0);

        setInitStudentData(id);

        setInitScoreData(id);

        ActivityResultLauncher<Intent> addScoreLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                        Intent intent = result.getData();
                        String score = intent.getStringExtra("score");
                        long date = intent.getLongExtra("date", 0);

                    HashMap<String, String> map = new HashMap<>();
                    Date d = new Date(date);
                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                    map.put("score", score);
                    map.put("date", sd.format(d));

                    scoreList.add(map);
                    adapter.notifyDataSetChanged();
                });
        binding.detailAddScoreButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScoreAddActivity.class);
            intent.putExtra("id", id);
            addScoreLauncher.launch(intent);
        });
    }

    private void setInitScoreData(int id) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_score where student_id=? order by date",
                new String[]{String.valueOf(id)});

        scoreList = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            Date d = new Date(Long.parseLong(cursor.getString(2)));
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            map.put("score", cursor.getString(3));
            map.put("date", sd.format(d));
            scoreList.add(map);
        }
        db.close();

        binding.detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(this, scoreList);
        binding.detailRecyclerView.setAdapter(adapter);
        binding.detailRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void setInitStudentData(int id) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_student where _id=?", new String[]{String.valueOf(id)});
        String photoFilePath = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);

            binding.detailName.setText(name);
            binding.detailEmail.setText(email);
            binding.detailPhone.setText(phone);

            photoFilePath = cursor.getString(4);

            student = new Student(cursor.getInt(0), name, email, phone, cursor.getString(5), photoFilePath);
        }

        db.close();
    }
}