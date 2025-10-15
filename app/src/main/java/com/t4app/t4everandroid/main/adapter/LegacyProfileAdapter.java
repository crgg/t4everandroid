package com.t4app.t4everandroid.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class LegacyProfileAdapter extends RecyclerView.Adapter<LegacyProfileAdapter.ViewHolder> {

    private List<LegacyProfile> profileList;
    private static ListenersUtils.OnProfileActionListener listener;

    public LegacyProfileAdapter(List<LegacyProfile> profileList, ListenersUtils.OnProfileActionListener listener) {
        this.profileList = profileList;
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
        holder.bind(profile);
    }

    @Override
    public int getItemCount() {
        return profileList != null ? profileList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valueName, valueRelationship, valueAge, valueLanguage, valueCountry, valuePersonality;
        TextView profileStatus, userNameProfile;
        MaterialButton selectBtn, chatBtn, editBtn, deleteBtn;

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

        public void bind(LegacyProfile profile) {
            valueName.setText(profile.getName());
            userNameProfile.setText(profile.getName());
            valueRelationship.setText(profile.getFamilyRelationship());
            valueAge.setText(String.valueOf(profile.getAge()));
            valueLanguage.setText(profile.getLanguage());
            valueCountry.setText(profile.getCountry());
            String personality = String.join(", ", profile.getBasePersonality());
            valuePersonality.setText(personality);
//            if(profile.status != null && !profile.status.isEmpty()) {
//                profileStatus.setText(profile.status);
//                profileStatus.setVisibility(View.VISIBLE);
//            } else {
//                profileStatus.setVisibility(View.INVISIBLE);
//            }

            selectBtn.setOnClickListener(v -> listener.onSelect(profile));
            chatBtn.setOnClickListener(v -> listener.onChat(profile));
            editBtn.setOnClickListener(v -> listener.onEdit(profile));
            deleteBtn.setOnClickListener(v -> listener.onDelete(profile));
        }
    }

    public void setProfileList(List<LegacyProfile> profileList) {
        this.profileList = profileList;
        notifyDataSetChanged();
    }
}
