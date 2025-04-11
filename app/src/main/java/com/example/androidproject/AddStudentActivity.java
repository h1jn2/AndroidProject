package com.example.androidproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidproject.databinding.ActivityAddStudentBinding;
import com.example.androidproject.db.DBHelper;
import com.example.androidproject.util.DialogUtil;

public class AddStudentActivity extends AppCompatActivity {
    ActivityAddStudentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_save) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String name = binding.addName.getText().toString();
        String email = binding.addEmail.getText().toString();
        String phone = binding.addPhone.getText().toString();
        String memo = binding.addMemo.getText().toString();

        if (name == null || name.equals("")) {
            DialogUtil.showToast(this, getString(R.string.add_name_null));
        } else {
            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("phone", phone);
            values.put("memo", memo);

            // execSql() 함수로는 return 데이터가 없음
            // insert 후에 그 row 의 식별자 값을 얻고싶을 때
            long newRowId = db.insert("tb_student", null, values);
            db.close();

            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            intent.putExtra("id", newRowId);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("memo", memo);
            
            finish();
        }
    }
}














