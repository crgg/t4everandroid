package com.t4app.t4everandroid.main.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class LegacyProfileAdapter extends RecyclerView.Adapter<LegacyProfileAdapter.ViewHolder> {

    private List<LegacyProfile> profileList;
    private final ListenersUtils.OnProfileActionListener listener;
    private final Activity activity;

    private static Integer lastClicked = RecyclerView.NO_POSITION;

    public LegacyProfileAdapter(List<LegacyProfile> profileList, Activity activity,ListenersUtils.OnProfileActionListener listener) {
        this.profileList = profileList;
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LegacyProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_legacy_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LegacyProfileAdapter.ViewHolder holder, int position) {
        LegacyProfile profile = profileList.get(position);

        holder.valueName.setText(profile.getName());
        holder.userNameProfile.setText(profile.getName());
        holder.valueRelationship.setText(profile.getFamilyRelationship());
        holder.valueAge.setText(String.valueOf(profile.getAge()));
        holder.valueLanguage.setText(profile.getLanguage());
        holder.valueCountry.setText(profile.getCountry());
        holder.valuePersonality.setText(String.join(", ", profile.getBasePersonality()));
//            if(profile.status != null && !profile.status.isEmpty()) {
//                profileStatus.setText(profile.status);
//                profileStatus.setVisibility(View.VISIBLE);
//            } else {
//                profileStatus.setVisibility(View.INVISIBLE);
//            }
        holder.bindSelection(position == lastClicked, activity);

        holder.selectBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int newPos = holder.getAdapterPosition();
                if (newPos == RecyclerView.NO_POSITION || newPos == lastClicked) return;

                int prevPos = lastClicked;
                lastClicked = newPos;

                if (prevPos != RecyclerView.NO_POSITION) notifyItemChanged(prevPos, "payload_selection");
                notifyItemChanged(lastClicked, "payload_selection");

                listener.onSelect(profile);
            }
        });

        holder.chatBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                listener.onChat(profile);
            }
        });
        holder.editBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION){
                    listener.onEdit(profile, currentPos);
                }
            }
        });
        holder.deleteBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION){
                    listener.onDelete(profile, currentPos);
                }
            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull LegacyProfileAdapter.ViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains("payload_selection")) {
            holder.bindSelection(position == lastClicked, activity);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return profileList != null ? profileList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valueName, valueRelationship, valueAge, valueLanguage, valueCountry, valuePersonality;
        TextView profileStatus, userNameProfile;
        MaterialButton selectBtn, chatBtn, editBtn;
        AppCompatImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            valueName = itemView.findViewById(R.id.value_name);
            userNameProfile = itemView.findViewById(R.id.user_name_profile);
            valueRelationship = itemView.findViewById(R.id.value_relationship);
            valueAge = itemView.findViewById(R.id.value_age);
            valueLanguage = itemView.findViewById(R.id.value_language);
            valueCountry = itemView.findViewById(R.id.value_country);
            valuePersonality = itemView.findViewById(R.id.value_personality);

            profileStatus = itemView.findViewById(R.id.profileStatus);

            selectBtn = itemView.findViewById(R.id.select_btn);
            chatBtn   = itemView.findViewById(R.id.chat_btn);
            editBtn   = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        public void bindSelection(boolean selected, Activity activity) {
            int bgColor = selected ? R.color.second_login_color : R.color.white;
            int iconColor = selected ? R.color.white : R.color.second_login_color;
            int text = selected ? R.string.selected : R.string.select;
            int icon = selected ? R.drawable.ic_check : R.drawable.ic_stop;
            int textColor = selected
                    ? Color.WHITE
                    : ContextCompat.getColor(activity, R.color.second_login_color);

            selectBtn.setBackgroundTintList(ContextCompat.getColorStateList(activity, bgColor));
            selectBtn.setTextColor(textColor);
            selectBtn.setText(text);
            selectBtn.setIcon(ContextCompat.getDrawable(activity, icon));
            selectBtn.setIconTint(ContextCompat.getColorStateList(activity, iconColor));
        }
    }

    public void setProfileList(List<LegacyProfile> profileList) {
        this.profileList = profileList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position < 0 || position >= profileList.size()) return;

        profileList.remove(position);
        notifyItemRemoved(position);

        notifyItemRangeChanged(position, profileList.size() - position);
        if (position == lastClicked) {
            lastClicked = RecyclerView.NO_POSITION;
        } else if (position < lastClicked) {
            lastClicked--;
        }
    }

}
