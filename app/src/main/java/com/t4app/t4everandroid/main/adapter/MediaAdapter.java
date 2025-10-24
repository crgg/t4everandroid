package com.t4app.t4everandroid.main.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.Media;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG ="MEDIA_ADAPTER";
    private List<Media> items;
    private Context context;
    private static ListenersUtils.OnMediaActionsListener listener;

    public MediaAdapter(Context context, List<Media> items,
                        ListenersUtils.OnMediaActionsListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_media, parent, false);
            return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Media item = items.get(position);
        ((ConversationViewHolder) holder).bind(item);
    }

    public Context getContext() {
        return context;
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtDate ;
        AppCompatImageButton btnDelete;
        ImageView iconType, iconTypeContent, iconPlay, iconDownload;
        ConversationViewHolder(View v) {
            super(v);
            txtType = v.findViewById(R.id.txtType);
            txtDate = v.findViewById(R.id.date_upload);
            btnDelete = v.findViewById(R.id.btn_delete_conversation);
            iconType = v.findViewById(R.id.icon_type);
            iconPlay = v.findViewById(R.id.icon_play);
            iconDownload = v.findViewById(R.id.icon_download);
            iconTypeContent = v.findViewById(R.id.icon_type_content);
        }

        void bind(Media media) {
            txtType.setText(media.getType());

            switch (media.getType()){
                case "text":
                    iconType.setImageResource(R.drawable.ic_doc);
                    iconTypeContent.setImageResource(R.drawable.ic_doc);
                    iconPlay.setImageResource(R.drawable.ic_eye);
                    break;
                case "video":
                    iconType.setImageResource(R.drawable.ic_video);
                    iconTypeContent.setImageResource(R.drawable.ic_video);
                    iconPlay.setImageResource(R.drawable.ic_play_rounded);
                    break;
                case "image":
                    iconType.setImageResource(R.drawable.ic_gallery);
                    iconTypeContent.setImageResource(R.drawable.ic_gallery);
                    iconPlay.setImageResource(R.drawable.ic_eye);
                    break;
                case "audio":
                    iconType.setImageResource(R.drawable.ic_headset);
                    iconTypeContent.setImageResource(R.drawable.ic_headset);
                    iconPlay.setImageResource(R.drawable.ic_play_rounded);
                    break;

            }

            txtDate.setText(parseDate(media.getDateUpload()));

            iconPlay.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onView(media, currentPos);
                    }
                }
            });

            iconDownload.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onDownload(media, currentPos);
                    }
                }
            });

            btnDelete.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onDelete(media, currentPos);
                    }
                }
            });
        }

    }

    public void addItem(Media newItem) {
        items.add(newItem);
        notifyItemInserted(items.size() - 1);
    }

    public void updateList(List<Media> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }


    public static String parseDate(String dateString) {
        try {
            String dateClean = dateString.replaceAll(":(?=[0-9]{2}$)", "");
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            Date date = isoFormat.parse(dateClean);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getDefault());
            return outputFormat.format(date);

        } catch (Exception e) {
            Log.e(TAG, "parseDate: ", e);
            return dateString;
        }
    }

    public void deleteItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        }
    }


}
