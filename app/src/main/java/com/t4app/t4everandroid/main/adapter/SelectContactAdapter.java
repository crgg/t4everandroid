package com.t4app.t4everandroid.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactAdapter.UserViewHolder> {

    private List<LegacyProfile> users;
    private OnUserClickListener listener;
    private Activity activity;

    private static int lastClicked = RecyclerView.NO_POSITION;

    public interface OnUserClickListener {
        void onClick(LegacyProfile user);
    }

    public SelectContactAdapter(List<LegacyProfile> users, Activity activity, OnUserClickListener listener) {
        this.users = users;
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        LegacyProfile user = users.get(position);

        holder.txtName.setText(user.getName());

        if (user.getId().equalsIgnoreCase(GlobalDataCache.legacyProfileSelected.getId())) {

            lastClicked = holder.getAbsoluteAdapterPosition();
        }

        holder.bindSelection(position == lastClicked, activity);

        if (user.getAvatarPath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getAvatarPath())
                    .circleCrop()
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_person);
        }

        holder.itemView.setOnClickListener(v -> {
            int newPos = holder.getBindingAdapterPosition();
            if (newPos == RecyclerView.NO_POSITION || newPos == lastClicked) return;

            int previousPos = lastClicked;
            lastClicked = newPos;

            if (previousPos != RecyclerView.NO_POSITION)
                notifyItemChanged(previousPos, "payload_selection");

            notifyItemChanged(lastClicked, "payload_selection");

            if (listener != null) listener.onClick(user);
        });
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains("payload_selection")) {
            holder.bindSelection(position == lastClicked, activity);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        ImageView imgAvatar;
        View onlineIndicator, selected;
        TextView txtName, txtLastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            container = itemView.findViewById(R.id.container_contact);
            onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            selected = itemView.findViewById(R.id.selected);
        }

        public void bindSelection(boolean isSelected, Activity activity) {
            int bgColor = isSelected ? R.color.second_login_color : R.color.background;
            int bgTintColor = isSelected ? R.color.background : R.color.soft_gray;

            selected.setBackgroundTintList(
                    ContextCompat.getColorStateList(activity, bgColor)
            );

            container.setBackgroundTintList(
                    ContextCompat.getColorStateList(activity, bgTintColor)
            );
        }
    }
}
