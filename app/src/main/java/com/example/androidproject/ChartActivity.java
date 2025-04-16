package com.example.androidproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidproject.databinding.ActivityChartBinding;
import com.example.androidproject.db.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChartActivity extends AppCompatActivity {
    ArrayList<Integer> scoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityChartBinding binding = ActivityChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        getScoreDB(id);

        binding.chartStudentName.setText(name);

        WebSettings settings = binding.chartWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        binding.chartWebView.addJavascriptInterface(new MyJavascript(), "android");
        binding.chartWebView.loadUrl("file:///android_asset/test.html");
    }

    private void getScoreDB(int id) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_score where student_id=? order by date desc",
                new String[]{String.valueOf(id)});

        scoreList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int score = Integer.parseInt(cursor.getString(3));
            scoreList.add(score);
        }
        db.close();
    }
    class MyJavascript {
        @JavascriptInterface
        public String getChartData() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("[");
            for (int i = 0; i < scoreList.size(); i++) {
                buffer.append("[" + i + "," + scoreList.get(i) + "]");
                if (i < scoreList.size() - 1) {
                    buffer.append(",");
                }
            }
            buffer.append("]");
            Log.d("kkang", buffer.toString());
            return buffer.toString();
        }
    }
}
