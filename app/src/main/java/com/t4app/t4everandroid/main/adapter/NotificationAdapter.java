package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onItemChecked(NotificationItem item, boolean isChecked);
        void onEditClicked(NotificationItem item);
        void onDeleteClicked(NotificationItem item);
    }

    private Context context;
    private List<NotificationItem> notificationList;
    private OnNotificationClickListener listener;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);

        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());
        holder.date.setText(item.getDate());
        holder.action.setText(item.getActionText());
        holder.checkBox.setChecked(item.isChecked());


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (listener != null) listener.onItemChecked(item, isChecked);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, message, date, action;
        ImageButton btnEdit, btnDelete;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView;
            checkBox = itemView.findViewById(R.id.item_notification_check);
            title = itemView.findViewById(R.id.title_notification);
            message = itemView.findViewById(R.id.content_notification);
            date = itemView.findViewById(R.id.date);
            action = itemView.findViewById(R.id.action_not);
            btnEdit = itemView.findViewById(R.id.btn_edit_question);
            btnDelete = itemView.findViewById(R.id.btn_delete_question);
        }
    }

    public void updateList(List<NotificationItem> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }
}
