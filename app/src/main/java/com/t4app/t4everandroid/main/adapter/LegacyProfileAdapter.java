package com.t4app.t4everandroid.main.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class LegacyProfileAdapter extends RecyclerView.Adapter<LegacyProfileAdapter.ViewHolder> {

    private static final String TAG = "LEGACY_ADAPTER";

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
        if (profile.getAlias() != null && !profile.getAlias().isEmpty()){
            holder.userNameProfile.setText(profile.getAlias());
        }else{
            holder.userNameProfile.setText(profile.getName());
        }
        holder.valueRelationship.setText(profile.getFamilyRelationship());
        holder.valueAge.setText(String.valueOf(profile.getAge()));
        holder.valueLanguage.setText(profile.getLanguage());
        holder.valueCountry.setText(profile.getCountry());
        holder.valuePersonality.setText(String.join(", ", profile.getBasePersonality()));

        if (profile.getAvatarPath() != null){
            Glide.with(holder.itemView.getContext())
                    .load(profile.getAvatarPath())
                    .transform(new CircleCrop())
                    .into(holder.imgProfile);
        }

        if (profile.getOpenSession() != null && profile.getLastSession() == null){
            Log.d(TAG, "HAS OPEN SESSION " + profile.getName());
            holder.itemView.post(() -> {
                int newPos = holder.getBindingAdapterPosition();
                if (newPos == RecyclerView.NO_POSITION || newPos == lastClicked) return;

                int prevPos = lastClicked;
                lastClicked = newPos;

                if (prevPos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(prevPos, "payload_selection");
                }
                notifyItemChanged(lastClicked, "payload_selection");

                if (listener != null) listener.onSelect(profile);
            });
        }
        holder.bindSelection(position == lastClicked, activity);

        holder.selectBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int newPos = holder.getBindingAdapterPosition();
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

        holder.questionsBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                listener.onQuestion(profile);
            }
        });

        holder.editBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int currentPos = holder.getAbsoluteAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION){
                    listener.onEdit(profile, currentPos);
                }
            }
        });
        holder.deleteBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int currentPos = holder.getAbsoluteAdapterPosition();
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
        AppCompatImageButton selectBtn, chatBtn, editBtn, questionsBtn, deleteBtn;
        ImageView imgProfile;
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container_item_legacy);
            valueName = itemView.findViewById(R.id.value_name);
            imgProfile = itemView.findViewById(R.id.img_profile);
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
            questionsBtn = itemView.findViewById(R.id.questions_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        public void bindSelection(boolean selected, Activity activity) {
            int bgColor = selected ? R.drawable.bg_border_line_stroke_2_selected : R.drawable.bg_border_line_stroke_2 ;
            int bgTintColor = selected ? R.color.white : R.color.second_login_color;
            int bgContainer = selected ? R.drawable.bg_only_border_line : R.color.background;

            container.setBackgroundResource(bgContainer);

            selectBtn.setBackgroundResource(bgColor);
            selectBtn.setImageTintList(ContextCompat.getColorStateList(activity, bgTintColor));
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
