package com.example.androidproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.example.androidproject.adapter.MainAdapter;
import com.example.androidproject.callback.DialogCallback;
import com.example.androidproject.databinding.ActivityMainBinding;
import com.example.androidproject.db.DBHelper;
import com.example.androidproject.model.Student;
import com.example.androidproject.util.DialogUtil;
import com.example.androidproject.util.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<Intent> addLauncher;
    ArrayList<Student> datas = new ArrayList<>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 새로운 학생 정보 ArrayList 에 add
                    Intent intent = result.getData();
                    int id = (int) intent.getLongExtra("id", 0);
                    String name = intent.getStringExtra("name");
                    String email = intent.getStringExtra("email");
                    String phone = intent.getStringExtra("phone");
                    String memo = intent.getStringExtra("memo");
                    String photo = intent.getStringExtra("photo");

                    Student student = new Student(id, name, email, phone, memo, photo);

                    datas.add(student);
                    adapter.notifyDataSetChanged();
                }
        );

        // isAllGranted 에 Callback 값이 반환
        PermissionUtil.checkAllPermission(this, isAllGranted -> {
            if (isAllGranted) {
                makeRecyclerView();
            } else {
                showDialog();
            }
        });
    }

    private void makeRecyclerView() {
        getListData();
        adapter = new MainAdapter(this, datas);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    // ArrayList 에 전체 DB add
    private void getListData() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from tb_student order by name", null);

        while (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getInt(0));
            student.setName(cursor.getString(1));
            student.setEmail(cursor.getString(2));
            student.setPhone(cursor.getString(3));
            student.setPhoto(cursor.getString(4));
            student.setMemo(cursor.getString(5));

            datas.add(student);
        }
    }

    private void showDialog() {
        DialogUtil.showMessageDialog(this, getString(R.string.permission_denied),
                "확인", null, new DialogCallback() {
            @Override
            public void onPositiveCallback() {

            }

            @Override
            public void onNegativeCallback() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_add) {
            Intent intent = new Intent(this, AddStudentActivity.class);
            addLauncher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}