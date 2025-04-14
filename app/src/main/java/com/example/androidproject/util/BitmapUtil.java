package com.example.androidproject.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

public class BitmapUtil {
    // 유저가 갤러리 앱에서 이미지를 선택하고 되돌아 올 때 식별자 값이 Uri
    // 그 Stream 에 Uri 만 지정하면 편리함
    public static Bitmap getGalleryBitmapFromStream(Context context, Uri uri) {
        try {
            // 데이터 사이즈가 크기 때문에 그냥 로딩하면 OOM (Out Of MemoryException) 문제 발생
            // 데이터 사이즈를 줄여서 로딩해야함
            // Bitmap 은 직접 생성되지 않고 BitmapFactory 에 의해서 생성
            // BitmapFactory 의 작업 옵션에 명시하면 알아서 줄여줌

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10; // 1 / 10 사이즈로 줄여서 로딩

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 문자열로 된 파일의 경로 (절대경로)를 매개변수로 지정하면 Bitmap 으로 리턴
    // Stream 으로 읽는 건 갤러리 앱에서 되돌아 온 순간에만 가능 이후엔 불가능
    // 그래서 db 에 파일 경로를 지정했다가 읽어야 함
    public static  Bitmap getGalleryBitmapFromFile(Context context, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;

        return BitmapFactory.decodeFile(filePath, options);
    }
}
