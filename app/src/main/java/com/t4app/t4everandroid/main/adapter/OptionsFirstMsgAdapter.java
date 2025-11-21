package com.t4app.t4everandroid.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        holder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        holder.title.setText(item.getText());
        holder.icon.setImageResource(item.getIconResId());
        holder.container.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(),
                item.getColorId()));

        int animRes;
        if (position % 2 == 0) {
            animRes = R.anim.item_tilt_left_right;
        } else {
            animRes = R.anim.item_tilt_right_left;
        }
        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), animRes);
        anim.setRepeatCount(Animation.INFINITE);
        holder.itemView.startAnimation(anim);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull CategoryVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull CategoryVH holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAbsoluteAdapterPosition();
        int animRes = (position % 2 == 0)
                ? R.anim.item_tilt_left_right
                : R.anim.item_tilt_right_left;

        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), animRes);
        anim.setRepeatCount(Animation.INFINITE);

        holder.itemView.startAnimation(anim);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CategoryVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        LinearLayout container;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_item);
            title = itemView.findViewById(R.id.category_title);
            icon = itemView.findViewById(R.id.category_icon);
        }
    }
}

