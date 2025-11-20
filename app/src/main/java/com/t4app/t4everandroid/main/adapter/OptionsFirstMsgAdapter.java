package com.t4app.t4everandroid.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.CategoryItem;

import java.util.List;

public class OptionsFirstMsgAdapter extends RecyclerView.Adapter<OptionsFirstMsgAdapter.CategoryVH> {

    private final List<CategoryItem> list;
    private final OnCategoryClick listener;

    public interface OnCategoryClick {
        void onClick(CategoryItem item);
    }

    public OptionsFirstMsgAdapter(List<CategoryItem> list, OnCategoryClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_category, parent, false);
        return new CategoryVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        CategoryItem item = list.get(position);

        holder.title.setText(item.getText());
        holder.icon.setImageResource(item.getIconResId());

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CategoryVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title);
            icon = itemView.findViewById(R.id.category_icon);
        }
    }
}

