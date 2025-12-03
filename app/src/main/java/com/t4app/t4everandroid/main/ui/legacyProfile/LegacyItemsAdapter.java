package com.t4app.t4everandroid.main.ui.legacyProfile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class LegacyItemsAdapter extends RecyclerView.Adapter<LegacyItemsAdapter.ProfileViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LegacyProfile profile);
    }

    private List<LegacyProfile> profiles;
    private OnItemClickListener listener;

    public LegacyItemsAdapter(List<LegacyProfile> profiles) {
        this.profiles = profiles;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_legacy_text, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        LegacyProfile profile = profiles.get(position);
        holder.bind(profile);
    }

    @Override
    public int getItemCount() {
        return profiles != null ? profiles.size() : 0;
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        void bind(LegacyProfile profile) {
            textView.setText(profile.getName());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(profile);
                }
            });
        }
    }
}

