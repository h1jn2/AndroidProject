package com.example.androidproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.androidproject.util.BitmapUtil;

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
    ActivityResultLauncher<Intent> requestGalleryLauncher;
    ActivityResultLauncher<Intent> requestEditLauncher;

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
                    if (result.getData() != null) {
                        Intent intent = result.getData();
                        String score = intent.getStringExtra("score");
                        long date = intent.getLongExtra("date", 0);

                        HashMap<String, String> map = new HashMap<>();
                        Date d = new Date(date);
                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                        map.put("score", score);
                        map.put("date", sd.format(d));

                        scoreList.add(map);
                        setScoreProgress(score);
                        adapter.notifyDataSetChanged();
                    }
                });

        requestEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Intent intent = result.getData();
                        String name = intent.getStringExtra("name");
                        String email = intent.getStringExtra("email");
                        String phone = intent.getStringExtra("phone");

                        if (name != null && !name.equals(student.getName())) {
                            student.setName(name);
                            binding.detailName.setText(name);
                        }
                        if (email != null && !email.equals(student.getEmail())) {
                            student.setEmail(email);
                            binding.detailEmail.setText(email);
                        }
                        if (phone != null && !phone.equals(student.getPhone())) {
                            student.setPhone(phone);
                            binding.detailPhone.setText(phone);
                        }
                    }
                }
        );

        binding.detailAddScoreButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScoreAddActivity.class);
            intent.putExtra("id", id);
            addScoreLauncher.launch(intent);
        });

        binding.detailScoreChartButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChartActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("name", student.getName());
            startActivity(intent);
        });

        binding.detailMemoButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MemoActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });

        requestGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        // 갤러리 앱에서 사진 선택 후 되돌아 온 상황
                        // 유저가 선택한 사진의 식별자 값을 Uri 객체로 념겨줌
                        Uri uri = result.getData().getData();
                        // uri 로 식별되는 사진의 경로를 획득, db 에 저장했다가 나중에 이용하기 위해
                        String[] proj = new String[]{MediaStore.Images.Media.DATA};
                        Cursor galleryCursor = getContentResolver().query(
                                uri, proj, null, null, null
                        );
                        if (galleryCursor != null) {
                            if (galleryCursor.moveToNext()) {
                                // 사진 경로 얻기
                                String filePath = galleryCursor.getString(0);
                                // 테이블에 데이터 저장
                                DBHelper helper = new DBHelper(this);
                                SQLiteDatabase db = helper.getWritableDatabase();
                                db.execSQL("update tb_student set photo=? where _id=?",
                                        new String[]{filePath, String.valueOf(id)});
                                db.close();
                            }
                        }
                        Bitmap bitmap = BitmapUtil.getGalleryBitmapFromStream(this, uri);

                        if (bitmap != null) {
                            binding.detailImage.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void setScoreProgress(String score) {
        int progressValue = 0;
        if (!score.isEmpty()) {
            progressValue = Integer.parseInt(score);
        }
        binding.detailDoughnutView.setProgress(progressValue);
    }

    private void setInitScoreData(int id) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_score where student_id=? order by date desc",
                new String[]{String.valueOf(id)});

        scoreList = new ArrayList<>();
        String score = "";

        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            Date d = new Date(Long.parseLong(cursor.getString(2)));
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            map.put("score", cursor.getString(3));
            map.put("date", sd.format(d));
            map.put("_id", cursor.getString(0));
            scoreList.add(map);
            if (cursor.getPosition() == 0) score = cursor.getString(3);
        }
        db.close();

        binding.detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(this, scoreList, binding.detailDoughnutView);
        binding.detailRecyclerView.setAdapter(adapter);
        binding.detailRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        setScoreProgress(score);
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

        Bitmap bitmap = BitmapUtil.getGalleryBitmapFromFile(this, photoFilePath);
        if (bitmap != null)
            binding.detailImage.setImageBitmap(bitmap);

        binding.detailImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            requestGalleryLauncher.launch(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 시험 점수 공유
        if (item.getItemId() == R.id.menu_detail_share) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + student.getPhone()));
            intent.putExtra("sms_body", scoreList.get(0).get("date") + " 의 점수: " + scoreList.get(0).get("score"));
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_detail_edit) {
            Intent intent = new Intent(this, AddStudentActivity.class);
            intent.putExtra("id", student.getId());
            requestEditLauncher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}