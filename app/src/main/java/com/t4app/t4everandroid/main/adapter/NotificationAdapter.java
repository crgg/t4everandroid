package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {



    private Context context;
    private List<NotificationItem> notificationList;
    private ListenersUtils.OnNotificationClickListener listener;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList, ListenersUtils.OnNotificationClickListener listener) {
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

        holder.btnMarkAsRead.setOnClickListener(v -> {
            int currentPos = holder.getAbsoluteAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION){
                item.setRead(true);
                listener.onMarkRead(item, currentPos);
                notifyItemChanged(currentPos);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int currentPos = holder.getAbsoluteAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION){
                listener.onDeleteClicked(item, currentPos);
            }
        });

        if (item.getType().equalsIgnoreCase(context.getString(R.string.information))){
            holder.title.setTextColor(ContextCompat.
                    getColorStateList(context, R.color.border_color_buttons));
            holder.icTypeNot.setImageResource(R.drawable.ic_info);
            holder.icTypeNot.setImageTintList(ContextCompat.
                    getColorStateList(context, R.color.border_color_buttons));
        }else if (item.getType().equalsIgnoreCase(context.getString(R.string.success))){
            holder.title.setTextColor(ContextCompat.
                    getColorStateList(context, R.color.green_success));
            holder.icTypeNot.setImageResource(R.drawable.ic_check);
            holder.icTypeNot.setImageTintList(ContextCompat.
                    getColorStateList(context, R.color.green_success));
        }else if (item.getType().equalsIgnoreCase(context.getString(R.string.warning))){
            holder.title.setTextColor(ContextCompat.
                    getColorStateList(context, R.color.alert_color));
            holder.icTypeNot.setImageResource(R.drawable.ic_warning);
            holder.icTypeNot.setImageTintList(ContextCompat.
                    getColorStateList(context, R.color.alert_color));
        }else if (item.getType().equalsIgnoreCase(context.getString(R.string.error))){
            holder.title.setTextColor(ContextCompat.
                    getColorStateList(context, R.color.red));
            holder.icTypeNot.setImageResource(R.drawable.ic_error);
            holder.icTypeNot.setImageTintList(ContextCompat.
                    getColorStateList(context, R.color.red));
        }

        if (item.isRead()){
            holder.btnMarkAsRead.setImageResource(R.drawable.ic_eye);
            holder.containerNotification.setBackgroundTintList(ContextCompat.
                    getColorStateList(context, R.color.white));
        }else{
            holder.btnMarkAsRead.setImageResource(R.drawable.ic_visibility_off);
            holder.containerNotification.setBackgroundTintList(ContextCompat.
                    getColorStateList(context, R.color.soft_gray));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, message, date, action;
        ImageButton btnMarkAsRead, btnDelete;
        ImageView icTypeNot;
        CardView card;
        LinearLayout containerNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView;
            checkBox = itemView.findViewById(R.id.item_notification_check);
            title = itemView.findViewById(R.id.title_notification);
            message = itemView.findViewById(R.id.content_notification);
            date = itemView.findViewById(R.id.date);
            action = itemView.findViewById(R.id.action_not);
            icTypeNot = itemView.findViewById(R.id.icon_type_notification);
            btnMarkAsRead = itemView.findViewById(R.id.btn_is_read);
            btnDelete = itemView.findViewById(R.id.btn_delete_notification);
            containerNotification = itemView.findViewById(R.id.container_notification);
        }
    }

    public void updateList(List<NotificationItem> newList) {
//        notificationList.clear();
        this.notificationList = newList;
        notifyDataSetChanged();
    }
}
