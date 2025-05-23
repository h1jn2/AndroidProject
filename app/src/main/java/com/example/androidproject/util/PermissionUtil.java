package com.example.androidproject.util;

import android.os.Build;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.androidproject.callback.PermissionCallback;

import java.util.HashSet;

public class PermissionUtil {
    // Permission 띄워주고 callback 에 반환값
    public static void checkAllPermission(ComponentActivity activity, PermissionCallback callback) {
        HashSet<String> permissionSet = new HashSet<>();
        permissionSet.add("android.permission.CALL_PHONE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionSet.add("android.permission.READ_MEDIA_IMAGES");
        } else {
            permissionSet.add("android.permission.READ_EXTERNAL_STORAGE");
        }

        ActivityResultLauncher<String[]> launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean isGranted : result.values()) {
                        if (!isGranted) {
                            allGranted = false;
                            break;
                        }
                    }
                    callback.onPermissionResult(allGranted);
                }
        );
        launcher.launch(permissionSet.toArray(new String[permissionSet.size()]));
    }
}
