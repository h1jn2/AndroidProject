package com.example.androidproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "studentdb", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tb_student (" +
                "_id integer primary key autoincrement," +
                "name text not null, " +
                "email text, " +
                "phone text, " +
                "photo text, " +
                "memo text)");
        db.execSQL("create table tb_score (" +
                "_id integer primary key autoincrement," +
                "student_id integer not null," +
                "date," +
                "score)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            db.execSQL("drop table tb_student");
            onCreate(db);
        }
    }
}
