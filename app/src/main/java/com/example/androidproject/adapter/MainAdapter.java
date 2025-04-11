package com.example.androidproject.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.databinding.ItemMainBinding;
import com.example.androidproject.model.Student;
import com.example.androidproject.util.DialogUtil;
import com.example.androidproject.util.PermissionUtil;

import java.util.ArrayList;

class MainViewHolder extends RecyclerView.ViewHolder {
    ItemMainBinding binding;
    public MainViewHolder(ItemMainBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    ArrayList<Student> datas;
    Activity context;

    public MainAdapter (Activity context, ArrayList<Student> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainBinding binding = ItemMainBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Student student = datas.get(position);
        holder.binding.itemNameView.setText(student.getName());
        holder.binding.itemImageView.setOnClickListener(view -> {
            DialogUtil.showCustomDialog(context, R.drawable.ic_student_large);
        });
        holder.binding.itemCallView.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                callPhone(student.getPhone());
            } else {
                DialogUtil.showToast(context, context.getString(R.string.permission_denied));
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private void callPhone(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.equals("")) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        } else {
            DialogUtil.showToast(context, context.getString(R.string.main_list_phone_error));
        }
    }
}
