package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.EmailTest;

import java.util.List;

public class EmailAdapter  extends RecyclerView.Adapter<EmailAdapter.ViewHolder> {
    private Context context;
    private List<EmailTest> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmailTest item, int position);
    }

    public EmailAdapter(List<EmailTest> items, Context context,OnItemClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmailTest email = items.get(position);
        holder.bind(email, position, listener);

        holder.nameContact.setText(email.getContactName());
        holder.titleEmail.setText(email.getTitle());
        holder.emailContent.setText(email.getContent());
        holder.dateEmail.setText(email.getDate());
        holder.favoriteEmailIcon.setImageResource(
                email.isFavorite() ? R.drawable.ic_star : R.drawable.ic_star_off
        );
        int color = email.isFavorite()
                ? ContextCompat.getColor(context, R.color.alert_color)
                : ContextCompat.getColor(context, R.color.gray_hint);

        holder.favoriteEmailIcon.setImageTintList(ColorStateList.valueOf(color));

        holder.favoriteEmailIcon.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (email.isFavorite()){
                    holder.favoriteEmailIcon.setImageResource(R.drawable.ic_star_off);
                    holder.favoriteEmailIcon.
                            setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_hint)));
                    email.setFavorite(false);
                }else {
                    holder.favoriteEmailIcon.setImageResource(R.drawable.ic_star);
                    holder.favoriteEmailIcon.
                            setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.alert_color)));
                    email.setFavorite(true);
                }

            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null){
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION){
                    listener.onItemClick(email, currentPos);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkEmail;
        ImageView favoriteEmailIcon;
        TextView nameContact;
        TextView titleEmail;
        TextView emailContent;
        TextView dateEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkEmail = itemView.findViewById(R.id.check_email);
            favoriteEmailIcon = itemView.findViewById(R.id.favorite_email_icon);
            nameContact = itemView.findViewById(R.id.name_contact);
            titleEmail = itemView.findViewById(R.id.title_email);
            emailContent = itemView.findViewById(R.id.email_content);
            dateEmail = itemView.findViewById(R.id.date_email);
        }

        public void bind(EmailTest emailTest, int position, OnItemClickListener listener) {


        }
    }
}
