package com.example.androidproject.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.databinding.ItemDetailBinding;
import com.example.androidproject.db.DBHelper;
import com.example.androidproject.DoughnutView;

import java.util.ArrayList;
import java.util.Map;

class DetailViewHolder extends RecyclerView.ViewHolder {
    ItemDetailBinding binding;
    DetailViewHolder(ItemDetailBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {
    ArrayList<Map<String, String>> datas;
    Activity context;
    DoughnutView doughnutView;
    int progressValue = 0;

    public DetailAdapter(Activity context, ArrayList<Map<String, String>> datas, DoughnutView doughnutView) {
        this.context = context;
        this.datas = datas;
        this.doughnutView = doughnutView;
    }
    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailBinding binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Map<String, String> score = datas.get(position);
        holder.binding.detailItemScore.setText(score.get("score"));
        holder.binding.detailItemDate.setText(score.get("date"));
        holder.binding.detailItemDelete.setOnClickListener(view -> {
            deleteData(score.get("_id"), position);
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private void deleteData(String idx, int position) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from tb_score where _id=?", new String[]{idx});
        db.close();

        datas.remove(position);

        if (!datas.isEmpty()) progressValue = Integer.parseInt(datas.get(0).get("score"));
        else progressValue = 0;

        doughnutView.setProgress(progressValue);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, getItemCount());

        Log.d("kkang", ""+getItemCount());
    }
}
