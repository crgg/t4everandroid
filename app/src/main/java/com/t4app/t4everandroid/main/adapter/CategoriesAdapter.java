package com.t4app.t4everandroid.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<String> categories;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String categoria);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public CategoriesAdapter(List<String> categories) {
        this.categories = categories;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(category);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemText);
        }

        public void bind(String  categoria) {
            tvName.setText(categoria);
        }
    }
}