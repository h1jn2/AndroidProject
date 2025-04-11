package com.example.androidproject.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.androidproject.callback.DialogCallback;
import com.example.androidproject.databinding.DialogImageBinding;

// 객체를 반복적으로 생성해서 각자의 메모리에 데이터를 유지하기 위한 목적이 아니고
// 여러 곳에서 사용하는 코드의 중복을 피하여 필요한 곳에서 호출하며 사용하겠다는 의도
// 이런 경우 object member로 만들기보단 static member로 만들어 사용
public class DialogUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showCustomDialog(Context context, int defaultResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        DialogImageBinding binding = DialogImageBinding.inflate(inflater);
        builder.setView(binding.dialogImage);
        AlertDialog dialog = builder.create();
        dialog.show();

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), defaultResId);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(bitmap.getWidth(), bitmap.getWidth());
        }
    }

    public static void showMessageDialog(Context context, String message, String positiveText, String negativeText, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Message");
        builder.setMessage(message);
        if (positiveText != null) {
            builder.setPositiveButton(positiveText, (dialog, which) -> {
                callback.onPositiveCallback();
            });
        }
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, (dialog, which) -> {
                callback.onNegativeCallback();
            });
        }
        builder.show();
    }
}
